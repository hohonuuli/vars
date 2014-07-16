package vars.annotation.ui.buttons;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.swing.JFancyButton;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.eventbus.ObservationsAddedEvent;
import vars.annotation.ui.eventbus.ObservationsRemovedEvent;
import vars.annotation.ui.eventbus.VideoArchiveChangedEvent;
import vars.annotation.ui.eventbus.VideoArchiveSelectedEvent;
import vars.annotation.ui.table.JXObservationTable;
import vars.annotation.ui.table.ObservationTableModel;

import javax.swing.*;
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
                JXObservationTable myTable = (JXObservationTable) Lookup.getObservationTableDispatcher().getValueObject();
                ObservationTableModel model = (ObservationTableModel) myTable.getModel();
                int n = model.getRowCount();
                if (n > 1) {
                    int i = myTable.getSelectedRow();
                    Random random = new Random();
                    int r = i;
                    while (i == r) {
                        r = random.nextInt(n);
                    }
                    Observation obs = myTable.getObservationAt(r);
                    myTable.setSelectedObservation(obs);
                }

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
        JXObservationTable myTable = (JXObservationTable) Lookup.getObservationTableDispatcher().getValueObject();
        boolean enabled = myTable.getRowCount() > 2;
        setEnabled(enabled);
    }
}
