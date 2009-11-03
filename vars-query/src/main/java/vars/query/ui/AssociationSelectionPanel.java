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


package vars.query.ui;

import com.google.inject.Inject;
import vars.LinkBean;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.*;

import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.swing.SearchableComboBoxModel;
import vars.ILink;
import vars.knowledgebase.*;
import vars.query.QueryDAO;
import vars.knowledgebase.SimpleConceptNameBean;
import vars.shared.ui.AllConceptNamesComboBox;


//~--- classes ----------------------------------------------------------------

/**
 * @author Brian Schlining
 * @version $Id: AssociationSelectionPanel.java 429 2006-11-20 22:51:32Z hohonuuli $
 */
public class AssociationSelectionPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1823103250255605396L;
    private final Logger log = LoggerFactory.getLogger(AssociationSelectionPanel.class);
    private final LinkBean nilAssociationBean = new LinkBean(
        ILink.VALUE_NIL, ILink.VALUE_NIL, ILink.VALUE_NIL);
    private final ConceptName nilConceptName = new SimpleConceptNameBean(
        ILink.VALUE_NIL.toUpperCase(), ConceptNameTypes.PRIMARY.getName());
    private final ConceptName selfConceptName = new SimpleConceptNameBean(ILink.VALUE_SELF,
        ConceptNameTypes.PRIMARY.getName());

    //~--- fields -------------------------------------------------------------

    /**
	 * @uml.property  name="topPanel"
	 * @uml.associationEnd  
	 */
    private JPanel topPanel = null;
    /**
	 * @uml.property  name="tfSearch"
	 * @uml.associationEnd  
	 */
    private JTextField tfSearch = null;
    /**
	 * @uml.property  name="tfLinkValue"
	 * @uml.associationEnd  
	 */
    private JTextField tfLinkValue = null;
    /**
	 * @uml.property  name="tfLinkName"
	 * @uml.associationEnd  
	 */
    private JTextField tfLinkName = null;
    /**
	 * @uml.property  name="middlePanel"
	 * @uml.associationEnd  
	 */
    private JPanel middlePanel = null;
    /**
	 * @uml.property  name="lblToConcept"
	 * @uml.associationEnd  
	 */
    private JLabel lblToConcept = null;
    /**
	 * @uml.property  name="lblSearch"
	 * @uml.associationEnd  
	 */
    private JLabel lblSearch = null;
    /**
	 * @uml.property  name="lblLinkValue"
	 * @uml.associationEnd  
	 */
    private JLabel lblLinkValue = null;
    /**
	 * @uml.property  name="lblLinkName"
	 * @uml.associationEnd  
	 */
    private JLabel lblLinkName = null;
    /**
	 * @uml.property  name="conceptNames"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.lang.String"
	 */
    private Collection conceptNames = new ArrayList();
    /**
	 * @uml.property  name="cbToConcept"
	 * @uml.associationEnd  
	 */
    private AllConceptNamesComboBox cbToConcept = null;
    /**
	 * @uml.property  name="cbAssociations"
	 * @uml.associationEnd  
	 */
    private JComboBox cbAssociations = null;
    /**
	 * @uml.property  name="bottomPanel"
	 * @uml.associationEnd  
	 */
    private JPanel bottomPanel = null;

    private final KnowledgebaseFactory knowledgebaseFactory;
    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;
    private final QueryDAO queryDAO;

    //~--- constructors -------------------------------------------------------

    /**
     *
     */
    @Inject
    public AssociationSelectionPanel(KnowledgebaseDAOFactory knowledgebaseDAOFactory,
            KnowledgebaseFactory knowledgebaseFactory, QueryDAO queryDAO) {
        super();
        this.knowledgebaseDAOFactory = knowledgebaseDAOFactory;
        this.knowledgebaseFactory = knowledgebaseFactory;
        this.queryDAO = queryDAO;
        initialize();
    }

    //~--- get methods --------------------------------------------------------

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @return
     */
    public LinkBean getAssociationBean() {
        LinkBean bean = new LinkBean();
        bean.setLinkName(getTfLinkName().getText());
        bean.setToConcept((String) getCbToConcept().getSelectedItem());
        bean.setLinkValue(getTfLinkValue().getText());
        return bean;
    }

    /**
	 * This method initializes jPanel
	 * @return  javax.swing.JPanel
	 * @uml.property  name="bottomPanel"
	 */
    private JPanel getBottomPanel() {
        if (bottomPanel == null) {
            bottomPanel = new JPanel();
            lblLinkValue = new JLabel();
            bottomPanel.setLayout(
                    new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
            lblToConcept = new JLabel();
            lblLinkName = new JLabel();
            lblLinkName.setText("link");
            lblToConcept.setText("to");
            lblLinkValue.setText("value");
            bottomPanel.add(lblLinkName, null);
            bottomPanel.add(Box.createHorizontalStrut(5));
            bottomPanel.add(getTfLinkName(), null);
            bottomPanel.add(Box.createHorizontalStrut(10));
            bottomPanel.add(lblToConcept, null);
            bottomPanel.add(Box.createHorizontalStrut(5));
            bottomPanel.add(getCbToConcept(), null);
            bottomPanel.add(Box.createHorizontalStrut(10));
            bottomPanel.add(lblLinkValue, null);
            bottomPanel.add(Box.createHorizontalStrut(5));
            bottomPanel.add(getTfLinkValue(), null);
        }

        return bottomPanel;
    }

    /**
	 * This method initializes jComboBox
	 * @return  javax.swing.JComboBox
	 * @uml.property  name="cbAssociations"
	 */
    private JComboBox getCbAssociations() {
        if (cbAssociations == null) {
            cbAssociations = new JComboBox();
            cbAssociations.setModel(new SearchableComboBoxModel());
            cbAssociations.setToolTipText("Links in Knowledgebase");
            cbAssociations.addItemListener(new java.awt.event.ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        setAssociationBean((LinkBean) e.getItem());
                    }
                }

            });
        }

        return cbAssociations;
    }

    /**
	 * This method initializes jComboBox1
	 * @return  javax.swing.JComboBox
	 * @uml.property  name="cbToConcept"
	 */
    private AllConceptNamesComboBox getCbToConcept() {
        if (cbToConcept == null) {
            cbToConcept = new AllConceptNamesComboBox(queryDAO);
            cbToConcept.addItem(nilConceptName);
            cbToConcept.addItem(selfConceptName);
        }

        return cbToConcept;
    }

    /**
	 * This method initializes jPanel
	 * @return  javax.swing.JPanel
	 * @uml.property  name="middlePanel"
	 */
    private JPanel getMiddlePanel() {
        if (middlePanel == null) {
            middlePanel = new JPanel();
            middlePanel.setLayout(
                    new BoxLayout(middlePanel, BoxLayout.X_AXIS));
            middlePanel.add(getCbAssociations(), null);
        }

        return middlePanel;
    }

    /**
	 * This method initializes jTextField
	 * @return  javax.swing.JTextField
	 * @uml.property  name="tfLinkName"
	 */
    private JTextField getTfLinkName() {
        if (tfLinkName == null) {
            tfLinkName = new JTextField();
            tfLinkName.setEditable(false);
            tfLinkName.setPreferredSize(new Dimension(120, 20));
        }

        return tfLinkName;
    }

    /**
	 * This method initializes jTextField1
	 * @return  javax.swing.JTextField
	 * @uml.property  name="tfLinkValue"
	 */
    private JTextField getTfLinkValue() {
        if (tfLinkValue == null) {
            tfLinkValue = new JTextField();
            tfLinkValue.setPreferredSize(new Dimension(120, 20));
        }

        return tfLinkValue;
    }

    /**
	 * This method initializes jTextField
	 * @return  javax.swing.JTextField
	 * @uml.property  name="tfSearch"
	 */
    private JTextField getTfSearch() {
        if (tfSearch == null) {
            tfSearch = new JTextField();
            tfSearch.setPreferredSize(new Dimension(120, 20));
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
                    final JComboBox cb = getCbAssociations();
                    int startIndex = cb.getSelectedIndex() + 1;
                    SearchableComboBoxModel linksModel = (SearchableComboBoxModel) cb.getModel();
                    int index = linksModel.searchForItemContaining(tfSearch.getText(),
                        startIndex);
                    if (index > -1) {
                        // Handle if match was found
                        cb.setSelectedIndex(index);
                        cb.hidePopup();
                    } else {
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

    /**
	 * This method initializes jPanel
	 * @return  javax.swing.JPanel
	 * @uml.property  name="topPanel"
	 */
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

    //~--- methods ------------------------------------------------------------

    /**
     * This method initializes this
     *
     */
    private void initialize() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setSize(384, 110);
        this.setBorder(
                javax.swing.BorderFactory.createTitledBorder(null,
                "with association", javax.swing.border.TitledBorder.LEFT,
                    javax.swing.border.TitledBorder.TOP,
                        new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                            Color.RED));
        this.add(getTopPanel(), null);
        this.add(getMiddlePanel(), null);
        this.add(getBottomPanel(), null);
    }

    //~--- set methods --------------------------------------------------------

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param associationBean
     */
    public void setAssociationBean(LinkBean associationBean) {
        if (associationBean == null) {
            associationBean = nilAssociationBean;
        }

        getTfLinkName().setText(associationBean.getLinkName());
        getCbToConcept().setSelectedItem(associationBean.getToConcept());
        getTfLinkValue().setText(associationBean.getLinkValue());
    }

    /**
	 * This method populates the available Associations that a user can select from in the cbAssociations.
	 * @param  conceptNames_
	 * @uml.property  name="conceptNames"
	 */
    public void setConceptNames(Collection conceptNames_) {

        /*
         * Do nothing if they are equal. Technically equals is only required to
         * do a value comparison for Lists and Sets. That's really what we're using
         * although the method signature does not enforce it.
         */
        if (conceptNames.equals(conceptNames_)) {
            return;
        } else {
            conceptNames = conceptNames_;
        }

        if (log.isInfoEnabled()) {
            log.info("Using the following concept-names: " + conceptNames);
        }

        /*
         * Error handiling. We must have a Collection and it needs to have at least
         * one name in it. If these conditions arene't met we create a colleciton
         * with a default (i.e root) name.
         */
        if (conceptNames == null) {
            conceptNames = new ArrayList();
        }

        if (conceptNames.isEmpty()) {
            conceptNames.add(ConceptName.NAME_DEFAULT);
        }

        /*
         * Look up associations that were used to annotate the concepts. If one of
         * the concepts is NIL (which is a wildcard like *), then rather than retrieve
         * associations we use LinkTemplates.
         */
        Collection<ILink> associationBeans = null;
        try {
            if (conceptNames.contains(ILink.VALUE_NIL.toLowerCase()) ||
                    conceptNames.contains(ILink.VALUE_NIL.toUpperCase())) {
                associationBeans = queryDAO.findAllLinkTemplates();
            } else {
                associationBeans = queryDAO.findByConceptNames(conceptNames);
            }
        } catch (Exception e) {
            EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
            log.error("Failed to look up associations", e);
        }

        /*
         * If an error occured, we don't want to hang the UI so we create a default
         * Collection. We also always want to add the nilAssociationBean. That should
         * always be an option for a user to select
         */
        if (associationBeans == null) {
            associationBeans = new ArrayList();
        }

        if (!associationBeans.contains(nilAssociationBean)) {
            associationBeans.add(nilAssociationBean);
        }

        /*
         * The method setConceptNames may be invoked on a Thread other than the
         * EventDispatchThread. We need to ensure that the redraw actions occur 
         * on the swing thread.
         */
        final SearchableComboBoxModel model = (SearchableComboBoxModel) getCbAssociations().getModel();
        final Collection aBeans = associationBeans;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                model.clear();
                model.addAll(aBeans);
                model.setSelectedItem(nilAssociationBean);
            }
        });

    }

}    // @jve:decl-index=0:visual-constraint="10,10"

