package vars.knowledgebase;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 5, 2009
 * Time: 2:29:50 PM
 * To change this template use File | Settings | File Templates.
 */
public enum MediaTypes {

    ICON("Icon"), IMAGE("Image"), VIDEO("Video");

    private String type;

    MediaTypes(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }


}
