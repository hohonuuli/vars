/*
 * @(#)VideoArchiveEditorPanel.java   2013.10.02 at 04:24:54 PDT
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



package vars.annotation.ui.ppanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.LayoutStyle;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import mbarix4j.swing.SearchableComboBoxModel;
import mbarix4j.text.ObjectToStringConverter;
import vars.ILink;
import vars.LinkBean;
import vars.LinkComparator;
import vars.LinkUtilities;
import vars.annotation.CameraDirections;
import vars.annotation.ui.CameraDirectionComboBox;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.eventbus.ObservationsAddedEvent;
import vars.annotation.ui.eventbus.ObservationsChangedEvent;
import vars.annotation.ui.eventbus.ObservationsRemovedEvent;
import vars.annotation.ui.eventbus.ObservationsSelectedEvent;
import vars.annotation.ui.eventbus.UIEventSubscriber;
import vars.annotation.ui.eventbus.VideoArchiveChangedEvent;
import vars.annotation.ui.eventbus.VideoArchiveSelectedEvent;
import vars.annotation.ui.eventbus.VideoFramesChangedEvent;
import vars.shared.ui.ConceptNameComboBox;
import vars.shared.ui.LinkListCellRenderer;

/**
 * @author Brian Schlining
 * @since 2013-10-02
 */
public class VideoArchiveEditorPanel extends JPanel implements UIEventSubscriber {

    private JPanel actionPanel;
    private JComboBox associationComboBox;
    private JButton btnAddAssociation;
    private JButton btnDelete;
    private JButton btnMoveFrames;
    private JButton btnRefresh;
    private ImageIcon needsRefreshIcon = new ImageIcon(getClass().getResource("/images/vars/annotation/refresh-red.png"));
    private ImageIcon noRefreshIcon = new ImageIcon(getClass().getResource("/images/vars/annotation/refresh-green.png"));
    private JButton btnRemoveAssociations;
    private JButton btnRenameConcepts;
    private JButton btnReplaceAssociations;
    private JButton btnSearch;
    private JComboBox cameraDirectionCB;
    private JCheckBox chckbxAssociation;
    private JCheckBox chckbxConcept;
    private ConceptNameComboBox conceptComboBox;
    private final VideoArchivePanelController controller;
    private JPanel controlsPanel;
    private JPanel searchPanel;
    private JToolBar toolBar;
    private JLabel refreshLabel = new JLabel("                                                                    ");

    /**
     * Constructs ...
     *
     * @param toolBelt
     */
    public VideoArchiveEditorPanel(ToolBelt toolBelt) {
        initialize();
        refreshLabel.setForeground(Color.RED);
        controller = new VideoArchivePanelController(this, toolBelt);
        AnnotationProcessor.process(this);
    }

    private JPanel getActionPanel() {
        if (actionPanel == null) {
            actionPanel = new JPanel();
            actionPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        }

        return actionPanel;
    }

    /**
     * @return
     */
    public JComboBox getAssociationComboBox() {
        if (associationComboBox == null) {
            associationComboBox = new JComboBox();
            associationComboBox.setRenderer(new LinkListCellRenderer());
            SearchableComboBoxModel<ILink> model = new SearchableComboBoxModel<ILink>(new LinkComparator(),
                    new ObjectToStringConverter<ILink>() {

                        public String convert(ILink object) {
                            return LinkUtilities.formatAsString(object);
                        }

                    });
            associationComboBox.setModel(model);
            ILink link = new LinkBean(ILink.VALUE_NIL, ILink.VALUE_NIL, ILink.VALUE_NIL);
            model.addElement(link);
            associationComboBox.setSelectedItem(link);
        }

        return associationComboBox;
    }

