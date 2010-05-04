/*
 * @(#)AssociationEditorPanel.java   2009.12.17 at 11:37:50 PST
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

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import foxtrot.Task;
import foxtrot.Worker;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import org.bushe.swing.event.EventBus;
import org.mbari.swing.LabeledSpinningDialWaitIndicator;
import org.mbari.swing.SearchableComboBoxModel;
import org.mbari.swing.SortedComboBoxModel;
import org.mbari.swing.WaitIndicator;
import org.mbari.text.ObjectToStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.ILink;
import vars.LinkBean;
import vars.LinkComparator;
import vars.LinkUtilities;
import vars.annotation.AnnotationPersistenceService;
import vars.annotation.Association;
import vars.annotation.Observation;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;
import vars.knowledgebase.Concept;
import vars.knowledgebase.LinkTemplate;
import vars.shared.ui.FancyButton;
import vars.shared.ui.HierachicalConceptNameComboBox;
import vars.shared.ui.LinkListCellRenderer;

/**
 *
 *
 * @version        Enter version here..., 2009.12.15 at 02:11:16 PST
 * @author         Brian Schlining [brian@mbari.org]
 */
public class AssociationEditorPanel extends JPanel {

    private final ILink nil = new LinkBean(ILink.VALUE_NIL, ILink.VALUE_NIL, ILink.VALUE_NIL);
    private final Logger log = LoggerFactory.getLogger(getClass());
    private JButton cancelButton;
    private final AssociationEditorPanelController controller;
    private JLabel lblLinkName;
    private JLabel lblSearchFor;
    private JLabel lblToConcept;
    private JLabel lblValue;
    private JTextField linkNameTextField;
    private JTextField linkValueTextField;
    private JComboBox linksComboBox;
    private JButton okButton;
    private JTextField searchTextField;
    private HierachicalConceptNameComboBox toConceptComboBox;

    /**
     * Constructs ...
     *
     * @param toolBelt
     */
    public AssociationEditorPanel(ToolBelt toolBelt) {
        controller = new AssociationEditorPanelController(toolBelt, this);
        initialize();
    }

    /**
     * Adds an <code>ActionListener</code>. The listener will receive an
     * action event the user finishes making a selection.
     *
     * @param  l the <code>ActionListener</code> that is to be notified
     */
    public void addActionListener(final ActionListener l) {
        getOkButton().addActionListener(l);
        getCancelButton().addActionListener(l);
    }

