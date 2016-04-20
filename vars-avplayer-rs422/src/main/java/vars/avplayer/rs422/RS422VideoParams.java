package vars.avplayer.rs422;

import vars.avplayer.IVideoParams;

/**
 * @author Brian Schlining
 * @since 2016-04-05T12:36:00
 */
public class RS422VideoParams implements IVideoParams {

    private final String serialPortName;
    private final String platformName;
    private final Integer sequenceNumber;
    private final Integer tapeNumber;
    private final boolean isHD;


    public RS422VideoParams(String serialPortName, String platformName, Integer sequenceNumber, Integer tapeNumber, boolean isHD) {
        this.serialPortName = serialPortName;
        this.platformName = platformName;
        this.sequenceNumber = sequenceNumber;
        this.tapeNumber = tapeNumber;
        this.isHD = isHD;
    }


    public RS422VideoParams(String platformName, Integer sequenceNumber, Integer tapeNumber, boolean isHD) {
        this.serialPortName = null;
        this.platformName = platformName;
        this.sequenceNumber = sequenceNumber;
        this.tapeNumber = tapeNumber;
        this.isHD = isHD;
    }


    public String getSerialPortName() {
        return serialPortName;
    }

    public Boolean isHD() {
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



}
