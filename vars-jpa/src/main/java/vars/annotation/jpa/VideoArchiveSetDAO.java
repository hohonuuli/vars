package vars.annotation.jpa;

import vars.jpa.DAO;
import vars.annotation.IVideoArchiveSetDAO;
import vars.annotation.IVideoArchiveSet;
import vars.annotation.IVideoArchiveDAO;
import vars.annotation.IVideoArchive;
import vars.annotation.ICameraDeployment;
import vars.knowledgebase.Concept;
import org.mbari.jpax.EAO;
import org.mbari.jpax.NonManagedEAO;

import java.util.Set;
import java.util.Date;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import com.google.inject.Inject;


public class VideoArchiveSetDAO extends DAO implements IVideoArchiveSetDAO {

    private final IVideoArchiveDAO videoArchiveDAO;

    @Inject
    public VideoArchiveSetDAO(EAO eao, IVideoArchiveDAO videoArchiveDAO) {
        super(eao);
        this.videoArchiveDAO = videoArchiveDAO;
    }

    public Set<String> findAllLinkValues(IVideoArchiveSet videoArchiveSet, String linkName) {
        return findAllLinkValues(videoArchiveSet, linkName, null);
    }

    public Set<String> findAllLinkValues(IVideoArchiveSet videoArchiveSet, String linkName, Concept concept) {
        Set<String> linkValues = new HashSet<String>();
        for (IVideoArchive videoArchive : videoArchiveSet.getVideoArchives()) {
            linkValues.addAll(videoArchiveDAO.findAllLinkValues(videoArchive, linkName, concept));
        }

        return linkValues;
    }

    public Collection<IVideoArchiveSet> findAllBetweenDates(Date startDate, Date endDate) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("date0", startDate);
        params.put("date1", endDate);
        return getEAO().findByNamedQuery("VideoArchiveSet.findBetweenDates", params);
    }

    public Collection<IVideoArchiveSet> findAll() {
        final Map<String, Object> params = new HashMap<String, Object>();
        return getEAO().findByNamedQuery("VideoArchiveSet.findAll", params);
    }

    public Collection<IVideoArchiveSet> findAllByPlatformAndSequenceNumber(String platform, int sequenceNumber) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("platformName", platform);
        params.put("sequenceNumber", sequenceNumber);
        return getEAO().findByNamedQuery("VideoArchiveSet.findByPlatformAndSequenceNumber", params);
    }

    public Collection<IVideoArchiveSet> findAllByPlatformAndTrackingNumber(String platform, String trackingNumber) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("platformName", platform);
        params.put("trackingNumber", trackingNumber);
        return getEAO().findByNamedQuery("VideoArchiveSet.findByPlatformAndTrackingNumber", params);
    }

    public Collection<IVideoArchiveSet> findAllByTrackingNumber(String trackingNumber) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("trackingNumber", trackingNumber);
        return getEAO().findByNamedQuery("VideoArchiveSet.findByTrackingNumber", params);
    }

    public Set<Integer> findAllSequenceNumbersByPlatformName(String platformName) {
        Set<Integer> sequenceNumbers = new HashSet<Integer>();

        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("platformName", platformName);
        List<IVideoArchiveSet> videoArchiveSets = getEAO().findByNamedQuery("VideoArchiveSet.findByPlatformName", params);
        for (IVideoArchiveSet videoArchiveSet : videoArchiveSets) {
            for (ICameraDeployment cameraDeployment : videoArchiveSet.getCameraDeployments()) {
                sequenceNumbers.add(cameraDeployment.getSequenceNumber());
            }
        }

        return sequenceNumbers;
    }

    public Integer findVideoFrameCountByPrimaryKey(Object primaryKey) {

        Integer count = 0;

        IVideoArchiveSet videoArchiveSet = findByPrimaryKey(VideoArchiveSet.class, primaryKey);
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

    public Collection<IVideoArchiveSet> findAllWithoutCameraDeployment() {
        return null;  // TODO implement this method.
    }

    public Collection<IVideoArchiveSet> findAllWithoutTrackingNumber() {
        return null;  // TODO implement this method.
    }

    public Collection<IVideoArchiveSet> findAllThatDuplicatePlatformAndSequenceNumber() {
        return null;  // TODO implement this method.
    }

    public Collection<IVideoArchiveSet> findAllWithMultipleCameraDeployments() {
        return null;  // TODO implement this method.
    }

    public Collection<IVideoArchiveSet> findAllWithoutDates() {
        return null;  // TODO implement this method.
    }
}
