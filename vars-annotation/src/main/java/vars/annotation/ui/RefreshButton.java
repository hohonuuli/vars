/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui;

import javax.swing.ImageIcon;
import vars.annotation.ui.actions.ClearDatabaseCacheAction;
import vars.shared.ui.FancyButton;

/**
 *
 * @author brian
 */
public class RefreshButton extends FancyButton {

    public RefreshButton(final ToolBelt toolBelt) {
        addActionListener(new ClearDatabaseCacheAction(toolBelt));
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/24px/refresh.png")));
        setText("");
        setToolTipText("Refresh Database");
    }

}
