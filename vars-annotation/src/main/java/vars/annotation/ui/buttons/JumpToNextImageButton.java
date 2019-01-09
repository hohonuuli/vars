package vars.annotation.ui.buttons;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.swing.JFancyButton;
import org.mbari.vcr4j.time.Timecode;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.VideoFrame;
import vars.annotation.ui.StateLookup;
import vars.annotation.ui.eventbus.ObservationsAddedEvent;
import vars.annotation.ui.eventbus.ObservationsRemovedEvent;
import vars.annotation.ui.eventbus.VideoArchiveSelectedEvent;
import vars.annotation.ui.table.JXObservationTable;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2019-01-09T10:27:00
 */
public class JumpToNextImageButton extends JFancyButton {

    Comparator<Observation> comparator = (a, b) ->
        a.getVideoFrame().getTimecode().compareTo(b.getVideoFrame().getTimecode());


    public JumpToNextImageButton() {
        super();
        setAction(new ActionAdapter() {
            @Override
            public void doAction() {
                jump();
            }
        });
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/nav_right_blue.png")));
        setText("");
        setToolTipText("Jump to next video frame");
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
                    .orElse("00:00:00:00");

            List<Observation> jumpableObservations = new ArrayList<>();
            for (VideoFrame vf : videoArchive.getVideoFrames()) {
                if (vf.getTimecode().compareTo(lastSelectedTimecode) > 0) {
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
