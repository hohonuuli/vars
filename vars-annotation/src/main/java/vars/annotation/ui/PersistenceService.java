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

import java.util.ArrayList;
import java.util.Collection;
import vars.DAO;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.AnnotationFactory;
import vars.annotation.Association;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveSet;
import vars.annotation.VideoFrame;

/**
 * PersistenceService manages database transactions for the Userinterface. It will keep the
 * pesistent objects AND the user interface in synch.
 *
 * @version        Enter version here..., 2009.11.16 at 10:49:40 PST
 * @author         Brian Schlining [brian@mbari.org]
 */
public class PersistenceService {

    private final AnnotationDAOFactory annotationDAOFactory;
    private final AnnotationFactory annotationFactory;

    /**
     * Constructs ...
     *
     * @param toolBelt
     */
    public PersistenceService( AnnotationDAOFactory annotationDAOFactory, AnnotationFactory annotationFactory) {
        super();
        this.annotationDAOFactory = annotationDAOFactory;
        this.annotationFactory = annotationFactory;
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
        DAO dao = annotationDAOFactory.newDAO();
        dao.startTransaction();
        videoFrame = dao.merge(videoFrame);
        videoFrame.addObservation(observation);
        dao.persist(videoFrame);
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

    /**
     *
     * @param observations
     * @return
     */
    public Collection<Observation> updateObservations(Collection<Observation> observations) {
        DAO dao = annotationDAOFactory.newDAO();
        final Collection<Observation> updatedObservations = new ArrayList<Observation>(observations.size());
        dao.startTransaction();
        for (Observation observation : observations) {
            updatedObservations.add(dao.merge(observation));
        }
        dao.endTransaction();
        updateUI(updatedObservations); // update view
        return updatedObservations;

    }

    /**
     *
     * @param videoFrame
     * @return
     */
    public Collection<VideoFrame> updateVideoFrames(Collection<VideoFrame> videoFrames) {
        DAO dao = annotationDAOFactory.newDAO();
        final Collection<VideoFrame> updatedVideoFrames = new ArrayList<VideoFrame>(videoFrames.size());
        final Collection<Observation> observations = new ArrayList<Observation>();
        dao.startTransaction();
        for (VideoFrame vf : videoFrames) {
            VideoFrame updatedVideoFrame = dao.merge(vf);
            updatedVideoFrames.add(updatedVideoFrame);
            observations.addAll(updatedVideoFrame.getObservations());
        }
        dao.endTransaction();
        updateUI(observations); // update view
        return updatedVideoFrames;
    }
    
    public Collection<Association> insertAssociations(Collection<Observation> observations, Association associationTemplate) {
        final Collection<Association> associations = new ArrayList<Association>(observations.size());
        final Collection<Observation> uiObservations = new ArrayList<Observation>();
        final DAO dao = annotationDAOFactory.newDAO();
        dao.startTransaction();
        for (Observation observation : observations) {
            observation = dao.merge(observation);
            uiObservations.add(observation);
            Association ass = annotationFactory.newAssociation(associationTemplate.getLinkName(), 
                    associationTemplate.getToConcept(), associationTemplate.getLinkValue());
            observation.addAssociation(ass);
            dao.persist(ass);
            associations.add(associationTemplate);            
        }
        dao.endTransaction();
        updateUI(uiObservations); // update view
        return associations;
    }
    
    public Collection<Association> updateAssociations(Collection<Association> associations) {
        final DAO dao = annotationDAOFactory.newDAO();
        Collection<Association> updatedAssociations = new ArrayList<Association>(associations.size());
        Collection<Observation> uiObservations = new ArrayList<Observation>();
        dao.startTransaction();
        for (Association association : associations) {
            association = dao.merge(association);
            updatedAssociations.add(association);
            uiObservations.add(association.getObservation());
        }
        dao.endTransaction();
        updateUI(uiObservations);
        return updatedAssociations;
    }
    
    private void updateUI(Collection<Observation> observations) {
        // TODO implement this.
        
     // Redraw the table
//        ObservationTableDispatcher.getInstance().getObservationTable().redrawAll();
//
//        // Update everything that's listening to the videoarchive.
//        final VideoArchiveDispatcher vad = VideoArchiveDispatcher.getInstance();
//        vad.setVideoArchive(vad.getVideoArchive());
    }
    
    private void updateUI(VideoArchiveSet videoArchiveSet) {
        
    }
    
}
