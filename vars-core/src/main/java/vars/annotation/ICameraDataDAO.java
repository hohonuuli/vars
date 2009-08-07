package vars.annotation;

import vars.IDAO;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 2:28:27 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ICameraDataDAO extends IDAO {

    Set<ICameraData> findByFrameGrabURLContaining(String s);

    Set<ICameraData> findByFrameGrabURLPostfix(String postfix);

    Set<ICameraData> findByFrameGrabURLPrefix(String prefix);

}
