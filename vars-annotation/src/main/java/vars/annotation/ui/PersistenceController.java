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
import java.util.Arrays;
import java.util.Collection;
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
        updateUI(videoArchiveSet);
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
        // NO UI update is needed?
        return videoFrame;
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
        // TODO implement this.
        
     // Redraw the table
//        ObservationTableDispatcher.getInstance().getObservationTable().redrawAll();
//
//        // Update everything that's listening to the videoarchive.
//        final VideoArchiveDispatcher vad = VideoArchiveDispatcher.getInstance();
//        vad.setVideoArchive(vad.getVideoArchive());
        
        // if no obs selected scroll to last line
//        Arrays.sort(rows);
//        final int rowCount = table.getRowCount();
//        int activeRow = rows[n - 1] - n + 1;
//        if (activeRow > rowCount - 1) {
//            activeRow = rowCount - 1;
//        }
//
//        if (rowCount > 0) {
//            table.setSelectedObservation(table.getObservationAt(activeRow));
//            table.scrollToVisible(activeRow, 0);
//        }
    }
    
    public void updateUI(VideoArchiveSet videoArchiveSet) {
     // TODO implement me
    }
    
    public void updateUI() {
        
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
    


    
}
