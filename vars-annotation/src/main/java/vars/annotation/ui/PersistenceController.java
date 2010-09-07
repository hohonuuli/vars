/*
 * @(#)PersistenceController.java   2009.12.12 at 09:31:16 PST
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



package vars.annotation.ui;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import org.mbari.net.URLUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.DAO;
import vars.ILink;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.AnnotationFactory;
import vars.annotation.Association;
import vars.annotation.AssociationDAO;
import vars.annotation.CameraData;
import vars.annotation.Observation;
import vars.annotation.ObservationDAO;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.annotation.VideoArchiveSet;
import vars.annotation.VideoFrame;
import vars.annotation.ui.roweditor.RowEditorPanel;
import vars.annotation.ui.table.ObservationTable;
import vars.annotation.ui.table.ObservationTableModel;
import vars.knowledgebase.ConceptDAO;

/**
 * PersistenceService manages database transactions for the user-interface. It will keep the
 * persistent objects AND the user interface in synch.
 *
 * @version        Enter version here..., 2009.11.16 at 10:49:40 PST
 * @author         Brian Schlining [brian@mbari.org]
 */
public class PersistenceController {

    private static final NumberFormat f0123 = new DecimalFormat("0000");
    private static final NumberFormat f01 = new DecimalFormat("00");
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final AnnotationDAOFactory annotationDAOFactory;
    private final AnnotationFactory annotationFactory;
    private final ToolBelt toolBelt;

    /**
     * Constructs ...
     *
     * @param toolBelt
     */
    public PersistenceController(ToolBelt toolBelt) {
        super();
        this.toolBelt = toolBelt;
        this.annotationDAOFactory = toolBelt.getAnnotationDAOFactory();
        this.annotationFactory = toolBelt.getAnnotationFactory();
    }

    /**
     *
     * @param observations
     * @return
     */
    public Collection<Observation> deleteAllAssociationsFrom(Collection<Observation> observations) {
        Collection<Observation> updateObservations = new ArrayList<Observation>();
        DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
        dao.startTransaction();

        for (Observation observation : observations) {
            observation = dao.find(observation);

            if (observation != null) {

                for (Association association : new ArrayList<Association>(observation.getAssociations())) {
                    observation.removeAssociation(association);
                    dao.remove(association);
                }

                updateObservations.add(observation);
            }
        }

        updateUI();
        return updateObservations;
    }

    /**
     *
     * @param associations
     */
    public void deleteAssociations(Collection<Association> associations) {
        final DAO dao = annotationDAOFactory.newDAO();
        Collection<Observation> modifiedObservations = new ArrayList<Observation>();
        dao.startTransaction();

        for (Association association : associations) {
            association = dao.find(association);

            if (association != null) {
                final Observation observation = association.getObservation();
                observation.removeAssociation(association);
                dao.remove(association);

                if (!modifiedObservations.contains(observation)) {
                    modifiedObservations.add(observation);
                }
            }
        }

        dao.endTransaction();
        updateUI(modifiedObservations);
    }

    /**
     *
     * @param videoArchive
     * @return
     */
    public VideoArchive deleteEmptyVideoFramesFrom(VideoArchive videoArchive) {
        final DAO dao = annotationDAOFactory.newDAO();
        dao.startTransaction();
        videoArchive = dao.find(videoArchive);
        Collection<VideoFrame> videoFrames = new ArrayList<VideoFrame>(videoArchive.getEmptyVideoFrames());
        for (VideoFrame videoFrame : videoFrames) {
            videoArchive.removeVideoFrame(videoFrame);
            dao.remove(videoFrame);
        }

        dao.endTransaction();
        return videoArchive;
    }

    /**
     *
     * @param observations
     */
    public void deleteObservations(Collection<Observation> observations) {
        final DAO dao = annotationDAOFactory.newDAO();
        dao.startTransaction();

        for (Observation observation : observations) {
            observation = dao.find(observation);

            if (observation != null) {
                VideoFrame videoFrame = observation.getVideoFrame();
                videoFrame.removeObservation(observation);
                dao.remove(observation);

                if (videoFrame.getObservations().size() == 0) {
                    VideoArchive videoArchive = videoFrame.getVideoArchive();
                    videoArchive.removeVideoFrame(videoFrame);
                    dao.remove(videoFrame);
                }
            }

        }

        dao.endTransaction();
        updateUI();
    }

