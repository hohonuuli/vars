/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui;

import javax.swing.ImageIcon;
import org.mbari.swing.JFancyButton;
import vars.annotation.ui.actions.ClearDatabaseCacheAction;

/**
 *
 * @author brian
 */
public class RefreshButton extends JFancyButton {

    public RefreshButton(final ToolBelt toolBelt) {
        addActionListener(new ClearDatabaseCacheAction(toolBelt));
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/16px/refresh.png")));
        setText("");
        setToolTipText("Refresh Database");
    }

}