    private JButton getBtnAddAssociation() {
        if (btnAddAssociation == null) {
            btnAddAssociation = new JButton("");
            btnAddAssociation.setToolTipText("Add Association");
            btnAddAssociation.setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/branch_add.png")));
            btnAddAssociation.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    controller.addAssociation();
                }

            });
        }

        return btnAddAssociation;
    }

    private JButton getBtnDelete() {
        if (btnDelete == null) {
            btnDelete = new JButton("");
            btnDelete.setToolTipText("Delete Observations");
            btnDelete.setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/row_delete.png")));
            btnDelete.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    controller.deleteObservations();
                }

            });
        }

        return btnDelete;
    }

    private JButton getBtnMoveFrames() {
        if (btnMoveFrames == null) {
            btnMoveFrames = new JButton("");
            btnMoveFrames.setToolTipText("Move Frames");
            btnMoveFrames.setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/row_replace.png")));
            btnMoveFrames.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    controller.moveObservations();
                }

            });
        }

        return btnMoveFrames;
    }

    private JButton getBtnRefresh() {
        if (btnRefresh == null) {
            btnRefresh = new JButton("");
            btnRefresh.setToolTipText("Refresh");
            btnRefresh.setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/refresh.png")));
            btnRefresh.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    refresh();
                }

            });
        }

        return btnRefresh;
    }

    private JButton getBtnRemoveAssociations() {
        if (btnRemoveAssociations == null) {
            btnRemoveAssociations = new JButton("");
            btnRemoveAssociations.setToolTipText("Remove Associations");
            btnRemoveAssociations.setIcon(
                    new ImageIcon(getClass().getResource("/images/vars/annotation/branch_delete.png")));
            btnRemoveAssociations.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    controller.removeAssociations();
                }

            });
        }

        return btnRemoveAssociations;
    }

    private JButton getBtnRenameConcepts() {
        if (btnRenameConcepts == null) {
            btnRenameConcepts = new JButton("");
            btnRenameConcepts.setToolTipText("Rename Observations");
            btnRenameConcepts.setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/row_edit.png")));
            btnRenameConcepts.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    controller.renameObservations();
                }
            });
        }

        return btnRenameConcepts;
    }

    private JButton getBtnReplaceAssociations() {
        if (btnReplaceAssociations == null) {
            btnReplaceAssociations = new JButton("");
            btnReplaceAssociations.setToolTipText("Replace Associations");
            btnReplaceAssociations.setIcon(
                    new ImageIcon(getClass().getResource("/images/vars/annotation/branch_edit.png")));
            btnReplaceAssociations.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    controller.renameAssociations();
                }

            });
        }

        return btnReplaceAssociations;
    }

    private JButton getBtnSearch() {
        if (btnSearch == null) {
            btnSearch = new JButton("Search");
            btnSearch.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    controller.search();
                }

            });
        }

        return btnSearch;
    }

    private JComboBox getCameraDirectionCB() {
        if (cameraDirectionCB == null) {
            cameraDirectionCB = new CameraDirectionComboBox();
            cameraDirectionCB.setToolTipText("Change the camera direction of the selected video-frames");
            cameraDirectionCB.addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        controller.changeCameraDirectionsTo((CameraDirections) cameraDirectionCB.getSelectedItem());
                    }
                }

            });
        }

        return cameraDirectionCB;
    }

    protected JCheckBox getChckbxAssociation() {
        if (chckbxAssociation == null) {
            chckbxAssociation = new JCheckBox("Association");
        }

        return chckbxAssociation;
    }

    protected JCheckBox getChckbxConcept() {
        if (chckbxConcept == null) {
            chckbxConcept = new JCheckBox("Concept");
        }

        return chckbxConcept;
    }

    protected ConceptNameComboBox getConceptComboBox() {
        if (conceptComboBox == null) {
            conceptComboBox = new ConceptNameComboBox();
        }

        return conceptComboBox;
    }

    private JPanel getControlsPanel() {
        if (controlsPanel == null) {
            controlsPanel = new JPanel();
            controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.X_AXIS));
            controlsPanel.add(getSearchPanel());
            controlsPanel.add(getActionPanel());
        }

        return controlsPanel;
    }

    private JPanel getSearchPanel() {
        if (searchPanel == null) {
            searchPanel = new JPanel();
            GroupLayout gl_searchPanel = new GroupLayout(searchPanel);
            gl_searchPanel
                    .setHorizontalGroup(gl_searchPanel.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(gl_searchPanel.createSequentialGroup().addContainerGap()
                                    .addGroup(gl_searchPanel.createParallelGroup(GroupLayout.Alignment.LEADING)
                                            .addGroup(gl_searchPanel.createSequentialGroup()
                                                    .addGroup(gl_searchPanel.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                            .addComponent(getChckbxAssociation()).addComponent(getChckbxConcept()))
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(gl_searchPanel.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                            .addComponent(getConceptComboBox(), 0, 309, Short.MAX_VALUE)
                                                            .addComponent(getAssociationComboBox(), 0, 309, Short.MAX_VALUE)))
                                            .addComponent(getBtnSearch(), GroupLayout.Alignment.TRAILING))
                                    .addContainerGap()));
            gl_searchPanel
                    .setVerticalGroup(gl_searchPanel.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addGroup(gl_searchPanel.createSequentialGroup()
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(gl_searchPanel.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(getChckbxConcept())
                                            .addComponent(getConceptComboBox(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                                    GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(gl_searchPanel
                                            .createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(getChckbxAssociation())
                                            .addComponent(getAssociationComboBox(),
                                                    GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                                    GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement
                                            .RELATED).addComponent(getBtnSearch())));
            searchPanel.setLayout(gl_searchPanel);
        }

        return searchPanel;
    }

    private JToolBar getToolBar() {
        if (toolBar == null) {
            toolBar = new JToolBar();
            toolBar.add(getBtnRefresh());
            toolBar.add(getBtnMoveFrames());
            toolBar.add(getBtnRenameConcepts());
            toolBar.add(getBtnDelete());
            toolBar.add(getBtnAddAssociation());
            toolBar.add(getBtnReplaceAssociations());
            toolBar.add(getBtnRemoveAssociations());
            toolBar.add(getCameraDirectionCB());
        }

        return toolBar;
    }

    protected void initialize() {
        setLayout(new BorderLayout(0, 0));
        add(getToolBar(), BorderLayout.NORTH);
        add(getControlsPanel(), BorderLayout.CENTER);
        add(refreshLabel, BorderLayout.SOUTH);
        addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                refresh();
            }

        });
    }

    /**
     *
     * @param event
     */
    @EventSubscriber(eventClass = ObservationsAddedEvent.class)
    @Override
    public void respondTo(ObservationsAddedEvent event) {
        needsRefresh();

    }

    /**
     *
     * @param event
     */
    @EventSubscriber(eventClass = ObservationsChangedEvent.class)
    @Override
    public void respondTo(ObservationsChangedEvent event) {
        needsRefresh();
    }

    /**
     *
     * @param event
     */
    @EventSubscriber(eventClass = ObservationsRemovedEvent.class)
    @Override
    public void respondTo(ObservationsRemovedEvent event) {
        needsRefresh();
    }

    /**
     *
     * @param event
     */
    @EventSubscriber(eventClass = VideoArchiveChangedEvent.class)
    @Override
    public void respondTo(VideoArchiveChangedEvent event) {
        refresh();
    }

    /**
     *
     * @param event
     */
    @EventSubscriber(eventClass = VideoArchiveSelectedEvent.class)
    @Override
    public void respondTo(VideoArchiveSelectedEvent event) {
        refresh();
    }

    /**
     *
     * @param event
     */
    @EventSubscriber(eventClass = VideoFramesChangedEvent.class)
    @Override
    public void respondTo(VideoFramesChangedEvent event) {
        needsRefresh();
    }

    private void refresh() {
        controller.refresh();
        getBtnRefresh().setIcon(noRefreshIcon);
        refreshLabel.setText("                                                                    ");
    }

    private void needsRefresh() {
        getBtnRefresh().setIcon(needsRefreshIcon);
        refreshLabel.setText("Press the refresh button (in red) to synchronize with latest edits!!");
    }

    @Override
    public void respondTo(ObservationsSelectedEvent event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
