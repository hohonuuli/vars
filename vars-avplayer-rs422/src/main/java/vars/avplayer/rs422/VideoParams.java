package vars.avplayer.rs422;

import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2016-04-05T12:36:00
 */
public class VideoParams {

    private final String serialPortName;
    private final String platformName;
    private final Integer sequenceNumber;
    private final Integer tapeNumber;
    private final boolean isHD;


    public VideoParams(String serialPortName, String platformName, Integer sequenceNumber, Integer tapeNumber, boolean isHD) {
        this.serialPortName = serialPortName;
        this.platformName = platformName;
        this.sequenceNumber = sequenceNumber;
        this.tapeNumber = tapeNumber;
        this.isHD = isHD;
    }


    public VideoParams(String platformName, Integer sequenceNumber, Integer tapeNumber, boolean isHD) {
        this.serialPortName = null;
        this.platformName = platformName;
        this.sequenceNumber = sequenceNumber;
        this.tapeNumber = tapeNumber;
        this.isHD = isHD;
    }


    public String getSerialPortName() {
        return serialPortName;
    }

    public boolean isHD() {
        return isHD;
    }

    public String getPlatformName() {
        return platformName;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public Integer getTapeNumber() {
        return tapeNumber;
    }

    public String getVideoArchiveName() {
        return platformName.substring(0, 0).toUpperCase() +
                String.format("%04d-%02d", sequenceNumber, tapeNumber) +
                (isHD ? "HD" : "");
    }


}
