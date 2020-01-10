/*
 * @(#)MergeEXPDAnnotations2.java   2013.03.07 at 02:43:56 PST
 *
 * Copyright 2009 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package org.mbari.vars.integration;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.inject.Injector;
import java.util.*;
import java.util.stream.Collectors;

import org.mbari.expd.*;
import org.mbari.expd.actions.CollateByAlternateTimecodeFunction;
import org.mbari.expd.actions.CollateByDateFunction;
import org.mbari.expd.actions.CollateByTimecodeFunction;
import org.mbari.expd.actions.CollateFunction;
import org.mbari.expd.jdbc.DAOFactoryImpl;
import org.mbari.expd.jdbc.UberDatumImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.DAO;
import vars.annotation.*;
import vars.annotation.ui.StateLookup;
import vars.integration.MergeFunction;
import vars.integration.MergeHistory;
import vars.integration.MergeHistoryDAO;
import vars.integration.MergeType;

/**
 * Implementation of the merge function
 * @author brian
 */
public class MergeEXPDAnnotations implements MergeFunction<Map<VideoFrame, UberDatum>> {

    private final double offsetSecs = 7.5;

    /**  */
    public final Logger log = LoggerFactory.getLogger(getClass());
    private Dive dive;
    private MergeHistory mergeHistory  = new MergeHistory();
    private final String platform;
    private final int sequenceNumber;
    private Collection<UberDatum> uberData;
    private final boolean useHD;
    private Collection<VideoFrame> videoFrames;

    /**
     * Constructs ...
     *
     * @param platform
     * @param sequenceNumber
     * @param useHD
     */
    public MergeEXPDAnnotations(String platform, int sequenceNumber, boolean useHD) {
        this.platform = platform;
        this.sequenceNumber = sequenceNumber;
        this.useHD = useHD;

    }

    /**
     *
     * @param mergeType
     * @return
     */
    public Map<VideoFrame, UberDatum> apply(MergeType mergeType) {
        if (log.isDebugEnabled()) {
            log.debug("Applying " + mergeType + " merge to " + platform + " #" + sequenceNumber + " [use HD = " +
                    useHD + "]");
        }
        fetch();

        Map<VideoFrame, UberDatum> data = coallate(mergeType);

        update(data, mergeType);

        return data;
    }

    /**
     *
     * @param mergeType
     * @return
     */
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