    private JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new FancyButton();
            cancelButton.setPreferredSize(new Dimension(30, 23));
            cancelButton.setToolTipText("Cancel");
            cancelButton.setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/stop.png")));
            cancelButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    resetDisplay();
                }

            });
        }

        return cancelButton;
    }

    private JLabel getLblLinkName() {
        if (lblLinkName == null) {
            lblLinkName = new JLabel("Link");
        }

        return lblLinkName;
    }

    private JLabel getLblSearchFor() {
        if (lblSearchFor == null) {
            lblSearchFor = new JLabel("Search for: ");
        }

        return lblSearchFor;
    }

    private JLabel getLblToConcept() {
        if (lblToConcept == null) {
            lblToConcept = new JLabel("To");
        }

        return lblToConcept;
    }

    private JLabel getLblValue() {
        if (lblValue == null) {
            lblValue = new JLabel("Value");
        }

        return lblValue;
    }

    protected JTextField getLinkNameTextField() {
        if (linkNameTextField == null) {
            linkNameTextField = new JTextField();
            linkNameTextField.setColumns(10);
            linkNameTextField.setToolTipText("Link Name");
            linkNameTextField.setEditable(false);
            linkNameTextField.addFocusListener(new FocusAdapter() {

                @Override
                public void focusGained(FocusEvent e) {
                    linkNameTextField.setSelectionStart(0);
                    linkNameTextField.setSelectionEnd(linkNameTextField.getText().length());
                }

            });
        }

        return linkNameTextField;
    }

    protected JTextField getLinkValueTextField() {
        if (linkValueTextField == null) {
            linkValueTextField = new JTextField();
            linkValueTextField.setColumns(10);
            linkValueTextField.setToolTipText("Link Value");
            linkValueTextField.addFocusListener(new FocusAdapter() {

                @Override
                public void focusGained(FocusEvent e) {
                    linkValueTextField.setSelectionStart(0);
                    linkValueTextField.setSelectionEnd(linkValueTextField.getText().length());
                }

            });
        }

        return linkValueTextField;
    }

    protected JComboBox getLinksComboBox() {
        if (linksComboBox == null) {
            linksComboBox = new JComboBox();
            linksComboBox.setRenderer(new LinkListCellRenderer());
            linksComboBox.setModel(new SearchableComboBoxModel<ILink>(new LinkComparator(),
                    new ObjectToStringConverter<ILink>() {

                public String convert(ILink object) {
                    return LinkUtilities.formatAsString(object);
                }

            }));

            linksComboBox.setToolTipText("Links in Knowledgebase");
            linksComboBox.addItemListener(new ItemListener() {

                public void itemStateChanged(final ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        setLink((ILink) e.getItem());
                    }
                }

            });
        }

        return linksComboBox;
    }

    private JButton getOkButton() {
        if (okButton == null) {
            okButton = new FancyButton();
            okButton.setPreferredSize(new Dimension(30, 23));
            okButton.setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/add.png")));
            okButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    ILink link = new LinkBean(getLinkNameTextField().getText(),
                                              (String) getToConceptComboBox().getSelectedItem(),
                                              getLinkValueTextField().getText());
                    controller.doOkay(link);
                }
            });
        }

        return okButton;
    }

    private JTextField getSearchTextField() {
        if (searchTextField == null) {
            searchTextField = new JTextField();
            searchTextField.setColumns(10);
            searchTextField.addFocusListener(new FocusAdapter() {

                @Override
                public void focusGained(FocusEvent e) {
                    searchTextField.setSelectionStart(0);
                    searchTextField.setSelectionEnd(searchTextField.getText().length());
                }

            });

            searchTextField.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    JComboBox comboBox = getLinksComboBox();
                    SearchableComboBoxModel<ILink> model = (SearchableComboBoxModel<ILink>) comboBox.getModel();
                    int startIndex = comboBox.getSelectedIndex() + 1;
                    if (startIndex >= model.getSize()) {
                        startIndex = 0;
                    }

                    int index = model.searchForItemContaining(searchTextField.getText(), startIndex);
                    if (index > -1) {

                        // Match was found
                        comboBox.setSelectedIndex(index);
                        comboBox.hidePopup();
                    }
                    else {

                        // No match was found. Try again from the top of the list
                        if (startIndex > 0) {
                            index = model.searchForItemContaining(searchTextField.getText());

                            if (index > -1) {

                                // Match found
                                comboBox.setSelectedIndex(index);
                                comboBox.hidePopup();
                            }
                        }
                    }

                }
            });
        }

        return searchTextField;
    }

    private HierachicalConceptNameComboBox getToConceptComboBox() {
        if (toConceptComboBox == null) {
            toConceptComboBox = new HierachicalConceptNameComboBox(
                controller.getToolBelt().getAnnotationPersistenceService());
            toConceptComboBox.setToolTipText("To Concept");
            toConceptComboBox.addFocusListener(new FocusAdapter() {

                @Override
                public void focusGained(FocusEvent e) {
                    toConceptComboBox.getEditor().selectAll();
                }

            });
        }

        return toConceptComboBox;
    }

    private void initialize() {
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                        .addComponent(getLinksComboBox(), 0, 438, Short.MAX_VALUE)
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addComponent(getLblSearchFor())
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(getSearchTextField(), GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE))
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(getLblLinkName())
                                                        .addComponent(getLblValue()))
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(groupLayout.createSequentialGroup()
                                                                .addComponent(getLinkNameTextField(), GroupLayout.PREFERRED_SIZE, 202, GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                                .addComponent(getLblToConcept())
                                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                                .addComponent(getToConceptComboBox(), 0, 205, Short.MAX_VALUE))
                                                        .addGroup(groupLayout.createSequentialGroup()
                                                                .addComponent(getLinkValueTextField(), GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                                .addComponent(getCancelButton())
                                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                                .addComponent(getOkButton())))))
                                .addGap(4))
        );
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(getLblSearchFor())
                                        .addComponent(getSearchTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(getLinksComboBox(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                        .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                                .addComponent(getLblLinkName())
                                                .addComponent(getLinkNameTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                                .addComponent(getLblToConcept())
                                                .addComponent(getToConceptComboBox(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(getLblValue())
                                        .addComponent(getLinkValueTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(getCancelButton())
                                        .addComponent(getOkButton()))
                                .addContainerGap(165, Short.MAX_VALUE))
        );
        setLayout(groupLayout);
        setFocusTraversalPolicy(new FocusTraversalPolicy() {

            @Override
            public Component getComponentAfter(Container aContainer, Component aComponent) {

                Component componentAfter = getOkButton();

                if (aComponent == getOkButton()) {
                    componentAfter = getCancelButton();
                }

                if (aComponent == getCancelButton()) {
                    setFocusCycleRoot(false);
                    componentAfter = getFocusCycleRootAncestor().getFocusTraversalPolicy()
                        .getComponentAfter(getFocusCycleRootAncestor(), AssociationEditorPanel.this);
                    setFocusCycleRoot(true);
                }

                return componentAfter;

            }

            @Override
            public Component getComponentBefore(Container aContainer, Component aComponent) {

                Component componentBefore = getSearchTextField();

                if (aComponent == getSearchTextField()) {
                    setFocusCycleRoot(false);
                    componentBefore = getFocusCycleRootAncestor().getFocusTraversalPolicy()
                        .getComponentBefore(getFocusCycleRootAncestor(), AssociationEditorPanel.this);
                    setFocusCycleRoot(true);
                }

                if (aComponent == getCancelButton()) {
                    componentBefore = getOkButton();
                }

                return componentBefore;
            }

            @Override
            public Component getFirstComponent(Container aContainer) {
                return getSearchTextField();
            }

            @Override
            public Component getLastComponent(Container aContainer) {
                return getCancelButton();
            }

            @Override
            public Component getDefaultComponent(Container aContainer) {
                return getSearchTextField();
            }
        });
        setFocusCycleRoot(true);
        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentShown(ComponentEvent e) {
                getRootPane().setDefaultButton(getOkButton());
                getSearchTextField().requestFocus();
            }

        });
        resetDisplay();
    }

    /**
     */
    public void resetDisplay() {
        getSearchTextField().setText("");
        setLink(nil);
    }

    /**
     *
     * @param enabled
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        getCancelButton().setEnabled(enabled);
        getOkButton().setEnabled(enabled);
        getToConceptComboBox().setEnabled(enabled);
        getLinkNameTextField().setEnabled(enabled);
        getLinkValueTextField().setEnabled(enabled);
        getLinksComboBox().setEnabled(enabled);
    }

    private void setLink(final ILink link) {
        WaitIndicator waitIndicator = new LabeledSpinningDialWaitIndicator(this,
                "Loading " + link.getToConcept() + " ...");
        getLinkNameTextField().setText(link.getLinkName());
        getLinkValueTextField().setText(link.getLinkValue());
        final HierachicalConceptNameComboBox comboBox = getToConceptComboBox();
        comboBox.hidePopup();
        getLinksComboBox().hidePopup();
        String conceptName = link.getToConcept();

        if (conceptName.equals(ILink.VALUE_NIL) || conceptName.equals(ILink.VALUE_SELF)) {
            SortedComboBoxModel<String> model = (SortedComboBoxModel<String>) comboBox.getModel();
            model.clear();
            model.addElement(conceptName);
        }
        else {

            // Retrieve the child concepts and add to gui
            try {
                final Concept c = (Concept) Worker.post(new Task() {

                    @Override
                    public Object run() throws Exception {
                        return controller.getToolBelt().getAnnotationPersistenceService().findConceptByName(
                            link.getToConcept());
                    }

                });
                comboBox.setConcept(c);
                conceptName = c.getPrimaryConceptName().getName();

            }
            catch (final Exception e) {
                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
                comboBox.addItem(conceptName);
            }
        }

        comboBox.setSelectedItem(conceptName);
        waitIndicator.dispose();
        repaint();
    }

    /**
     *
     * @param observation
     * @param association
     */
    public void setTarget(final Observation observation, final Association association) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {


                controller.setTarget(observation, association);
                ILink defaultLink = nil;

                /*
                 * ---- Step 1:
                 * If an association is set we're editing it. If not we're adding it.
                 *
                 * Here we figure out what the intial link and conceptName should be.
                 */
                if (observation != null) {

                    if (association != null) {
                        defaultLink = association;
                        getOkButton().setToolTipText("Accept Edits");
                    }
                    else {
                        getOkButton().setToolTipText("Add Association");
                    }
                }

                JComboBox comboBox = getLinksComboBox();
                SearchableComboBoxModel<ILink> model = (SearchableComboBoxModel<ILink>) comboBox.getModel();

                /*
                 * ---- Step 2:
                 * Update the available LinkTemplates in the UI
                 */
                model.clear();
                model.addElement(nil);
                AnnotationPersistenceService service = controller.getToolBelt().getAnnotationPersistenceService();

                try {

                    Concept concept = service.findConceptByName(observation.getConceptName());
                    if (concept == null) {
                        log.warn("A concept named" + observation.getConceptName() +
                                 " was not found in the knowledgebase");
                        concept = service.findRootConcept();
                    }

                    Collection<ILink> linkTemplates = Collections2.transform(service.findLinkTemplatesFor(concept),
                        new Function<LinkTemplate, ILink>() {

                        public ILink apply(LinkTemplate from) {
                            return (ILink) from;
                        }

                    });

                    model.addAll(linkTemplates);
                }
                catch (Exception e) {
                    EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
                }

                /*
                 * Set the selected link in the combobox. It should be the one that
                 * matches the association or 'nil' if no association was provided.
                 */

                if (!model.contains(defaultLink)) {
                    model.addElement(defaultLink);
                }

                comboBox.setSelectedItem(defaultLink);
                setLink(defaultLink);


            }
        });


    }
}
