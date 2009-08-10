package vars.annotation.jpa;

import vars.jpa.DAO;
import vars.annotation.ICameraDataDAO;
import vars.annotation.ICameraData;
import org.mbari.jpax.EAO;

import java.util.Set;

import com.google.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 3:56:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class CameraDataDAO extends DAO implements ICameraDataDAO {

    @Inject
    public CameraDataDAO(EAO eao) {
        super(eao);
    }

    public Set<ICameraData> findByFrameGrabURLContaining(String s) {
        return null;  // TODO implement this method.
    }

    public Set<ICameraData> findByFrameGrabURLPostfix(String postfix) {
        return null;  // TODO implement this method.
    }

    public Set<ICameraData> findByFrameGrabURLPrefix(String prefix) {
        return null;  // TODO implement this method.
    }
}
