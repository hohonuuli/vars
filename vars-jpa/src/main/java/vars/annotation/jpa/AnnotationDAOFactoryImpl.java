package vars.annotation.jpa;

import javax.persistence.EntityManagerFactory;

import vars.DAO;
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

import com.google.inject.Inject;
import com.google.inject.name.Named;

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

    @Inject
    public AnnotationDAOFactoryImpl(@Named("annotationPersistenceUnit") EntityManagerFactory entityManagerFactory,
            AnnotationFactory annotationFactory) {
        this.entityManagerFactory = entityManagerFactory;
        this.annotationFactory = annotationFactory;
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

}
