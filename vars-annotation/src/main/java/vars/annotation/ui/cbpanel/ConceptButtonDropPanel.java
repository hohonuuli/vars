/*
 * @(#)ConceptButtonDropPanel.java   2009.12.07 at 11:49:52 PST
 *
 * Copyright 2009 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.annotation.ui.cbpanel;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import org.bushe.swing.event.EventBus;
import org.mbari.awt.layout.WrappingFlowLayout;
import org.mbari.swing.IPopup;
import org.mbari.swing.SwingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.ui.StateLookup;
import vars.annotation.ui.ToolBelt;
import vars.shared.ui.StringTransferable;

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

    private static final String PREF_BUTTON_NAME = "name";
    private static final String PREF_BUTTON_ORDER = "order";
    private final Logger log = LoggerFactory.getLogger(getClass());
    protected Preferences tabPreferences = null;

    /**
     *     the actions supported by this drop target
     */
    private final int acceptableActions = DnDConstants.ACTION_COPY_OR_MOVE;
    @SuppressWarnings("unused")
    private DropTarget dropTarget;
    DropTargetListener dtListener;
    private final ToolBelt toolbelt;

    /**
     * This is the constructor
     *
     * @param tabPreferences
     *            is the Preferences object that hold the configuration of the
     *            buttons on this ConceptButtonDropPanel
     * @param toolbelt
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
    }

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

        try {

            /*
             *  Fetch the preferences that define each button and create the buttons. Store them
             *  in a list in the order they're supposed to be in.
             */
            List<ButtonNode> buttonNodes = new Vector<ButtonNode>();

            for (String nodeName : tabPreferences.childrenNames()) {
                Preferences buttonPreferences = tabPreferences.node(nodeName);
                String conceptName = buttonPreferences.get(PREF_BUTTON_NAME, "unknown");
                int order = buttonPreferences.getInt(PREF_BUTTON_ORDER, 0);
                JButton button = new NewObservationUsingConceptNameButton(conceptName, toolbelt);

                makeButtonDeletable(button);

                ButtonNode buttonNode = new ButtonNode(nodeName, order, conceptName, button);

                buttonNodes.add(buttonNode);
            }

            /*
             * Correct the order and add to the UI
             */
            Collections.sort(buttonNodes, new Comparator<ButtonNode>() {

                public int compare(ButtonNode o1, ButtonNode o2) {
                    return o1.order - o2.order;
                }

            });

            for (int i = 0; i < buttonNodes.size(); i++) {
                ButtonNode buttonNode = buttonNodes.get(i);
                Preferences preferences = tabPreferences.node(buttonNode.conceptName);
                preferences.putInt(PREF_BUTTON_ORDER, i);
                add(buttonNode.button);
            }

        }
        catch (Exception e) {
            EventBus.publish(StateLookup.TOPIC_NONFATAL_ERROR, e);
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
     *
     */
    private void saveButtonPositionPreferences() {
        final Component[] buttons = this.getComponents();

        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] instanceof NewObservationUsingConceptNameButton) {
                final String nodeName = ((NewObservationUsingConceptNameButton) buttons[i]).getConceptName();
                final Preferences preferences = tabPreferences.node(nodeName);

                // Add the preferences
                preferences.putInt(PREF_BUTTON_ORDER, i);
                preferences.put(PREF_BUTTON_NAME, nodeName);
            }
        }
    }

    class ButtonNode {

        final JButton button;
        final String conceptName;
        final String nodeName;
        final int order;

        /**
         * Constructs ...
         *
         * @param nodeName
         * @param order
         * @param conceptName
         * @param button
         */
        public ButtonNode(String nodeName, int order, String conceptName, JButton button) {
            super();
            this.nodeName = nodeName;
            this.order = order;
            this.conceptName = conceptName;
            this.button = button;
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
            if ((e.isLocalTransfer() == true) && e.isDataFlavorSupported(StringTransferable.LOCAL_STRING_FLAVOR)) {
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
