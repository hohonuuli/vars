/*
 * @(#)RowEditorPanel.java   2009.12.16 at 02:49:54 PST
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



package vars.annotation.ui.roweditor;

import com.google.common.collect.ImmutableSet;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.LayoutFocusTraversalPolicy;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.CacheClearedEvent;
import vars.CacheClearedListener;
import vars.DAO;
import vars.UserAccount;
import vars.annotation.Observation;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.eventbus.ObservationsSelectedEvent;
import vars.annotation.ui.eventbus.ObservationsChangedEvent;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;
import vars.shared.ui.AllConceptNamesComboBox;
import vars.shared.ui.event.LoggingTopicSubscriber;

/**
 * <p>THis panel is explcitly desinged for editing Observations in the
 * ObservationTable.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @created  May 3, 2004
 */
public class RowEditorPanel extends JPanel {


    /** Listens for forward tabs in JTextArea */
    private static final Set<KeyStroke> tab = ImmutableSet.of(KeyStroke.getKeyStroke("TAB"));

    /** Listens for reverse (shift) tabs in JTextArea */
    private static final Set<KeyStroke> shifttab = ImmutableSet.of(KeyStroke.getKeyStroke("shift TAB"));
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     *     The actions for changing the focus behavior of a JTextArea
     */
    protected Action nextFocusAction = new AbstractAction("Move Focus Forwards") {

        public void actionPerformed(ActionEvent evt) {
            ((Component) evt.getSource()).transferFocus();
        }
    };
    private final EventTopicSubscriber loggingSubscriber = new LoggingTopicSubscriber();
    protected Action prevFocusAction = new AbstractAction("Move Focus Backwards") {

        public void actionPerformed(ActionEvent evt) {
            ((Component) evt.getSource()).transferFocusBackward();
        }
    };
    private JComboBox conceptComboBox;
    private JPanel jPanel;
    private AssociationListEditorPanel listPanel;

    /**
     *     A collection of names that we should not allow notes to be added to.
     */
    private Collection<String> notableConceptNames;
    private JTextArea notesArea;
    private Observation observation;
    private final ToolBelt toolBelt;

    /**
     * Constructor for the RowEditorPanel object
     *
     * @param toolBelt
     */
    public RowEditorPanel(ToolBelt toolBelt) {
        super();
        AnnotationProcessor.process(this); // Create EventBus Proxy
        this.toolBelt = toolBelt;
        initialize();

        /**
         * Listen for changes in the observation
         *
         */
//        Lookup.getSelectedObservationsDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
//
//            public void propertyChange(final PropertyChangeEvent evt) {
//                final Collection<Observation> observations = (Collection<Observation>) evt.getNewValue();
//                Observation obs = (observations.size() == 1) ? observations.iterator().next() : null;
//                setObservation(obs);
//            }
//
//        });

        /*
         * This allows the notable concept names to be refreshed is the
         * KnowledgebaseCache is cleared
         */
        toolBelt.getPersistenceCache().addCacheClearedListener(new CacheClearedListener() {

            public void afterClear(final CacheClearedEvent evt) {
                ((AllConceptNamesComboBox) getConceptComboBox()).updateConceptNames();
            }

            public void beforeClear(final CacheClearedEvent evt) {
                notableConceptNames = null;
            }

        });


    }

    /**
     * EventBus method
     * @param selectionEvent
     */
    @EventSubscriber(eventClass = ObservationsSelectedEvent.class)
    public void updateObservationSelection(ObservationsSelectedEvent selectionEvent) {
        final Collection<Observation> observations = selectionEvent.get();
        Observation obs = (observations.size() == 1) ? observations.iterator().next() : null;
        setObservation(obs);
    }

    /**
     * EventBus method
     * @param updateEvent
     */
    @EventSubscriber(eventClass = ObservationsChangedEvent.class)
    public void updateObservationReference(ObservationsChangedEvent updateEvent) {
        Observation selectedObservation = getObservation();
        if (selectedObservation != null) {
            DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
            for (Observation obs : updateEvent.get()) {
                if (dao.equalInDatastore(selectedObservation, obs)) {
                    setObservation(obs);
                    break;
                }
            }
            dao.close();
        }
    }

