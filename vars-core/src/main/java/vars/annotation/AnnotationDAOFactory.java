package vars.annotation;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 11, 2009
 * Time: 9:25:37 AM
 * To change this template use File | Settings | File Templates.
 */
public interface AnnotationDAOFactory {

    IAssociationDAO newAssociationDAO();
    ICameraDataDAO newCameraDataDAO();
    ICameraDeploymentDAO newCameraDeploymentDAO();
    IObservationDAO newObservationDAO();
    IPhysicalDataDAO newPhysicalDataDAO();
    IVideoArchiveDAO newVideoArchiveDAO();
    IVideoArchiveSetDAO newVideoArchiveSetDAO();

}
