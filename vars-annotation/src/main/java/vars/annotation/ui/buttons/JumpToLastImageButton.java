package vars.annotation.ui.buttons;

import mbarix4j.awt.event.ActionAdapter;
import mbarix4j.swing.JFancyButton;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.VideoFrame;
import vars.annotation.ui.StateLookup;
import vars.annotation.ui.table.JXObservationTable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Brian Schlining
 * @since 2019-01-09T10:27:00
 */
public class JumpToLastImageButton extends JFancyButton {

    Comparator<Observation> comparator = (a, b) ->
            b.getVideoFrame().getTimecode().compareTo(a.getVideoFrame().getTimecode());


    public JumpToLastImageButton() {
        super();
        setAction(new ActionAdapter() {
            @Override
            public void doAction() {
                jump();
            }
        });
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/nav_left_blue.png")));
        setText("");
        setToolTipText("Jump to proceeding video frame");
        StateLookup.videoArchiveProperty().addListener((obs, oldv, newv) -> setEnabled(checkEnabled()));
        setEnabled(checkEnabled());
    }

    private void jump() {
        VideoArchive videoArchive = StateLookup.getVideoArchive();

        if (videoArchive != null) {

            String lastSelectedTimecode = StateLookup.getSelectedObservations()
                    .stream()
                    .map(obs -> obs.getVideoFrame().getTimecode())
                    .distinct()
                    .sorted()
                    .reduce((a, b) -> b)
                    .orElse("99:99:99:99");

            List<Observation> jumpableObservations = new ArrayList<>();
            for (VideoFrame vf : videoArchive.getVideoFrames()) {
                if (vf.getTimecode().compareTo(lastSelectedTimecode) < 0) {
                    List<Observation> observations = vf.getObservations();
                    if (observations.size() > 0) {
                        jumpableObservations.add(observations.get(0));
                    }
                }
            }
            jumpableObservations.sort(comparator);

            if (jumpableObservations.size() > 0) {
                Observation selectedObservation = jumpableObservations.get(0);
                JXObservationTable myTable = (JXObservationTable) StateLookup.getObservationTable();
                myTable.setSelectedObservation(selectedObservation);
            }
        }
    }

    private boolean checkEnabled() {
        VideoArchive videoArchive = StateLookup.getVideoArchive();
        return videoArchive != null;
    }
}
