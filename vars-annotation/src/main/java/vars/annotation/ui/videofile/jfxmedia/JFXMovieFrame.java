package vars.annotation.ui.videofile.jfxmedia;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.ui.Lookup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.net.URL;

/**
 * Created by brian on 12/16/13. Based on an example from
 * http://docs.oracle.com/javafx/2/swing/swing-fx-interoperability.htm
 */
public class JFXMovieFrame extends JFrame {

    private JFXPanel panel;
    private JFXMovieFrameController controller;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public JFXMovieFrame() throws HeadlessException {
        setLayout(new BorderLayout());
        add(getPanel());
        setSize(480, 320);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
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

    public void setMovieLocation(final String location) {
        Platform.runLater(() -> {
            try {
                controller.setMediaLocation(location);
            }
            catch (MediaException e) {
                EventBus.publish(Lookup.TOPIC_WARNING, "Unable to open the file at " + location + "using the Built-in (JavaFX) player");
            }
        });
    }

    private void initFX(JFXPanel panel) throws IOException {
        URL controllerLocation = getClass().getResource("/fxml/JFXMovieFrame.fxml");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(controllerLocation);
        loader.setBuilderFactory(new JavaFXBuilderFactory());

        Parent root = (Parent) loader.load(controllerLocation.openStream());
        controller = loader.getController();
        controller.setFrame(this);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/JFXMovieFrame.css");
        panel.setScene(scene);

    }

    public JFXMovieFrameController getController() {
        if (controller == null) {
            getPanel(); // initFX;
        }
        return controller; // TODO this is not thread safe
    }

}
