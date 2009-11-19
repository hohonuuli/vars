/*
 * @(#)LinkTemplateSelectionPanel.java   2009.11.16 at 08:54:59 PST
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



package vars.query.ui;

import foxtrot.Job;
import foxtrot.Worker;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.mbari.swing.SearchableComboBoxModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.ILink;
import vars.LinkBean;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.LinkTemplateDAO;
import vars.knowledgebase.SimpleConceptBean;
import vars.knowledgebase.SimpleConceptNameBean;
import vars.query.QueryPersistenceService;
import vars.shared.ui.HierachicalConceptNameComboBox;

/**
 * @author Brian Schlining
 */
public class LinkTemplateSelectionPanel extends JPanel {

    private static final Concept selfConcept = new SimpleConceptBean(new SimpleConceptNameBean(ILink.VALUE_SELF,
        ConceptNameTypes.PRIMARY.toString()));
    private static final ILink nilLinkTemplate = new LinkBean(ConceptConstraints.WILD_CARD_STRING,
        ConceptConstraints.WILD_CARD_STRING, ConceptConstraints.WILD_CARD_STRING);
    private static final Concept nilConcept = new SimpleConceptBean(
        new SimpleConceptNameBean(ConceptConstraints.WILD_CARD_STRING, ConceptNameTypes.PRIMARY.toString()));
    private JPanel bottomPanel = null;
    private JComboBox cbLinkTemplates = null;
    private HierachicalConceptNameComboBox cbToConcept = null;
    private JLabel lblLinkName = null;
    private JLabel lblLinkValue = null;
    private JLabel lblSearch = null;
    private JLabel lblToConcept = null;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private JPanel middlePanel = null;
    private JTextField tfLinkName = null;
    private JTextField tfLinkValue = null;
    private JTextField tfSearch = null;
    private JPanel topPanel = null;
    private final ConceptDAO conceptDAO;
    private final LinkTemplateDAO linkTemplateDAO;
    private final QueryPersistenceService queryDAO;

    /**
     *
     *
     * @param conceptDAO
     * @param linkTemplateDAO
     * @param queryDAO
     */
    public LinkTemplateSelectionPanel(ConceptDAO conceptDAO, LinkTemplateDAO linkTemplateDAO,
                                      QueryPersistenceService queryDAO) {
        super();
        this.conceptDAO = conceptDAO;
        this.queryDAO = queryDAO;
        this.linkTemplateDAO = linkTemplateDAO;
        initialize();
    }

    /**
     *
     * @param conceptDAO
     * @param linkTemplateDAO
     * @param queryDAO
     * @param isDoubleBuffered
     */
    public LinkTemplateSelectionPanel(ConceptDAO conceptDAO, LinkTemplateDAO linkTemplateDAO, QueryPersistenceService queryDAO,
                                      boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        this.conceptDAO = conceptDAO;
        this.queryDAO = queryDAO;
        this.linkTemplateDAO = linkTemplateDAO;
        initialize();
    }

    /**
     *
     * @param conceptDAO
     * @param linkTemplateDAO
     * @param queryDAO
     * @param layout
     */
    public LinkTemplateSelectionPanel(ConceptDAO conceptDAO, LinkTemplateDAO linkTemplateDAO, QueryPersistenceService queryDAO,
                                      LayoutManager layout) {
        super(layout);
        this.conceptDAO = conceptDAO;
        this.queryDAO = queryDAO;
        this.linkTemplateDAO = linkTemplateDAO;
        initialize();
    }

    /**
     *
     * @param conceptDAO
     * @param linkTemplateDAO
     * @param queryDAO
     * @param layout
     * @param isDoubleBuffered
     */
    public LinkTemplateSelectionPanel(ConceptDAO conceptDAO, LinkTemplateDAO linkTemplateDAO, QueryPersistenceService queryDAO,
                                      LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
        this.conceptDAO = conceptDAO;
        this.linkTemplateDAO = linkTemplateDAO;
        this.queryDAO = queryDAO;
        initialize();
    }

    private JPanel getBottomPanel() {
        if (bottomPanel == null) {
            bottomPanel = new JPanel();
            lblLinkValue = new JLabel();
            bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
            lblToConcept = new JLabel();
            lblLinkName = new JLabel();
            lblLinkName.setText("link");
            lblToConcept.setText("to");
            lblLinkValue.setText("value");
            bottomPanel.add(lblLinkName, null);
            bottomPanel.add(getTfLinkName(), null);
            bottomPanel.add(lblToConcept, null);
            bottomPanel.add(getCbToConcept(), null);
            bottomPanel.add(lblLinkValue, null);
            bottomPanel.add(getTfLinkValue(), null);
        }

        return bottomPanel;
    }

