package vars.annotation;

import vars.IDAO;

import java.util.Set;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 2:28:27 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ICameraDataDAO extends IDAO {

    List<ICameraData> findByFrameGrabURLContaining(String s);

    List<ICameraData> findByFrameGrabURLPostfix(String postfix);

    List<ICameraData> findByFrameGrabURLPrefix(String prefix);

}
