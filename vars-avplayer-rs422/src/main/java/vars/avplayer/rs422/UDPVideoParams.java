package vars.avplayer.rs422;

/**
 * @author Brian Schlining
 * @since 2016-04-06T10:00:00
 */
public class UDPVideoParams implements IVideoParams {

    private final String hostName;
    private final Integer port;
    private final String platformName;
    private final Integer sequenceNumber;
    private final Integer tapeNumber;
    private final boolean isHD;

    public UDPVideoParams(String hostName, Integer port, String platformName, Integer sequenceNumber, Integer tapeNumber, boolean isHD) {
        this.hostName = hostName;
        this.port = port;
        this.platformName = platformName;
        this.sequenceNumber = sequenceNumber;
        this.tapeNumber = tapeNumber;
        this.isHD = isHD;
    }

    public String getHostName() {
        return hostName;
    }

    public Boolean isHD() {
        return isHD;
    }

    public String getPlatformName() {
        return platformName;
    }

    public Integer getPort() {
        return port;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public Integer getTapeNumber() {
        return tapeNumber;
    }
}
