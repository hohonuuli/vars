package vars.avplayer.sharktopoda;


import java.util.Optional;

/**
 * At a minimum we need a movieLocation to open a videoarchive. If no match is found in the database, then we will also
 * need the other params of platformName, sequenceNumber and (maybe) TimeSource
 * Created by brian on 1/13/14.
 */
public class VideoParams {

    private final String movieLocation;
    private final String platformName;
    private final Integer sequenceNumber;
    private final int sharktopodaPort;
    private final int framecapturePort;


    public VideoParams(String movieLocation, String platformName, Integer sequenceNumber, int sharktopodaPort, int framecapturePort) {
        this.movieLocation = movieLocation;
        this.platformName = platformName;
        this.sequenceNumber = sequenceNumber;
        this.sharktopodaPort = sharktopodaPort;
        this.framecapturePort = framecapturePort;
    }

    public String getMovieLocation() {
        return movieLocation;
    }

    public Optional<String> getPlatformName() {
        return Optional.ofNullable(platformName);
    }

    public Optional<Integer> getSequenceNumber() {
        return Optional.ofNullable(sequenceNumber);
    }

    public int getSharktopodaPort() {
        return sharktopodaPort;
    }

    public int getFramecapturePort() {
        return framecapturePort;
    }
}
