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
import java.io.IOException;
import java.net.URL;

/**
 * Created by brian on 12/19/13.
 */
public class JFXOpenVideoArchiveDialog extends JDialog {

    private JFXPanel panel;
    private JFXOpenVideoArchiveDialogController controller;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public JFXOpenVideoArchiveDialog() {
        setContentPane(panel);
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

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/JFXOpenVideoArchiveDialog.css");
    }

    public JFXOpenVideoArchiveDialogController getController() {
        if (controller == null) {
            getPanel(); // initFX
        }
        return controller; // TODO this is not thread safe
    }
}
