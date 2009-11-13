/*
 * OpenVideoArchiveSetDialog3.java
 *
 * Created on March 19, 2007, 4:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.mbari.vars.annotation.locale;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import javax.swing.JButton;
import org.mbari.vars.annotation.ui.dialogs.*;
import org.mbari.vars.util.AppFrameDispatcher;

/**
 * Dialog for opening a VideoArchive.
 * @author brian
 */
public class OpenVideoArchiveSetDialog3 extends OkayCancelDialog {
    
    private VideoSourceSelectionPanel selectionPanel;
    
    /** Creates a new instance of OpenVideoArchiveSetDialog3 */
    public OpenVideoArchiveSetDialog3() {
        super(AppFrameDispatcher.getFrame());
        initialize();
    }
    
    void initialize() {
        getContentPane().add(getSelectionPanel(), BorderLayout.CENTER);
        JButton okButton = getOkayButton();
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okButtonAction();
            }
        });
        pack();
    }
    
    private VideoSourceSelectionPanel getSelectionPanel() {
        if (selectionPanel == null) {
            selectionPanel = new VideoSourceSelectionPanel();
            // Resize the frame when the videosource changes.
            selectionPanel.addContainerListener(new ContainerListener() {
                public void componentAdded(ContainerEvent e) {
                    pack();
                }
                public void componentRemoved(ContainerEvent e) {
                    // Do nothing
                }
            });
        }
        return selectionPanel;
    }
    
    
    private void okButtonAction() {
        getSelectionPanel().open();
    }
    
}
