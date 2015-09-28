package vars.queryfx.ui;

import com.guigarage.sdk.util.Icon;

public enum AppIcons implements Icon {

    PLUS("\uf067"),
    GEARS("\uF085"),
    SEARCH("\uF002"),
    SEARCH_PLUS("\uF00E"),
    TRASH("\uF014"),
    PLAY("\uF04B");


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
