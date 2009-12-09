/*
 * @(#)PersistenceService.java   2009.11.16 at 11:07:33 PST
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.DAO;
import vars.ILink;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.AnnotationFactory;
import vars.annotation.AnnotationPersistenceService;
import vars.annotation.Association;
import vars.annotation.AssociationDAO;
import vars.annotation.Observation;
import vars.annotation.ObservationDAO;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveSet;
import vars.annotation.VideoFrame;
import vars.annotation.ui.table.ObservationTable;
import vars.annotation.ui.table.ObservationTableModel;

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
    private final AnnotationDAOFactory annotationDAOFactory;
    private final AnnotationFactory annotationFactory;
    private final ToolBelt toolBelt;
    private final Logger log = LoggerFactory.getLogger(getClass());

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
     * @param videoFrame
     * @param observation
     * @return
     */
    public Observation insertObservation(VideoFrame videoFrame, Observation observation) {
        ObservationDAO dao = annotationDAOFactory.newObservationDAO();
        dao.startTransaction();
        videoFrame = dao.merge(videoFrame);
        videoFrame.addObservation(observation);
        dao.persist(observation);
        dao.validateName(observation);
        Collection<Observation> obs = new ArrayList<Observation>(videoFrame.getObservations());
        dao.endTransaction();
        updateUI(obs); // update view
        return observation;
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
        videoArchive = dao.merge(videoArchive);
        videoArchive.addVideoFrame(videoFrame);
        dao.persist(videoFrame);
        dao.endTransaction();
        return videoFrame;
    }

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
     * @param observations
     * @return
     */
    public Collection<Observation> updateObservations(Collection<Observation> observations) {
        AnnotationPersistenceService service = toolBelt.getAnnotationPersistenceService();
        Collection<Observation> updatedObservations = service.updateAndValidate(observations);
        updateUI(updatedObservations); // update view
        return updatedObservations;
    }

    /**
     *
     * @param videoFrame
     * @return
     */
    public Collection<VideoFrame> updateVideoFrames(Collection<VideoFrame> videoFrames) {
        ObservationDAO dao = annotationDAOFactory.newObservationDAO();
        final Collection<VideoFrame> updatedVideoFrames = new ArrayList<VideoFrame>(videoFrames.size());
        final Collection<Observation> observations = new ArrayList<Observation>();
        dao.startTransaction();
        for (VideoFrame vf : videoFrames) {
            VideoFrame updatedVideoFrame = dao.merge(vf);
            updatedVideoFrames.add(updatedVideoFrame);
            observations.addAll(updatedVideoFrame.getObservations());
        }
        for (Observation observation : observations) {
            dao.validateName(observation);
        }
        dao.endTransaction();
        updateUI(observations); // update view
        return updatedVideoFrames;
    }

    public Collection<Association> insertAssociations(Collection<Observation> observations, ILink associationTemplate) {
        final Collection<Association> associations = new ArrayList<Association>(observations.size());
        final Collection<Observation> uiObservations = new ArrayList<Observation>();
        final AssociationDAO dao = annotationDAOFactory.newAssociationDAO();
        dao.startTransaction();
        for (Observation observation : observations) {
            observation = dao.merge(observation);
            uiObservations.add(observation);
            Association ass = annotationFactory.newAssociation(associationTemplate.getLinkName(),
                    associationTemplate.getToConcept(), associationTemplate.getLinkValue());
            observation.addAssociation(ass);
            dao.persist(ass);
            dao.validateName(ass);
            associations.add(ass);
        }
        dao.endTransaction();
        updateUI(uiObservations); // update view
        return associations;
    }

    public Collection<Association> updateAssociations(Collection<Association> associations) {
        final AssociationDAO dao = annotationDAOFactory.newAssociationDAO();
        Collection<Association> updatedAssociations = new ArrayList<Association>(associations.size());
        Collection<Observation> uiObservations = new ArrayList<Observation>();
        dao.startTransaction();
        for (Association association : associations) {
            association = dao.merge(association);
            dao.validateName(association);
            updatedAssociations.add(association);
            uiObservations.add(association.getObservation());
        }
        dao.endTransaction();
        updateUI(uiObservations);
        return updatedAssociations;
    }

    public void deleteAssociations(Collection<Association> associations) {
        final DAO dao = annotationDAOFactory.newDAO();
        Collection<Observation> modifiedObservations = new ArrayList<Observation>();
        dao.startTransaction();
        for (Association association : associations) {
            association = dao.merge(association);
            final Observation observation = association.getObservation();
            observation.removeAssociation(association);
            dao.remove(association);
            if (!modifiedObservations.contains(observation)) {
                modifiedObservations.add(observation);
            }
        }
        dao.endTransaction();
        updateUI(modifiedObservations);
    }

    public VideoArchive deleteEmptyVideoFramesFrom(VideoArchive videoArchive) {
        final DAO dao = annotationDAOFactory.newDAO();
        dao.startTransaction();
        videoArchive = dao.merge(videoArchive);
        Collection<VideoFrame> videoFrames = new ArrayList<VideoFrame>(videoArchive.getVideoFrames());
        for (VideoFrame videoFrame : videoFrames) {
            if (videoFrame.getObservations().size() == 0) {
                videoArchive.removeVideoFrame(videoFrame);
                dao.remove(videoFrame);
            }
        }
        dao.endTransaction();
        return videoArchive;
    }

    public void deleteObservations(Collection<Observation> observations) {
        final DAO dao = annotationDAOFactory.newDAO();
        dao.startTransaction();
        for (Observation observation : observations) {
            VideoFrame videoFrame = observation.getVideoFrame();
            videoFrame.removeObservation(observation);
            dao.remove(observation);
            if (videoFrame.getObservations().size() == 0) {
                VideoArchive videoArchive = videoFrame.getVideoArchive();
                videoArchive.removeVideoFrame(videoFrame);
                dao.remove(videoFrame);
            }

        }
        dao.endTransaction();
        updateUI();
    }

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

    public Collection<Observation> deleteAllAssociationsFrom(Collection<Observation> observations) {
        Collection<Observation> updateObservations = new ArrayList<Observation>();
        DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
        dao.startTransaction();
        for (Observation observation : observations) {
            observation = dao.merge(observation);
            for (Association association : new ArrayList<Association>(observation.getAssociations())) {
                observation.removeAssociation(association);
                dao.remove(association);
            }
            updateObservations.add(observation);
        }
        updateUI();
        return updateObservations;
    }

    public void updateUI(Collection<Observation> observations) {

        // Get the TableModel
        final ObservationTable observationTable = (ObservationTable) Lookup.getObservationTableDispatcher().getValueObject();
        if (observationTable == null) {
            log.info("No UI is available to update");
            return;
        }
        final ObservationTableModel model = (ObservationTableModel) ((JTable) observationTable).getModel();
        for (Observation observation : observations) {
            int row = model.getObservationRow(observation);
            if (row > -1 && row < model.getRowCount()) {
            	observationTable.redrawRow(row);
            }
            else {
            	observationTable.addObservation(observation);
            	row = model.getObservationRow(observation);
            	if (row > -1 && row < model.getRowCount()) {
            		observationTable.scrollToVisible(row, 0);
            	}
            }
        }
    }

    public void updateUI(VideoArchive videoArchive) {

        // Get the TableModel
        final ObservationTable observationTable = (ObservationTable) Lookup.getObservationTableDispatcher().getValueObject();
        if (observationTable == null) {
            log.info("No UI is available to update");
            return;
        }

        final ObservationTableModel model = (ObservationTableModel) ((JTable) observationTable).getModel();
        // Remove the current contents
        model.clear();

        // Repopulate it with the contents of the new VideoArchive
        if (videoArchive != null) {

            // DAOTX - Needed to deal with lazy loading
            DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
            dao.startTransaction();
            videoArchive = dao.merge(videoArchive);

            final Collection<VideoFrame> videoFrames = videoArchive.getVideoFrames();
            for (VideoFrame videoFrame : videoFrames) {
                final Collection<Observation> observations = videoFrame.getObservations();
                for (Observation observation : observations) {
                    model.addObservation(observation);
                }
            }
            dao.endTransaction();
        }
    }

    public void updateUI() {
        VideoArchive videoArchive = (VideoArchive) Lookup.getVideoArchiveDispatcher().getValueObject();
        updateUI(videoArchive);
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
     * VideoFrames need to be loaded from the database. Call this method to fetch all
     * of them for a particular {@link VideoArchive}. Be sre to grab the returned reference
     * in order to access them.
     * @param videoArchive
     * @return
     */
    public VideoArchive loadVideoFramesFor(VideoArchive videoArchive) {
        DAO dao = annotationDAOFactory.newDAO();
        dao.startTransaction();
        videoArchive = dao.merge(videoArchive);
        @SuppressWarnings("unused")
        Collection<VideoFrame> videoFrames = videoArchive.getVideoFrames();
        dao.endTransaction();
        return videoArchive;
    }
}
