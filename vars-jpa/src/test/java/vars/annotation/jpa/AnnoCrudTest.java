package vars.annotation.jpa;

import vars.testing.AnnotationTestObjectFactory;
import vars.annotation.AnnotationFactory;
import vars.annotation.IVideoArchiveSet;
import vars.annotation.IVideoArchiveSetDAO;
import vars.annotation.AnnotationDAOFactory;
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
import org.mbari.jpax.EAO;
import org.mbari.jpax.NonManagedEAO;
import org.mbari.jpax.NonManagedEAOImpl;
import org.mbari.util.IObservable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.IDAO;
import vars.annotation.IAssociation;
import vars.annotation.ICameraDeployment;
import vars.annotation.IObservation;
import vars.annotation.IVideoArchive;
import vars.annotation.IVideoFrame;


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
    NonManagedEAO eao;

    @Before
    public void setup() {
        Injector injector = Guice.createInjector(new VarsJpaTestModule());
        annotationFactory = injector.getInstance(AnnotationFactory.class);
        testObjectFactory = new AnnotationTestObjectFactory(annotationFactory);
        daoFactory = injector.getInstance(AnnotationDAOFactory.class);
        eao = new NonManagedEAOImpl("test");
    }

    @Test
    public void bigTest() {

        IVideoArchiveSet vas = testObjectFactory.makeObjectGraph("BIG-TEST", 2);
        IVideoArchiveSetDAO dao = daoFactory.newVideoArchiveSetDAO();
        EntityUtilities eu = new EntityUtilities(eao);
        log.info("ANNOTATION TREE BEFORE TEST:\n" + eu.buildTextTree(vas));
        vas = dao.makePersistent(vas);
        Long vasKey = ((JPAEntity) vas).getId();
        Assert.assertNotNull("Primary Key [ID] was not set!", vasKey);
        vas = dao.findByPrimaryKey(vas.getClass(), ((VideoArchiveSet) vas).getId());
        log.info("ANNOTATION TREE AFTER INSERT:\n" + eu.buildTextTree(vas));
        vas = dao.makeTransient(vas);
        log.info("KNOWLEDGEBASE TREE AFTER DELETE:\n" + eu.buildTextTree(vas));
        vas = dao.findByPrimaryKey(vas.getClass(), vasKey);
        Assert.assertNull("Whoops!! We can still lookup the entity after deleteing it", vas);
        
    }

    @Test
    public void bottomUpDelete() {

        IVideoArchiveSet vas = testObjectFactory.makeObjectGraph("BIG-TEST", 2);
        IDAO dao = daoFactory.newVideoArchiveSetDAO();
        vas = dao.makePersistent(vas);

        // Gather all the objects
        Collection<IVideoArchive> videoArchives = new ArrayList<IVideoArchive>(vas.getVideoArchives());

        Collection<IVideoFrame> videoFrames = new ArrayList<IVideoFrame>(vas.getVideoFrames());

        Collection<IObservation> observations = new ArrayList<IObservation>();
        for (IVideoFrame videoFrame : videoFrames) {
            observations.addAll(videoFrame.getObservations());
        }

        Collection<IAssociation> associations = new ArrayList<IAssociation>();
        for (IObservation observation : observations) {
            associations.addAll(observation.getAssociations());
        }

        // Start deleting
        for (IAssociation association : associations) {
            association.getObservation().removeAssociation(association);
            association = dao.makeTransient(association);
        }

        EntityUtilities eu = new EntityUtilities(eao);
        log.info("KNOWLEDGEBASE TREE AFTER ASSOCIATION DELETE:\n" + eu.buildTextTree(vas));

        for (IObservation observation : observations) {
            observation.getVideoFrame().removeObservation(observation);
            observation = dao.makeTransient(observation);

        }
        log.info("KNOWLEDGEBASE TREE AFTER OBSERVATION DELETE:\n" + eu.buildTextTree(vas));

        for (IVideoFrame videoFrame : videoFrames) {
            videoFrame.getVideoArchive().removeVideoFrame(videoFrame);
            videoFrame = dao.makeTransient(videoFrame);

        }
        log.info("KNOWLEDGEBASE TREE AFTER VIDEOFRAME DELETE:\n" + eu.buildTextTree(vas));

        for (IVideoArchive videoArchive : videoArchives) {
            videoArchive.getVideoArchiveSet().removeVideoArchive(videoArchive);
            videoArchive = dao.makeTransient(videoArchive);

        }
        log.info("KNOWLEDGEBASE TREE AFTER VIDEOARCHIVE DELETE:\n" + eu.buildTextTree(vas));

        vas = dao.findByPrimaryKey(vas.getClass(), ((JPAEntity) vas).getId());
        log.info("KNOWLEDGEBASE TREE AFTER DATABASE LOOKUP:\n" + eu.buildTextTree(vas));

        for(ICameraDeployment cameraDeployment : vas.getCameraDeployments()) {
            cameraDeployment.getVideoArchiveSet().removeCameraDeployment(cameraDeployment);
            cameraDeployment = dao.makeTransient(cameraDeployment);
            break;
        }
        vas = dao.findByPrimaryKey(vas.getClass(), ((JPAEntity) vas).getId());
        log.info("KNOWLEDGEBASE TREE AFTER A SINGLE CAMERADEPLOYMENT DELETE:\n" + eu.buildTextTree(vas));

        dao.makeTransient(vas);


    }

    
}
