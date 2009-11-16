/*
 * Copyright 2005 MBARI
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.mbari.vars.annotation.ui;

import vars.shared.ui.StringTransferable;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import org.mbari.awt.layout.WrappingFlowLayout;
import org.mbari.swing.IPopup;
import org.mbari.swing.SwingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.ui.ToolBelt;

/**
 * <p>
 * This class is a panel that accepts drops from a drag and drop operation and
 * then creates buttons that have the names that match the string data of the
 * <code>ConceptButtonTransferable</code> class
 * </p>
 * <hr>
 *
 */
public class ConceptButtonDropPanel extends JPanel {


    private final Logger log = LoggerFactory.getLogger(getClass());
    protected Preferences tabPreferences = null;

    /**
     *     the actions supported by this drop target
     */
    private final int acceptableActions = DnDConstants.ACTION_COPY_OR_MOVE;

    private DropTarget dropTarget;

    DropTargetListener dtListener;

    private final ToolBelt toolbelt;


    /**
     * This is the constructor
     *
     * @param tabPreferences
     *            is the Preferences object that hold the configuration of the
     *            buttons on this ConceptButtonDropPanel
     */
    public ConceptButtonDropPanel(final Preferences tabPreferences, final ToolBelt toolbelt) {

        this.tabPreferences = tabPreferences;
        this.toolbelt = toolbelt;

        // Set the layout of the buttons to flow
        final FlowLayout cbFlow = new WrappingFlowLayout(FlowLayout.LEFT);
        this.setLayout(cbFlow);
        cbFlow.setHgap(5);


        // Create a new DropTarget that associated this panel, the acceptable
        // actions and the drop target listener
        dropTarget = new DropTarget(this, acceptableActions, getDTListener(), true);

        // Now load buttons in from preferences
        loadButtonsFromPrefs();
    }    // End constructor
    
    
    DropTargetListener getDTListener() {
        if (dtListener == null) {
            dtListener = new DTListener();
        }
        return dtListener;
    }

