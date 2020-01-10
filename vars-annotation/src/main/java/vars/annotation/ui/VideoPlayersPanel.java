package vars.annotation.ui;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.mbari.swing.SwingUtils;
import org.mbari.util.Tuple2;
import vars.annotation.VideoArchive;
import vars.avplayer.VideoController;
import vars.avplayer.VideoPlayer;
import vars.avplayer.VideoPlayerDialogUI;
import vars.avplayer.VideoPlayers;
import vars.shared.rx.RXEventBus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.Vector;

/**
 * @author Brian Schlining
 * @since 2016-04-07T11:36:00
 */
public class VideoPlayersPanel extends JPanel {

    private JComboBox<VideoPlayer> videoPlayerComboBox;
    private final ToolBelt toolBelt;
    private final RXEventBus eventBus;
    private VideoLabel videoLabel;

    public VideoPlayersPanel(ToolBelt toolBelt, RXEventBus eventBus) {
        this.toolBelt = toolBelt;
        this.eventBus = eventBus;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        initialize();
    }

    protected void initialize() {
        add(new JLabel("Video Device:"));
        add(Box.createHorizontalStrut(10));
        add(getVideoPlayerComboBox());
        add(Box.createHorizontalStrut(10));
        add(getVideoLabel());
        StateLookup.videoControllerProperty().addListener((obs, oldVal, newVal) -> {
            getVideoLabel().updateLabel(StateLookup.getVideoArchive(), newVal);
        });

        StateLookup.videoArchiveProperty().addListener((obs, oldVal, newVal) -> {
            getVideoLabel().updateLabel(newVal, StateLookup.getVideoController());
        });
    }

    private JComboBox<VideoPlayer> getVideoPlayerComboBox() {
        if (videoPlayerComboBox == null) {
            Vector<VideoPlayer> videoPlayers = new Vector<>(VideoPlayers.get());
            videoPlayerComboBox = new JComboBox<>(videoPlayers);
            videoPlayerComboBox.setRenderer(new VideoPlayerRenderer());
            Dimension d = videoPlayerComboBox.getPreferredSize();
            videoPlayerComboBox.setPreferredSize(new Dimension(150, d.height));
        }
        return videoPlayerComboBox;
    }

    public VideoPlayer getSelectedVideoPlayer() {
        return (VideoPlayer) getVideoPlayerComboBox().getSelectedItem();
    }


    private VideoLabel getVideoLabel() {
        if (videoLabel == null) {
            videoLabel = new VideoLabel();
            Dimension d = videoLabel.getPreferredSize();
            videoLabel.setPreferredSize(new Dimension(400, d.height));
        }
        return videoLabel;
    }



    class VideoPlayerRenderer extends JLabel implements ListCellRenderer<VideoPlayer> {

        @Override
        public Component getListCellRendererComponent(JList<? extends VideoPlayer> list,
                VideoPlayer value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {


            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            setText(value.getName());

            return this;
        }
    }

    class VideoLabel extends StatusLabel {


        public VideoLabel() {
            /*
         * When the user clicks this label a dialog should pop up allowing them
         * to open the VCR.
         */
            addMouseListener(new MouseAdapter() {

                //Frame frame = StateLookup.getAnnotationFrame();

                @Override
                public void mouseClicked(final MouseEvent me) {
                    SwingUtils.flashJComponent(VideoLabel.this, 2);

                    final VideoPlayerDialogUI dialog = getSelectedVideoPlayer().getConnectionDialog(toolBelt, eventBus);
                    final Point locationOnScreen = VideoLabel.this.getLocationOnScreen();
                    dialog.setLocation(locationOnScreen.x, locationOnScreen.y);
                    dialog.setVisible(true);

                }


            });

            AnnotationProcessor.process(this); // Register with EventBus
            updateLabel(StateLookup.getVideoArchive(), StateLookup.getVideoController());
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            updateLabel((VideoArchive) evt.getNewValue(), StateLookup.getVideoController());
        }

        /**
         * Builds the label string indicating the videoarchve & videocontroller
         * @param videoArchive
         * @param videoController
         * @return A tuple of (tooltip, labelText)
         */
        public Tuple2<String, String> buildLabel(final VideoArchive videoArchive, final VideoController videoController) {

            String videoPart = videoArchive == null ? "NONE" : videoArchive.getName();
            String toolTip = videoPart;
            if ((videoPart.length() > 20) &&
                    (videoPart.toLowerCase().startsWith("http:") || videoPart.toLowerCase().startsWith("file:"))) {
                String[] parts = videoPart.split("/");
                if (parts.length > 0) {
                    videoPart = ".../" + parts[parts.length - 1];
                }
            }

            String connectionID = videoController != null ? " @ " + videoController.getConnectionID() : "";
            String text = videoPart + connectionID;

            text = "Video: " + text;
            toolTip = toolTip + connectionID; // Long form without "Video"
            return new Tuple2<>(toolTip, text);
        }

        public void updateLabel(final VideoArchive videoArchive, final VideoController videoController) {
            boolean ok = videoArchive != null;
            Tuple2<String, String> txt = buildLabel(videoArchive, videoController);
            String text = txt.getB();
            String toolTip = txt.getA();
            setText(text);
            setToolTipText(toolTip);
            setOk(ok);
        }

    }
}