    /**
     *
     * @param observations
     * @param associationTemplate
     * @return
     */
    public Collection<Association> insertAssociations(Collection<Observation> observations, ILink associationTemplate) {
        final Collection<Association> associations = new ArrayList<Association>(observations.size());
        final Collection<Observation> uiObservations = new ArrayList<Observation>();
        final Collection<Observation> mergedObservations = new ArrayList<Observation>();
        final ConceptDAO conceptDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        final AssociationDAO dao = annotationDAOFactory.newAssociationDAO();

        // DAOTX - Add Association to each observation
        dao.startTransaction();
        for (Observation observation : observations) {
            // Try a merge first to try an update the conceptName incase it's been
            // changed.
            try {
                observation = dao.merge(observation);
            }
            catch (Exception e) {
                observation = dao.find(observation);
            }
            
            if (observation != null) {
                uiObservations.add(observation);
                Association ass = annotationFactory.newAssociation(associationTemplate.getLinkName(),
                    associationTemplate.getToConcept(), associationTemplate.getLinkValue());
                observation.addAssociation(ass);
                dao.persist(ass);
                dao.validateName(ass, conceptDAO);
                associations.add(ass);
            }
        }

        dao.endTransaction();

        resetObservationsInDispatcher(uiObservations);
        updateUI(uiObservations);    // update view
        return associations;
    }

    /**
     * If any of the observations provided in the argument match those in those
     * referenced in {@code (Collection<Observation>) Lookup.getSelectedObservationsDispatcher().getValueObject();}
     * then they ones in the dispatcher will be replaced by those you provided.
     * This helps keep observations in sync
     * 
     * @param observations
     */
    private void resetObservationsInDispatcher(Collection<Observation> observations) {

        // ---- Remove overlapping references from the observations in the
        // dispatcher. We'll replace them with the observations in our argument
        // collection.
        Collection<Observation> selectedObservations = (Collection<Observation>) Lookup.getSelectedObservationsDispatcher().getValueObject();
        Function<Observation, Long> f = new Function<Observation, Long>() {
            public Long apply(Observation from) {
                return (Long) from.getPrimaryKey();
            }
        };
        final Collection<Long> pks = Collections2.transform(observations, f);
        final Collection<Long> selectedPks = Collections2.transform(selectedObservations, f);
        selectedPks.removeAll(pks);
        selectedObservations = Collections2.filter(selectedObservations, new Predicate<Observation>() {
            public boolean apply(Observation input) {
                return selectedPks.contains((Long) input.getPrimaryKey());
            }
        });

        // ---- Once the overlaps are removed add the replacements
        Collection<Observation> uiObservations = new ArrayList<Observation>(selectedObservations);
        uiObservations.addAll(observations);

        Lookup.getSelectedObservationsDispatcher().setValueObject(uiObservations);
    }

    /**
     *
     * @param videoFrame
     * @param observation
     * @return
     */
    public Observation insertObservation(VideoFrame videoFrame, Observation observation) {
        ObservationDAO dao = annotationDAOFactory.newObservationDAO();
        final ConceptDAO conceptDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        Collection<Observation> newObservations = new ArrayList<Observation>();
        dao.startTransaction();
        videoFrame = dao.find(videoFrame);

        if (videoFrame != null) {
            videoFrame.addObservation(observation);
            dao.persist(observation);
            dao.validateName(observation, conceptDAO);
            newObservations.add(observation);
        }

        dao.endTransaction();
        updateUI(newObservations);    // update view
        return observation;
    }

    /**
     *
     * @param videoFrame
     * @param observations
     * @return
     */
    public Collection<Observation> insertObservations(VideoFrame videoFrame, Collection<Observation> observations) {
        ObservationDAO dao = annotationDAOFactory.newObservationDAO();
        final ConceptDAO conceptDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        dao.startTransaction();
        videoFrame = dao.find(videoFrame);

        for (Observation observation : observations) {
            videoFrame.addObservation(observation);
            dao.persist(observation);
            dao.validateName(observation, conceptDAO);
        }

        dao.endTransaction();
        updateUI(observations);    // update view
        return observations;
    }