    private JComboBox getConceptComboBox() {
        if (conceptComboBox == null) {
            conceptComboBox = new AllConceptNamesComboBox(toolBelt.getQueryPersistenceService());
            conceptComboBox.setPreferredSize(new Dimension(250, 23));

            conceptComboBox.addItemListener(new ItemListener() {

                public void itemStateChanged(final ItemEvent e) {

                    /*
                     *  If a new item is selected this updates the conceptname in the observation and
                     *  changes the observers name.
                     */
                    if (e.getStateChange() == ItemEvent.SELECTED) {

                        final String conceptName = (String) conceptComboBox.getSelectedItem();
                        getNotesArea().setEditable(!getNotableConceptNames().contains(conceptName));

                        if ((observation != null) && !observation.getConceptName().equals(conceptName)) {

                            // DAOTX
                            observation.setConceptName(conceptName);
                            final UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher()
                                .getValueObject();
                            observation.setObserver(userAccount.getUserName());
                            observation.setObservationDate(new Date());

                            if (log.isDebugEnabled()) {
                                log.debug("Observation changed to " + conceptName + " by " + userAccount.getUserName());
                            }
                        }
                    }

                    /*
                     *  When the conceptname is changed in the combobox. We want to
                     *  close the AssociationEditorPanel. If we don't the contents
                     *  of the AssociationEditorPanel do not match the selected
                     *  Concept.
                     */
                    final AssociationListEditorPanel p = getListPanel();
                    if (p.isEditingAssociation()) {
                        p.getAssociationEditorPanel().resetDisplay();
                        p.setEditingAssociation(false);
                    }
                }

            });

            /*
             *  Updates the conceptName to the primary name when [ENTER] is typed
             */
            final JTextField editor = (JTextField) conceptComboBox.getEditor().getEditorComponent();
            editor.addKeyListener(new KeyAdapter() {

                @Override
                public void keyTyped(final KeyEvent ke) {
                    final char c = ke.getKeyChar();
                    if (c == KeyEvent.VK_ENTER) {

                        /*
                         * When [ENTER] is pressed in the combobox, we want to:
                         *  - close the popup window
                         *  - Find the primary name of the concept selected in the combobox
                         *  - Set the combo box to the primary name if it's not already done.
                         *
                         * When a new item is selected in the comboBox, for example, when it's validated,
                         * it triggers an ItemEvent which in turn updates the Observation's concept name.
                         */
                        log.debug("ENTER Pressed in Editor");
                        Concept concept;
                        String primaryName;
                        final String selectedName = (String) conceptComboBox.getSelectedItem();
                        conceptComboBox.setPopupVisible(false);
                        conceptComboBox.setEnabled(false);

                        try {

                            // DAOTX
                            concept = toolBelt.getAnnotationPersistenceService().findConceptByName(selectedName);
                            primaryName = concept.getPrimaryConceptName().getName();
                        }
                        catch (final Exception e1) {
                            log.error("Failed to lookup '" + conceptComboBox.getSelectedItem() + "' from database", e1);
                            EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e1);
                            primaryName = ConceptName.NAME_DEFAULT;
                        }

                        if (!selectedName.equals(primaryName)) {
                            conceptComboBox.setSelectedItem(primaryName);
                        }

                        conceptComboBox.setEnabled(true);
                        conceptComboBox.requestFocus();
                    }
                }

            });

            // Tried to update concept name before adding an association, but this doesn't
            // work as I though it would
//            conceptComboBox.addFocusListener(new FocusAdapter() {
//                @Override
//                public void focusLost(FocusEvent e) {
//                    Collection<Observation> observations = ImmutableList.of(getObservation());
//                    toolBelt.getPersistenceController().updateAndValidate(observations);
//                    //throw new UnsupportedOperationException("Not supported yet.");
//                }
//            });
        }

