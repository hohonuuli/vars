package vars.annotation.ui;


import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.mbari.swing.SwingUtils;
import org.mbari.util.Dispatcher;
import org.mbari.util.Tuple2;
import org.mbari.vcr4j.timer.StatusMonitor;
import vars.annotation.VideoArchive;
import vars.annotation.ui.eventbus.VideoArchiveChangedEvent;
import vars.annotation.ui.eventbus.VideoArchiveSelectedEvent;
import vars.annotation.ui.videofile.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.Optional;

/**
 * DEVELOPER NOTE: Based off of StatusLabelForVideoArchive. Refer to that class if things aren't working.
 * Created by brian on 1/6/14.
 */
public class StatusContainerForVideoPlayer extends JPanel {

    private static final String NO_FILE = "Video: None Selected";
    private final StatusMonitor statusMonitor = new StatusMonitor();
    private JComboBox<String> videoPlayerComboBox;
    private StatusLabel statusLabel;
    private VideoPlayers videoPlayers;

    public StatusContainerForVideoPlayer(final ToolBelt toolBelt) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        videoPlayers = new VideoPlayers(toolBelt.getAnnotationDAOFactory());
        statusLabel = new StatusLabelForVideoPlayer(toolBelt);

        add(statusLabel);
        add(Box.createHorizontalStrut(20));
        add(new JLabel("Video Player:"));
        add(getVideoPlayerComboBox());

        final Dispatcher videoArchiveDispatcher = Lookup.getVideoArchiveDispatcher();
        update((VideoArchive) videoArchiveDispatcher.getValueObject());

        AnnotationProcessor.process(this); // Register with EventBus
    }

    private JComboBox<String> getVideoPlayerComboBox() {
        if (videoPlayerComboBox == null) {
            videoPlayerComboBox = new JComboBox<>();
            for (VideoPlayer v : videoPlayers.get()) {
                videoPlayerComboBox.addItem(v.getName());
            }
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
        VideoPlayer videoPlayer = null;
        for (VideoPlayer v: videoPlayers.get()) {
            if (v.getName().equals(name)) {
                videoPlayer = v;
                break;
            }
        }
        return videoPlayer;
    }


    class StatusLabelForVideoPlayer extends StatusLabel {

        StatusLabelForVideoPlayer(final ToolBelt toolBelt) {
            super();

            addMouseListener(new MouseAdapter() {

                Frame frame = (Frame) Lookup.getApplicationFrameDispatcher().getValueObject();


                @Override
                public void mouseClicked(final MouseEvent me) {
                    SwingUtils.flashJComponent(StatusLabelForVideoPlayer.this, 2);

                    final Point mousePosition = me.getPoint();

                    SwingUtilities.convertPointToScreen(mousePosition, StatusLabelForVideoPlayer.this);

                    // Get the correct AccessUI. This provides a dialog to open a VideoArchive and a VideoPlayerController
                    // for a selected VideoPlayer module
                    final VideoPlayer videoPlayer = getSelectedVideoPlayer();
                    final VideoPlayerAccessUI accessUI = videoPlayer.getAccessUI();
                    final VideoPlayerDialogUI dialog = accessUI.getOpenDialog(frame, toolBelt);

                    dialog.onOkay(() -> {
                        dialog.setVisible(false);
                        final VideoParams videoParams = dialog.getVideoParams();
                        Tuple2<VideoArchive, VideoPlayerController> t = accessUI.openMoviePlayer(videoParams);
                        VideoArchive videoArchive = t.getA();
                        VideoPlayerController videoPlayerController = t.getB();
                        VideoArchiveChangedEvent event = new VideoArchiveChangedEvent(this, videoArchive);
                        Lookup.getImageCaptureServiceDispatcher().setValueObject(videoPlayerController.getImageCaptureService());
                        Lookup.getVideoControlServiceDispatcher().setValueObject(videoPlayerController.getVideoControlService());
                        EventBus.publish(event);
                    });

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


