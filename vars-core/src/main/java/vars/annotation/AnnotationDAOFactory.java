package vars.annotation;

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

}
