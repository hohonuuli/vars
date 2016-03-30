package vars.annotation.ui.buttons;

import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.UserAccount;
import vars.annotation.Observation;
import vars.annotation.ui.StateLookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.commandqueue.CommandEvent;
import vars.annotation.ui.commandqueue.impl.RemoveImageReferencesCmd;
import vars.shared.ui.FancyButton;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Brian Schlining
 * @since Sep 21, 2010
 */
public class DeleteImageReferenceButton extends FancyButton {

    private Logger log = LoggerFactory.getLogger(getClass());
    private final ToolBelt toolBelt;

    public DeleteImageReferenceButton(ToolBelt toolBelt) {
        super();
        this.toolBelt = toolBelt;
        setToolTipText("Remove image-references from the selected observations");
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/rfgbutton_delete.png")));
        setEnabled(false);
        setText("");
        addActionListener(e -> deleteImageReference());

        StateLookup.selectedObservationsProperty().addListener((obsv, oldVal, newVal) -> {
            final UserAccount userAccount = StateLookup.getUserAccount();
            final Collection<Observation> observations = StateLookup.getSelectedObservations();
            boolean enabled = (userAccount != null) && (observations != null) && (observations.size() > 0);
            if (enabled) {
                for (Observation obs : observations) {
                    enabled = obs.getVideoFrame().getCameraData().getImageReference() != null;
                    if (enabled) {
                        break;
                    }
                }
            }
            setEnabled(enabled);
        });



    }

    private void deleteImageReference() {
        final Collection<Observation> observations = StateLookup.getSelectedObservations();
        final int count = observations.size();
        final Object[] options = { "OK", "CANCEL" };
        final int confirm = JOptionPane.showOptionDialog(StateLookup.getAnnotationFrame(),
                                "Do you want to remove the image reference from " + count + " observation(s)?", "VARS - Confirm Delete",
                                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        if (confirm == JOptionPane.NO_OPTION) {
            return;
        }

        log.debug("Setting image references to " + observations.size() + " observations to null");
        Command command = new RemoveImageReferencesCmd(new ArrayList<Observation>(observations));
        CommandEvent commandEvent = new CommandEvent(command);
        EventBus.publish(commandEvent);
    }
}
