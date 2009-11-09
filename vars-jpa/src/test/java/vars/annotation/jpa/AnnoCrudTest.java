package vars.annotation.jpa;

import vars.testing.AnnotationTestObjectFactory;
import vars.jpa.VarsJpaTestModule;
import vars.jpa.EntityUtilities;
import vars.jpa.JPAEntity;
import com.google.inject.Injector;
import com.google.inject.Guice;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.DAO;
import vars.annotation.*;


/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 11, 2009
 * Time: 11:06:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class AnnoCrudTest {

    public final Logger log = LoggerFactory.getLogger(getClass());
    AnnotationFactory annotationFactory;
    AnnotationTestObjectFactory testObjectFactory;
    AnnotationDAOFactory daoFactory;

    @Before
    public void setup() {
        Injector injector = Guice.createInjector(new VarsJpaTestModule());
        annotationFactory = injector.getInstance(AnnotationFactory.class);
        testObjectFactory = new AnnotationTestObjectFactory(annotationFactory);
        daoFactory = injector.getInstance(AnnotationDAOFactory.class);
    }

    @Test
    public void bigTest() {

        log.info("---------- TEST: bigTest ----------");
        VideoArchiveSet vas = testObjectFactory.makeObjectGraph("BIG-TEST", 2);
        VideoArchiveSetDAO dao = daoFactory.newVideoArchiveSetDAO();
        EntityUtilities eu = new EntityUtilities();
        log.info("ANNOTATION TREE BEFORE TEST:\n" + eu.buildTextTree(vas));
        dao.persist(vas);
        Long vasKey = ((JPAEntity) vas).getId();
        Assert.assertNotNull("Primary Key [ID] was not set!", vasKey);
        vas = dao.findByPrimaryKey(vas.getClass(), ((JPAEntity) vas).getId());
        log.info("ANNOTATION TREE AFTER INSERT:\n" + eu.buildTextTree(vas));
        dao.remove(vas);
        log.info("KNOWLEDGEBASE TREE AFTER DELETE:\n" + eu.buildTextTree(vas));
        vas = dao.findByPrimaryKey(vas.getClass(), vasKey);
        Assert.assertNull("Whoops!! We can still lookup the entity after deleteing it", vas);
        
    }

    @Test
    public void bottomUpDelete() {

        log.info("---------- TEST: bottomUpDelete ----------");
        VideoArchiveSet vas = testObjectFactory.makeObjectGraph("BIG-TEST", 2);
        DAO dao = daoFactory.newVideoArchiveSetDAO();
        dao.persist(vas);

        // Gather all the objects
        Collection<VideoArchive> videoArchives = new ArrayList<VideoArchive>(vas.getVideoArchives());

        Collection<VideoFrame> videoFrames = new ArrayList<VideoFrame>(vas.getVideoFrames());

        Collection<Observation> observations = new ArrayList<Observation>();
        for (VideoFrame videoFrame : videoFrames) {
            observations.addAll(videoFrame.getObservations());
        }

        Collection<Association> associations = new ArrayList<Association>();
        for (Observation observation : observations) {
            associations.addAll(observation.getAssociations());
        }

        // Start deleting
        for (Association association : associations) {
            association.getObservation().removeAssociation(association);
            dao.remove(association);
        }

        EntityUtilities eu = new EntityUtilities();
        log.info("KNOWLEDGEBASE TREE AFTER ASSOCIATION DELETE:\n" + eu.buildTextTree(vas));

        for (Observation observation : observations) {
            observation.getVideoFrame().removeObservation(observation);
            dao.remove(observation);

        }
        log.info("KNOWLEDGEBASE TREE AFTER OBSERVATION DELETE:\n" + eu.buildTextTree(vas));

        for (VideoFrame videoFrame : videoFrames) {
            videoFrame.getVideoArchive().removeVideoFrame(videoFrame);
            dao.remove(videoFrame);

        }
        log.info("KNOWLEDGEBASE TREE AFTER VIDEOFRAME DELETE:\n" + eu.buildTextTree(vas));

        for (VideoArchive videoArchive : videoArchives) {
            videoArchive.getVideoArchiveSet().removeVideoArchive(videoArchive);
            dao.remove(videoArchive);

        }
        log.info("KNOWLEDGEBASE TREE AFTER VIDEOARCHIVE DELETE:\n" + eu.buildTextTree(vas));

        vas = dao.findByPrimaryKey(vas.getClass(), ((JPAEntity) vas).getId());
        log.info("KNOWLEDGEBASE TREE AFTER DATABASE LOOKUP:\n" + eu.buildTextTree(vas));

        for(CameraDeployment cameraDeployment : vas.getCameraDeployments()) {
            cameraDeployment.getVideoArchiveSet().removeCameraDeployment(cameraDeployment);
            dao.remove(cameraDeployment);
            break;
        }
        vas = dao.findByPrimaryKey(vas.getClass(), ((JPAEntity) vas).getId());
        log.info("KNOWLEDGEBASE TREE AFTER A SINGLE CAMERADEPLOYMENT DELETE:\n" + eu.buildTextTree(vas));

        dao.remove(vas);


    }

    
}
