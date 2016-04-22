package vars.annotation.ui;


import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.mbari.swing.SwingUtils;

import vars.annotation.VideoArchive;
import vars.annotation.ui.eventbus.VideoArchiveChangedEvent;
import vars.annotation.ui.eventbus.VideoArchiveSelectedEvent;
import vars.avplayer.VideoPlayer;
import vars.avplayer.VideoPlayerDialogUI;
import vars.avplayer.VideoPlayers;
import vars.avplayer.noop.NoopVideoPlayer;
import vars.shared.rx.RXEventBus;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Optional;

/**
 * DEVELOPER NOTE: Based off of StatusLabelForVideoArchive. Refer to that class if things aren't working.
 * Created by brian on 1/6/14.
 */
public class StatusContainerForVideoPlayer extends JPanel {

    private static final String NO_FILE = "Video: None Selected";
    private JComboBox<String> videoPlayerComboBox;
    private StatusLabel statusLabel;
    private List<VideoPlayer> videoPlayers;
    private final ToolBelt toolBelt;
    private final RXEventBus eventBus;

    public StatusContainerForVideoPlayer(final ToolBelt toolBelt, RXEventBus eventBus) {
        super();
        this.toolBelt = toolBelt;
        this.eventBus = eventBus;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        videoPlayers = VideoPlayers.get();
        statusLabel = new StatusLabelForVideoPlayer(toolBelt);

        add(new JLabel("Video Device:"));
        add(Box.createHorizontalStrut(20));
        add(statusLabel);
        add(Box.createHorizontalStrut(20));
        add(new JLabel("Video Player:"));
        add(getVideoPlayerComboBox());

        VideoArchive videoArchive = StateLookup.getVideoArchive();
        update(videoArchive);

        AnnotationProcessor.process(this); // Register with EventBus
    }

    private JComboBox<String> getVideoPlayerComboBox() {
        if (videoPlayerComboBox == null) {
            videoPlayerComboBox = new JComboBox<>();
            for (VideoPlayer v : videoPlayers) {
                videoPlayerComboBox.addItem(v.getName());
            }
            Dimension d = videoPlayerComboBox.getPreferredSize();
            videoPlayerComboBox.setPreferredSize(new Dimension(200, d.height));
        }
        return videoPlayerComboBox;
    }




    /**
     * EventBus Listener method
     * @param event
     */
    @EventSubscriber(eventClass = VideoArchiveChangedEvent.class)
    public void respondTo(VideoArchiveChangedEvent event) {
        update(event.get());
    }

    @EventSubscriber(eventClass = VideoArchiveSelectedEvent.class)
    public void respondTo(VideoArchiveSelectedEvent event) {
        update(event.get());
    }

    /**
     * Sets the videoArchive registered with the label. In general, you don't need
     * to call this. This status label registers with
     * PredefinedDispatcher.VIDEOARCHIVE and listens for when the videoArchive
     * is set there.
     *
     * @param videoArchive Sets the videoArchive to be registered with the label
     */
    public void update(final VideoArchive videoArchive) {
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

        statusLabel.setText("Video: " + text);
        setToolTipText(toolTip);
        statusLabel.setOk(ok);
    }

    VideoPlayer getSelectedVideoPlayer() {
        JComboBox<String> cb = getVideoPlayerComboBox();
        String name = cb.getItemAt(cb.getSelectedIndex());
        Optional<VideoPlayer> videoPlayer = videoPlayers.stream()
                .filter(v -> v.getName().equals(name))
                .findFirst();
        return videoPlayer.orElse(new NoopVideoPlayer());
    }


    class StatusLabelForVideoPlayer extends StatusLabel {

        StatusLabelForVideoPlayer(final ToolBelt toolBelt) {
            super();

            addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(final MouseEvent me) {
                    SwingUtils.flashJComponent(StatusLabelForVideoPlayer.this, 2);

                    final Point mousePosition = me.getPoint();

                    SwingUtilities.convertPointToScreen(mousePosition, StatusLabelForVideoPlayer.this);

                    // Get the correct AccessUI. This provides a dialog to open a VideoArchive and a VideoPlayerController
                    // for a selected VideoPlayerOld module
                    final VideoPlayer videoPlayer = getSelectedVideoPlayer();
                    final VideoPlayerDialogUI dialog = videoPlayer.getConnectionDialog(toolBelt, eventBus);

                    int x = mousePosition.x;
                    if (x < 1) {
                        x = 1;
                    }
                    int y = mousePosition.y - dialog.getHeight();
                    if (y < 1) {
                        y = 1;
                    }

                    dialog.setLocation(x, y);
                    dialog.setVisible(true);

                }

            });
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            StatusContainerForVideoPlayer.this.update((VideoArchive) evt.getNewValue());
        }
    }


}