        return data;
    }

    /**
     * Match by Date, then any that aren't matched, match by timecode
     */
    private Map<VideoFrame, UberDatum> coallateConservative() {

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

    /**
     * Match bogus dates by timecode, all others by date
     * @return
     */
    private Map<VideoFrame, UberDatum> coallatePragmatic() {

        // Merge annotations with bogus dates by timecode
        List<VideoFrame> bogusDates = videoFrames.stream()
                .filter(input -> {
                    Date date = input.getRecordedDate();
                    return (date == null) || date.before(dive.getStartDate()) || date.after(dive.getEndDate());
                })
                .collect(Collectors.toList());

        log.debug(Joiner.on(", ").join(bogusDates));

        Collection<VideoFrame> leftovers = new ArrayList<VideoFrame>(videoFrames);
        Map<VideoFrame, UberDatum> merged = new HashMap<>();
        try {
            merged = mergeByTimecode(bogusDates, uberData);
            leftovers.removeAll(merged.keySet());
        }
        catch (Exception e) {
            log.warn("Failed to merge " + bogusDates.size() + " annotations with bad dates by timecode", e);
            leftovers.remove(bogusDates);
        }

        if (leftovers.size() > 0) {
            merged.putAll(mergeByDate(leftovers, uberData));
        }

        return merged;

    }

    private void fetch() {

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
        List<UberDatum> uberData = uberDatumDAO.fetchData(dive, useHD, offsetSecs);

        // If no cameraData is found we don't get any nav data either. In that case
        // we just fetch nav data and convert it to uberdata.
        if (uberData.size() == 0) {
            NavigationDatumDAO navigationDatumDAO = daoFactory.newNavigationDatumDAO();
            List<NavigationDatum> navigationData = navigationDatumDAO.fetchBestNavigationData(dive);
            uberData.addAll(Collections2.transform(navigationData, new Function<NavigationDatum, UberDatum>() {

                public UberDatum apply(NavigationDatum from) {
                    return new UberDatumImpl(null, from, null);
                }

            }));
        }

        return uberData;
    }


    private List<VideoFrame> fetchVarsData() {
        Injector injector = StateLookup.GUICE_INJECTOR;
        AnnotationDAOFactory annotationDAOFactory = injector.getInstance(AnnotationDAOFactory.class);
        VideoArchiveSetDAO videoArchiveSetDAO = annotationDAOFactory.newVideoArchiveSetDAO();
        List<VideoFrame> myVideoFrames = new ArrayList<VideoFrame>();

        videoArchiveSetDAO.startTransaction();

        Collection<VideoArchiveSet> videoArchiveSets = videoArchiveSetDAO.findAllByPlatformAndSequenceNumber(platform,
                sequenceNumber);

        for (VideoArchiveSet videoArchiveSet : videoArchiveSets) {
            // Only fetch frames appropriate for HD or Beta merge (not both!!)
            for (VideoArchive videoArchive : videoArchiveSet.getVideoArchives()) {
                if (useHD && videoArchive.getName().toUpperCase().endsWith("HD")) {
                    myVideoFrames.addAll(videoArchive.getVideoFrames());
                }
                else if (!useHD && !videoArchive.getName().toUpperCase().endsWith("HD")) {
                    myVideoFrames.addAll(videoArchive.getVideoFrames());
                }
            }
        }

        videoArchiveSetDAO.endTransaction();
        videoArchiveSetDAO.close();

        if (myVideoFrames.size() == 0) {
            mergeHistory.setStatusMessage(mergeHistory.getStatusMessage() + "; No annotations found in VARS");
        }

        return myVideoFrames;
    }

    /**
     * @return
     */
    public MergeHistory getMergeHistory() {
        return mergeHistory;
    }

    private Map<VideoFrame, UberDatum> mergeByDate(Collection<VideoFrame> vfc, Collection<UberDatum> udc) {

        // Merge by Date
        CollateFunction<Date> f1 = new CollateByDateFunction();

        // Extract the dates from the video frames
        List<Date> d = vfc.stream()
                .map(VideoFrame::getRecordedDate)
                .collect(Collectors.toList());

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
        CollateFunction<String> f2 = useHD
                ? new CollateByAlternateTimecodeFunction() : new CollateByTimecodeFunction();

        List<String> d = vfc.stream()
                .map(VideoFrame::getTimecode)
                .collect(Collectors.toList());

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
     *
     * @param data
     * @param mergeType
     */
    public void update(Map<VideoFrame, UberDatum> data, MergeType mergeType) {
        fetch();

        mergeHistory.setMergeDate(new Date());
        mergeHistory.setStatusMessage("Using " + mergeType + " merge");

        Injector injector = StateLookup.GUICE_INJECTOR;
        AnnotationDAOFactory annotationDAOFactory = injector.getInstance(AnnotationDAOFactory.class);
        DAO dao = annotationDAOFactory.newDAO();

        dao.startTransaction();

        // Modify data
        int fixedDateCount = 0;
        int badCameraDataCount = 0;
        int badCtdDataCount = 0;
        int badNavDataCount = 0;

        for (VideoFrame videoFrame : data.keySet()) {
            UberDatum uberDatum = data.get(videoFrame);
            String nav = (uberDatum.getNavigationDatum() == null)
                    ? "NO Navigation Data"
                    : uberDatum.getNavigationDatum().getDate() + " : Depth = " +
                    uberDatum.getNavigationDatum().getDepth();
            String cam = (uberDatum.getCameraDatum() == null)
                    ? "NO Camera Data"
                    : uberDatum.getCameraDatum().getTimecode() + " - " +
                    uberDatum.getCameraDatum().getAlternativeTimecode() + " - " +
                    uberDatum.getCameraDatum().getDate();

            String vf = videoFrame.getRecordedDate() == null ? "MISSING" :  videoFrame.getRecordedDate().toInstant().toString();

            log.debug(videoFrame.getTimecode() + " : " + vf + " :NAV: " + nav + " :CAM: " + cam);

            videoFrame = dao.find(videoFrame);
            if (mergeHistory.getVideoArchiveSetID() == null || mergeHistory.getVideoArchiveSetID() < 0) {
                mergeHistory.setVideoArchiveSetID((Long) videoFrame.getVideoArchive().getVideoArchiveSet().getPrimaryKey());
            }

            Date recordedDate = videoFrame.getRecordedDate();

            // ---- Update cameradata
            CameraData cameraData = videoFrame.getCameraData();
            CameraDatum cameraDatum = uberDatum.getCameraDatum();

            if (cameraDatum != null) {

                cameraData.setFocus((cameraDatum.getFocus() == null) ? null : Math.round(cameraDatum.getFocus()));
                cameraData.setLogDate(cameraDatum.getDate());
                if (useHD) {
                    videoFrame.setAlternateTimecode(cameraDatum.getTimecode());
                }
                else {
                    videoFrame.setAlternateTimecode(cameraDatum.getAlternativeTimecode());
                }
                cameraData.setZoom((cameraDatum.getZoom() == null) ? null : Math.round(cameraDatum.getZoom()));
                cameraData.setIris((cameraDatum.getIris() == null) ? null : Math.round(cameraDatum.getIris()));
            }
            else {
                log.debug("No camera data was found in EXPD for {}", videoFrame);
                cameraData.setFocus(null);
                cameraData.setZoom(null);
                cameraData.setIris(null);
                cameraData.setLogDate(null);
                badCameraDataCount++;
            }

            // ---- Update physicaldata
            PhysicalData physicalData = videoFrame.getPhysicalData();
            CtdDatum ctdDatum = uberDatum.getCtdDatum();

            if (ctdDatum != null) {
                physicalData.setLight(ctdDatum.getLightTransmission());
                physicalData.setOxygen(ctdDatum.getOxygen());
                physicalData.setSalinity(ctdDatum.getSalinity());
                physicalData.setTemperature(ctdDatum.getTemperature());
            }
            else {
                log.debug("No CTD data was found in EXPD for {}", videoFrame);
                physicalData.setLight(null);
                physicalData.setOxygen(null);
                physicalData.setSalinity(null);
                physicalData.setTemperature(null);
                badCtdDataCount++;
            }

            NavigationDatum navigationDatum = uberDatum.getNavigationDatum();

            if (navigationDatum != null) {
                // HACK Used to addrees JIRA: VARS-661
                // try {
                //   double depth = Seawater.depth(ctdDatum.getPressure(), navigationDatum.getLatitude());
                //   physicalData.setDepth((float) depth);
                // }
                // catch (Exception e) {
                //     log.warn("Hack failed: {}", e);
                // }
                physicalData.setDepth(navigationDatum.getDepth());
                physicalData.setLatitude(navigationDatum.getLatitude());
                physicalData.setLogDate(navigationDatum.getDate());
                physicalData.setLongitude(navigationDatum.getLongitude());
            }
            else {
                log.debug("No navigation data was found in EXPD for {}", videoFrame);
                physicalData.setDepth(null);
                physicalData.setLatitude(null);
                physicalData.setLogDate(null);
                physicalData.setLongitude(null);
                badNavDataCount++;
            }

            // ---- Update date
            Date date = null;
            switch (mergeType) {
                case PESSIMISTIC:
                    mergeHistory.setDateSource("EXPD");

                    // Change dates to ones found in EXPD
                    if (navigationDatum != null) {
                        date = navigationDatum.getDate();
                    }

                    if ((date == null) && (cameraDatum != null)) {
                        date = cameraDatum.getDate();
                    }

                    if (recordedDate == null || !recordedDate.equals(date)) {
                        videoFrame.setRecordedDate(date);
                        fixedDateCount++;
                    }

                    break;

                case PRAGMATIC:
                    if ((recordedDate == null) || recordedDate.before(dive.getStartDate()) ||
                            recordedDate.after(dive.getEndDate())) {

                        if (navigationDatum != null) {
                            date = navigationDatum.getDate();
                        }
                        if ((date == null) && (cameraDatum != null)) {
                            date = cameraDatum.getDate();
                        }

                        videoFrame.setRecordedDate(date);

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


        dao.endTransaction();
        dao.close();

        // --- Update MergeHistory -----------------------------------------------------------------

        // ---- Set the navigationedited flag
        Collection<UberDatum> rawNavRecords = Collections2.filter(data.values(), new Predicate<UberDatum>() {
            public boolean apply(UberDatum input) {
                NavigationDatum nav = input.getNavigationDatum();
                return (nav != null) && (nav.isEdited() == Boolean.FALSE);
            }
        });

        // ---- Specify the source of the data information
        switch (mergeType) {
            case PESSIMISTIC:
                mergeHistory.setDateSource("EXPD");
                break;
            default:
                mergeHistory.setDateSource("VARS");
        }


        if (fixedDateCount > 0) {
            mergeHistory.setDateSource("Both");
            mergeHistory.setStatusMessage(mergeHistory.getStatusMessage() + "; Fixed " + fixedDateCount +
                    " annotation dates");
        }

        if (badCameraDataCount > 0) {
            mergeHistory.setStatusMessage(mergeHistory.getStatusMessage() + "; " +
                    badCameraDataCount + " annotations without camera data");
        }

        if (badCtdDataCount > 0) {
            mergeHistory.setStatusMessage(mergeHistory.getStatusMessage() + "; " +
                    badCtdDataCount + " annotations without ctd data");
        }

        if (badNavDataCount > 0) {
            mergeHistory.setStatusMessage(mergeHistory.getStatusMessage() + "; " +
                    badNavDataCount + " annotations without navigation data");
        }

        mergeHistory.setHd(useHD);
        mergeHistory.setMergeType(mergeType.name());
        mergeHistory.setNavigationEdited(rawNavRecords.size() == 0);
        mergeHistory.setVideoFrameCount(videoFrames.size());

        DAOFactory daoFactory = new DAOFactoryImpl();
        DiveDAO diveDAO = daoFactory.newDiveDAO();
        MergeHistoryDAO mergeHistoryDAO = new MergeHistoryDAOImpl(annotationDAOFactory, diveDAO);

        if (data.size() > 0) {
            mergeHistoryDAO.update(mergeHistory);
        }


    }
}