        return conceptComboBox;
    }

    private JPanel getJPanel() {
        if (jPanel == null) {
            jPanel = new JPanel();
            jPanel.setLayout(new BorderLayout(4, 4));
            jPanel.add(getConceptComboBox(), BorderLayout.WEST);
            final JScrollPane scrollPane = new JScrollPane(getNotesArea());
            jPanel.add(scrollPane, BorderLayout.CENTER);
        }

        return jPanel;
    }

    private AssociationListEditorPanel getListPanel() {
        if (listPanel == null) {
            listPanel = new AssociationListEditorPanel(toolBelt);
            listPanel.setMinimumSize(new Dimension(200, 100));
        }

        return listPanel;
    }

    /**
     *     <p>The video lab has requested that top level concepts should not be able to have notes added to them. This is because annotators on the ship frequently use 'object' or 'physical-object' as the concept name but then but the detail of what the object is in the notes. They would like to stop this practice and force users to choose a concept.</p> <p>This method gets the root concept name and the names of its child concepts (and only the root concepts child concepts) and adds them to a collection. </p>
     *     @return   A collection of strings representing the root conceptname and it's  child names.
     */
    private Collection<String> getNotableConceptNames() {
        if (notableConceptNames == null) {


            notableConceptNames = new HashSet<String>();

            try {
                final Concept rootConcept = toolBelt.getAnnotationPersistenceService().findRootConcept();
                notableConceptNames.add(rootConcept.getPrimaryConceptName().getName());
                final Collection<Concept> chillin = rootConcept.getChildConcepts();
                for (final Iterator<Concept> i = chillin.iterator(); i.hasNext(); ) {
                    final Concept child = i.next();
                    notableConceptNames.add(child.getPrimaryConceptName().getName());
                }
            }
            catch (final Exception e) {
                if (log.isErrorEnabled()) {
                    log.error("Failed to get root concept from database", e);
                }
            }
        }

        return notableConceptNames;
    }

    private JTextArea getNotesArea() {
        if (notesArea == null) {
            notesArea = new JTextArea(1, 1);
            notesArea.setToolTipText("Notes");
            notesArea.getInputMap().put(KeyStroke.getKeyStroke("tab"), nextFocusAction.getValue(Action.NAME));
            notesArea.getInputMap().put(KeyStroke.getKeyStroke("shifttab"), prevFocusAction.getValue(Action.NAME));
            notesArea.getActionMap().put(nextFocusAction.getValue(Action.NAME), nextFocusAction);
            notesArea.getActionMap().put(prevFocusAction.getValue(Action.NAME), prevFocusAction);
            notesArea.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, tab);
            notesArea.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, shifttab);
            notesArea.setWrapStyleWord(true);
            notesArea.setLineWrap(true);

            notesArea.getDocument().addDocumentListener(new DocumentListener() {

                public void changedUpdate(final DocumentEvent e) {
                    update();
                }

                public void insertUpdate(final DocumentEvent e) {
                    update();
                }

                public void removeUpdate(final DocumentEvent e) {
                    update();
                }

                void update() {
                    if (observation != null) {
                        observation.setNotes(getNotesArea().getText());
                    }
                }

            });

        }

        return notesArea;
    }

    public Observation getObservation() {
        return observation;
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    private void initialize() {
        setLayout(new BorderLayout());
        add(getJPanel(), BorderLayout.NORTH);
        add(getListPanel(), BorderLayout.CENTER);
        setEnabled(false);
        setFocusCycleRoot(true);
        Dimension size = getPreferredSize();
        Dimension preferredSize = new Dimension(size.height, 200);
        setPreferredSize(preferredSize);

        //FocusTraversalPolicy policy = getFocusTraversalPolicy();
        setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {

            /*
             *  Warning, when a component should not be focused, I
             *  recursively call getComponentAfter, if all components should not be focused,
             * this could lead to an infinite loop.
             */
            @Override
            public Component getComponentAfter(final Container focusCycleRoot, final Component aComponent) {
                Component retval = super.getComponentAfter(focusCycleRoot, aComponent);
                if (retval == notesArea) {
                    retval = this.getComponentAfter(focusCycleRoot, retval);
                }

                if (retval instanceof JList) {
                    if (listPanel.getObservation().getAssociations().size() == 0) {
                        retval = this.getComponentAfter(focusCycleRoot, retval);
                    }
                }

                return retval;
            }
        });
    }

    /**
     * Set this component to an enabled/disabled state.
     *
     * @param  shouldEnable The new enabled value
     */
    @Override
    public void setEnabled(final boolean shouldEnable) {
        super.setEnabled(shouldEnable);
        getNotesArea().setEnabled(shouldEnable);
        getListPanel().setEnabled(shouldEnable);
        getConceptComboBox().setEnabled(shouldEnable);
    }

    public void setObservation(final Observation observation) {

        this.observation = observation;
        final boolean isNull = (observation == null);
        setEnabled(!isNull);
        getListPanel().setObservation(observation);

        if (isNull) {
            try {
                ConceptDAO conceptDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
                conceptDAO.startTransaction();
                Concept rootConcept = conceptDAO.findRoot();
                conceptDAO.endTransaction();
                getConceptComboBox().setSelectedItem(rootConcept.getPrimaryConceptName().getName());
            }
            catch (final Exception e) {
                getConceptComboBox().setSelectedIndex(0);

                if (log.isErrorEnabled()) {
                    log.error("Failed to find root concept in the KnowledgeBaseCache", e);
                }
            }

            getNotesArea().setText("");
            getNotesArea().setEnabled(false);
        }
        else {
            getConceptComboBox().setSelectedItem(observation.getConceptName());
            getNotesArea().setText(observation.getNotes());
            getNotesArea().setEnabled(!getNotableConceptNames().contains(observation.getConceptName()));
        }

        getConceptComboBox().requestFocus();
    }


}
