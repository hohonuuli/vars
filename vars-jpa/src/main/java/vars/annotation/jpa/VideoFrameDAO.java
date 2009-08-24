package vars.annotation.jpa;

import vars.jpa.DAO;
import vars.annotation.IVideoFrameDAO;
import vars.annotation.IVideoFrame;
import org.mbari.jpax.EAO;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;

import com.google.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:42:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class VideoFrameDAO extends DAO implements IVideoFrameDAO {

    @Inject
    public VideoFrameDAO(EAO eao) {
        super(eao);
    }

    public Set<IVideoFrame> findAllByVideoArchivePrimaryKey(Object primaryKey) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("primaryKey", primaryKey);
        List<IVideoFrame> list= getEAO().findByNamedQuery("VideoFrame.findByVideoArchivePrimaryKey", params);
        Set<IVideoFrame> set = new HashSet<IVideoFrame>();
        set.addAll(list);
        return set;
    }
}
