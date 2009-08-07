package vars.annotation.jpa;

import vars.jpa.DAO;
import vars.annotation.IVideoFrameDAO;
import vars.annotation.IVideoFrame;
import org.mbari.jpax.IEAO;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:42:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class VideoFrameDAO extends DAO implements IVideoFrameDAO {

    public VideoFrameDAO(IEAO eao) {
        super(eao);
    }

    public Set<IVideoFrame> findAllByVideoArchivePrimaryKey(Object primaryKey) {
        return null;  // TODO implement this method.
    }
}
