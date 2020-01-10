package vars.avplayer.jfx;


import org.mbari.vcr4j.time.Timecode;

import java.time.Duration;

/**
 * @author Brian Schlining
 * @since 2016-05-16T14:16:00
 */
public class JFXUtilities {

    public static double FAUX_FRAMERATE = 100D;

    private JFXUtilities() {
        // No instantiation allowed
    }

    public static Timecode jfxDurationToTimecode(javafx.util.Duration jfxDuration) {
        return new Timecode(jfxDuration.toMillis() / 10D, FAUX_FRAMERATE);
    }

    public static Timecode durationToTimecode(java.time.Duration duration) {
        return new Timecode(duration.toMillis() / 10D, FAUX_FRAMERATE);
    }

    public static java.time.Duration timecodeToDuration(Timecode timecode) {
        Timecode completeTimecode = timecode.isComplete() ? timecode :
                new Timecode(timecode.toString(), 100D);
        return Duration.ofMillis(Math.round(completeTimecode.getSeconds() * 1000L));
    }
}
