/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mbari.vars.integration;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.mbari.expd.CameraDatum;
import org.mbari.expd.CtdDatum;
import org.mbari.expd.DAOFactory;
import org.mbari.expd.Dive;
import org.mbari.expd.DiveDAO;
import org.mbari.expd.NavigationDatum;
import org.mbari.expd.UberDatum;
import org.mbari.expd.UberDatumDAO;
import org.mbari.expd.actions.CoallateByAlternateTimecodeFunction;
import org.mbari.expd.actions.CoallateByDateFunction;
import org.mbari.expd.actions.CoallateByTimecodeFunction;
import org.mbari.expd.actions.CoallateFunction;
import org.mbari.expd.jdbc.DAOFactoryImpl;
import vars.DAO;
import vars.integration.MergeStatusDAO;
import vars.integration.MergeFunction;
import vars.integration.MergeFunction.MergeType;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.CameraData;
import vars.annotation.PhysicalData;
import vars.annotation.VideoArchiveSet;
import vars.annotation.VideoArchiveSetDAO;
import vars.annotation.VideoFrame;
import vars.annotation.ui.Lookup;
import vars.integration.MergeStatus;
import vars.jpa.JPAEntity;

/**
 * Implementation of the merge function
 * @author brian
 */
public class MergeEXPDAnnotations implements MergeFunction<Map<VideoFrame, UberDatum>> {

    private final String platform;
    private final int sequenceNumber;
    private final boolean useHD;
    private Collection<VideoFrame> videoFrames;
    private Collection<UberDatum> uberData;
    private final double offsetSecs = 7.5;
    private Dive dive;
    private MergeStatus mergeStatus;

    public MergeEXPDAnnotations(String platform, int sequenceNumber, boolean useHD) {
        this.platform = platform;
        this.sequenceNumber = sequenceNumber;
        this.useHD = useHD;
        
    }

    public Map<VideoFrame, UberDatum> apply(MergeType mergeType) {
        fetch();
        Map<VideoFrame, UberDatum> data = coallate(mergeType);
        update(data, mergeType);
        return data;
    }

    public Map<VideoFrame, UberDatum> coallate(MergeType mergeType) {
        fetch();
        Map<VideoFrame, UberDatum> data = new HashMap<VideoFrame, UberDatum>();
        switch (mergeType) {
            case CONSERVATIVE:
                data = coallateConservative();
                break;
            case OPTIMISTIC:
                data = coallateOptimistic();
                break;
            case PESSIMISTIC:
                data = coallatePessimistic();
                break;
            case PRAGMATIC:
                data = coallatePragmatic();
                break;
        }
        mergeStatus.setMergeDate(new Date());
        mergeStatus.setStatusMessage("Using " + mergeType + " merge");
        return data;
    }

