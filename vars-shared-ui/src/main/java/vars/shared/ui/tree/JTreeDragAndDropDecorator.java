/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.shared.ui.tree;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.InvalidDnDOperationException;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.knowledgebase.Concept;
import vars.shared.ui.StringTransferable;
import vars.shared.ui.UIDecorator;

/**
 *
 * @author brian
 */
public class JTreeDragAndDropDecorator implements UIDecorator {

    private final JTree tree;

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * This is the <code>DragGestureListener</code> that watches for mouse
     * movements and asked the <code>DragSouce</code> to start a drag operation
     * if a mouse drag is detected
     */
    private final DragGestureListener dragGestureListener;


    /**
     * This is the drag source that enables this component to serve as a
     * drag source in a drag and drop operation
     */
    private final DragSource dragSource;

    private final DragSourceListener  dragSourceListener;

    public JTreeDragAndDropDecorator(JTree tree) {

        this.tree = tree;

        this.dragSource = DragSource.getDefaultDragSource();
        this.dragGestureListener = new DGListener();
        this.dragSourceListener = new DSListener();
        this.dragSource.createDefaultDragGestureRecognizer(tree, DnDConstants.ACTION_COPY, this.dragGestureListener);

    }

        /**
     * This inner class implements the DragGestureListener and calls the
     * DragSource to start a drag.
     *
     * @author  brian
     * @version
     * @see  java.awt.dnd.DragGestureListener
     * @see  java.awt.dnd.DragSource
     * @see  java.awt.datatransfer.StringSelection
     */
    class DGListener implements DragGestureListener {

        /**
         * This method checks the DragGestureEvent to make sure
         * it is compatible with the supported actions in this class and
         * if that is OK, it starts the drag.  It also constructs a
         * <code>ConceptButtonTransferable</code> to tranfer data during
         * the drag
         *
         * @param  e the <code>DragGestureEvent</code> object
         */
        public void dragGestureRecognized(final DragGestureEvent e) {

            // if the action is ok we go ahead otherwise we punt
            // ORIGNIAL LINE: if ((e.getDragAction() & ConceptTreeReadOnly.this.dragAction) == 0) {
            if ((e.getDragAction() & DnDConstants.ACTION_COPY )== 0) {
                return;
            }

            // Get the selected concept and create a transferable for it
            TreePath treePath = tree.getSelectionPath();
            if (treePath != null) {
                DefaultMutableTreeNode node =  (DefaultMutableTreeNode) treePath.getLastPathComponent();
                Concept concept = (Concept) node.getUserObject();
                final Transferable transferable = new StringTransferable(concept.getPrimaryConceptName().getName());
                // now kick off the drag
                try {

                    // Use the DragSourceEvent to start the drag
                    e.startDrag(DragSource.DefaultCopyNoDrop, transferable, dragSourceListener);
                }
                catch (final InvalidDnDOperationException idoe) {
                    if (log.isDebugEnabled()) {
                        log.debug("Failed drag and drop", idoe);
                    }
                }
            }
            
        }
    }

    /**
     * This class implements the DragSourceListener inteface to respond to events
     * in a drag and drop transaction
     *
     * @author  brian
     * @version
     * @see  java.awt.dnd.DragSourceListener
     * @see  java.awt.dnd.DragSource
     * @see  java.awt.datatransfer.StringSelection
     */
    class DSListener implements DragSourceListener {

        /**
         * This method is called when the drag drop event is ended
         *
         * @param  e the <code>DragSourceDropEvent
         */
        public void dragDropEnd(final DragSourceDropEvent e) {

            // Currently does nothing
        }

        /**
         * This method is called when a drag enters (over) the component with
         * this listener.
         *
         * @param  e the <code>DragSourceEvent</code> event
         */
        public void dragEnter(final DragSourceDragEvent e) {

            // Grag the DragSourceContext from the event
            final DragSourceContext context = e.getDragSourceContext();

            // Get the action from the event
            final int myaction = e.getDropAction();

            // Check to make sure it is compatible with any actions in this listener
            if ((myaction & DnDConstants.ACTION_COPY) != 0) {
                context.setCursor(DragSource.DefaultCopyDrop);
            }
            else {
                context.setCursor(DragSource.DefaultCopyNoDrop);
            }
        }

        /**
         * This method is called when drag event exits component with this listener
         *
         * @param  e the <code>DragSourceEvent</code> event
         */
        public void dragExit(final DragSourceEvent e) {

            // Currently does nothing
        }

        /**
         * This method is called when a drag over event occurs
         *
         * @param  e the <code>DragSouceDragEvent</code>
         */
        public void dragOver(final DragSourceDragEvent e) {

            // Currently does nothing
        }

        /**
         * This method is called when the user changes something during the drag.
         * For example, press shift during drag to change to a link action
         *
         * @param  e the <code>DragSourceEvent</code>
         */
        public void dropActionChanged(final DragSourceDragEvent e) {

            // Currently does nothing
        }
    }



}
