package vars.annotation.jpa;

import vars.DAO;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
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

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 11, 2009
 * Time: 9:30:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class AnnotationDAOFactoryImpl implements AnnotationDAOFactory {

    private final EntityManagerFactory entityManagerFactory;
    private final AnnotationFactory annotationFactory;
    private final KnowledgebaseDAOFactory kbFactory;

    @Inject
    public AnnotationDAOFactoryImpl(@Named("annotationPersistenceUnit") String persistenceUnit,
            AnnotationFactory annotationFactory,
            KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
        this.entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnit);
        this.annotationFactory = annotationFactory;
        this.kbFactory = knowledgebaseDAOFactory;
    }

    public AssociationDAO newAssociationDAO() {
        return new AssociationDAOImpl(entityManagerFactory.createEntityManager(), kbFactory.newConceptDAO());
    }

    public CameraDataDAO newCameraDataDAO() {
        return new CameraDataDAOImpl(entityManagerFactory.createEntityManager());
    }

    public CameraDeploymentDAO newCameraDeploymentDAO() {
        return new CameraDeploymentDAOImpl(entityManagerFactory.createEntityManager());
    }

    public ObservationDAO newObservationDAO() {
        return new ObservationDAOImpl(entityManagerFactory.createEntityManager(), kbFactory.newConceptDAO());
    }

    public PhysicalDataDAO newPhysicalDataDAO() {
        return new PhysicalDataDAOImpl(entityManagerFactory.createEntityManager());
    }

    public VideoArchiveDAO newVideoArchiveDAO() {
        return new VideoArchiveDAOImpl(entityManagerFactory.createEntityManager(), annotationFactory);
    }

    public VideoArchiveSetDAO newVideoArchiveSetDAO() {
        return new VideoArchiveSetDAOImpl(entityManagerFactory.createEntityManager(), newVideoArchiveDAO());
    }

    public VideoFrameDAO newVideoFrameDAO() {
        return new VideoFrameDAOImpl(entityManagerFactory.createEntityManager());
    }

    public DAO newDAO() {
        return new vars.jpa.DAO(entityManagerFactory.createEntityManager());
    }

}
