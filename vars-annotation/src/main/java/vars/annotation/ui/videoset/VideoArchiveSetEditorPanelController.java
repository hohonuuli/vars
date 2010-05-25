/*
 * @(#)VideoArchiveSetEditorPanelController.java   2010.03.11 at 04:16:47 PST
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
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.mbari.awt.AwtUtilities;
import org.mbari.swing.LabeledSpinningDialWaitIndicator;
import org.mbari.swing.SearchableComboBoxModel;
import org.mbari.swing.WaitIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.DAO;
import vars.ILink;
import vars.LinkComparator;
import vars.LinkUtilities;
import vars.annotation.AnnotationFactory;
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
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.LinkTemplateDAO;
import vars.shared.ui.LinkSelectionPanel;
import vars.shared.ui.dialogs.ConceptNameSelectionDialog;
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
    private AssociationSelectionDialog addAssociationDialog;
    private final MoveVideoFrameWithDialogAction moveAction;
    private final VideoArchiveSetEditorPanel panel;
    private AssociationSelectionDialog removeAssociationsDialog;
    private AssociationSelectionDialog renameAssociationsDialog;
    private ConceptNameSelectionDialog renameObservationsDialog;
    private final ToolBelt toolBelt;

    /**
     * Constructs ...
     *
     * @param panel
     * @param toolBelt
     */
    public VideoArchiveSetEditorPanelController(VideoArchiveSetEditorPanel panel, ToolBelt toolBelt) {
        this.panel = panel;
        this.toolBelt = toolBelt;
        this.moveAction = new MoveVideoFrameWithDialogAction(AwtUtilities.getFrame(panel), toolBelt);
    }

    protected void addAssociation() {
        Collection<ILink> linkTemplates = findLinkTemplatesForObservations(getObservations(true));

        // Show dialog with links to all associations in selected rows.
        AssociationSelectionDialog dialog = getAddAssociationDialog();
        dialog.setLinks(linkTemplates);
        dialog.setVisible(true);
    }

    protected void deleteObservations() {

        Collection<Observation> observations = getObservations(true);
        final int count = observations.size();

        if (observations.size() < 1) {
            return;
        }

        final Object[] options = { "OK", "CANCEL" };
        final int confirm = JOptionPane.showOptionDialog(
            (Frame) Lookup.getApplicationFrameDispatcher().getValueObject(),
            "Do you want to delete " + count + " observation(s)?", "VARS - Confirm Delete", JOptionPane.DEFAULT_OPTION,
            JOptionPane.WARNING_MESSAGE, null, options, options[0]);
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
        }

    }

    /**
     * Fetches a collection of [@link LinkTemplate}s from the database that
     * can be applied to the observations in the collection you provide.
     * @param observations
     * @return
     */
    private Collection<ILink> findLinkTemplatesForObservations(Collection<Observation> observations) {
        Collection<ILink> linkTemplates = new HashSet<ILink>();
        Collection<String> conceptNames = new ArrayList<String>();

        // The 2 DAO's share the same entityManger and thus the same transaction
        LinkTemplateDAO linkTemplateDAO = toolBelt.getKnowledgebaseDAOFactory().newLinkTemplateDAO();
        ConceptDAO conceptDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO(linkTemplateDAO.getEntityManager());
        linkTemplateDAO.startTransaction();

        // Aggregate the link templates that can be applied to the selected concepts
        for (Observation observation : observations) {
            if (!conceptNames.contains(observation.getConceptName())) {
                Concept concept = conceptDAO.findByName(observation.getConceptName());
                if (concept != null) {

                    // Search for the link templates that can be used by each concept
                    // in the annotations. We'll limit the selection to these. NOTE
                    // this isn't perfect since it allows applying linktemplates
                    // to concepts they don't belong to.
                    linkTemplates.addAll(linkTemplateDAO.findAllApplicableToConcept(concept));
                }
                else {
                    log.debug("Unable to find Concept named '" + observation.getConceptName() +
                              "' in the knowledgebase");
                }

                conceptNames.add(observation.getConceptName());
            }
        }

        linkTemplateDAO.endTransaction();

        return linkTemplates;
    }

    private AssociationSelectionDialog getAddAssociationDialog() {
        if (addAssociationDialog == null) {
            addAssociationDialog = new AssociationSelectionDialog();
            addAssociationDialog.setTitle("VARS - Add an Association");
            addAssociationDialog.getCancelButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    addAssociationDialog.dispose();
                }

            });
            addAssociationDialog.getOkayButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {

                    ILink link = addAssociationDialog.getLink();
                    Collection<Observation> observations = getObservations(true);
                    AnnotationFactory annotationFactory = toolBelt.getAnnotationFactory();
                    DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
                    dao.startTransaction();

                    for (Observation observation : observations) {
                        observation = dao.find(observation);

                        if (observation != null) {
                            Association association = annotationFactory.newAssociation(link.getLinkName(),
                                link.getToConcept(), link.getLinkValue());
                            observation.addAssociation(association);
                            dao.persist(association);
                        }
                    }

                    dao.endTransaction();
                    addAssociationDialog.dispose();
                    refresh();

                }

            });
        }

        return addAssociationDialog;
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
            for (int i = 0; i < n; i++) {
                observations.add(model.getObservationAt(i));
            }
        }

        return observations;

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
                        "Do you want to delete " + associations.size() + " association(s)?", "VARS - Confirm Delete",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);

                    if (confirm == JOptionPane.YES_OPTION) {
                        WaitIndicator waitIndicator = new LabeledSpinningDialWaitIndicator(panel,
                            "Deleting Associations ...");
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
                    refresh();
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

    private AssociationSelectionDialog getRenameAssociationsDialog() {
        if (renameAssociationsDialog == null) {
            renameAssociationsDialog = new AssociationSelectionDialog();
            renameAssociationsDialog.getCancelButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    renameAssociationsDialog.dispose();
                }

            });
            renameAssociationsDialog.getOkayButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {

                    // Get associations that match the one used for searching
                    ILink match = (ILink) panel.getAssociationComboBox().getSelectedItem();
                    final Collection<Association> associations = getFilteredAssocations(true, match);

                    // Update the values for each association in background thread
                    final ILink newLink = renameAssociationsDialog.getLink();
                    renameAssociationsDialog.dispose();
                    Worker.post(new Job() {

                        @Override
                        public Object run() {
                            DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
                            dao.startTransaction();

                            for (Association association : associations) {
                                association = dao.find(association);
                                association.setLinkName(newLink.getLinkName());
                                association.setToConcept(newLink.getToConcept());
                                association.setLinkValue(newLink.getLinkValue());
                            }

                            dao.endTransaction();
                            return null;
                        }
                    });

                    refresh();
                }

            });
        }

        return renameAssociationsDialog;
    }

    private ConceptNameSelectionDialog getRenameObservationsDialog() {
        if (renameObservationsDialog == null) {
            renameObservationsDialog = new ConceptNameSelectionDialog();
            renameObservationsDialog.getCancelButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    renameObservationsDialog.dispose();
                }
            });
            renameObservationsDialog.getOkayButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    final String name = renameObservationsDialog.getSelectedItem();
                    renameObservationsDialog.dispose();
                    final Collection<Observation> observations = getObservations(true);
                    Worker.post(new Job() {

                        @Override
                        public Object run() {
                            DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
                            dao.startTransaction();
                            for (Observation observation : observations) {
                                observation = dao.find(observation);
                                if (observation != null) {
                                    observation.setConceptName(name);
                                }
                            }
                            dao.endTransaction();
                            return null;
                        }
                    });

                    refresh();
                }
            });
            renameObservationsDialog.setItems(toolBelt.getQueryPersistenceService().findAllConceptNamesAsStrings());
        }
        return renameObservationsDialog;
    }

    protected Collection<VideoFrame> getVideoFrames(boolean useSelectedOnly) {
        Collection<VideoFrame> videoFrames = new HashSet<VideoFrame>();
        for (Observation observation : getObservations(useSelectedOnly)) {
            videoFrames.add(observation.getVideoFrame());
        }

        return videoFrames;
    }

    protected void moveObservations() {
        Collection<VideoFrame> videoFrames = getVideoFrames(true);
        moveAction.setVideoFrames(videoFrames);
        moveAction.doAction();
        refresh();
    }

    protected void renameObservations() {
        JDialog dialog = getRenameObservationsDialog();
        dialog.setVisible(true);
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
        WaitIndicator waitIndicator = new LabeledSpinningDialWaitIndicator(panel, "Refreshing ...");
        Collection<Observation> selectedObservations = getObservations(true);
        VideoArchiveSet videoArchiveSet = panel.getVideoArchiveSet();

        JXObservationTable myTable = panel.getTable();
        ObservationTableModel tableModel = (ObservationTableModel) myTable.getModel();
        tableModel.clear();

        if (videoArchiveSet != null) {
            if (log.isDebugEnabled()) {
                log.debug("Retrieving all video frames for " + videoArchiveSet);
            }


            // Fetch data in background thread
            final VideoArchiveSet vas = videoArchiveSet;
            Collection<VideoFrame> videoFrames = (Collection<VideoFrame>) Worker.post(new Job() {

                @Override
                public Object run() {
                    VideoArchiveSetDAO dao = toolBelt.getAnnotationDAOFactory().newVideoArchiveSetDAO();
                    dao.startTransaction();
                    VideoArchiveSet foundVas = dao.find(vas);    // Bring it into the transaction
                    final Collection<VideoFrame> videoFrames = (foundVas == null) ? new ArrayList<VideoFrame>() : ImmutableList.copyOf(foundVas.getVideoFrames());
                    dao.endTransaction();
                    return videoFrames;
                }
            });


            // repopulate the table and pull out parts need to set other UI components
            Collection<String> names = new HashSet<String>(); // for ConceptComboBox
            Collection<ILink> associations = new HashSet<ILink>(); // for associationComboBox
            for (VideoFrame videoFrame : videoFrames) {
                final Collection<Observation> observations = ImmutableList.copyOf(videoFrame.getObservations());
                for (Observation observation : observations) {
                    myTable.addObservation(observation);
                    names.add(observation.getConceptName());
                    associations.addAll(observation.getAssociations());
                }
            }

            // Update the conceptComboBox
            String[] namesArray = new String[names.size()];
            names.toArray(namesArray);
            panel.getConceptComboBox().updateModel(namesArray);

            // Populate the Association combobox
            SearchableComboBoxModel<ILink> model = (SearchableComboBoxModel<ILink>) panel.getAssociationComboBox().getModel();
            model.clear();
            model.addAll(associations);
        }

        myTable.setSelectedObservations(selectedObservations);

        toolBelt.getPersistenceController().updateUI();
        waitIndicator.dispose();

    }

    /**
     * Grabs all associations from the selected rows and adds them to a dialog.
     * The user selects the association to remove from the available list
     */
    protected void removeAssociations() {


        Collection<Observation> observations = getObservations(true);
        Collection<Association> associations = new HashSet<Association>();
        for (Observation observation : observations) {
            associations.addAll(observation.getAssociations());
        }

        Collection<ILink> links = Collections2.transform(associations, new Function<Association, ILink>() {

            public ILink apply(Association from) {
                return (ILink) from;
            }

        });

        // Show dialog with links to all associations in selected rows.
        AssociationSelectionDialog dialog = getRemoveAssociationsDialog();
        dialog.setTitle("VARS - Select association to delete");
        dialog.setLinks(links);
        dialog.setVisible(true);
    }

    protected void renameAssociations() {
        Collection<ILink> linkTemplates = findLinkTemplatesForObservations(getObservations(true));
 
        // Build dialog
        ILink link = (ILink) panel.getAssociationComboBox().getSelectedItem();
        AssociationSelectionDialog dialog = getRenameAssociationsDialog();
        dialog.setTitle("VARS - Replace " + LinkUtilities.formatAsString(link) + " with:");
        dialog.setLinks(linkTemplates);
        dialog.setVisible(true);
    }

    protected void search() {
        Collection<Observation> observations = getObservations(false);

        // Filter by concept
        if (panel.getChckbxConcept().isSelected()) {
            final String conceptName = (String) panel.getConceptComboBox().getSelectedItem();
            observations = Collections2.filter(observations, new Predicate<Observation>() {
                public boolean apply(Observation input) {
                    return input.getConceptName().equals(conceptName);
                }
            });
        }

        if (panel.getChckbxAssociation().isSelected()) {
            final ILink link = (ILink) panel.getAssociationComboBox().getSelectedItem();
            observations = Collections2.filter(observations, new Predicate<Observation>() {
                LinkComparator linkComparator = new LinkComparator();
                public boolean apply(Observation input) {
                    boolean ok = false;
                    for (Association association : input.getAssociations()) {
                        if (linkComparator.compare(link, association) == 0) {
                            ok = true;
                            break;
                        }
                    }
                    return ok;
                }

            });
        }
        selectObservations(observations);

    }

    /**
     * Selects the rows in the table for the observations provided.
     * @param observations
     */
    protected void selectObservations(Collection<Observation> observations) {
        JXObservationTable myTable = panel.getTable();
        Collection<Observation> allObservations = getObservations(false);
        allObservations.retainAll(observations);
        myTable.setSelectedObservations(allObservations);
    }

    /**
     * Internal Dialog for working selecting Associations
     */
    private class AssociationSelectionDialog extends StandardDialog {

        private LinkSelectionPanel linkSelectionPanel;

        /**
         * Constructs ...
         */
        public AssociationSelectionDialog() {
            super(AwtUtilities.getFrame(panel), "VARS - Select Association", true);
            linkSelectionPanel = new LinkSelectionPanel(toolBelt.getAnnotationPersistenceService());
            add(linkSelectionPanel, BorderLayout.CENTER);
            pack();
        }

        /**
         *
         * @param b
         */
        public void allowEditing(boolean b) {
            linkSelectionPanel.getLinkValueTextField().setEditable(b);
            linkSelectionPanel.getToConceptComboBox().setEnabled(b);
        }

        /**
         * @return
         */
        public ILink getLink() {
            return linkSelectionPanel.getLink();
        }

        /**
         *
         * @param links
         */
        public void setLinks(Collection<ILink> links) {
            linkSelectionPanel.setLinks(links);
        }
    }

}
