package vars.annotation;

import vars.DAO;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 2:31:10 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CameraDeploymentDAO extends DAO {

    List<CameraDeployment> findAllByChiefScientistName(String name);

    List<CameraDeployment> findAllWithoutChiefScientistName();
    
}
