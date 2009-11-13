/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mbari.vars.annotation.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;
import javax.swing.JButton;

/**
 *
 * @author brian
 */
public class ConceptButtonDropPanelWithHighlights extends ConceptButtonDropPanel {
    
    JButton lastButtonPressed;
    Color defaultBackgroundColor;
    Color defaultForegroundColor;
    Color highlightBackgroundColor = Color.LIGHT_GRAY;
    Color highLightForegroundColor = Color.RED.darker();
    
    final ActionListener actionListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            
            // Make sure we have the colors
            if (defaultBackgroundColor == null || defaultForegroundColor == null) {
                resolveDefaultColors();
            }
            
            // Reset the old button to it's original colors
            if (lastButtonPressed != null) {
                lastButtonPressed.setBackground(defaultBackgroundColor);
                lastButtonPressed.setForeground(defaultForegroundColor);
            }
            
            // Swap the colors on the new button to make it stand out.
            lastButtonPressed = (JButton) e.getSource();
            lastButtonPressed.setForeground(highLightForegroundColor);
            lastButtonPressed.setBackground(highlightBackgroundColor);
        }
    };
    
    public ConceptButtonDropPanelWithHighlights(final Preferences tabPreferences) {
        super(tabPreferences);
        checkForActionListener();
    }
    
    /**
     * We override the parents method so that we can stick in our own 
     * DragTransferListener
     * @return
     */
    @Override
    DropTargetListener getDTListener() {
        if (dtListener == null) {
            dtListener = new DTListener2();
        }
        return dtListener;
    }
    
    private void resolveDefaultColors() {
        Component[] components = getComponents();
        for (Component component : components) {
            if (component instanceof JButton) {
                final JButton button = (JButton) component;
                defaultBackgroundColor = button.getBackground();
                defaultForegroundColor = button.getForeground();
                break;
            }
        }
    }
    
    private void checkForActionListener() {
        Component[] components = getComponents();
        for (Component component : components) {
            if (component instanceof JButton) {
                final JButton button = (JButton) component;
                
                // Check the button for the presence of our custom actionlistener
                final ActionListener[] registeredListeners = button.getActionListeners();
                boolean foundIt = false;
                for (ActionListener listener : registeredListeners) {
                    foundIt = listener == actionListener;
                    if (foundIt) {
                        break;
                    }
                }
                
                // Add the actionlistener if it wasn't found
                if (!foundIt) {
                    button.addActionListener(actionListener);
                }
            }
        }

    }

    
    /**
     * Listens for button drops and adds the ActionListener to new buttons
     */
    class DTListener2 extends ConceptButtonDropPanel.DTListener {

        @Override
        public void drop(DropTargetDropEvent e) {
            super.drop(e);
            checkForActionListener();
        }
        
    }
}
