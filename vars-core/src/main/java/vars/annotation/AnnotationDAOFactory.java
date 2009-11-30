package vars.annotation;

import javax.persistence.EntityManager;
import vars.DAO;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 11, 2009
 * Time: 9:25:37 AM
 * To change this template use File | Settings | File Templates.
 */
public interface AnnotationDAOFactory {

    DAO newDAO();
    AssociationDAO newAssociationDAO();
    CameraDataDAO newCameraDataDAO();
    CameraDeploymentDAO newCameraDeploymentDAO();
    ObservationDAO newObservationDAO();
    PhysicalDataDAO newPhysicalDataDAO();
    VideoFrameDAO newVideoFrameDAO();
    VideoArchiveDAO newVideoArchiveDAO();
    VideoArchiveSetDAO newVideoArchiveSetDAO();

    DAO newDAO(EntityManager entityManager);
    AssociationDAO newAssociationDAO(EntityManager entityManager);
    CameraDataDAO newCameraDataDAO(EntityManager entityManager);
    CameraDeploymentDAO newCameraDeploymentDAO(EntityManager entityManager);
    ObservationDAO newObservationDAO(EntityManager entityManager);
    PhysicalDataDAO newPhysicalDataDAO(EntityManager entityManager);
    VideoFrameDAO newVideoFrameDAO(EntityManager entityManager);
    VideoArchiveDAO newVideoArchiveDAO(EntityManager entityManager);
    VideoArchiveSetDAO newVideoArchiveSetDAO(EntityManager entityManager);

}
