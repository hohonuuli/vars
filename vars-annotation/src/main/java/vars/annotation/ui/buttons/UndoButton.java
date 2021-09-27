package vars.annotation.ui.buttons;

import org.bushe.swing.event.EventBus;
import mbarix4j.swing.JFancyButton;
import vars.annotation.ui.commandqueue.RedoEvent;
import vars.annotation.ui.commandqueue.UndoEvent;

import javax.swing.ImageIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Brian Schlining
 * @since 2011-10-11
 */
public class UndoButton extends JFancyButton {
    public UndoButton() {
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/24px/undo.png")));
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventBus.publish(new UndoEvent());
            }
        });
        setToolTipText("Undo");
    }
}
