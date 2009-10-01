package vars.annotation.rules;

import vars.PersistenceRule;
import vars.annotation.VideoArchive;
import vars.annotation.VideoFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import vars.VARSPersistenceException;

/**
 * A rule the removes empty videoFrames (i.e. it contains no observations) from your VideoArchive
 */
public class NoEmptyVideoFramesRule implements PersistenceRule<VideoArchive> {

    public final Logger log = LoggerFactory.getLogger(getClass());

    public VideoArchive apply(VideoArchive object) {

        Collection<? extends VideoFrame> videoFrames = object.getEmptyVideoFrames();

        if (videoFrames.size() == 0) {
            throw new VARSPersistenceException(object + " does not contain any video frames. This is not allowed");
        }

        return object;
    }
}
