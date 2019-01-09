package vars.annotation.ui.buttons;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.swing.JFancyButton;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.VideoFrame;
import vars.annotation.ui.StateLookup;
import vars.annotation.ui.eventbus.ObservationsAddedEvent;
import vars.annotation.ui.eventbus.ObservationsRemovedEvent;
import vars.annotation.ui.eventbus.VideoArchiveSelectedEvent;
import vars.annotation.ui.table.JXObservationTable;

import javax.swing.*;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by brian on 7/15/14.
 */
public class JumpToRandomVideoFrameButton extends JFancyButton {

    public JumpToRandomVideoFrameButton() {
        super();
        setAction(new ActionAdapter() {
            @Override
            public void doAction() {
                jump();
            }
        });
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/random_button.png")));
        setText("");
        setToolTipText("Jump to random video frame");
        StateLookup.videoArchiveProperty().addListener((obs, oldv, newv) -> setEnabled(checkEnabled()));
        setEnabled(checkEnabled());
    }

    private boolean checkEnabled() {
        VideoArchive videoArchive = StateLookup.getVideoArchive();
        return videoArchive != null;
    }

    private void jump() {
        VideoArchive videoArchive = StateLookup.getVideoArchive();
        if (videoArchive != null) {
            List<Observation> jumpableObservations = new ArrayList<Observation>();
            for (VideoFrame vf : videoArchive.getVideoFrames()) {
                Collection<Observation> observations = vf.getObservations();
                if (observations.size() == 1) {
                    jumpableObservations.addAll(observations);
                }
            }
            if (jumpableObservations.size() > 0) {
                Random random = new Random();
                int r = random.nextInt(jumpableObservations.size());
                Observation selectedObservation = jumpableObservations.get(r);
                JXObservationTable myTable = (JXObservationTable) StateLookup.getObservationTable();
                myTable.setSelectedObservation(selectedObservation);
            }

        }
    }
}
