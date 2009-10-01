/*
 * LinkEditorPanel.java
 *
 * Created on May 23, 2006, 12:49 PM
 */

package vars.knowledgebase.ui;

import foxtrot.Job;
import foxtrot.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.swing.SearchableComboBoxModel;
import org.mbari.vars.dao.DAOException;
import org.mbari.vars.knowledgebase.model.Concept;
import org.mbari.vars.knowledgebase.model.ConceptName;
import vars.knowledgebase.IConceptName;
import org.mbari.vars.knowledgebase.model.LinkTemplate;
import org.mbari.vars.knowledgebase.model.dao.KnowledgeBaseCache;
import vars.ILink;
import org.mbari.vars.query.ui.ConceptConstraints;
import org.mbari.vars.ui.AllConceptNamesComboBox;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * 
 * @author brian
 */
public class LinkEditorPanel extends javax.swing.JPanel implements
        ILockableEditor {

    private static final long serialVersionUID = -496765277972685684L;

    /**
	 * @uml.property  name="locked"
	 */
    private boolean locked;

    /**
	 * @uml.property  name="link"
	 * @uml.associationEnd  
	 */
    private ILink link;

    /**
	 * @uml.property  name="title"
	 */
    private String title = "";

    /**
	 * @uml.property  name="concept"
	 * @uml.associationEnd  
	 */
    private Concept concept;

    private static final Logger log = LoggerFactory.getLogger(LinkEditorPanel.class);

    private static final Concept selfConcept = new Concept(new ConceptName(
            "self", ConceptName.NAMETYPE_PRIMARY), null);

    public static final LinkTemplate nilLinkTemplate = new LinkTemplate(
            ConceptConstraints.WILD_CARD_STRING,
            ConceptConstraints.WILD_CARD_STRING,
            ConceptConstraints.WILD_CARD_STRING);

    private static final Concept nilConcept = new Concept(new ConceptName(
            ConceptConstraints.WILD_CARD_STRING, ConceptName.NAMETYPE_PRIMARY),
            null);

    /** Creates new form LinkEditorPanel */
    public LinkEditorPanel() {
        initialize();
    }

    public String getToConcept() {
        return (String) toConceptComboBox.getSelectedItem();
    }

    public String getFromConcept() {
        return (String) fromConceptComboBox.getSelectedItem();
    }

    public String getLinkName() {
        return linkNameField.getText();
    }

    public String getLinkValue() {
        return linkValueField.getText();
    }

    /**
	 * @return  the title
	 * @uml.property  name="title"
	 */
    public String getTitle() {
        return title;
    }

    /**
	 * @param title  the title to set
	 * @uml.property  name="title"
	 */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
	 * @return  the concept
	 * @uml.property  name="concept"
	 */
    public Concept getConcept() {
        return concept;
    }

    /**
	 * @param concept  the concept to set
	 * @uml.property  name="concept"
	 */
    public void setConcept(Concept concept) {

        log.info("Retrieveing LinkTemplates from " + concept);

        if (concept == null) {
            try {
                // Add all linktemplates
                concept = KnowledgeBaseCache.getInstance().findRootConcept();
            }
            catch (DAOException e) {
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
                return Arrays.asList(fConcept.getHierarchicalLinkTemplates());
            }
        });

        /*
         * Concepts return immutable lists from accessor methods. We need to add
         * to the collection so we generate a copy.
         */
        linkTemplates = new ArrayList(linkTemplates);
        linkTemplates.add(nilLinkTemplate);
        SearchableComboBoxModel model = (SearchableComboBoxModel) linkComboBox
                .getModel();
        model.clear();
        model.addAll(linkTemplates);
        model.setSelectedItem(nilLinkTemplate);

        this.concept = concept;
    }

    /**
	 * @param link  the link to set
	 * @uml.property  name="link"
	 */
    public void setLink(ILink link) {

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
                log.debug("Defaulting to " + IConceptName.NAME_DEFAULT);
            }
            fromCb.setSelectedItem(IConceptName.NAME_DEFAULT);
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
        if (link.getToConcept().toLowerCase().trim().equals("self")) {
            toConcept = selfConcept;
        }
        else if (link.getToConcept().equalsIgnoreCase(
                ConceptConstraints.WILD_CARD_STRING)) {
            toConcept = nilConcept;
        }
        else {
            try {
                toConcept = KnowledgeBaseCache.getInstance().findConceptByName(
                        link.getToConcept());

                if (toConcept == null) {
                    toConcept = KnowledgeBaseCache.getInstance()
                            .findRootConcept();
                }
            }
            catch (DAOException e) {
                log.error("Failed to lookup " + link.getToConcept(), e);

                /*
                 * In case the database lookup fails will create a Concept
                 * objecdt so that the GUI continues to function in a
                 * predicatable manner
                 */
                toConcept = new Concept();
                ConceptName conceptName = new ConceptName();
                conceptName.setName(ConceptConstraints.WILD_CARD_STRING);
                conceptName.setNameType(ConceptName.NAMETYPE_PRIMARY);
                toConcept.setPrimaryConceptName(conceptName);
            }
        }

        /*
         * Update the fromConceptCobmoBox
         */
        final AllConceptNamesComboBox toCb = (AllConceptNamesComboBox) toConceptComboBox;
        toCb.setSelectedItem(toConcept.getPrimaryConceptNameAsString());
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
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code
    // ">//GEN-BEGIN:initComponents
    private void initialize() {
        jLabel4 = new javax.swing.JLabel();
        searchField = new javax.swing.JTextField();
        linkComboBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        linkNameField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        toConceptComboBox = new AllConceptNamesComboBox();
        jLabel3 = new javax.swing.JLabel();
        linkValueField = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        fromConceptComboBox = new AllConceptNamesComboBox();

        jLabel4.setText("Search:");

        linkComboBox.setModel(new SearchableComboBoxModel());

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
                                                                                searchField,
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                305,
                                                                                Short.MAX_VALUE))
                                                        .add(linkComboBox, 0,
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
                                                                searchField,
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
                                                linkComboBox,
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

        /*
         * setup searchField
         */
        searchField.addFocusListener(new FocusAdapter() {
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
                int startIndex = linkComboBox.getSelectedIndex() + 1;
                SearchableComboBoxModel linksModel = (SearchableComboBoxModel) linkComboBox
                        .getModel();
                int index = linksModel.searchForItemContaining(searchField
                        .getText(), startIndex);
                if (index > -1) {
                    // Handle if match was found
                    linkComboBox.setSelectedIndex(index);
                    linkComboBox.hidePopup();
                }
                else {
                    // If no match was found search from the start of
                    // the
                    // list.
                    if (startIndex > 0) {
                        index = linksModel.searchForItemContaining(searchField
                                .getText());

                        if (index > -1) {
                            // Handle if match was found
                            linkComboBox.setSelectedIndex(index);
                            linkComboBox.hidePopup();
                        }
                    }
                }
            }
        });

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

    }// </editor-fold>//GEN-END:initComponents

    public boolean isLocked() {
        return locked;
    }

    /**
	 * @param locked  the locked to set
	 * @uml.property  name="locked"
	 */
    public void setLocked(boolean locked) {
        this.locked = locked;
        fromConceptComboBox.setEnabled(!locked);
        linkNameField.setEnabled(!locked);
        linkValueField.setEnabled(!locked);
        toConceptComboBox.setEnabled(!locked);
    }

    /**
	 * @return  the link
	 * @uml.property  name="link"
	 */
    public ILink getLink() {
        return link;
    }

    /**
	 * @return  the linkNameField
	 * @uml.property  name="linkNameField"
	 */
    public javax.swing.JTextField getLinkNameField() {
        return linkNameField;
    }

    /**
	 * @return  the linkValueField
	 * @uml.property  name="linkValueField"
	 */
    public javax.swing.JTextField getLinkValueField() {
        return linkValueField;
    }

    /**
	 * @return  the linkComboBox
	 * @uml.property  name="linkComboBox"
	 */
    public JComboBox getLinkComboBox() {
        return linkComboBox;
    }

    /**
	 * @return  the searchField
	 * @uml.property  name="searchField"
	 */
    public JTextField getSearchField() {
        return searchField;
    }

    /**
	 * @return  the toConceptComboBox
	 * @uml.property  name="toConceptComboBox"
	 */
    public JComboBox getToConceptComboBox() {
        return toConceptComboBox;
    }

    /**
	 * @return  the fromConceptComboBox
	 * @uml.property  name="fromConceptComboBox"
	 */
    public JComboBox getFromConceptComboBox() {
        return fromConceptComboBox;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    /**
	 * @uml.property  name="fromConceptComboBox"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.lang.String"
	 */
    private javax.swing.JComboBox fromConceptComboBox;

    /**
	 * @uml.property  name="jLabel1"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private javax.swing.JLabel jLabel1;

    /**
	 * @uml.property  name="jLabel2"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private javax.swing.JLabel jLabel2;

    /**
	 * @uml.property  name="jLabel3"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private javax.swing.JLabel jLabel3;

    /**
	 * @uml.property  name="jLabel4"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private javax.swing.JLabel jLabel4;

    /**
	 * @uml.property  name="jLabel5"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private javax.swing.JLabel jLabel5;

    /**
	 * @uml.property  name="jSeparator1"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private javax.swing.JSeparator jSeparator1;

    /**
	 * @uml.property  name="linkComboBox"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private javax.swing.JComboBox linkComboBox;

    /**
	 * @uml.property  name="linkNameField"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private javax.swing.JTextField linkNameField;

    /**
	 * @uml.property  name="linkValueField"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private javax.swing.JTextField linkValueField;

    /**
	 * @uml.property  name="searchField"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private javax.swing.JTextField searchField;

    /**
	 * @uml.property  name="toConceptComboBox"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.lang.String"
	 */
    private javax.swing.JComboBox toConceptComboBox;
    // End of variables declaration//GEN-END:variables

}
