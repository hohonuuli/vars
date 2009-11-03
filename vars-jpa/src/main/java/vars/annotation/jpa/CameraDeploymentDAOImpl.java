package vars.annotation.jpa;

import vars.annotation.CameraDeploymentDAO;
import vars.annotation.CameraDeployment;
import vars.jpa.DAO;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import com.google.inject.Inject;
import javax.persistence.EntityManager;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 3:59:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class CameraDeploymentDAOImpl extends DAO implements CameraDeploymentDAO {

    @Inject
    public CameraDeploymentDAOImpl(EntityManager entityManager) {
        super(entityManager);
    }

    public List<CameraDeployment> findAllByChiefScientistName(String name) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("chiefScientistName", name);
        return findByNamedQuery("CameraDeployment.findByChiefScientistName", params);
    }

}
