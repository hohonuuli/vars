/*
 * @(#)NewObservationUsingConceptNameButton.java   2009.11.16 at 09:31:48 PST
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;
import org.bushe.swing.event.EventBus;
import org.mbari.swing.IPopup;
import org.mbari.util.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.CacheClearedEvent;
import vars.CacheClearedListener;
import vars.UserAccount;
import vars.annotation.VideoArchive;
import vars.annotation.ui.actions.NewObservationUsingConceptNameAction;
import vars.annotation.ui.ToolBelt;
import vars.shared.ui.video.VideoControlService;
import vars.annotation.ui.Lookup;
import vars.knowledgebase.KnowledgebasePersistenceService;
import vars.shared.ui.FancyButton;

/**
 * <p>Creates a new Observation and changes its fromConcept to one specified
 * in the constructor. This implements various drag and drop interfaces so that
 * the button can be moved around in a tabbed panel, allowing users to reorganize
 * thier buttons.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class NewObservationUsingConceptNameButton extends FancyButton
        implements IPopup, Transferable, DragSourceListener, DragGestureListener {

    /**  */
    public static final String DISPATCHER_KEY_DRAG_LOCK = "NewObservationUsingConceptNameButton.DRAG_LOCK";

    /**  */
    public final static DataFlavor BUTTON_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType,
        "NewObservationUsingConceptNameButton");
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final DataFlavor[] _flavors = { BUTTON_FLAVOR };
    private final String conceptName;
    private boolean hasPerson;
    private boolean hasVcr;
    private boolean hasVideoArchive;
    private boolean isInitialized;
    private JPopupMenu popupMenu;
    private final ToolBelt toolbelt;

    /**
     * Constructor for the NewObservationUsingConceptNameButton object
     *
     * @param  conceptName New <code>Observation</code>s will be created using
     * this conceptName.
     * @param toolbelt
     */
    public NewObservationUsingConceptNameButton(final String conceptName, final ToolBelt toolbelt) {
        super();
        this.toolbelt = toolbelt;
        this.conceptName = conceptName;
        intialize();

        final DragSource dragSource = DragSource.getDefaultDragSource();

        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
        setAction(new NewObservationUsingConceptNameAction(toolbelt, conceptName));
        setText(conceptName);
        setToolTipText(conceptName);


        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(final MouseEvent e) {
                maybeShowPopup(e);
            }
            @Override
            public void mouseReleased(final MouseEvent e) {
                maybeShowPopup(e);
            }
            private void maybeShowPopup(final MouseEvent e) {
                if (e.isPopupTrigger()) {
                    getPopupMenu().show(e.getComponent(), e.getX(), e.getY());
                }
            }

        });

        /*
         * Enable this button if someone is logged in AND the Observation
         * in the ObservationDispather is not null and the VCR is enabled.
         */
        Lookup.getVideoArchiveDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                hasVideoArchive = (evt.getNewValue() != null);
                checkEnable();
            }
        });
        
        Lookup.getUserAccountDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
            
            public void propertyChange(PropertyChangeEvent evt) {
                hasPerson = (evt.getNewValue() != null);
                checkEnable();
            }
        });
        
        Lookup.getVideoControlServiceDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
            
            public void propertyChange(PropertyChangeEvent evt) {
                VideoControlService videoService = (VideoControlService) evt.getNewValue();
                hasVcr = (videoService != null && videoService.isConnected());
                checkEnable();
            }
        });


        toolbelt.getPersistenceCache().addCacheClearedListener(new CacheClearedListener() {

            public void afterClear(CacheClearedEvent evt) {
                checkEnable();
            }

            public void beforeClear(CacheClearedEvent evt) {
                setEnabled(false);
            }
        });

        checkEnable();
    }


    private void checkEnable() {
        boolean enable = false;

        if (hasVcr && hasPerson && hasVideoArchive) {

            // Check that the name is in the knowledgebase
            try {
                KnowledgebasePersistenceService kbPersistenceService = toolbelt.getKnowledgebasePersistenceService();

                enable = kbPersistenceService.doesConceptNameExist(conceptName);
            }
            catch (final Exception e) {
                log.error("Failed to lookup '" + conceptName + "' from the database", e);
            }
        }

        setEnabled(enable);
    }

    /**
     *  Drag and drop methods
     *
     * @param  dsde Description of the Parameter
     * @see java.awt.dnd.DragSourceListener#dragDropEnd(java.awt.dnd.DragSourceDropEvent)
     */
    public void dragDropEnd(final DragSourceDropEvent dsde) {

        // Do Nothing
    }

    /**
     *  Drag and drop methods
     *
     * @param  dsde Description of the Parameter
     * @see java.awt.dnd.DragSourceListener#dragEnter(java.awt.dnd.DragSourceDragEvent)
     */
    public void dragEnter(final DragSourceDragEvent dsde) {

        // Do Nothing
    }

    /**
     *  Drag and drop methods
     *
     * @param  dse Description of the Parameter
     *  @see java.awt.dnd.DragSourceListener#dragExit(java.awt.dnd.DragSourceEvent)
     */
    public void dragExit(final DragSourceEvent dse) {

        // Do Nothing
    }

    /**
     *  Drag and drop methods
     *
     * @param  dragGesture A dragGestureEvent
     * @see java.awt.dnd.DragGestureListener#dragGestureRecognized(java.awt.dnd.DragGestureEvent)
     */
    public void dragGestureRecognized(final DragGestureEvent dragGesture) {

        /*
         * A user may want to lock the panel so the buttons can't be reordered.
         * We store that value in a Dispatcher. True = allow drag reordering,
         * FALSE = prevent drag ordering.
         */
        final Dispatcher dispatcher = Dispatcher.getDispatcher(DISPATCHER_KEY_DRAG_LOCK);
        final Boolean isDragLocked = (Boolean) dispatcher.getValueObject();

        if ((isDragLocked == null) || Boolean.FALSE.equals(isDragLocked)) {
            final Point ptDragOrigin = dragGesture.getDragOrigin();
            final Component component = getComponentAt(ptDragOrigin);

            // if the component is not a button, don't drag it
            if (!(component instanceof NewObservationUsingConceptNameButton)) {
                return;
            }

            final NewObservationUsingConceptNameButton dragButton = (NewObservationUsingConceptNameButton) component;

            dragGesture.startDrag(DragSource.DefaultMoveDrop, dragButton, this);
        }
    }

    /**
     *  Drage and drop method
     *
     * @param  dsde Description of the Parameter
     * @see java.awt.dnd.DragSourceListener#dragOver(java.awt.dnd.DragSourceDragEvent)
     */
    public void dragOver(final DragSourceDragEvent dsde) {

        // Do nothing
    }

    /**
     *  Description of the Method
     *
     * @param  dsde Description of the Parameter
     * @see java.awt.dnd.DragSourceListener#dropActionChanged(java.awt.dnd.DragSourceDragEvent)
     */
    public void dropActionChanged(final DragSourceDragEvent dsde) {

        // Do Nothing
    }

    /**
     *     Gets the conceptName attribute of the NewObservationUsingConceptNameButton object
     *     @return   The conceptName value used to create new Observations
     */
    public String getConceptName() {
        return conceptName;
    }

    /**
     *     Generate a popup menu with a menu item that locates the conceptName in the Knowledgebase tree. Access to this popup menu is needed so that the Panel that these buttons gets dropped onto can add a delete item.
     *     @return   The popupMenu value
     */
    public JPopupMenu getPopupMenu() {
        if (popupMenu == null) {
            popupMenu = new JPopupMenu();

            final JMenuItem find = new JMenuItem("Find in Knowledgebase tree");

            find.addActionListener(new ActionListener() {

                public void actionPerformed(final ActionEvent ae) {

                    // Send a message to select a concept in the tree 
                    EventBus.publish(Lookup.TOPIC_SELECT_CONCEPT, getConceptName());
                }


            });
            popupMenu.add(find);
        }

        return popupMenu;
    }

    /**
     *  Gets the transferData attribute of the NewObservationUsingConceptNameButton object
     *
     * @param  flavor Description of the Parameter
     * @return  The transferData value
     * @exception  UnsupportedFlavorException Description of the Exception
     */
    public synchronized Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException {
        if (flavor.isMimeTypeEqual(BUTTON_FLAVOR.getMimeType())) {

            // DataFlavor.javaJVMLocalObjectMimeType))
            return this;
        }
        else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    /**
     *  Gets the transferDataFlavors attribute of the NewObservationUsingConceptNameButton object
     *
     * @return  The transferDataFlavors value
     */
    public DataFlavor[] getTransferDataFlavors() {
        return _flavors;
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    void intialize() {

        /*
         * Buttons have been squezed togehter vertically
         */
        final Border border = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(Color.ORANGE,
            Color.GRAY), BorderFactory.createEmptyBorder(0, 2, 0, 2));

        setBorder(border);

        if (!isInitialized) {
            final VideoArchive obj1 = (VideoArchive) Lookup.getVideoArchiveDispatcher().getValueObject();

            hasVideoArchive = (obj1 == null) ? false : true;

            final UserAccount obj2 = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();

            hasPerson = (obj2 == null) ? false : true;

            final VideoControlService videoService = (VideoControlService) Lookup.getVideoControlServiceDispatcher().getValueObject();
            final Object obj3 = (videoService == null) ? null : videoService;

            hasVcr = (obj3 == null) ? false : true;
        }
    }

    /**
     *  Gets the dataFlavorSupported attribute of the NewObservationUsingConceptNameButton object
     *
     * @param  flavor Description of the Parameter
     * @return  The dataFlavorSupported value
     */
    public boolean isDataFlavorSupported(final DataFlavor flavor) {
        return java.util.Arrays.asList(_flavors).contains(flavor);
    }
}
