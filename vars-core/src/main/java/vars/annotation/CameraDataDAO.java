package vars.annotation;

import vars.DAO;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 2:28:27 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CameraDataDAO extends DAO {

    List<CameraData> findByFrameGrabURLContaining(String s);

    List<CameraData> findByFrameGrabURLPostfix(String postfix);

    List<CameraData> findByFrameGrabURLPrefix(String prefix);

}
