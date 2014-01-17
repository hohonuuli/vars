package vars.annotation.ui.videofile;

import org.mbari.vcr.qt.TimeSource;

import java.util.Optional;

/**
 * At a minimum we need a movieLocation to open a videoarchive. If no match is found in the database, then we will also
 * need the other params of platformName, sequenceNumber and (maybe) TimeSource
 * Created by brian on 1/13/14.
 */
public class VideoParams {
    private final String movieLocation;
    private final Optional<String> platformNameOpt;
    private final Optional<Integer> sequenceNumberOpt;
    private final Optional<TimeSource> timeSourceOpt;

    public VideoParams(String movieLocation, Optional<String> platformNameOpt,
                       Optional<Integer> sequenceNumberOpt, Optional<TimeSource> timeSourceOpt) {
        this.movieLocation = movieLocation;
        this.platformNameOpt = platformNameOpt;
        this.sequenceNumberOpt = sequenceNumberOpt;
        this.timeSourceOpt = timeSourceOpt;
    }

    public VideoParams(String movieLocation, String platformName, Integer sequenceNumber, TimeSource timeSource) {
        this(movieLocation, Optional.ofNullable(platformName), Optional.ofNullable(sequenceNumber),
                Optional.ofNullable(timeSource));
    }

    public String getMovieLocation() {
        return movieLocation;
    }

    public Optional<String> getPlatformNameOpt() {
        return platformNameOpt;
    }

    public Optional<Integer> getSequenceNumberOpt() {
        return sequenceNumberOpt;
    }

    public Optional<TimeSource> getTimeSourceOpt() {
        return timeSourceOpt;
    }
}
