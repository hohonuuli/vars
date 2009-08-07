package vars.annotation.rules;

import vars.IPersistenceRule;
import vars.annotation.IVideoArchive;
import vars.annotation.IVideoFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * A rule the removes empty videoFrames (i.e. it contains no observations) from your VideoArchive
 */
public class NoEmptyVideoFramesRule implements IPersistenceRule<IVideoArchive> {

    public final Logger log = LoggerFactory.getLogger(getClass());

    public IVideoArchive apply(IVideoArchive object) {

        Collection<? extends IVideoFrame> videoFrames = object.getEmptyVideoFrames();

        if (videoFrames.size() > 0) {
            log.warn("Removing " + videoFrames.size() + " empty videoframes from " + object);
        }

        for (IVideoFrame vf : videoFrames) {
            object.removeVideoFrame(vf);
        }

        return object;
    }
}
