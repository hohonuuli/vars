package vars.knowledgebase;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 5, 2009
 * Time: 2:29:50 PM
 * To change this template use File | Settings | File Templates.
 */
public enum MediaTypes {

    ICON("Icon"), IMAGE("Image"), VIDEO("Video"), UNDEFINED("Undefined");

    private String type;

    MediaTypes(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static MediaTypes getType(String type) {
        MediaTypes mediaType = UNDEFINED;
        for (MediaTypes t : values()) {
            if (t.getType().equalsIgnoreCase(type)) {
                mediaType = t;
                break;
            }
        }
        return mediaType;
    }

    @Override
    public String toString() {
        return type;
    }


}
