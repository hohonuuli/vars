/*
 * @(#)VideoArchiveSetEditorPanel.java   2010.03.04 at 07:21:20 PST
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



package vars.annotation.ui.videoset;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import org.mbari.swing.LabeledSpinningDialWaitIndicator;
import org.mbari.swing.WaitIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.VideoArchiveSet;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.table.JXObservationTable;

/**
 *
 *
 * @version        Enter version here..., 2010.03.04 at 07:21:20 PST
 * @author         Brian Schlining [brian@mbari.org]
 */
public class VideoArchiveSetEditorPanel extends JPanel {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private JPanel actionPanel;
    private JComboBox associationComboBox;
    private JButton btnAddAssociation;
    private JButton btnDelete;
    private JButton btnMoveFrames;
    private JButton btnRefresh;
    private JButton btnRemoveAssociations;
    private JButton btnRenameConcepts;
    private JButton btnReplaceAssociations;
    private JButton btnSearch;
    private JCheckBox chckbxAssociation;
    private JCheckBox chckbxConcept;
    private JComboBox conceptComboBox;
    private final VideoArchiveSetEditorPanelController controller;
    private JPanel controlsPanel;
    private JPanel innerPanel;
    private JScrollPane scrollPane;
    private JPanel searchPanel;
    private JXObservationTable table;
    private JToolBar toolBar;
    private ToolBelt toolBelt;
    private volatile VideoArchiveSet videoArchiveSet;

    /**
     * Create the frame.
     */
    public VideoArchiveSetEditorPanel() {
        initialize();
        controller = new VideoArchiveSetEditorPanelController(this, toolBelt);
    }

    private JPanel getActionPanel() {
        if (actionPanel == null) {
            actionPanel = new JPanel();
            actionPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        }

        return actionPanel;
    }

    private JComboBox getAssociationComboBox() {
        if (associationComboBox == null) {
            associationComboBox = new JComboBox();
        }

        return associationComboBox;
    }

    private JButton getBtnAddAssociation() {
        if (btnAddAssociation == null) {
            btnAddAssociation = new JButton("Add Association");
        }

        return btnAddAssociation;
    }

    private JButton getBtnDelete() {
        if (btnDelete == null) {
            btnDelete = new JButton("Delete");
            btnDelete.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    controller.delete();
                }
            });
        }

        return btnDelete;
    }

    private JButton getBtnMoveFrames() {
        if (btnMoveFrames == null) {
            btnMoveFrames = new JButton("Move Frames");
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
            btnRefresh = new JButton("Refresh");
        }

        return btnRefresh;
    }

    private JButton getBtnRemoveAssociations() {
        if (btnRemoveAssociations == null) {
            btnRemoveAssociations = new JButton("Remove Associations");
        }

        return btnRemoveAssociations;
    }

    private JButton getBtnRenameConcepts() {
        if (btnRenameConcepts == null) {
            btnRenameConcepts = new JButton("Rename Concepts");
        }

        return btnRenameConcepts;
    }

    private JButton getBtnReplaceAssociations() {
        if (btnReplaceAssociations == null) {
            btnReplaceAssociations = new JButton("Replace Association");
        }

        return btnReplaceAssociations;
    }

    private JButton getBtnSearch() {
        if (btnSearch == null) {
            btnSearch = new JButton("Search");
        }

        return btnSearch;
    }

    private JCheckBox getChckbxAssociation() {
        if (chckbxAssociation == null) {
            chckbxAssociation = new JCheckBox("Association");
        }

        return chckbxAssociation;
    }

    private JCheckBox getChckbxConcept() {
        if (chckbxConcept == null) {
            chckbxConcept = new JCheckBox("Concept");
        }

        return chckbxConcept;
    }

    private JComboBox getConceptComboBox() {
        if (conceptComboBox == null) {
            conceptComboBox = new JComboBox();
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

    private JPanel getInnerPanel() {
        if (innerPanel == null) {
            innerPanel = new JPanel();
            innerPanel.setLayout(new BorderLayout(0, 0));
            innerPanel.add(getScrollPane(), BorderLayout.CENTER);
            innerPanel.add(getControlsPanel(), BorderLayout.NORTH);
        }

        return innerPanel;
    }

    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane();
            scrollPane.setViewportView(getTable());
        }

        return scrollPane;
    }

    private JPanel getSearchPanel() {
        if (searchPanel == null) {
                searchPanel = new JPanel();
                GroupLayout groupLayout = new GroupLayout(searchPanel);
                groupLayout.setHorizontalGroup(
                        groupLayout.createParallelGroup(Alignment.LEADING)
                                .addGroup(groupLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                                .addGroup(groupLayout.createSequentialGroup()
                                                        .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                                                .addComponent(getChckbxAssociation())
                                                                .addComponent(getChckbxConcept()))
                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                        .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                                                .addComponent(getConceptComboBox(), 0, 309, Short.MAX_VALUE)
                                                                .addComponent(getAssociationComboBox(), 0, 309, Short.MAX_VALUE)))
                                                .addComponent(getBtnSearch(), Alignment.TRAILING))
                                        .addContainerGap())
                );
                groupLayout.setVerticalGroup(
                        groupLayout.createParallelGroup(Alignment.TRAILING)
                                .addGroup(groupLayout.createSequentialGroup()
                                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                                .addComponent(getChckbxConcept())
                                                .addComponent(getConceptComboBox(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                                .addComponent(getChckbxAssociation())
                                                .addComponent(getAssociationComboBox(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(getBtnSearch()))
                );
                searchPanel.setLayout(groupLayout);
        }
        return searchPanel;
    }

    protected JXObservationTable getTable() {
        if (table == null) {
            table = new JXObservationTable();
        }

        return table;
    }

    private JToolBar getToolBar() {
        if (toolBar == null) {
            toolBar = new JToolBar();
            toolBar.add(getBtnRefresh());
            toolBar.add(getBtnDelete());
            toolBar.add(getBtnMoveFrames());
            toolBar.add(getBtnRenameConcepts());
            toolBar.add(getBtnAddAssociation());
            toolBar.add(getBtnRemoveAssociations());
            toolBar.add(getBtnReplaceAssociations());
        }

        return toolBar;
    }

    /**
     * @return
     */
    public synchronized VideoArchiveSet getVideoArchiveSet() {
        return videoArchiveSet;
    }

    private void initialize() {
        addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                populateTable();
            }

        });
        setLayout(new BorderLayout(0, 0));
        add(getInnerPanel(), BorderLayout.CENTER);
        add(getToolBar(), BorderLayout.NORTH);
    }

    private void populateTable() {
        WaitIndicator waitIndicator = new LabeledSpinningDialWaitIndicator(this, "Refreshing ...");
        synchronized (this) {
            controller.refresh();
        }
        waitIndicator.dispose();
    }

    /**
     *
     * @param videoArchiveSet
     */
    public synchronized void setVideoArchiveSet(VideoArchiveSet videoArchiveSet) {
        this.videoArchiveSet = videoArchiveSet;
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                getTable().setSelectedObservation(null);
                populateTable();
            }

        });
    }


}
