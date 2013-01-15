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

    CameraData findByImageReference(String imageReference);

    List<CameraData> findByImageReferenceContaining(String s);

    List<CameraData> findByImageReferencePostfix(String postfix);

    List<CameraData> findByImageReferencePrefix(String prefix);

}
