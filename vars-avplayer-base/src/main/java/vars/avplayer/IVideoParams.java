package vars.avplayer;

/**
 * This interface provides support for legacy tape/videoarchive naming.
 * @author Brian Schlining
 * @since 2016-04-06T10:05:00
 */
public interface IVideoParams {

    String getPlatformName();
    Integer getSequenceNumber();
    Integer getTapeNumber();
    Boolean isHD();

    default String getVideoArchiveName() {
        return getPlatformName().trim().substring(0, 1).toUpperCase() +
                String.format("%04d-%02d", getSequenceNumber(), getTapeNumber()) +
                (isHD() ? "HD" : "");
    }
}