    public void update(Map<VideoFrame, UberDatum> data, MergeType mergeType) {
        fetch();
        Injector injector = (Injector) Lookup.getGuiceInjectorDispatcher().getValueObject();
        AnnotationDAOFactory annotationDAOFactory = injector.getInstance(AnnotationDAOFactory.class);
        DAO dao = annotationDAOFactory.newDAO();
        dao.startTransaction();

        // Modify data
        int fixedDateCount = 0;
        

        for (VideoFrame videoFrame : data.keySet()) {
            UberDatum uberDatum = data.get(videoFrame);
            videoFrame = dao.find(videoFrame);
            Date recordedDate = videoFrame.getRecordedDate();

            // ---- Update cameradata
            CameraData cameraData = videoFrame.getCameraData();
            CameraDatum cameraDatum = uberDatum.getCameraDatum();
            cameraData.setFocus(cameraDatum.getFocus() == null ? null : Math.round(cameraDatum.getFocus()));
            cameraData.setLogDate(cameraDatum.getDate());
            videoFrame.setAlternateTimecode(cameraDatum.getAlternativeTimecode());
            cameraData.setZoom(cameraDatum.getZoom() == null ? null : Math.round(cameraDatum.getZoom()));
            cameraData.setIris(cameraDatum.getIris() == null ? null : Math.round(cameraDatum.getIris()));

            // ---- Update physicaldata
            PhysicalData physicalData = videoFrame.getPhysicalData();
            CtdDatum ctdDatum = uberDatum.getCtdDatum();
            physicalData.setLight(ctdDatum.getLightTransmission());
            physicalData.setOxygen(ctdDatum.getOxygen());
            physicalData.setSalinity(ctdDatum.getSalinity());
            physicalData.setTemperature(ctdDatum.getTemperature());

            NavigationDatum navigationDatum = uberDatum.getNavigationDatum();
            physicalData.setDepth(navigationDatum.getDepth());
            physicalData.setLatitude(navigationDatum.getLatitude());
            physicalData.setLogDate(navigationDatum.getDate());
            physicalData.setLongitude(navigationDatum.getLongitude());


            // ---- Update date 
            switch (mergeType) {
            case PESSIMISTIC:
                mergeStatus.setDateSource("EXPD");

                // Change dates to ones found in EXPD
                if (recordedDate != null && recordedDate.equals(cameraDatum.getDate())) {
                    videoFrame.setRecordedDate(cameraDatum.getDate());
                    fixedDateCount++;
                }
                break;
            case PRAGMATIC:
                if (videoFrame.getRecordedDate() == null ||
                        recordedDate.before(dive.getStartDate()) ||
                        recordedDate.after(dive.getEndDate())) {
                    videoFrame.setRecordedDate(cameraDatum.getDate());
                    fixedDateCount++;
                }
                break;
            }

        }
        
        // ---- Change unmerged dates to null
        if (MergeType.PESSIMISTIC == mergeType) {
            Collection<VideoFrame> unmerged = new ArrayList<VideoFrame>(videoFrames);
            unmerged.removeAll(data.keySet());
            for (VideoFrame videoFrame : unmerged) {
                videoFrame = dao.find(videoFrame);
                videoFrame.setRecordedDate(null);
            }
        }

        // ---- Specify the source of the data information
        switch (mergeType) {
            case PESSIMISTIC:
                mergeStatus.setDateSource("EXPD");
                break;
            default:
                mergeStatus.setDateSource("VARS");
        }

        mergeStatus.setMerged(fixedDateCount);
        if (fixedDateCount > 0) {
            mergeStatus.setDateSource("Both");
            mergeStatus.setStatusMessage(mergeStatus.getStatusMessage() + "; Fixed " +
                    fixedDateCount + " annotation dates");
        }

        dao.endTransaction();


        // ---- Set the navigationedited flag
        Collection<UberDatum> rawNavRecords = Collections2.filter(data.values(), new Predicate<UberDatum>() {
            public boolean apply(UberDatum input) {
                return input.getNavigationDatum().isEdited() == Boolean.FALSE;
            }
        });

        mergeStatus.setNavigationEdited(rawNavRecords.size() == 0 ? 1 : 0);

        // Set merged flat
        mergeStatus.setMerged(data.size() > 0 ? 1 : 0);

        DAOFactory daoFactory = new DAOFactoryImpl();
        DiveDAO diveDAO = daoFactory.newDiveDAO();
        MergeStatusDAO mergeStatusDAO = new MergeStatusDAOImpl(annotationDAOFactory, diveDAO);
        mergeStatusDAO.update(mergeStatus);


    }

    private void fetch() {

        if (mergeStatus == null) {
            mergeStatus = fetchMergeStatus();
        }

        if (uberData == null) {
            uberData = fetchExpdData();
        }

        if (videoFrames == null) {
            videoFrames = fetchVarsData();
        }
        
    }
    
    private List<UberDatum> fetchExpdData() {
        // Lookup dive
        DAOFactory daoFactory = new DAOFactoryImpl();
        DiveDAO diveDAO = daoFactory.newDiveDAO();
        dive = diveDAO.findByPlatformAndDiveNumber(platform, sequenceNumber);
        
        // Fetch EXPD data
        UberDatumDAO uberDatumDAO = daoFactory.newUberDatumDAO();
        return uberDatumDAO.fetchData(dive, useHD, offsetSecs);
    }

    private MergeStatus fetchMergeStatus() {
        Injector injector = (Injector) Lookup.getGuiceInjectorDispatcher().getValueObject();
        AnnotationDAOFactory annotationDAOFactory = injector.getInstance(AnnotationDAOFactory.class);
        VideoArchiveSetDAO videoArchiveSetDAO = annotationDAOFactory.newVideoArchiveSetDAO();
        DAOFactory daoFactory = new DAOFactoryImpl();
        DiveDAO diveDAO = daoFactory.newDiveDAO();
        MergeStatusDAO dao = new MergeStatusDAOImpl(annotationDAOFactory, diveDAO);
        MergeStatus myMergeStatus =  dao.findByPlatformAndSequenceNumber(platform, sequenceNumber);
        if (myMergeStatus == null) {
            myMergeStatus = new MergeStatus();
            videoArchiveSetDAO.startTransaction();
            Collection<VideoArchiveSet> videoArchiveSets = videoArchiveSetDAO.findAllByPlatformAndSequenceNumber(platform, sequenceNumber);
            if (videoArchiveSets.size() > 0) {
                VideoArchiveSet videoArchiveSet = videoArchiveSets.iterator().next();
                myMergeStatus.setVideoArchiveSetID(((JPAEntity) videoArchiveSet).getId());
                myMergeStatus.setVideoFrameCount((long) videoArchiveSet.getVideoFrames().size());
                myMergeStatus.setStatusMessage("");
            }
            videoArchiveSetDAO.endTransaction();
        }
        return myMergeStatus;
    }

