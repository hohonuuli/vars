package vars.avplayer.jfx;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Created by brian on 4/28/14.
 */
public class JFXMovieApp {

    private static final String MEDIA_URL =
            "http://download.oracle.com/otndocs/products/javafx/oow2010-2.flv";

    JFrame frame;
    JPanel panel;
    JFXPanel mediaPanel;
    MediaView mediaView;
    JFXPanel controlPanel;

    public static void main(String[] args) {
        JFXMovieApp app = new JFXMovieApp();
        JFrame frame = app.getFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public JFXMovieApp() {
        initialize();
    }

    protected void initialize() {
        JFrame myFrame = getFrame();
        Platform.runLater(() -> {
            Media media = new Media(MEDIA_URL);
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setAutoPlay(true);
            getMediaView().setMediaPlayer(mediaPlayer);
            mediaPlayer.setOnReady(() -> {
                myFrame.setSize(media.getWidth(), media.getHeight());
            });
        });

        myFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Platform.runLater(() -> {
                    getMediaView().setFitWidth(myFrame.getWidth());
                    getMediaView().setFitHeight(myFrame.getHeight());
                });
            }
        });

    }

    public JFrame getFrame() {
        if (frame == null) {
            frame = new JFrame();
            frame.add(getPanel());
        }
        return frame;
    }

    public JPanel getPanel() {
        if (panel == null) {
            panel = new JPanel(new BorderLayout());
            panel.add(getMediaPanel(), BorderLayout.CENTER);
            panel.add(getControlPanel(), BorderLayout.SOUTH);
            Platform.runLater(this::createScenes);
        }
        return panel;
    }

    public JFXPanel getMediaPanel() {
        if (mediaPanel == null) {
            mediaPanel = new JFXPanel();
        }
        return mediaPanel;
    }

    public MediaView getMediaView() {
        if (mediaView == null) {
            mediaView = new MediaView();
        }
        return mediaView;
    }

    public void createScenes() {
        getMediaPanel().setScene(new Scene(new Pane(getMediaView())));
    }

    public JFXPanel getControlPanel() {
        if (controlPanel == null) {
            controlPanel = new JFXPanel();
        }
        return controlPanel;
    }
}
