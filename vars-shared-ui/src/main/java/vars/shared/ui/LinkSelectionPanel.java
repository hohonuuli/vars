/*
 * @(#)LinkSelectionPanel.java   2010.03.10 at 10:35:38 PST
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



package vars.shared.ui;

import com.google.inject.Inject;
import foxtrot.Task;
import foxtrot.Worker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import org.bushe.swing.event.EventBus;
import org.mbari.swing.LabeledSpinningDialWaitIndicator;
import org.mbari.swing.SearchableComboBoxModel;
import org.mbari.swing.SortedComboBoxModel;
import org.mbari.swing.WaitIndicator;
import org.mbari.text.ObjectToStringConverter;
import vars.ILink;
import vars.LinkBean;
import vars.LinkComparator;
import vars.LinkUtilities;
import vars.annotation.AnnotationPersistenceService;
import vars.knowledgebase.Concept;

/**
 *
 *
 * @version        Enter version here..., 2010.03.10 at 10:35:38 PST
 * @author         Brian Schlining [brian@mbari.org]
 */
public class LinkSelectionPanel extends JPanel {

    private final ILink DEFAULT_LINK = new LinkBean(ILink.VALUE_NIL, ILink.VALUE_NIL, ILink.VALUE_NIL);
    private final AnnotationPersistenceService annotationPersistenceService;
    private JLabel lblLinkName;
    private JLabel lblLinkValue;
    private JLabel lblSearchFor;
    private JLabel lblToConcept;
    private JTextField linkNameTextField;
    private JTextField linkValueTextField;
    private JComboBox linksComboBox;
    private JTextField searchTextField;
    private HierachicalConceptNameComboBox toConceptComboBox;

    /**
     * Create the dialog.
     *
     * @param annotationPersistenceService
     */
    @Inject
    public LinkSelectionPanel(AnnotationPersistenceService annotationPersistenceService) {
        this.annotationPersistenceService = annotationPersistenceService;
        initialize();
    }

    private JLabel getLblLinkName() {
        if (lblLinkName == null) {
            lblLinkName = new JLabel("Link Name");
        }

        return lblLinkName;
    }

    private JLabel getLblLinkValue() {
        if (lblLinkValue == null) {
            lblLinkValue = new JLabel("Link Value");
        }

        return lblLinkValue;
    }

    private JLabel getLblSearchFor() {
        if (lblSearchFor == null) {
            lblSearchFor = new JLabel("Search For");
        }

        return lblSearchFor;
    }

    private JLabel getLblToConcept() {
        if (lblToConcept == null) {
            lblToConcept = new JLabel("To Concept");
        }

        return lblToConcept;
    }


    public JTextField getLinkNameTextField() {
        if (linkNameTextField == null) {
            linkNameTextField = new JTextField();
            linkNameTextField.setEditable(false);
            linkNameTextField.setColumns(10);
        }

        return linkNameTextField;
    }

    public JTextField getLinkValueTextField() {
        if (linkValueTextField == null) {
            linkValueTextField = new JTextField();
            linkValueTextField.setColumns(10);
        }

        return linkValueTextField;
    }

    private JComboBox getLinksComboBox() {
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

    public HierachicalConceptNameComboBox getToConceptComboBox() {
        if (toConceptComboBox == null) {
            toConceptComboBox = new HierachicalConceptNameComboBox(annotationPersistenceService);
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

    protected void initialize() {
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
                                                .addComponent(getSearchTextField(), GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE))
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(getLblLinkName())
                                                        .addComponent(getLblToConcept())
                                                        .addComponent(getLblLinkValue()))
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(getToConceptComboBox(), 0, 360, Short.MAX_VALUE)
                                                        .addComponent(getLinkNameTextField(), GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
                                                        .addComponent(getLinkValueTextField(), Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE))))
                                .addContainerGap())
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
                                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(getLblLinkName())
                                        .addComponent(getLinkNameTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(getLblToConcept())
                                        .addComponent(getToConceptComboBox(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(getLblLinkValue())
                                        .addComponent(getLinkValueTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(70, Short.MAX_VALUE))
        );
        setLayout(groupLayout);
        setLinks(new ArrayList<ILink>());
        resetDisplay();
    }

    /**
     */
    public void resetDisplay() {
        getSearchTextField().setText("");
        setLink(DEFAULT_LINK);
    }

    /**
     *
     * @param enabled
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        getToConceptComboBox().setEnabled(enabled);
        getLinkNameTextField().setEnabled(enabled);
        getLinkValueTextField().setEnabled(enabled);
        getLinksComboBox().setEnabled(enabled);
    }

    /**
     * Set the selected link
     * @param link
     */
    public void setLink(final ILink link) {
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
                        return annotationPersistenceService.findConceptByName(link.getToConcept());
                    }

                });
                comboBox.setConcept(c);
                conceptName = c.getPrimaryConceptName().getName();

            }
            catch (final Exception e) {
                EventBus.publish(GlobalStateLookup.TOPIC_NONFATAL_ERROR, e);
                comboBox.addItem(conceptName);
            }
        }

        comboBox.setSelectedItem(conceptName);
        waitIndicator.dispose();
        repaint();
    }

    /**
     *
     * @param links
     */
    public void setLinks(Collection<ILink> links) {
        JComboBox comboBox = getLinksComboBox();
        SearchableComboBoxModel<ILink> model = (SearchableComboBoxModel<ILink>) comboBox.getModel();
        model.clear();
        model.addAll(links);

        if (!model.contains(DEFAULT_LINK)) {
            model.addElement(DEFAULT_LINK);
        }

        comboBox.setSelectedItem(DEFAULT_LINK);
        setLink(DEFAULT_LINK);

        // trigger redraw
        repaint();
    }

    /**
     * Returns a new {@link ILink} object from the fields set in the
     * UI
     * @return
     */
    public ILink getLink() {
        String linkName = getLinkNameTextField().getText();
        String toConcept = (String) getToConceptComboBox().getSelectedItem();
        String linkValue = getLinkValueTextField().getText();
        return new LinkBean(linkName, toConcept, linkValue);
    }
}
