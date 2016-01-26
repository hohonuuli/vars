package vars.avplayer.novideo;

/**
 * @author Brian Schlining
 * @since 2016-01-25T12:00:00
 */
import com.google.common.base.Preconditions;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.mbari.util.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.ToolBelt;
import vars.annotation.VideoArchive;
import vars.avplayer.VideoParams;
import vars.avplayer.VideoPlayerController;
import vars.avplayer.VideoPlayerDialogUI;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class NVOpenVideoArchiveDialog extends JDialog implements VideoPlayerDialogUI {


    private JFXPanel panel;
    private NVOpenVideoArchiveDialogController controller;
    private final Logger log = LoggerFactory.getLogger(getClass());
    // not used locally. But reference need to pass to controller which is created later.
    private final ToolBelt toolBelt;


    public NVOpenVideoArchiveDialog(Dialog owner, ToolBelt toolBelt) {
        super(owner);
        Preconditions.checkArgument(toolBelt != null, "ToolBelt can not be null");
        this.toolBelt = toolBelt;
        setContentPane(getPanel());
        pack();
    }

    public JFXPanel getPanel() {
        if (panel == null) {
            panel = new JFXPanel();
            Platform.runLater(() -> {
                try {
                    initFX(panel);
                }
                catch (IOException e) {
                    log.error("Failed to initialize JavaFX scene", e);
                }
            });
        }
        return panel;
    }

    private void initFX(JFXPanel panel) throws IOException {
        URL controllerLocation = getClass().getResource("/fxml/JFXOpenVideoArchiveDialog.fxml");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(controllerLocation);
        loader.setBuilderFactory(new JavaFXBuilderFactory());

        Parent root = (Parent) loader.load(controllerLocation.openStream());
        controller = loader.getController();
        controller.setDialog(this);
        controller.setToolBelt(toolBelt);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/JFXOpenVideoArchiveDialog.css");
        panel.setScene(scene);
    }

    @Override
    public boolean getSupportTimeSource() {
        return false;
    }

    @Override
    public void onOkay(Runnable fn) {

    }

    @Override
    public Tuple2<VideoArchive, VideoPlayerController> openVideoArchive() {
        return null;
    }

    @Override
    public VideoParams getVideoParams() {
        return null;
    }

    @Override
    public void setSupportTimeSource(boolean s) {

    }
}
