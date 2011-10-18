package vars.annotation.jpa;

import vars.annotation.FormatCodes;
import vars.jpa.DAO;
import vars.jpa.JPAEntity;
import vars.knowledgebase.Concept;
import vars.VARSPersistenceException;

import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.google.inject.Inject;
import javax.persistence.EntityManager;
import vars.annotation.AnnotationFactory;
import vars.annotation.Association;
import vars.annotation.CameraDeployment;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.annotation.VideoArchiveSet;
import vars.annotation.VideoFrame;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:41:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class VideoArchiveDAOImpl extends DAO implements VideoArchiveDAO {

    private final AnnotationFactory annotationFactory;

    @Inject
    public VideoArchiveDAOImpl(EntityManager entityManager, AnnotationFactory annotationFactory) {
        super(entityManager);
        this.annotationFactory = annotationFactory;
    }

    public Set<String> findAllLinkValues(VideoArchive videoArchive, String linkName) {

        return findAllLinkValues(videoArchive, linkName, null);
    }

    /**
     * This should only be called within JPA/DAO transaction
     * @param videoArchive
     * @param linkName
     * @param concept
     * @return
     */
    public Set<String> findAllLinkValues(VideoArchive videoArchive, String linkName, Concept concept) {

        // Due to lazy loading we want to iterate through all objects in a collection
        videoArchive = find(videoArchive);
        Collection<? extends VideoFrame> videoFrames = videoArchive.getVideoArchiveSet().getVideoFrames();
        Set<String> linkValues = new HashSet<String>();
        for (VideoFrame videoFrame : videoFrames) {
            for (Observation observation : videoFrame.getObservations()) {
                if (concept == null || concept.getConceptName(observation.getConceptName()) != null) {
                    for (Association association : observation.getAssociations()) {
                        if (linkName.equals(association.getLinkName())) {
                            linkValues.add(association.getLinkValue());
                        }
                    }
                }
            }
        }

        return linkValues;
    }

    /**
     * Call this within a transaction. The returned {@link VideoArchive} will be 
     * in the database
     */
    public VideoArchive findOrCreateByParameters(String platform, int sequenceNumber, String videoArchiveName) {

        // ---- Step 1: Look up VideoArchive by Name
        VideoArchive videoArchive = findByName(videoArchiveName);

        if (videoArchive == null) {
            // ---- Step 2: No match was found. See if the desired deployment exists
            VideoArchiveSet videoArchiveSet = null;
            Map<String, Object> params2 = new HashMap<String, Object>();
            params2.put("platformName", platform);
            params2.put("sequenceNumber", sequenceNumber);
            List<VideoArchiveSet> vas = findByNamedQuery("VideoArchiveSet.findByPlatformAndSequenceNumber", params2);
            if (vas.size() == 1) {
                videoArchiveSet = vas.get(0);
            } else if (vas.size() > 1) {
                throw new VARSPersistenceException("There's a problem!! More than one VideoArchiveSet " +
                        "with platform = " + platform + " and sequenceNumber = " + sequenceNumber +
                        " exists in the database");
            } 
            else {
                videoArchiveSet = annotationFactory.newVideoArchiveSet();
                videoArchiveSet.setPlatformName(platform);
                videoArchiveSet.setFormatCode(FormatCodes.UNKNOWN.getCode());
                persist(videoArchiveSet);
                CameraDeployment cameraDeployment = annotationFactory.newCameraDeployment();
                cameraDeployment.setSequenceNumber(sequenceNumber);
                videoArchiveSet.addCameraDeployment(cameraDeployment);
                persist(cameraDeployment);
            }
            
            videoArchive = annotationFactory.newVideoArchive();
            videoArchive.setName(videoArchiveName);
            videoArchiveSet.addVideoArchive(videoArchive);
            persist(videoArchive);
            
        }

        return videoArchive;
    }

    public VideoArchive findByName(final String name) {
        VideoArchive videoArchive = null;
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("name", name);
        }};

        List<VideoArchive> videoArchives = findByNamedQuery("VideoArchive.findByName", params);
        if (videoArchives.size() == 1) {
            videoArchive = videoArchives.get(0);
        } else if (videoArchives.size() > 1) {
            throw new VARSPersistenceException("There's a problem!! More than one VideoArchive named " +
                    name + " exists in the database");
        }

        return videoArchive;
    }

    /**
     * This should be called within a DAO transaction
     * @param videoArchive
     * @return
     */
    public VideoArchive deleteEmptyVideoFrames(VideoArchive videoArchive) {

        Collection<VideoFrame> emptyFrames = (Collection<VideoFrame>) videoArchive.getEmptyVideoFrames();
        int n = 0;
        boolean doFetch = emptyFrames.size() > 0;
        for (vars.annotation.VideoFrame videoFrame : emptyFrames) {
            videoArchive.removeVideoFrame(videoFrame);
            n++;
        }

        /*
         * Delete in a single transaction if possible
         */
        if (doFetch) {
            try {
                for (VideoFrame videoFrame : emptyFrames) {
                    remove(videoFrame);
                }
                log.debug("Deleted " + n + " empty VideoFrames from " + videoArchive);
            } finally {
                videoArchive = findByPrimaryKey(videoArchive.getClass(), ((JPAEntity) videoArchive).getId());
            }
        }
        return videoArchive;

    }

    @Override
    public VideoArchive findByPrimaryKey(Object primaryKey) {
        return findByPrimaryKey(VideoArchiveImpl.class, primaryKey);
    }
}
