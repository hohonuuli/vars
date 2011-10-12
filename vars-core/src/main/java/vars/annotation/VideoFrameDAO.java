package vars.annotation;

import java.util.Set;
import vars.DAO;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 2:57:21 PM
 * To change this template use File | Settings | File Templates.
 */
public interface VideoFrameDAO extends DAO {

    Set<VideoFrame> findAllByVideoArchivePrimaryKey(Object primaryKey);

    VideoFrame findByPrimaryKey(Object primaryKey);

}
