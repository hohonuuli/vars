package vars.annotation.jpa;

import vars.jpa.DAO;
import vars.annotation.CameraDeployment;
import vars.annotation.*;
import vars.knowledgebase.Concept;
import org.mbari.jpaxx.EAO;
import org.mbari.jpaxx.NonManagedEAO;

import java.util.Set;
import java.util.Date;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import com.google.inject.Inject;


public class VideoArchiveSetDAOImpl extends DAO implements VideoArchiveSetDAO {

    private final VideoArchiveDAO videoArchiveDAO;

    @Inject
    public VideoArchiveSetDAOImpl(EAO eao, VideoArchiveDAO videoArchiveDAO) {
        super(eao);
        this.videoArchiveDAO = videoArchiveDAO;
    }

    public Set<String> findAllLinkValues(VideoArchiveSet videoArchiveSet, String linkName) {
        return findAllLinkValues(videoArchiveSet, linkName, null);
    }

    public Set<String> findAllLinkValues(VideoArchiveSet videoArchiveSet, String linkName, Concept concept) {
        Set<String> linkValues = new HashSet<String>();
        for (VideoArchive videoArchive : videoArchiveSet.getVideoArchives()) {
            linkValues.addAll(videoArchiveDAO.findAllLinkValues(videoArchive, linkName, concept));
        }

        return linkValues;
    }

    public Collection<VideoArchiveSet> findAllBetweenDates(Date startDate, Date endDate) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("date0", startDate);
        params.put("date1", endDate);
        return getEAO().findByNamedQuery("VideoArchiveSet.findBetweenDates", params);
    }

    public Collection<VideoArchiveSet> findAll() {
        final Map<String, Object> params = new HashMap<String, Object>();
        return getEAO().findByNamedQuery("VideoArchiveSet.findAll", params);
    }

    public Collection<VideoArchiveSet> findAllByPlatformAndSequenceNumber(String platform, int sequenceNumber) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("platformName", platform);
        params.put("sequenceNumber", sequenceNumber);
        return getEAO().findByNamedQuery("VideoArchiveSet.findByPlatformAndSequenceNumber", params);
    }

    public Collection<VideoArchiveSet> findAllByPlatformAndTrackingNumber(String platform, String trackingNumber) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("platformName", platform);
        params.put("trackingNumber", trackingNumber);
        return getEAO().findByNamedQuery("VideoArchiveSet.findByPlatformAndTrackingNumber", params);
    }

    public Collection<VideoArchiveSet> findAllByTrackingNumber(String trackingNumber) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("trackingNumber", trackingNumber);
        return getEAO().findByNamedQuery("VideoArchiveSet.findByTrackingNumber", params);
    }

    public Set<Integer> findAllSequenceNumbersByPlatformName(String platformName) {
        Set<Integer> sequenceNumbers = new HashSet<Integer>();

        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("platformName", platformName);
        List<VideoArchiveSet> videoArchiveSets = getEAO().findByNamedQuery("VideoArchiveSet.findByPlatformName", params);
        for (VideoArchiveSet videoArchiveSet : videoArchiveSets) {
            for (CameraDeployment cameraDeployment : videoArchiveSet.getCameraDeployments()) {
                sequenceNumbers.add(cameraDeployment.getSequenceNumber());
            }
        }

        return sequenceNumbers;
    }

    public Integer findVideoFrameCountByPrimaryKey(Object primaryKey) {

        Integer count = 0;

        VideoArchiveSet videoArchiveSet = findByPrimaryKey(VideoArchiveSet.class, primaryKey);
        if (videoArchiveSet != null) {

            // Do inside of a transaction to account for lazy loading
            if (!getEAO().isManaged()) {
                ((NonManagedEAO) getEAO()).startTransaction();
            }

            count = videoArchiveSet.getVideoFrames().size();

            if (!getEAO().isManaged()) {
                ((NonManagedEAO) getEAO()).startTransaction();
            }
        }
        else {
            log.info("No VideoArchiveSet with id = " + primaryKey + " was found in the database");
        }

        return count;
    }

    public Collection<VideoArchiveSet> findAllWithoutCameraDeployment() {
        return null;  // TODO implement this method.
    }

    public Collection<VideoArchiveSet> findAllWithoutTrackingNumber() {
        return null;  // TODO implement this method.
    }

    public Collection<VideoArchiveSet> findAllThatDuplicatePlatformAndSequenceNumber() {
        return null;  // TODO implement this method.
    }

    public Collection<VideoArchiveSet> findAllWithMultipleCameraDeployments() {
        return null;  // TODO implement this method.
    }

    public Collection<VideoArchiveSet> findAllWithoutDates() {
        return null;  // TODO implement this method.
    }
}
