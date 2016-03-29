package vars.avplayer.jfx;


import java.util.Optional;

/**
 * At a minimum we need a movieLocation to open a videoarchive. If no match is found in the database, then we will also
 * need the other params of platformName, sequenceNumber and (maybe) TimeSource
 * Created by brian on 1/13/14.
 * @deprecated Helper class until the new method of locating movie refs is worked out
 */
public class VideoParams {

    private final String movieLocation;
    private final String platformName;
    private final Integer sequenceNumber;


    public VideoParams(String movieLocation, String platformName, Integer sequenceNumber) {
        this.movieLocation = movieLocation;
        this.platformName = platformName;
        this.sequenceNumber = sequenceNumber;
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

}