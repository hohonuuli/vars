package vars.queryfx.ui;

import com.guigarage.sdk.util.FontBasedIcon;

public enum AppIcons implements FontBasedIcon {

    CANCEL("\uF05E"),
    GEARS("\uF085"),
    HOME("\uF015"),
    PLAY("\uF04B"),
    PLUS("\uf067"),
    SEARCH("\uF002"),
    SEARCH_PLUS("\uF00E"),
    TRASH("\uF014");


    private String text;

    AppIcons(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return getText();
    }

}