    /**
     *
     * @param videoArchive
     * @param videoFrame
     * @return
     */
    public VideoFrame insertVideoFrame(VideoArchive videoArchive, VideoFrame videoFrame) {
        DAO dao = annotationDAOFactory.newDAO();
        dao.startTransaction();
        videoArchive = dao.find(videoArchive);
        videoArchive.addVideoFrame(videoFrame);
        dao.persist(videoFrame);
        dao.endTransaction();
        return videoFrame;
    }

    /**
     * VideoFrames need to be loaded from the database. Call this method to fetch all
     * of them for a particular {@link VideoArchive}. Be sre to grab the returned reference
     * in order to access them.
     * @param videoArchive
     * @return
     */
    public VideoArchive loadVideoFramesFor(VideoArchive videoArchive) {
        DAO dao = annotationDAOFactory.newDAO();
        dao.startTransaction();
        videoArchive = dao.find(videoArchive);
        @SuppressWarnings("unused") Collection<VideoFrame> videoFrames = videoArchive.getVideoFrames();
        dao.endTransaction();
        return videoArchive;
    }

    /**
     * Convenience method very specific to MBARI internal usage and naming
     * conventions. MBARI likes to name video archives so that a tape #3 from
     * dive# 302 (seqNumber)using the ROV Tiburon (platform) would be named
     * T0302-03.
     *
     * @param platform The platform name. THe first character of the name
     *          is used. This is stored in the VideoArchiveSet
     * @param seqNumber In MBARI's case we use dive number. seqNumber is
     *          stored in the CameraPlatformDeployment. Numbers with more than
     *          4 digits are not supported.
     * @param tapeNumber This is an MBARI specific value. It is not stored in
     *          the VARS database. Numbers of more than 2 digits are not supported.
     * @param postfix This is any text to be appended to the end of the {@link VideoArchive}'s name. At MBARI,
     *          we use this to indicate HD tapes by appending 'HD'. If it's null nothing will be
     *          appended
     * @return A string name that is generated from the supplied arguments
     */
    public static String makeVideoArchiveName(final String platform, final int seqNumber, final int tapeNumber,
            final String postfix) {
        StringBuffer sb = new StringBuffer();
        sb.append(platform.charAt(0));
        sb.append(f0123.format((long) seqNumber));
        sb.append("-");
        sb.append(f01.format((long) tapeNumber));

        if (postfix != null) {
            sb.append(postfix);
        }

        return sb.toString();
    }

    /**
     *
     * @param videoArchive
     * @param observations
     */
    public void moveObservationsTo(VideoArchive videoArchive, Collection<Observation> observations) {
        Collection<Observation> updateObservations = new ArrayList<Observation>();
        DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
        dao.startTransaction();
        videoArchive = dao.find(videoArchive);

        for (Observation observation : updateObservations) {
            observation = dao.find(observation);
            throw new UnsupportedOperationException("Implementation isn't finished yet");
        }
    }

    /**
     *
     * @param platform
     * @param sequenceNumber
     * @param videoArchiveName
     * @return
     */
    public VideoArchive openVideoArchive(String platform, int sequenceNumber, String videoArchiveName) {
        VideoArchiveDAO dao = toolBelt.getAnnotationDAOFactory().newVideoArchiveDAO();
        dao.startTransaction();
        VideoArchive videoArchive = dao.findOrCreateByParameters(platform, sequenceNumber, videoArchiveName);
        @SuppressWarnings("unused") Collection<VideoFrame> videoFrames = videoArchive.getVideoFrames();    // Load the videoFrames
        dao.endTransaction();
        Lookup.getVideoArchiveDispatcher().setValueObject(videoArchive);

        // TODO Do we need to call updateUI here?
        return videoArchive;
    }


