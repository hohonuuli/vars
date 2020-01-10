package vars.avplayer;

/**
 * @author Brian Schlining
 * @since 2016-04-20T11:37:00
 */
public class SimpleVideoParams implements IVideoParams {

    private String platformName;
    private Integer sequenceNumber;
    private Integer tapeNumber;
    private Boolean isHD;

    public SimpleVideoParams(String platformName, Integer sequenceNumber, Integer tapeNumber, Boolean isHD) {
        this.isHD = isHD;
        this.platformName = platformName;
        this.sequenceNumber = sequenceNumber;
        this.tapeNumber = tapeNumber;
    }


    public Boolean isHD() {
        return isHD;
    }

    @Override
    public String getPlatformName() {
        return platformName;
    }

    @Override
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    @Override
    public Integer getTapeNumber() {
        return tapeNumber;
    }
}
