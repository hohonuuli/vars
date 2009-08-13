package vars.annotation.jpa;

import vars.jpa.DAO;
import vars.jpa.JPAEntity;
import vars.annotation.IVideoArchiveDAO;
import vars.annotation.IVideoArchive;
import vars.annotation.IVideoFrame;
import vars.annotation.IObservation;
import vars.annotation.IAssociation;
import vars.annotation.AnnotationFactory;
import vars.annotation.IVideoArchiveSet;
import vars.annotation.ICameraDeployment;
import vars.knowledgebase.IConcept;
import vars.VARSPersistenceException;
import org.mbari.jpax.EAO;
import org.mbari.jpax.NonManagedEAO;

import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.google.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:41:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class VideoArchiveDAO extends DAO implements IVideoArchiveDAO {

    private final AnnotationFactory annotationFactory;

    @Inject
    public VideoArchiveDAO(EAO eao, AnnotationFactory annotationFactory) {
        super(eao);
        this.annotationFactory = annotationFactory;
    }

    public Set<String> findAllLinkValues(IVideoArchive videoArchive, String linkName) {

        return findAllLinkValues(videoArchive, linkName, null);
    }

    public Set<String> findAllLinkValues(IVideoArchive videoArchive, String linkName, IConcept concept) {

        // Due to lazy loading we want to iterate through all objects in a collection
        if (!getEAO().isManaged()) {
            ((NonManagedEAO) getEAO()).startTransaction();
        }

        videoArchive = findByPrimaryKey(VideoArchive.class, ((JPAEntity) videoArchive).getId()); // merge
        Collection<? extends IVideoFrame> videoFrames = videoArchive.getVideoArchiveSet().getVideoFrames();
        Set<String> linkValues = new HashSet<String>();
        for (IVideoFrame videoFrame : videoFrames) {
            for (IObservation observation : videoFrame.getObservations()) {
                if (concept == null || concept.getConceptName(observation.getConceptName()) != null) {
                    for (IAssociation association : observation.getAssociations()) {
                        if (linkName.equals(association.getLinkName())) {
                            linkValues.add(association.getLinkValue());
                        }
                    }
                }
            }
        }

        if (!getEAO().isManaged()) {
            ((NonManagedEAO) getEAO()).endTransaction();
        }

        return linkValues;
    }

    public IVideoArchive findOrCreateByParameters(String platform, int sequenceNumber, String videoArchiveName) {

        // ---- Step 1: Look up VideoArchive by Name
        IVideoArchive videoArchive = findByName(videoArchiveName);

        if (videoArchive == null) {
            // ---- Step 2: No match was found. See if the desired deployement exists
            IVideoArchiveSet videoArchiveSet = null;
            Map<String, Object> params2 = new HashMap<String, Object>();
            params2.put("platform", platform);
            params2.put("sequenceNumber", sequenceNumber);
            List<IVideoArchiveSet> vas = getEAO().findByNamedQuery("VideoArchiveSet.findByPlatformAndSequenceNumber", params2);
            if (vas.size() == 1) {
                videoArchiveSet = vas.get(0);
            }
            else if (vas.size() > 1) {
                throw new VARSPersistenceException("There's a problem!! More than one VideoArchvieSet " +
                        "with platform = " + platform + " and sequenceNumber = " + sequenceNumber +
                        " exists in the database");
            }
            else {
                videoArchiveSet = annotationFactory.newVideoArchiveSet();
                videoArchiveSet.setPlatformName(platform);
                ICameraDeployment cameraDeployment = annotationFactory.newCameraDeployment();
                cameraDeployment.setSequenceNumber(sequenceNumber);
                videoArchiveSet.addCameraDeployment(cameraDeployment);
                videoArchiveSet.addVideoArchive(videoArchive);
            }
        }

        return videoArchive;
    }

    public IVideoArchive findByName(final String name) {
        IVideoArchive videoArchive = null;
        Map<String, Object> params = new HashMap<String, Object>() {{ put("name", name); }};

        List<IVideoArchive> videoArchives = getEAO().findByNamedQuery("VideoArchive.findByName", params);
        if (videoArchives.size() == 1) {
            videoArchive = videoArchives.get(0);
        }
        else if (videoArchives.size() > 1) {
            throw new VARSPersistenceException("There's a problem!! More than one VideoArchive named " +
                    name + " exists in the database");
        }

        return videoArchive;
    }
}
