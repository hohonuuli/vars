/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.actions;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import vars.DAO;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.VideoFrame;


/**
 * Move's videoframes from their parent to the one you supply. The videoFrames
 * do not need to all share the same parent.
 * @author brian
 */
public class MoveVideoFramesFunction {

    private final AnnotationDAOFactory annotationDAOFactory;

    @Inject
    public MoveVideoFramesFunction(AnnotationDAOFactory annotationDAOFactory) {
        this.annotationDAOFactory = annotationDAOFactory;
    }

    Collection<VideoFrame> apply(VideoArchive targetVideoArchive, Collection<VideoFrame> videoFrames) {
        if (targetVideoArchive == null) {
            throw new IllegalArgumentException("The Target VideoArchive can not be null");
        }
        Collection<VideoFrame> modifiedVideoFrames = new ArrayList<VideoFrame>(videoFrames.size());

        DAO dao = annotationDAOFactory.newDAO();
        dao.startTransaction();
        targetVideoArchive = dao.find(targetVideoArchive);
        for (VideoFrame videoFrame : videoFrames) {
            videoFrame = dao.find(videoFrame); // Pull existing copy from db and bring into transation
            VideoArchive sourceVideoArchive = videoFrame.getVideoArchive();
            if (sourceVideoArchive != null &&
                    !sourceVideoArchive.getName().equals(targetVideoArchive.getName())) {

                sourceVideoArchive.removeVideoFrame(videoFrame);

                /*
                 * Check to see if the target VideoArchive already has a
                 * VideoFrame with that timecode.
                 */
                VideoFrame targetVideoFrame = targetVideoArchive.findVideoFrameByTimeCode(videoFrame.getTimecode());
                if (targetVideoFrame != null) {
                    mergeFrames(videoFrame, targetVideoFrame);
                    dao.remove(videoFrame);
                }
                else {
                    targetVideoArchive.addVideoFrame(videoFrame);
                }

            }
        }

        dao.endTransaction();

        return modifiedVideoFrames;
    }

      /**
     * Merges the information contained in 2 different videoFrames. <strong>This should be called within
     * a persistence transaction!!!</strong>
     * @param sourceFrame The VideoFrame whose contents will be moved to the target
     *  VideoFrame
     * @param targetFrame The destination for the information from the source
     *  VideoFrame
     */
    private void mergeFrames(final VideoFrame sourceFrame, final VideoFrame targetFrame) {

        /*
         * If a VideoFrame with the same timecode was found in the
         * target(and if you reached this point, one was found) copy the
         * observations to the new videoFrame
         */
        final Collection<Observation> observations = new ArrayList<Observation>(sourceFrame.getObservations());
        for (Observation observation : observations) {
            sourceFrame.removeObservation(observation);
            targetFrame.addObservation(observation);
        }

        /*
         * If the target does not have an image, but the source does, copy the image
         * to the target.
         */
        final String sourceImageRef = sourceFrame.getCameraData().getImageReference();
        final String targetImageRef = targetFrame.getCameraData().getImageReference();
        if (targetImageRef == null && sourceImageRef != null) {
            targetFrame.getCameraData().setImageReference(sourceImageRef);
        }

    }

}
