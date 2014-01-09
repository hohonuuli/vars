package vars.annotation.ui.videofile;

import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.videofile.jfxmedia.JFXAccessUI;
import vars.annotation.ui.videofile.quicktime.QTAccessUI;

import java.awt.*;

/**
 * This enumeration provides access to
 * Created by brian on 1/6/14.
 */
public enum VideoPlayers {

    // TODO implement VideoPlayerAccessUI's
    BUILTIN("Built-in", new JFXAccessUI()),
    QT4J("QuickTime", new QTAccessUI()),
    APPLE("Mac OS X", new EmptyVideoPlayerAccessUI());

    private final String name;
    private final VideoPlayerAccessUI accessUI;

    VideoPlayers(String name, VideoPlayerAccessUI accessUI) {
        this.name = name;
        this.accessUI = accessUI;
    }

    public String getName() {
        return name;
    }

    public VideoPlayerAccessUI getAccessUI() {
        return accessUI;
    }
}


class EmptyVideoPlayerAccessUI implements VideoPlayerAccessUI {
    @Override
    public VideoPlayerDialogUI getOpenDialog(Window parent, ToolBelt toolBelt) {
        return null;
    }
}
