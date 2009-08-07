package vars.annotation;

import vars.IDAO;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 2:31:10 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ICameraDeploymentDAO extends IDAO {

    List<ICameraDeployment> findAllByChiefScientistName(String name);
    
}
