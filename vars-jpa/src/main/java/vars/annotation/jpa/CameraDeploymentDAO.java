package vars.annotation.jpa;

import vars.annotation.ICameraDeploymentDAO;
import vars.annotation.ICameraDeployment;
import vars.jpa.DAO;
import org.mbari.jpax.EAO;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import com.google.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 3:59:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class CameraDeploymentDAO extends DAO implements ICameraDeploymentDAO {

    @Inject
    public CameraDeploymentDAO(EAO eao) {
        super(eao);
    }

    public List<ICameraDeployment> findAllByChiefScientistName(String name) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("chiefScientistName", name);
        return getEAO().findByNamedQuery("CameraDeployment.findByChiefScientistName", params);
    }

}
