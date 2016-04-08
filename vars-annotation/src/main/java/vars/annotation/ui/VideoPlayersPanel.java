package vars.annotation.ui;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.mbari.swing.SwingUtils;
import vars.annotation.VideoArchive;
import vars.annotation.ui.eventbus.VideoArchiveChangedEvent;
import vars.annotation.ui.eventbus.VideoArchiveSelectedEvent;
import vars.avplayer.VideoPlayer;
import vars.avplayer.VideoPlayerDialogUI;
import vars.avplayer.VideoPlayers;
import vars.shared.rx.RXEventBus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.Vector;

/**
 * @author Brian Schlining
 * @since 2016-04-07T11:36:00
 */
public class VideoPlayersPanel extends JPanel {

    private JComboBox<VideoPlayer> videoPlayerComboBox;
    private final ToolBelt toolBelt;
    private final RXEventBus eventBus;
    private StatusLabel videoLabel;

    public VideoPlayersPanel(ToolBelt toolBelt, RXEventBus eventBus) {
        this.toolBelt = toolBelt;
        this.eventBus = eventBus;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        initialize();
    }

    protected void initialize() {
        add(getVideoPlayerComboBox());
        add(Box.createHorizontalStrut(10));
        add(getVideoLabel());
    }

    private JComboBox<VideoPlayer> getVideoPlayerComboBox() {
        if (videoPlayerComboBox == null) {
            Vector<VideoPlayer> videoPlayers = new Vector<>(VideoPlayers.get());
            videoPlayerComboBox = new JComboBox<>(videoPlayers);
            videoPlayerComboBox.setRenderer(new VideoPlayerRenderer());
        }
        return videoPlayerComboBox;
    }

    public VideoPlayer getSelectedVideoPlayer() {
        return (VideoPlayer) getVideoPlayerComboBox().getSelectedItem();
    }


    private StatusLabel getVideoLabel() {
        if (videoLabel == null) {
            videoLabel = new VideoLabel();
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
            setVideoArchive(StateLookup.getVideoArchive());
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            setVideoArchive((VideoArchive) evt.getNewValue());
        }

        public void setVideoArchive(final VideoArchive videoArchive) {
            boolean ok = false;
            String text = "NONE";
            String toolTip = text;
            if (videoArchive != null) {
                text = videoArchive.getName() + "";
                toolTip = text;

                if ((text.length() > 20) &&
                        (text.toLowerCase().startsWith("http:") || text.toLowerCase().startsWith("file:"))) {
                    String[] parts = text.split("/");
                    if (parts.length > 0) {
                        text = ".../" + parts[parts.length - 1];
                    }

                }

                ok = true;
            }

            setText("Video: " + text);
            setToolTipText(toolTip);
            setOk(ok);
        }

        @EventSubscriber(eventClass = VideoArchiveChangedEvent.class)
        public void respondTo(VideoArchiveChangedEvent event) {
            setVideoArchive(event.get());
        }

        @EventSubscriber(eventClass = VideoArchiveSelectedEvent.class)
        public void respondTo(VideoArchiveSelectedEvent event) {
            setVideoArchive(event.get());
        }

    }
}