    /**
     * Thread-safe. Updates changes made to the observations in the database. Validates the
     * concept names used by the {@link Observation}s and their child {@link Association}
     * @param observations
     * @return
     */
    public Collection<Observation> updateAndValidate(Collection<Observation> observations) {
        Collection<Observation> updatedObservations = new Vector<Observation>(observations.size());
        ObservationDAO dao = toolBelt.getAnnotationDAOFactory().newObservationDAO();
        AssociationDAO aDao = toolBelt.getAnnotationDAOFactory().newAssociationDAO(dao.getEntityManager());
        final ConceptDAO conceptDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        dao.startTransaction();

        for (Observation observation : observations) {
            observation = dao.updateFields(observation);
            if (observation != null) {
                updatedObservations.add(observation);
                dao.validateName(observation, conceptDAO);
                for (Association association : new ArrayList<Association>(observation.getAssociations())) {
                    aDao.validateName(association, conceptDAO);
                }
            }
        }

        dao.endTransaction();
        return updatedObservations;
    }

    /**
     *
     * @param associations
     * @return
     */
    public Collection<Association> updateAssociations(Collection<Association> associations) {
        final AssociationDAO dao = annotationDAOFactory.newAssociationDAO();
        final ConceptDAO conceptDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        Collection<Association> updatedAssociations = new ArrayList<Association>(associations.size());
        Collection<Observation> uiObservations = new ArrayList<Observation>();
        dao.startTransaction();

        for (Association association : associations) {
            association = dao.merge(association);
            dao.validateName(association, conceptDAO);
            updatedAssociations.add(association);
            uiObservations.add(association.getObservation());
        }

        dao.endTransaction();
        updateUI(uiObservations);
        return updatedAssociations;
    }

    /**
     *
     * @param observations
     * @return
     */
    public Collection<Observation> updateObservations(Collection<Observation> observations) {
        Collection<Observation> updatedObservations = updateAndValidate(observations);
        updateUI(updatedObservations);    // update view
        return updatedObservations;
    }

    /**
     * Changes the CameraData URL's that match the currently set local directory to
     * use ther URL (image mapping target) defined in the uses preferences.
     *
     * @param videoArchive
     * @param imageTarget The  directory where the frames are saved into
     * @param imageTargetMapping The URL that maps imageTarget onto a web server
     * @return A Collection of CameraData objects whose URL's have been updated
     *  in the database. (Returns the udpated instance)
     */
    public Collection<CameraData> updateCameraDataUrls(VideoArchive videoArchive, File imageTarget, URL imageTargetMapping) throws MalformedURLException {
        Collection<CameraData> cameraDatas = new ArrayList<CameraData>();
        DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
        dao.startTransaction();
        videoArchive = dao.find(videoArchive);
        Collection<VideoFrame> videoFrames = videoArchive.getVideoFrames();
        URL imageTargetUrl = imageTarget.toURI().toURL();
        String imageTargetExternalForm = imageTargetUrl.toExternalForm();
        for (VideoFrame videoFrame : videoFrames) {
            CameraData cameraData = videoFrame.getCameraData();
            cameraData = dao.find(cameraData);
            String imageReference = cameraData.getImageReference();
            if (imageReference != null && imageReference.startsWith(imageTargetExternalForm)) {
                URL imageReferenceURL = new URL(imageReference);
                File imageReferenceFile = URLUtilities.toFile(imageReferenceURL);
                URL newUrl = fileToUrl(imageReferenceFile, imageTarget, imageTargetMapping);
                cameraData.setImageReference(newUrl.toExternalForm());
                cameraDatas.add(cameraData);
            }
        }
        dao.endTransaction();
        return cameraDatas;
    }

