/*
 * @(#)LinkEditorPanel.java   2009.10.24 at 08:58:58 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.knowledgebase.ui;

import org.mbari.text.ObjectToStringConverter;
import vars.shared.ui.FullLinkListCellRender;
import vars.shared.ui.ILockableEditor;
import foxtrot.Job;
import foxtrot.Worker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import org.mbari.swing.SearchableComboBoxModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.ILink;
import vars.LinkBean;
import vars.LinkComparator;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.LinkTemplateDAO;
import vars.knowledgebase.SimpleConceptBean;
import vars.knowledgebase.SimpleConceptNameBean;
import vars.shared.ui.AllConceptNamesComboBox;

/**
 *
 * @author brian
 */
public class LinkEditorPanel extends javax.swing.JPanel implements ILockableEditor {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Concept selfConcept = new SimpleConceptBean(new SimpleConceptNameBean(ILink.VALUE_SELF,
        ConceptNameTypes.PRIMARY.toString()));
    private final ILink nilLinkTemplate = new LinkBean(ILink.VALUE_NIL, ILink.VALUE_NIL, ILink.VALUE_NIL);
    private final Concept nilConcept = new SimpleConceptBean(new SimpleConceptNameBean(ILink.VALUE_NIL,
        ConceptNameTypes.PRIMARY.toString()));
    private String title = "";
    private Concept concept;

 
    private javax.swing.JComboBox fromConceptComboBox;

    private javax.swing.JLabel jLabel1;

    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JSeparator jSeparator1;
    private ILink link;
    private javax.swing.JComboBox linkComboBox;
    private javax.swing.JTextField linkNameField;
    private javax.swing.JTextField linkValueField;
    private boolean locked;
    private javax.swing.JTextField searchField;
    private javax.swing.JComboBox toConceptComboBox;


    private final ToolBelt toolBelt;

    /**
     * Creates new form LinkEditorPanel
     *
     * @param toolBelt
     */
    public LinkEditorPanel(ToolBelt toolBelt) {
        this.toolBelt = toolBelt;
        initialize();
    }

    /**
     * @return  the concept
     */
    public Concept getConcept() {
        return concept;
    }

    public String getFromConcept() {
        return (String) fromConceptComboBox.getSelectedItem();
    }

    /**
     * @return  the fromConceptComboBox
     */
    public JComboBox getFromConceptComboBox() {
        return fromConceptComboBox;
    }

    /**
     * @return  the link
     */
    public ILink getLink() {
        return link;
    }