    private JComboBox getCbLinkTemplates() {
        if (cbLinkTemplates == null) {
            cbLinkTemplates = new JComboBox();
            cbLinkTemplates.setModel(new SearchableComboBoxModel());
            cbLinkTemplates.setToolTipText("Links in Knowledgebase");
            cbLinkTemplates.addItemListener(new java.awt.event.ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        setLinkTemplate((ILink) e.getItem());
                    }
                }

            });
        }

        return cbLinkTemplates;
    }

    private HierachicalConceptNameComboBox getCbToConcept() {
        if (cbToConcept == null) {
            cbToConcept = new HierachicalConceptNameComboBox(conceptDAO);
        }

        return cbToConcept;
    }

    private JPanel getMiddlePanel() {
        if (middlePanel == null) {
            middlePanel = new JPanel();
            middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.X_AXIS));
            middlePanel.add(getCbLinkTemplates(), null);
        }

        return middlePanel;
    }

    private JTextField getTfLinkName() {
        if (tfLinkName == null) {
            tfLinkName = new JTextField();
            tfLinkName.setEditable(false);
        }

        return tfLinkName;
    }

    private JTextField getTfLinkValue() {
        if (tfLinkValue == null) {
            tfLinkValue = new JTextField();
        }

        return tfLinkValue;
    }

    private JTextField getTfSearch() {
        if (tfSearch == null) {
            tfSearch = new JTextField();
            tfSearch.addFocusListener(new FocusAdapter() {

                @Override
                public void focusGained(FocusEvent fe) {
                    tfSearch.setSelectionStart(0);
                    tfSearch.setSelectionEnd(tfSearch.getText().length());
                }
            });
            tfSearch.addActionListener(new ActionListener() {

                public void actionPerformed(final ActionEvent e) {

                    /*
                     *  FIXME 20040907 brian: There is a known bug here that occurs
                     *  when enter is pressed repeatedly when tfSearch has focus. This
                     *  bug causes the UI to hang.
                     */
                    final JComboBox cb = getCbLinkTemplates();
                    int startIndex = cb.getSelectedIndex() + 1;
                    SearchableComboBoxModel linksModel = (SearchableComboBoxModel) cb.getModel();
                    int index = linksModel.searchForItemContaining(tfSearch.getText(), startIndex);

                    if (index > -1) {

                        // Handle if match was found
                        cb.setSelectedIndex(index);
                        cb.hidePopup();
                    }
                    else {

                        // If no match was found search from the start of the
                        // list.
                        if (startIndex > 0) {
                            index = linksModel.searchForItemContaining(tfSearch.getText());

                            if (index > -1) {

                                // Handle if match was found
                                cb.setSelectedIndex(index);
                                cb.hidePopup();
                            }
                        }
                    }
                }

            });
        }

        return tfSearch;
    }

    private JPanel getTopPanel() {
        if (topPanel == null) {
            lblSearch = new JLabel();
            topPanel = new JPanel();
            topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
            lblSearch.setText("Search:");
            topPanel.add(lblSearch, null);
            topPanel.add(getTfSearch(), null);
        }

        return topPanel;
    }

    private void initialize() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setSize(384, 110);
        this.add(getTopPanel(), null);
        this.add(getMiddlePanel(), null);
        this.add(getBottomPanel(), null);
    }

    /**
     * @param concept
     */
    public void setConcept(Concept concept) {
        log.info("Retrieveing LinkTemplates from " + concept);

        if (concept == null) {
            try {
                concept = conceptDAO.findRoot();
            }
            catch (Exception e) {
                log.error("Failed to lookup root concept", e);
            }
        }

        /*
         * This step may take quite a while the first time it is called since it
         * has to load the entire knowledgebase
         */
        final Concept fConcept = concept;
        Collection linkTemplates = (Collection) Worker.post(new Job() {

            public Object run() {
                return Arrays.asList(linkTemplateDAO.findAllApplicableToConcept(fConcept));
            }
        });

        /*
         * Concepts return immutable lists from accessor methods. We need to add
         * to the collection so we generate a copy.
         */
        linkTemplates = new ArrayList(linkTemplates);
        linkTemplates.add(nilLinkTemplate);

        SearchableComboBoxModel model = (SearchableComboBoxModel) getCbLinkTemplates().getModel();

        model.clear();
        model.addAll(linkTemplates);
        model.setSelectedItem(nilLinkTemplate);
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param linkTemplate
     */
    public void setLinkTemplate(ILink linkTemplate) {
        if (linkTemplate == null) {
            linkTemplate = nilLinkTemplate;
        }

        getTfLinkName().setText(linkTemplate.getLinkName());

        Concept toConcept = null;

        if (linkTemplate.getToConcept().equalsIgnoreCase(ILink.VALUE_SELF)) {
            toConcept = selfConcept;
        }
        else if (linkTemplate.getToConcept().equalsIgnoreCase(ConceptConstraints.WILD_CARD_STRING)) {
            toConcept = nilConcept;
        }
        else {
            try {
                toConcept = conceptDAO.findByName(linkTemplate.getToConcept());

                if (toConcept == null) {
                    toConcept = conceptDAO.findRoot();
                }
            }
            catch (Exception e) {
                log.error("Failed to lookup " + linkTemplate.getToConcept(), e);

                /*
                 * In case the database lookup fails will create a Concept objecdt
                 * so that the GUI continues to function in a predicatable manner
                 */
                toConcept = new SimpleConceptBean();

                ConceptName conceptName = new SimpleConceptNameBean(ILink.VALUE_NIL,
                    ConceptNameTypes.PRIMARY.toString());

                conceptName.setName(ConceptConstraints.WILD_CARD_STRING);
                conceptName.setNameType(ConceptNameTypes.PRIMARY.toString());
                toConcept.addConceptName(conceptName);
            }
        }

        /*
         * This may take a while, we'll do it in a seperate thread.
         */
        final Concept fToConcept = toConcept;

        Worker.post(new Job() {

            public Object run() {
                getCbToConcept().setConcept(fToConcept);

                return null;
            }
        });
        getCbToConcept().setSelectedItem(toConcept.getPrimaryConceptName().getName());
        getCbToConcept().addItem(nilConcept.getPrimaryConceptName());
        getCbToConcept().addItem(selfConcept.getPrimaryConceptName());
        getTfLinkValue().setText(linkTemplate.getLinkValue());
    }
}