    private List<VideoFrame> fetchVarsData() {
        Injector injector = (Injector) Lookup.getGuiceInjectorDispatcher().getValueObject();
        AnnotationDAOFactory annotationDAOFactory = injector.getInstance(AnnotationDAOFactory.class);
        VideoArchiveSetDAO videoArchiveSetDAO = annotationDAOFactory.newVideoArchiveSetDAO();
        List<VideoFrame> myVideoFrames = new ArrayList<VideoFrame>();
        videoArchiveSetDAO.startTransaction();
        Collection<VideoArchiveSet> videoArchiveSets = videoArchiveSetDAO.findAllByPlatformAndSequenceNumber(platform, sequenceNumber);
        for (VideoArchiveSet videoArchiveSet : videoArchiveSets) {
            myVideoFrames.addAll(videoArchiveSet.getVideoFrames());
        }
        videoArchiveSetDAO.endTransaction();

        if (myVideoFrames.size() == 0) {
            mergeStatus.setStatusMessage(mergeStatus.getStatusMessage() + "; No annotations found in VARS");
        }
        return myVideoFrames;
    }

    private Map<VideoFrame, UberDatum> mergeByDate(Collection<VideoFrame> vfc, Collection<UberDatum> udc) {
        // Merge by Date
        CoallateFunction<Date> f1 = new CoallateByDateFunction();

        Collection<Date> d = Collections2.transform(vfc, new Function<VideoFrame, Date>() {
            public Date apply(VideoFrame from) {
                return from.getRecordedDate();
            }
        });

        final Map<Date, UberDatum> r1 = f1.apply(d, udc, (long) offsetSecs * 1000);

        // Associate UberDatum with VideoFrame
        final Map<VideoFrame, UberDatum> out = new LinkedHashMap<VideoFrame, UberDatum>();
        for (VideoFrame videoFrame : vfc) {
            UberDatum uberDatum = r1.get(videoFrame.getRecordedDate());
            if (uberDatum != null) {
                out.put(videoFrame, uberDatum);
            }
        }

        return out;
    }


    private Map<VideoFrame, UberDatum> mergeByTimecode(Collection<VideoFrame> vfc, Collection<UberDatum> udc) {

        // Make sure we're using the correct merge function for HD and Beta
        CoallateFunction<String> f2 = useHD ? new CoallateByAlternateTimecodeFunction() :
                    new CoallateByTimecodeFunction();

        Collection<String> d = Collections2.transform(vfc, new Function<VideoFrame, String>() {
            public String apply(VideoFrame from) {
                return from.getTimecode();
            }
        });

        final Map<String, UberDatum> r2 = f2.apply(d, udc, (long) offsetSecs * 1000);

        // Associate UberDatum with VideoFrame
        final Map<VideoFrame, UberDatum> out = new LinkedHashMap<VideoFrame, UberDatum>();
        for (VideoFrame videoFrame : vfc) {
            UberDatum uberDatum = r2.get(videoFrame.getTimecode());
            if (uberDatum != null) {
                out.put(videoFrame, uberDatum);
            }
        }

        return out;

    }


    /**
     * Match by Date, then any that aren't matched, match by timecode
     */
    private Map<VideoFrame, UberDatum>  coallateConservative() {

        // Merge by Date
        Map<VideoFrame, UberDatum> merged = mergeByDate(videoFrames, uberData);

        // Merge outstanding ones by timecode
        Collection<VideoFrame> leftovers = new ArrayList<VideoFrame>(videoFrames);
        leftovers.removeAll(merged.keySet());
        if (leftovers.size() > 0) {
            merged.putAll(mergeByTimecode(leftovers, uberData));
        }

        return merged;
    }


    /**
     * Match bogus dates by timecode, all others by date
     * @return
     */
    private Map<VideoFrame, UberDatum> coallatePragmatic() {

        // Merge annotations with bogus dates by timecode
        Collection<VideoFrame> bogusDates = Collections2.filter(videoFrames, new Predicate<VideoFrame>() {
            public boolean apply(VideoFrame input) {
                Date date = input.getRecordedDate();
                return (date != null) || date.before(dive.getStartDate()) || date.after(dive.getEndDate());
            }
        });

        Map<VideoFrame, UberDatum> merged = mergeByTimecode(bogusDates, uberData);

        // Merge outstanding ones by timecode
        Collection<VideoFrame> leftovers = new ArrayList<VideoFrame>(videoFrames);
        leftovers.removeAll(merged.keySet());
        if (leftovers.size() > 0) {
            merged.putAll(mergeByDate(leftovers, uberData));
        }

        return merged;

    }

    /**
     * Match by date only
     */
    private Map<VideoFrame, UberDatum> coallateOptimistic() {
        return mergeByDate(videoFrames, uberData);
    }

    /**
     * Match by timecode only
     */
    private Map<VideoFrame, UberDatum> coallatePessimistic() {
        return mergeByTimecode(videoFrames, uberData);
    }



}
