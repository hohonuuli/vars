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
        checkEnabled();
        AnnotationProcessor.process(this);
    }

    @EventSubscriber(eventClass = ObservationsAddedEvent.class)
    public void respondTo(ObservationsAddedEvent event) {
        checkEnabled();
    }

    @EventSubscriber(eventClass = ObservationsRemovedEvent.class)
    public void respondTo(ObservationsRemovedEvent event) {
        checkEnabled();
    }

    @EventSubscriber(eventClass = VideoArchiveSelectedEvent.class)
    public void respondTo(VideoArchiveSelectedEvent event) {
//        VideoArchive videoArchive = event.get();
//        boolean enabled = videoArchive.getVideoFrames().size() > 2;
//        setEnabled(enabled);
        checkEnabled();
    }

    private void checkEnabled() {
        JXObservationTable myTable = (JXObservationTable) StateLookup.getObservationTable();
        boolean enabled = myTable.getRowCount() > 2;
        setEnabled(enabled);
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