    /**
     * @return  the linkComboBox
     */
    public JComboBox getLinkComboBox() {
        if (linkComboBox == null) {
            linkComboBox = new javax.swing.JComboBox();
            linkComboBox.setModel(new SearchableComboBoxModel(new LinkComparator(), new ObjectToStringConverter() {
                public String convert(Object object) {
                    return object.toString().toLowerCase();
                }
            }));
            linkComboBox.setRenderer(new FullLinkListCellRender());
             /*
             * Setup linkComboBox
             */
            linkComboBox.addItemListener(new java.awt.event.ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        setLink((ILink) e.getItem());
                    }
                }

            });
        }
        return linkComboBox;
    }

    public String getLinkName() {
        return linkNameField.getText();
    }

    /**
     * @return  the linkNameField
     */
    public javax.swing.JTextField getLinkNameField() {
        return linkNameField;
    }

    public String getLinkValue() {
        return linkValueField.getText();
    }

    /**
     * @return  the linkValueField
     */
    public javax.swing.JTextField getLinkValueField() {
        return linkValueField;
    }

    /**
     * @return  the searchField
     */
    public JTextField getSearchField() {
        if (searchField == null) {
            searchField = new JTextField();
            /*
             * setup searchField
             */
            searchField.addFocusListener(new FocusAdapter() {

                @Override
                public void focusGained(FocusEvent fe) {
                    searchField.setSelectionStart(0);
                    searchField.setSelectionEnd(searchField.getText().length());
                }

            });

            searchField.addActionListener(new ActionListener() {

                public void actionPerformed(final ActionEvent e) {

                    /*
                     * FIXME 20040907 brian: There is a known bug here that occurs
                     * when enter is pressed repeatedly when tfSearch has focus.
                     * This bug causes the UI to hang.
                     */
                    JComboBox linkCb = getLinkComboBox();
                    int startIndex = linkCb.getSelectedIndex() + 1;
                    SearchableComboBoxModel linksModel = (SearchableComboBoxModel) linkCb.getModel();

                    startIndex = startIndex >= linksModel.getSize() ? 0 : startIndex;
                    String searchTerm = searchField.getText().toLowerCase(); // Case-insensitive serach

                    int index = linksModel.searchForItemContaining(searchTerm, startIndex);
                    if (index > -1) {

                        // Handle if match was found
                        linkCb.setSelectedIndex(index);
                        linkCb.hidePopup();
                    }
                    else {

                        // If no match was found search from the start of
                        // the
                        // list.
                        if (startIndex > 0) {
                            index = linksModel.searchForItemContaining(searchTerm);

                            if (index > -1) {

                                // Handle if match was found
                                linkCb.setSelectedIndex(index);
                                linkCb.hidePopup();
                            }
                        }
                    }
                }

            });
        }
        return searchField;
    }

    public String getTitle() {
        return title;
    }

    public String getToConcept() {
        return (String) toConceptComboBox.getSelectedItem();
    }

    /**
     * @return  the toConceptComboBox
     */
    public JComboBox getToConceptComboBox() {
        return toConceptComboBox;
    }

    private void initialize() {
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        linkNameField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        toConceptComboBox = new AllConceptNamesComboBox(toolBelt.getQueryPersistenceService());
        jLabel3 = new javax.swing.JLabel();
        linkValueField = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        fromConceptComboBox = new AllConceptNamesComboBox(toolBelt.getQueryPersistenceService());

        jLabel4.setText("Search:");


        jLabel1.setText("Link:");

        jLabel2.setText("To:");

        toConceptComboBox.setModel(toConceptComboBox.getModel());

        jLabel3.setText("Value:");

        jLabel5.setText("From:");

        fromConceptComboBox.setModel(fromConceptComboBox.getModel());

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
                this);
        this.setLayout(layout);
        layout
                .setHorizontalGroup(layout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                layout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .add(
                                                layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(
                                                                layout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                jLabel4)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                getSearchField(),
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                305,
                                                                                Short.MAX_VALUE))
                                                        .add(getLinkComboBox(), 0,
                                                                360,
                                                                Short.MAX_VALUE)
                                                        .add(
                                                                layout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                layout
                                                                                        .createParallelGroup(
                                                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                                                        .add(
                                                                                                jLabel5)
                                                                                        .add(
                                                                                                jLabel3))
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                layout
                                                                                        .createParallelGroup(
                                                                                                org.jdesktop.layout.GroupLayout.TRAILING)
                                                                                        .add(
                                                                                                linkValueField,
                                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                312,
                                                                                                Short.MAX_VALUE)
                                                                                        .add(
                                                                                                linkNameField,
                                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                312,
                                                                                                Short.MAX_VALUE)
                                                                                        .add(
                                                                                                org.jdesktop.layout.GroupLayout.LEADING,
                                                                                                fromConceptComboBox,
                                                                                                0,
                                                                                                312,
                                                                                                Short.MAX_VALUE)
                                                                                        .add(
                                                                                                toConceptComboBox,
                                                                                                0,
                                                                                                312,
                                                                                                Short.MAX_VALUE)))
                                                        .add(
                                                                jSeparator1,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                360,
                                                                Short.MAX_VALUE)
                                                        .add(jLabel1).add(
                                                                jLabel2))
                                        .addContainerGap()));
        layout
                .setVerticalGroup(layout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                layout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .add(
                                                layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                        .add(jLabel4)
                                                        .add(
                                                                getSearchField(),
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                jSeparator1,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                10,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                getLinkComboBox(),
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                        .add(jLabel5)
                                                        .add(
                                                                fromConceptComboBox,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                        .add(
                                                layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(
                                                                layout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                10,
                                                                                10,
                                                                                10)
                                                                        .add(
                                                                                jLabel1))
                                                        .add(
                                                                layout
                                                                        .createSequentialGroup()
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                linkNameField,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                        .add(jLabel2)
                                                        .add(
                                                                toConceptComboBox,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(jLabel3)
                                                        .add(
                                                                linkValueField,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                        .addContainerGap(
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)));




    }    // </editor-fold>//GEN-END:initComponents

    public boolean isLocked() {
        return locked;
    }

    /**
     * @param concept  the concept to set
     */
    @SuppressWarnings("unchecked")
	public void setConcept(Concept concept) {

        log.info("Retrieveing LinkTemplates from " + concept);

        if (concept == null) {
            try {

                // Add all linktemplates
                concept = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO().findRoot();
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
        List<ILink> linkTemplates = (List<ILink>) Worker.post(new Job() {

            public Object run() {
            	// DAOTX
                LinkTemplateDAO linkTemplateDAO = toolBelt.getKnowledgebaseDAOFactory().newLinkTemplateDAO();
                linkTemplateDAO.startTransaction();
                Concept daoConcept = linkTemplateDAO.find(fConcept);
                List<ILink> links = new Vector<ILink>(linkTemplateDAO.findAllApplicableToConcept(daoConcept));
                linkTemplateDAO.endTransaction();
                return links;
            }

        });

        /*
         * Concepts return immutable lists from accessor methods. We need to add
         * to the collection so we generate a copy.
         */
        linkTemplates.add(nilLinkTemplate);
        SearchableComboBoxModel model = (SearchableComboBoxModel) getLinkComboBox().getModel();
        model.clear();
        model.addAll(linkTemplates);
        model.setSelectedItem(nilLinkTemplate);

        Concept oldConcept = this.concept;
        this.concept = concept;
        firePropertyChange("concept", oldConcept, concept);
    }

    /**
     * @param link  the link to set
     */
    public void setLink(ILink link) {

        ConceptDAO conceptDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();

        ILink oldLink = this.link;
        this.link = link;

        if (link == null) {
            link = nilLinkTemplate;
        }

        /*
         * Update the fromConceptNameComboBox. The nilLinkTemlate does not have
         * a fromConcept so we need to be able to handle that.
         */
        final AllConceptNamesComboBox fromCb = (AllConceptNamesComboBox) fromConceptComboBox;
        try {
            fromCb.setSelectedItem(link.getFromConcept());
        }
        catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Defaulting to " + ConceptName.NAME_DEFAULT);
            }

            fromCb.setSelectedItem(ConceptName.NAME_DEFAULT);
        }

        /*
         * Update linkName and linkValue
         */
        linkValueField.setText(link.getLinkValue());
        linkNameField.setText(link.getLinkName());

        /*
         * Do a little code jig to get the correct toConcept
         */
        Concept toConcept = null;
        if (link.getToConcept().toLowerCase().trim().equals(ILink.VALUE_SELF)) {
            toConcept = selfConcept;
        }
        else if (link.getToConcept().equalsIgnoreCase(ILink.VALUE_NIL)) {
            toConcept = nilConcept;
        }
        else {
            try {
                toConcept = conceptDAO.findByName(link.getToConcept());

                if (toConcept == null) {
                    toConcept = conceptDAO.findRoot();
                }
            }
            catch (Exception e) {
                log.error("Failed to lookup " + link.getToConcept(), e);

                /*
                 * In case the database lookup fails will create a Concept
                 * object so that the GUI continues to function in a
                 * predictable manner
                 */
                KnowledgebaseFactory knowledgebaseFactory = toolBelt.getKnowledgebaseFactory();
                toConcept = knowledgebaseFactory.newConcept();
                ConceptName conceptName = knowledgebaseFactory.newConceptName();
                conceptName.setName(ILink.VALUE_NIL);
                conceptName.setNameType(ConceptNameTypes.PRIMARY.toString());
                toConcept.addConceptName(conceptName);
            }
        }

        /*
         * Update the fromConceptCobmoBox
         */
        final AllConceptNamesComboBox toCb = (AllConceptNamesComboBox) toConceptComboBox;
        toCb.setSelectedItem(toConcept.getPrimaryConceptName().getName());
        toCb.addItem(nilConcept.getPrimaryConceptName());
        toCb.addItem(selfConcept.getPrimaryConceptName());

        /*
         * Don't allow editing if the link template is the nil values
         */
        if (!locked) {
            boolean allowEditing = !nilLinkTemplate.equals(link);
            fromConceptComboBox.setEnabled(allowEditing);
            linkNameField.setEnabled(allowEditing);
            linkValueField.setEnabled(allowEditing);
            toConceptComboBox.setEnabled(allowEditing);
        }

        conceptDAO.close();
        firePropertyChange("link", oldLink, link);
    }

    /**
         * @param locked  the locked to set
         */
    public void setLocked(boolean locked) {
        this.locked = locked;
        fromConceptComboBox.setEnabled(!locked);
        linkNameField.setEnabled(!locked);
        linkValueField.setEnabled(!locked);
        toConceptComboBox.setEnabled(!locked);
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
