/*
 * @(#)VideoArchiveSetEditorPanelController.java   2010.03.04 at 07:22:03 PST
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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import foxtrot.Job;
import foxtrot.Worker;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import javax.swing.JOptionPane;
import org.mbari.swing.LabeledSpinningDialWaitIndicator;
import org.mbari.swing.SpinningDialWaitIndicator;
import org.mbari.swing.WaitIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.DAO;
import vars.ILink;
import vars.annotation.Association;
import vars.annotation.Observation;
import vars.annotation.VideoArchiveSet;
import vars.annotation.VideoArchiveSetDAO;
import vars.annotation.VideoFrame;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.actions.MoveVideoFrameWithDialogAction;
import vars.annotation.ui.table.JXObservationTable;
import vars.annotation.ui.table.ObservationTableModel;
import vars.shared.ui.LinkSelectionPanel;
import vars.shared.ui.dialogs.StandardDialog;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Mar 2, 2010
 * Time: 5:24:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class VideoArchiveSetEditorPanelController {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final VideoArchiveSetEditorPanel panel;
    private final ToolBelt toolBelt;
    private final MoveVideoFrameWithDialogAction moveAction;
    private AssociationSelectionDialog associationSelectionDialog;
    private AssociationSelectionDialog removeAssociationsDialog;

    /**
     * Constructs ...
     *
     * @param panel
     * @param toolBelt
     */
    public VideoArchiveSetEditorPanelController(VideoArchiveSetEditorPanel panel, ToolBelt toolBelt) {
        this.panel = panel;
        this.toolBelt = toolBelt;
        Frame frame = (Frame) Lookup.getApplicationFrameDispatcher().getValueObject();
        this.moveAction = new MoveVideoFrameWithDialogAction(frame, toolBelt);
        this.associationSelectionDialog = new AssociationSelectionDialog();
    }

    protected void addAssociation() {}

    protected void delete() {

        Collection<Observation> observations = getObservations(true);
        final int count = observations.size();

        if (observations.size() < 1) {
            return;
        }

        final Object[] options = { "OK", "CANCEL" };
        final int confirm = JOptionPane.showOptionDialog((Frame) Lookup.getApplicationFrameDispatcher().getValueObject(),
                                "Do you want to delete " + count + " observation(s)?", "VARS - Confirm Delete",
                                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        if (confirm == JOptionPane.YES_OPTION) {
            DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
            dao.startTransaction();
            for (Observation observation : observations) {
                observation = dao.find(observation);
                observation.getVideoFrame().removeObservation(observation);
                dao.remove(observation);
            }
            dao.endTransaction();
            refresh();
            toolBelt.getPersistenceController().updateUI();
        }

    }

    protected void moveObservations() {
        Collection<VideoFrame> videoFrames = getVideoFrames(true);
        moveAction.setVideoFrames(videoFrames);
        moveAction.doAction();
        refresh();
        toolBelt.getPersistenceController().updateUI();
    }

    protected void selectObservations(Collection<Observation> observations) {
        JXObservationTable myTable = panel.getTable();
        ObservationTableModel tableModel = (ObservationTableModel) myTable.getModel();
        Collection<Observation> allObservations = getObservations(false);
        allObservations.retainAll(observations);
        myTable.setSelectedObservations(allObservations);
    }

    /**
     *
     */
    protected void refresh() {

        /*
         * All observations that get added to the table, or that are currently
         * in the table get added to this set and removed from the
         * observationsInTable set. Then, observations left in the
         * observationsInTable set are removed from the table and the
         * observationsInTable variable is set to reference the new
         * observationsStillInTable set. (this is the most efficient thing I
         * could think of).
         */
        Collection<Observation> selectedObservations = getObservations(true);
        VideoArchiveSet videoArchiveSet = panel.getVideoArchiveSet();

        JXObservationTable myTable = panel.getTable();
        ObservationTableModel tableModel = (ObservationTableModel) myTable.getModel();
        tableModel.clear();

        VideoArchiveSetDAO dao = toolBelt.getAnnotationDAOFactory().newVideoArchiveSetDAO();

        if (videoArchiveSet != null) {
            if (log.isDebugEnabled()) {
                log.debug("Retrieving all video frames for " + videoArchiveSet);
            }

            // DAOTX - Delete associations
            dao.startTransaction();
            videoArchiveSet = dao.find(videoArchiveSet);    // Bring it into the transaction
            final Collection<VideoFrame> videoFrames = ImmutableList.copyOf(videoArchiveSet.getVideoFrames());
            for (VideoFrame videoFrame : videoFrames) {
                final Collection<Observation> observations = ImmutableList.copyOf(videoFrame.getObservations());
                for (Observation observation : observations) {
                    myTable.addObservation(observation);
                }
            }
            dao.endTransaction();

        }

        myTable.setSelectedObservations(selectedObservations);

    }

    protected void removeAssociations() {
        // Show dialog with links to all associations in selected rows.
        Collection<Observation> observations = getObservations(true);
        Collection<Association> associations = new HashSet<Association>();
        for (Observation observation : observations) {
            associations.addAll(observation.getAssociations());
        }
        Collection<ILink> links = Collections2.transform(associations, new Function<Association, ILink>(){
            public ILink apply(Association from) {
                return (ILink) from;
            }
        });

        AssociationSelectionDialog dialog = getRemoveAssociationsDialog();
        dialog.setLinks(links);
        dialog.setVisible(true);
    }

    protected void renameAssociations() {
        

    }

    protected void search() {}

    protected Collection<Observation> getObservations(boolean useSelectedOnly) {
        Collection<Observation> observations = new ArrayList<Observation>();

        JXObservationTable myTable = panel.getTable();
        ObservationTableModel model = (ObservationTableModel) myTable.getModel();
        if (useSelectedOnly) {
            int[] rows = myTable.getSelectedRows();
            for (int i : rows) {
                observations.add(model.getObservationAt(i));
            }
        }
        else {
            int n = model.getNumberOfObservations();
            for(int i = 0; i < n; i++) {
                observations.add(model.getObservationAt(i));
            }
        }
        return observations;

    }

    protected Collection<VideoFrame> getVideoFrames(boolean useSelectedOnly) {
        Collection<VideoFrame> videoFrames = new HashSet<VideoFrame>();
        for (Observation observation : getObservations(useSelectedOnly)) {
            videoFrames.add(observation.getVideoFrame());
        }
        return videoFrames;
    }

    /**
     *
     * @param useSelectedOnly if True only associations from selected rows will be
     *      returned. If false, than all associations are returned
     * @return
     */
    protected Collection<Association> getAssociations(boolean useSelectedOnly) {
        Collection<Association> associations = new ArrayList<Association>();
        for (Observation observation : getObservations(useSelectedOnly)) {
            associations.addAll(observation.getAssociations());
        }
        return associations;
    }

    /**
     *
     * @param useSelectedOnly if True only associations from selected rows will be
     *      returned. If false, than all associations are returned
     * @param matcher A Link used as a matching predicate. Only associations
     *      that match the linkName, toConcept and linkValue exactly are returned
     *      note that ILink.VALUE_NIL is a wild card and fields in the matcher
     *      with that value will not be used as predicates.
     * @return
     */
    protected Collection<Association> getFilteredAssocations(boolean useSelectedOnly, final ILink matcher) {
        return Collections2.filter(getAssociations(useSelectedOnly), new Predicate<Association>() {

            public boolean apply(Association input) {
                boolean match = true;
                if (!matcher.getLinkName().equalsIgnoreCase(ILink.VALUE_NIL)) {
                    match = input.getLinkName().equals(matcher.getLinkName());
                }

                if (match && !matcher.getToConcept().equalsIgnoreCase(ILink.VALUE_NIL)) {
                    match = input.getToConcept().equals(matcher.getToConcept());
                }

                if (match && !matcher.getLinkValue().equalsIgnoreCase(ILink.VALUE_NIL)) {
                    match = input.getLinkValue().equals(matcher.getLinkValue());
                }

                return match;
            }

        });
    }

    private AssociationSelectionDialog getRemoveAssociationsDialog() {
        if (removeAssociationsDialog == null) {
            removeAssociationsDialog = new AssociationSelectionDialog();

            /*
             * Define the Action that occurs when you delete. What happens is
             * 1) Find all associations that match the link you select in the
             * dialog
             * 2) Confirm that we want to delete those associations
             * 3)
             */
            removeAssociationsDialog.getOkayButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ILink link = removeAssociationsDialog.getLink();
                    final Collection<Association> associations = getFilteredAssocations(true, link);

                    final Object[] options = { "OK", "CANCEL" };
                    final int confirm = JOptionPane.showOptionDialog(
                            (Frame) Lookup.getApplicationFrameDispatcher().getValueObject(),
                            "Do you want to delete " + associations.size() + " association(s)?",
                            "VARS - Confirm Delete",
                             JOptionPane.DEFAULT_OPTION,
                             JOptionPane.WARNING_MESSAGE, null, options, options[0]);

                    if (confirm == JOptionPane.YES_OPTION) {
                        WaitIndicator waitIndicator = new LabeledSpinningDialWaitIndicator(panel, "Deleting Associations ...");
                        Worker.post(new Job() {
                            @Override
                            public Object run() {
                                DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
                                dao.startTransaction();
                                for (Association association : associations) {
                                    association = dao.find(association);
                                    if (association != null) {
                                        association.getObservation().removeAssociation(association);
                                        dao.remove(association);
                                    }
                                }
                                dao.endTransaction();
                                return null;
                            }
                        });
                        waitIndicator.dispose();
                    }


                    removeAssociationsDialog.dispose();
                }
            });
            removeAssociationsDialog.getCancelButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    removeAssociationsDialog.dispose();
                }
            });
        }
        return removeAssociationsDialog;
    }




    private class AssociationSelectionDialog extends StandardDialog {

        private LinkSelectionPanel panel;

        public AssociationSelectionDialog() {
            super((Frame) Lookup.getApplicationFrameDispatcher().getValueObject(),
                    "VARS - Select Association", false);
            panel = new LinkSelectionPanel(toolBelt.getAnnotationPersistenceService());
            add(panel, BorderLayout.CENTER);
        }

        public void allowEditing(boolean b) {
            panel.getLinkNameTextField().setEditable(b);
            panel.getLinkValueTextField().setEditable(b);
            panel.getToConceptComboBox().setEnabled(b);
        }


        public void setLinks(Collection<ILink> links) {
            panel.setLinks(links);
        }

        public ILink getLink() {
            return panel.getLink();
        }
    }

}