    /**
     * Creates a URL of [image.archive.url]/[platform]/images/[dive]/filename from
     * a file of [image.archive.dir]/[platform]/images/[dive]/filename
     *
     * @param  targetFile The File where the image that an image was copied to.
     * @return  The URL that corresponds to the File targetFile.
     * @exception  IllegalArgumentException Description of the Exception
     * @throws  MalformedURLException
     */
    URL fileToUrl(final File targetFile, final File imageTarget, final URL imageTargetMapping) throws IllegalArgumentException, MalformedURLException {

        // ---- Ensure that the file provided is located under the image archive directory
        String targetPath = targetFile.getAbsolutePath();
        final String rootPath = imageTarget.getAbsolutePath();
        if (!targetPath.startsWith(rootPath)) {
            throw new IllegalArgumentException("The file, " + targetPath +
                                               ", is not located in the expected location, " + rootPath);
        }

        // Chop off the part of the path that matches the image target
        // e.g. /Target/MyDir with an image of /Target/MyDir/images/Tiburon/foo.png
        // would yield a postfix of /images/Tiburon/foo.png
        String postfix = targetPath.substring(rootPath.length(), targetPath.length());
        final String[] parts = postfix.split("[\\\\\\\\,/]");
        StringBuffer dstUrl = new StringBuffer(imageTargetMapping.toExternalForm());

        // Make sure the URL ends with "/"
        if (!dstUrl.toString().endsWith("/")) {
            dstUrl.append("/");
        }

        // Chain the parts into a new URL
        boolean b = false; // Don't add "/" on the first loop iteration. It's already in the URL
        for (int i = 0; i < parts.length; i++) {
            if (!"".equals(parts[i])) {
                if (b) {
                    dstUrl.append("/");
                }

                dstUrl.append(parts[i]);
                b = true;
            }
        }

        String dstUrlString = dstUrl.toString().replaceAll(" ", "%20");    // Space break things

        URL out = null;
        try {
            out = new URL(dstUrlString);
        }
        catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Strings in Java suck!!!", e);
            }
        }

        return out;
    }

    /**
     *
     * @param observations
     */
    public void updateUI(final Collection<Observation> observations) {
        updateUI(observations, true);
    }

    /**
     * Updates the observations in the UI. Uses a flag to indicate if the
     * selected rows should be adjusted.
     * 
     * @param observations
     * @param updateSelection true = reselect rows, false = don't make any adjustments
     *  to selections (useful when you know none of the rows were selected)
     */
    public void updateUI(final Collection<Observation> observations, final boolean updateSelection) {

        Runnable runnable = new Runnable() {

            public void run() {

                // Get the TableModel
                final ObservationTable observationTable = (ObservationTable) Lookup.getObservationTableDispatcher().getValueObject();
                final JTable table = (JTable) observationTable;
                if (observationTable == null) {
                    log.info("No UI is available to update");
                    return;
                }

                final ObservationTableModel model = (ObservationTableModel) table.getModel();
                
                for (Observation observation : observations) {
                    int row = model.getObservationRow(observation);
                    if ((row > -1) && (row < model.getRowCount())) {
                        observationTable.updateObservation(observation);
                    }
                    else {
                        observationTable.addObservation(observation);
                        row = model.getObservationRow(observation);

                        if ((row > -1) && (row < model.getRowCount())) {
                            
                            observationTable.scrollToVisible(row, 0);
                        }
                    }
                }

                /*
                 * We need to keep the RowEditorPanel in sync. Doing this
                 * explicitly is probably best to avoid weird unintended UI
                 * side-effects.
                 */
                AnnotationFrame annotationFrame = (AnnotationFrame) Lookup.getApplicationFrameDispatcher().getValueObject();
                RowEditorPanel rowEditorPanel = annotationFrame.getRowEditorPanel();
                Observation reObservation = rowEditorPanel.getObservation();
                if (reObservation != null) {
                    DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
                    for (Observation obs : observations) {
                        if (dao.equalInDatastore(reObservation, obs)) {
                            rowEditorPanel.setObservation(obs);
                            break;
                        }
                    }
                }

                if (updateSelection) {
                    Collection<Observation> selectedObservations = (Collection<Observation>) Lookup.getSelectedObservationsDispatcher().getValueObject();
                    selectedObservations = new ArrayList<Observation>(selectedObservations);    // Copy to avoid thread issues

                    /*
                     * If we just added one select it in the table
                     */
                    if (observations.size() == 1) {
                        final Observation observation = observations.iterator().next();
                        observationTable.setSelectedObservation(observation);
                    }
                    else {
                        ListSelectionModel lsm = table.getSelectionModel();
                        lsm.clearSelection();

                        for (Observation observation : selectedObservations) {
                            int row = model.getObservationRow(observation);
                            lsm.addSelectionInterval(row, row);
                        }

                    }
                }

            }
        };

        /*
         * Execute on the proper thread.
         */
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        }
        else {
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (Exception ex) {
                log.warn("Failed to excecute updateUI() method on EDT", ex);
            }
        }

    }

    /**
     *
     * @param videoArchive
     */
    public void updateUI(VideoArchive videoArchive) {

        // Get the TableModel
        final ObservationTable observationTable = (ObservationTable) Lookup.getObservationTableDispatcher().getValueObject();
        if (observationTable == null) {
            log.info("No UI is available to update");
            return;
        }

        Runnable runnable = new Runnable() {

            public void run() {
                JTable table = observationTable.getJTable();
                table.getSelectionModel().clearSelection();

                // Remove the current contents of the table
                final ObservationTableModel model = (ObservationTableModel) ((JTable) observationTable).getModel();
                model.clear();

            }
        };

        /*
         * Clear the table on the proper thread
         */
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        }
        else {
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (Exception ex) {
                log.warn("Failed to clear tabel model", ex);
            }
        }


        Collection<Observation> observations = new ArrayList<Observation>();

        // Repopulate it with the contents of the new VideoArchive
        if (videoArchive != null) {

            // DAOTX - Needed to deal with lazy loading
            DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
            dao.startTransaction();
            videoArchive = dao.find(videoArchive);

            final Collection<VideoFrame> videoFrames = videoArchive.getVideoFrames();
            for (VideoFrame videoFrame : videoFrames) {
                observations.addAll(videoFrame.getObservations());
            }

            dao.endTransaction();

            updateUI(observations, false);
        }
    }

    /**
     * Bascially a UI refresh using the currently open videoArchive. Scrolls to the currently open
     * visible rectangle.
     */
    public void updateUI() {

         // Get the TableModel
        final ObservationTable observationTable = (ObservationTable) Lookup.getObservationTableDispatcher().getValueObject();
        if (observationTable == null) {
            log.info("No UI is available to update");
            return;
        }
        final JTable table = observationTable.getJTable();
        final Rectangle rect = table.getVisibleRect();
        VideoArchive videoArchive = (VideoArchive) Lookup.getVideoArchiveDispatcher().getValueObject();
        updateUI(videoArchive);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // When observations are deleted the table would jump to the last row UNLESS
                // we make this call which mostly preserves the current view.
                table.scrollRectToVisible(rect);
            }
        });
    }

    /**
     *
     * @param videoArchive
     * @return
     */
    public VideoArchive updateVideoArchive(VideoArchive videoArchive) {
        DAO dao = annotationDAOFactory.newDAO();
        dao.startTransaction();
        videoArchive = dao.merge(videoArchive);
        dao.endTransaction();
        updateUI();
        return videoArchive;
    }

    /**
     *
     * @param videoArchiveSet
     * @return
     */
    public VideoArchiveSet updateVideoArchiveSet(VideoArchiveSet videoArchiveSet) {
        DAO dao = annotationDAOFactory.newDAO();
        dao.startTransaction();
        videoArchiveSet = dao.merge(videoArchiveSet);
        dao.endTransaction();

        //updateUI(videoArchiveSet);
        return videoArchiveSet;
    }

    /**
     *
     *
     * @param videoFrames
     * @return
     */
    public Collection<VideoFrame> updateVideoFrames(Collection<VideoFrame> videoFrames) {
        ObservationDAO dao = annotationDAOFactory.newObservationDAO();
        final ConceptDAO conceptDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        final Collection<VideoFrame> updatedVideoFrames = new ArrayList<VideoFrame>(videoFrames.size());
        final Collection<Observation> observations = new ArrayList<Observation>();
        dao.startTransaction();

        for (VideoFrame vf : videoFrames) {
            VideoFrame updatedVideoFrame = dao.merge(vf);
            updatedVideoFrames.add(updatedVideoFrame);
            observations.addAll(updatedVideoFrame.getObservations());
        }

        for (Observation observation : observations) {
            dao.validateName(observation, conceptDAO);
        }

        dao.endTransaction();
        updateUI(observations);    // update view
        return updatedVideoFrames;
    }
}
