package vars.annotation.ui;

import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoState;
import vars.*;
import vars.avplayer.VideoPlayer;
import vars.avplayer.VideoPlayerDialogUI;
import vars.avplayer.VideoPlayers;
import vars.shared.rx.RXEventBus;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

/**
 * @author Brian Schlining
 * @since 2016-04-07T11:36:00
 */
public class VideoPlayersPanel extends JPanel {

    private JComboBox<VideoPlayer> videoPlayerComboBox;
    private JButton connectButton;
    private final ToolBelt toolBelt;
    private final RXEventBus eventBus;

    public VideoPlayersPanel(ToolBelt toolBelt, RXEventBus eventBus) {
        this.toolBelt = toolBelt;
        this.eventBus = eventBus;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        initialize();
    }

    protected void initialize() {
        add(getVideoPlayerComboBox());
        add(Box.createHorizontalStrut(10));
        add(getConnectButton());
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

    private JButton getConnectButton() {
        if (connectButton == null) {
            connectButton = new JButton("Open Video");
            connectButton.addActionListener(e -> {
                final VideoPlayerDialogUI dialog = getSelectedVideoPlayer().getConnectionDialog(toolBelt, eventBus);
                final Point locationOnScreen = connectButton.getLocationOnScreen();
                dialog.setLocation(locationOnScreen.x, locationOnScreen.y);
                dialog.setVisible(true);
            });
        }
        return connectButton;
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
}
