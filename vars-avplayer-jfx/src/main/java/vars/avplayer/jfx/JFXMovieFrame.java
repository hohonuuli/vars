package vars.avplayer.jfx;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.MediaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.avplayer.EventBus;
import vars.shared.rx.messages.WarningMsg;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

/**
 * Created by brian on 12/16/13. Based on an example from
 * http://docs.oracle.com/javafx/2/swing/swing-fx-interoperability.htm
 */
public class JFXMovieFrame extends JFrame {

    private JFXPanel panel;
    private volatile JFXMovieFrameController controller;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Runnable onReady;

    @Inject
    public JFXMovieFrame(final String location, Runnable onReady) throws HeadlessException {
        this.onReady = onReady;
        setLayout(new BorderLayout());
        add(initPanel(location));
        setSize(480, 320);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    }

    public JFXPanel getPanel() {
        return panel;
    }

    private JFXPanel initPanel(final String location) {
        if (panel == null) {
            panel = new JFXPanel();
            Platform.runLater(() -> {
                try {
                    URL controllerLocation = getClass().getResource("/fxml/JFXMovieFrame.fxml");
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(controllerLocation);
                    loader.setBuilderFactory(new JavaFXBuilderFactory());

                    Parent root = loader.load(controllerLocation.openStream());
                    controller = loader.getController();
                    controller.setFrame(this);

                    Scene scene = new Scene(root);
                    scene.getStylesheets().add("/styles/JFXMovieFrame.css");
                    panel.setScene(scene);
                    controller.setMediaLocation(location);
                    controller.readyProperty().addListener((obs, oldValue, newValue) -> {
                        if (newValue) {
                            System.out.println("READY = " + newValue);
                            onReady.run();
                        }
                    });
                }
                catch (IOException e) {
                    log.error("Failed to initialize JavaFX scene", e);
                }
            });
        }
        return panel;
    }


    public JFXMovieFrameController getController() {
        return controller;
    }

    public boolean isReady() {
        return panel != null && controller != null && controller.isReady();
    }


    @Override
    public void dispose() {
        if (controller != null) {
            controller.dispose();
        }
        super.dispose();
    }
}
