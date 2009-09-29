package vars.annotation;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 4, 2009
 * Time: 11:37:54 AM
 * To change this template use File | Settings | File Templates.
 */
public enum CameraDirections {

    ASCEND("ascend"), CRUISE("cruise"), DESCEND("descend"),
    ENDTRANSECT("endtransect"), LAUNCH("launch"), STARTTRANSECT("starttransect"),
    STATIONARY("stationary"), TRANSECT("transect"), UNSPECIFIED("unspecified");

    private final String direction;

    CameraDirections(String direction) {
        this.direction = direction;
    }

    public String getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return direction;
    }
}
