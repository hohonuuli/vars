package vars.annotation;

import java.util.Set;
import vars.IDAO;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 2:57:21 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IVideoFrameDAO extends IDAO {

    Set<IVideoFrame> findAllByVideoArchivePrimaryKey(Object primaryKey);

}
