package vars.annotation.ui.videofile.jfxmedia;

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
import vars.annotation.VideoArchive;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.videofile.VideoPlayerController;
import vars.annotation.ui.videofile.VideoPlayerDialogUI;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

/**
 * Created by brian on 12/19/13.
 */
public class JFXOpenVideoArchiveDialog extends JDialog implements VideoPlayerDialogUI {

    private JFXPanel panel;
    private JFXOpenVideoArchiveDialogController controller;
    private final Logger log = LoggerFactory.getLogger(getClass());
    // not used locally. But reference need to pass to controller which is created later.
    private final ToolBelt toolBelt;


    public JFXOpenVideoArchiveDialog(Window parent, final ToolBelt toolBelt) {
        super(parent);
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

    public JFXOpenVideoArchiveDialogController getController() {
        return controller; // TODO this is not thread safe
    }

    @Override
    public void onOkay(Runnable fn) {
        getController().setOnOKFunction(fn);
    }

    @Override
    public Tuple2<VideoArchive, VideoPlayerController> openVideoArchive() {
        return null;
    }
}
