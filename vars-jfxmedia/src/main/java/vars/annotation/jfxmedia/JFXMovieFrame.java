package vars.annotation.jfxmedia;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

/**
 * Created by brian on 12/16/13.
 */
public class JFXMovieFrame extends JFrame {

    private JFXPanel panel;
    private JFXMovieFrameController controller;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public JFXMovieFrame() throws HeadlessException {
        setLayout(new BorderLayout());
        add(getPanel());
        setSize(480, 320);
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
        Platform.runLater(() -> controller.setMediaLocation(location));
    }

    private void initFX(JFXPanel panel) throws IOException {
        URL controllerLocation = getClass().getResource("/fxml/Scene.fxml");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(controllerLocation);
        loader.setBuilderFactory(new JavaFXBuilderFactory());

        Parent root = (Parent) loader.load(controllerLocation.openStream());
        controller = loader.getController();

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/JFXMovieFrame.css");
        panel.setScene(scene);
    }

    public JFXMovieFrameController getController() {
        if (controller == null) {
            getPanel(); // initFX;
        }
        return controller;
    }

}
