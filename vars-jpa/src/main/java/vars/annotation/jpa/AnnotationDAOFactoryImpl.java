package vars.annotation.jpa;

import vars.DAO;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.AnnotationFactory;
import vars.annotation.AssociationDAO;
import vars.annotation.CameraDataDAO;
import vars.annotation.CameraDeploymentDAO;
import vars.annotation.ObservationDAO;
import vars.annotation.PhysicalDataDAO;
import vars.annotation.VideoArchiveDAO;
import vars.annotation.VideoArchiveSetDAO;
import vars.annotation.VideoFrameDAO;
import vars.jpa.EntityManagerFactoryAspect;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 11, 2009
 * Time: 9:30:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class AnnotationDAOFactoryImpl implements AnnotationDAOFactory, EntityManagerFactoryAspect {

    private final EntityManagerFactory entityManagerFactory;
    private final AnnotationFactory annotationFactory;
    private final KnowledgebaseDAOFactory kbFactory;

    @Inject
    public AnnotationDAOFactoryImpl(@Named("annotationPersistenceUnit") EntityManagerFactory entityManagerFactory,
            AnnotationFactory annotationFactory,
            KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
        this.entityManagerFactory = entityManagerFactory;
        this.annotationFactory = annotationFactory;
        this.kbFactory = knowledgebaseDAOFactory;
    }

    public AssociationDAO newAssociationDAO() {
        return new AssociationDAOImpl(entityManagerFactory.createEntityManager());
    }

    public CameraDataDAO newCameraDataDAO() {
        return new CameraDataDAOImpl(entityManagerFactory.createEntityManager());
    }

    public CameraDeploymentDAO newCameraDeploymentDAO() {
        return new CameraDeploymentDAOImpl(entityManagerFactory.createEntityManager());
    }

    public ObservationDAO newObservationDAO() {
        return new ObservationDAOImpl(entityManagerFactory.createEntityManager());
    }

    public PhysicalDataDAO newPhysicalDataDAO() {
        return new PhysicalDataDAOImpl(entityManagerFactory.createEntityManager());
    }

    public VideoArchiveDAO newVideoArchiveDAO() {
        return new VideoArchiveDAOImpl(entityManagerFactory.createEntityManager(), annotationFactory);
    }

    public VideoArchiveSetDAO newVideoArchiveSetDAO() {
        return new VideoArchiveSetDAOImpl(entityManagerFactory.createEntityManager(), annotationFactory);
    }

    public VideoFrameDAO newVideoFrameDAO() {
        return new VideoFrameDAOImpl(entityManagerFactory.createEntityManager());
    }

    public DAO newDAO() {
        return new vars.jpa.DAO(entityManagerFactory.createEntityManager());
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public AssociationDAO newAssociationDAO(EntityManager entityManager) {
        return new AssociationDAOImpl(entityManager);
    }

    public CameraDataDAO newCameraDataDAO(EntityManager entityManager) {
        return new CameraDataDAOImpl(entityManager);
    }

    public CameraDeploymentDAO newCameraDeploymentDAO(EntityManager entityManager) {
        return new CameraDeploymentDAOImpl(entityManager);
    }

    public ObservationDAO newObservationDAO(EntityManager entityManager) {
        return new ObservationDAOImpl(entityManager);
    }

    public PhysicalDataDAO newPhysicalDataDAO(EntityManager entityManager) {
        return new PhysicalDataDAOImpl(entityManager);
    }

    public VideoArchiveDAO newVideoArchiveDAO(EntityManager entityManager) {
        return new VideoArchiveDAOImpl(entityManager, annotationFactory);
    }

    public VideoArchiveSetDAO newVideoArchiveSetDAO(EntityManager entityManager) {
        return new VideoArchiveSetDAOImpl(entityManager, annotationFactory);
    }

    public VideoFrameDAO newVideoFrameDAO(EntityManager entityManager) {
        return new VideoFrameDAOImpl(entityManager);
    }

    public DAO newDAO(EntityManager entityManager) {
        return new vars.jpa.DAO(entityManager);
    }

}