    /**
     * The location to insert a new button
     *
     * @param dropLocation
     *
     * @return
     */
    private int getInsertionIndex(final Point dropLocation) {
        int insertionIndex = this.getComponentCount();
        int closestComponentDistance = 10000;
        final Component[] components = this.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i].getBounds().contains(dropLocation)) {
                insertionIndex = i + 1;

                break;
            }

            final Rectangle componentBounds = components[i].getBounds();
            final int rightSideOfComponent = componentBounds.x + componentBounds.width;
            if ((dropLocation.y > componentBounds.y + componentBounds.height) || (dropLocation.y < componentBounds.y)) {
                continue;
            }

            final int distanceAway = dropLocation.x - rightSideOfComponent;
            if (Math.abs(distanceAway) < Math.abs(closestComponentDistance)) {
                closestComponentDistance = distanceAway;
                final int rightOrLeft = (distanceAway > 0) ? 1 : -1;
                insertionIndex = i + rightOrLeft;
                insertionIndex = (insertionIndex < 0) ? 0 : insertionIndex;
            }
        }

        return insertionIndex;
    }

    /**
     * This method loads the buttons from the tab preferences
     */
    private void loadButtonsFromPrefs() {

        // First grab all the button names from the tab prefs
        String[] buttonNames = null;
        try {
            buttonNames = tabPreferences.childrenNames();
        }
        catch (final BackingStoreException bse) {
            log.warn("Unable to load buttons from saved preferences.", bse);
        }

        // Now loop through and make add the buttons in their correct order
        final Preferences[] buttonPrefs = new Preferences[buttonNames.length];

        // System.out.println("Reloading these buttons: ");
        for (int i = 0; i < buttonNames.length; i++) {
            buttonPrefs[i] = this.tabPreferences.node(buttonNames[i]);

            // System.out.print(buttonNames[i] + ", ");
        }

        final JButton[] buttons = new JButton[buttonNames.length];
        for (int i = 0; i < buttonNames.length; i++) {

            // TODO achase 20040525
            // It looks to me like this if statement does nothing. If the getInt
            // call
            // returns i+1, then the putInt is called, setting the value to i+1,
            // which
            // is exactly what was just returned by get int.
            if ((i + 1) == buttonPrefs[i].getInt("buttonOrder", 0)) {

                /*
                 * System.out.println( "Buttons old order number " +
                 * buttonPrefs[i].getInt("buttonOrder", 0));
                 */
                buttonPrefs[i].putInt("buttonOrder", i + 1);
            }

            final JButton tempJButton = new NewObservationUsingConceptNameButton(buttonPrefs[i].get("buttonName",
                                            "unknown"), toolbelt);
            makeButtonDeletable(tempJButton);

            // in case the buttonOrder preference was not stored properly, make
            // sure there is not a button already in the array location desired.
            int indexInButtonsArray = buttonPrefs[i].getInt("buttonOrder", 0);
            indexInButtonsArray = (indexInButtonsArray < 0) ? 0 : indexInButtonsArray;

            if ((indexInButtonsArray >= buttons.length) || (buttons[indexInButtonsArray] != null)) {
                indexInButtonsArray = 0;

                while (buttons[indexInButtonsArray] != null) {
                    indexInButtonsArray++;
                }
            }

            buttons[indexInButtonsArray] = tempJButton;

            // resave the buttonOrder preference based on where the button
            // is actually added to the panel
            buttonPrefs[i].putInt("buttonOrder", indexInButtonsArray);
        }

        for (int i = 0; i < buttons.length; i++) {
            add(buttons[i]);
        }
    }

    /**
     * Adds a delete JMenuItem to the popup list for on a button.
     *
     * @param button
     *            A JButton that implements the org.mbari.swing.IPopup interface
     */
    private void makeButtonDeletable(final JButton button) {
        if (button instanceof IPopup) {
            final JPopupMenu popup = ((IPopup) button).getPopupMenu();
            final JMenuItem delete = new JMenuItem("Delete");
            delete.addActionListener(new ActionListener() {

                public void actionPerformed(final ActionEvent ae) {
                    ConceptButtonDropPanel.this.remove(button);
                    ConceptButtonDropPanel.this.repaint();

                    try {

                        // Preferences tempnode =
                        // tabPreferences.node(button.getText());
                        tabPreferences.node(button.getText()).removeNode();
                    }
                    catch (final BackingStoreException e) {
                        log.warn("Unable to update preferences. Reason: " + e.getClass().getName() + " -> " +
                                 e.getMessage());
                    }
                }

            });
            popup.add(delete);
        }
        else {
            log.warn("Attempted to add a Button that does not implemement the IPopup interface");
        }
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    private void saveButtonPositionPreferences() {
        final Component[] buttons = this.getComponents();
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] instanceof NewObservationUsingConceptNameButton) {
                final String nodeName = ((NewObservationUsingConceptNameButton) buttons[i]).getConceptName();
                final Preferences tempButtonPref = tabPreferences.node(nodeName);

                // Add the preferences
                tempButtonPref.putInt("buttonOrder", i);
                tempButtonPref.put("buttonName", nodeName);
                tempButtonPref.put("buttonText", nodeName);
            }
        }
    }

    /**
     * This inner class implements the DropTargetListener interface to provide
     * the functionality of accepting the drop end of a drag and drop operation
     *
     * @see java.awt.dnd.DropTargetListener
     * @see java.awt.dnd.DropTarget
     */
    class DTListener implements DropTargetListener {

        /**
         * Called by drop and retrieves the <code>DataFlavor</code> for the
         * input <code>DropTargetDropEvent</code>
         *
         * @param e
         *            the DropTargetDropEvent object
         * @return the chosen DataFlavor or null if none match
         */
        private DataFlavor chooseDropFlavor(final DropTargetDropEvent e) {
            if ((e.isLocalTransfer() == true) &&
                    e.isDataFlavorSupported(StringTransferable.LOCAL_STRING_FLAVOR)) {
                return StringTransferable.LOCAL_STRING_FLAVOR;
            }

            DataFlavor chosen = null;
            if (e.isDataFlavorSupported(StringTransferable.LOCAL_STRING_FLAVOR)) {
                chosen = StringTransferable.LOCAL_STRING_FLAVOR;
            }

            if (e.isDataFlavorSupported(NewObservationUsingConceptNameButton.BUTTON_FLAVOR)) {
                chosen = NewObservationUsingConceptNameButton.BUTTON_FLAVOR;
            }

            return chosen;
        }    // End chooseDropFlavor

        /**
         * This method is called when a drag operation enters this component.
         * Depending on the action associated with the drag, it will be accepted
         * or rejected.
         *
         * @param e
         *            the DropTargetDragEvent
         */
        public void dragEnter(final DropTargetDragEvent e) {

            // Check if drag acceptable
            if (isDragOk(e) == false) {
                e.rejectDrag();

                return;
            }

            // Tell the event to accept the drag
            e.acceptDrag(e.getDropAction());
        }    // End dragEnter

        /**
         * This method is called when the drag exits this component
         *
         * @param e
         *            the DropTargetEvent
         */
        public void dragExit(final DropTargetEvent e) {

            // Currently does nothing
        }    // End dragExit

        /**
         * This method is called during the drag operation while it is over this
         * component
         *
         * @param e
         *            the DropTargetDragEvent
         */
        public void dragOver(final DropTargetDragEvent e) {

            // Check action of the drag against this component's acceptable
            // actions
            if (isDragOk(e) == false) {
                e.rejectDrag();

                return;
            }

            // Accept the drag
            e.acceptDrag(e.getDropAction());
        }    // End dragOver

        /**
         * This is the method that is called when the actual drop occurs
         *
         * @param e
         *            the DropTargetDropEvent
         */
        public void drop(final DropTargetDropEvent e) {

            // First choose the DataFlavor to try and grab from the event
            final DataFlavor chosen = chooseDropFlavor(e);

            // If the flavor is not supported, reject the drop
            if (chosen == null) {
                log.warn("No flavor match found");
                e.rejectDrop();

                return;
            }

            // Grab what the actual action is from the event
            // int da = e.getDropAction();
            // Grab the action that the drag source specified
            final int sa = e.getSourceActions();

            // ?
            if ((sa & ConceptButtonDropPanel.this.acceptableActions) == 0) {
                e.rejectDrop();

                return;
            }

            // This is the generic object that the data transfer will be
            // pulled into
            Object data = null;
            try {

                // Go ahead and accept the drop
                e.acceptDrop(ConceptButtonDropPanel.this.acceptableActions);

                // Grab the data object from the Transferable
                data = e.getTransferable().getTransferData(chosen);

                // If there is no data, throw NullPointException
                if (data == null) {
                    throw new NullPointerException();
                }
            }
            catch (final Throwable t) {
                log.warn("Unable to accept drag and dropped info.", t);

                // Notify the event that the drop was not completed successfully
                e.dropComplete(false);

                return;
            }

            NewObservationUsingConceptNameButton buttonToDrop = null;

            // Check to see if the data from the transferable is a String
            if (data instanceof String) {

                // Grab the string
                final String s = (String) data;

                // check if this button has already been made
                final Component[] components = ConceptButtonDropPanel.this.getComponents();
                for (int i = 0; i < components.length; i++) {
                    if (components[i] instanceof JButton) {
                        if (((JButton) components[i]).getText().equals(s)) {

                            // don't add another button, let the user know this
                            // button already exists
                            final JButton existingButton = (JButton) components[i];
                            SwingUtils.flashJComponent(existingButton, 2);

                            return;
                        }
                    }
                }

                // Now create a new button
                buttonToDrop = new NewObservationUsingConceptNameButton(s, toolbelt);
                makeButtonDeletable(buttonToDrop);
            }
            else if (data instanceof NewObservationUsingConceptNameButton) {
                buttonToDrop = (NewObservationUsingConceptNameButton) data;
                ConceptButtonDropPanel.this.remove(buttonToDrop);
            }

            if (buttonToDrop != null) {
                final Point dropLocation = e.getLocation();
                final int index = ConceptButtonDropPanel.this.getInsertionIndex(dropLocation);
                ConceptButtonDropPanel.this.add(buttonToDrop, index);
                saveButtonPositionPreferences();
            }
            else {

                // The object is unknown, so reject the drop
                e.dropComplete(false);

                return;
            }

            // If we got here the drop was successful and the event should be
            // notified of that
            e.dropComplete(true);
            ConceptButtonDropPanel.this.revalidate();
        }    // End drop

        /**
         * This method is called if the user changes something during the drag
         *
         * @param e
         *            the DropTargetDragEvent
         */
        public void dropActionChanged(final DropTargetDragEvent e) {

            // Check against acceptable actions
            if (isDragOk(e) == false) {
                e.rejectDrag();

                return;
            }

            // Accept the drag
            e.acceptDrag(e.getDropAction());
        }    // End dropActionChanged

        /**
         * This method is called by the interface implementation of
         * <code>isDragOk</code> method. Checks to see if the drag flavor is
         * acceptable for this component
         *
         * @param e
         *            the DropTargetDragEvent object
         * @return whether the flavor is acceptable
         */
        private boolean isDragFlavorSupported(final DropTargetDragEvent e) {
            boolean ok = false;
            if (e.isDataFlavorSupported(StringTransferable.LOCAL_STRING_FLAVOR)) {
                ok = true;
            }

            if (e.isDataFlavorSupported(NewObservationUsingConceptNameButton.BUTTON_FLAVOR)) {
                ok = true;
            }

            return ok;
        }    // End isDragFlavorSupported

        /**
         * Called by dragEnter and dragOver and checks the flavors and
         * operations
         *
         * @param e
         *            the DropTargetDragEvent
         * @return whether the flavor and operation is ok
         */
        private boolean isDragOk(final DropTargetDragEvent e) {
            if (isDragFlavorSupported(e) == false) {
                return false;
            }

            // Grab the drop action from the event and check against acceptable
            // actions
            final int da = e.getDropAction();

            // Compare to acceptable action
            if ((da & ConceptButtonDropPanel.this.acceptableActions) == 0) {
                return false;
            }

            return true;
        }    // End isDragOK
    }    // End DTListener
}
