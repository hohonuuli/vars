package vars.annotation.ui.commandqueue.impl;

import org.bushe.swing.event.EventBus;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.annotation.VideoFrame;
import vars.annotation.VideoFrameDAO;
import vars.annotation.ui.StateLookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.eventbus.VideoArchiveChangedEvent;
import vars.annotation.ui.eventbus.VideoFramesChangedEvent;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Moves VideoFrames from one videoarchive to another
 *
 * @author Brian Schlining
 * @since 2011-10-11
 */
public class MoveVideoFramesCmd implements Command {

    private final String targetVideoArchiveName;
    private final Collection<DataBean> originalData = new ArrayList<DataBean>();
    private final boolean refreshVideoArchive;


    /**
     *
     * @param targetVideoArchiveName The name of the VideoArchive that you are moving VideoFrames to.
     * @param originalVideoFrames The videoFrames that are being moved
     *
     * This does NOT refresh the annotations window. Used by the VideoSet Viewer
     */
    public MoveVideoFramesCmd(String targetVideoArchiveName,
                              Collection<VideoFrame> originalVideoFrames) {
        this(targetVideoArchiveName, originalVideoFrames, false);
    }

    /**
     *
     * @param targetVideoArchiveName The name of the VideoArchive that you are moving VideoFrames to.
     * @param originalVideoFrames The videoFrames that are being moved
     * @param refreshVideoArchive if true then a VideoArchiveChangedEvent is fired using the VideoArchive
     *                            in the current Lookup. (i.e. refreshes annotation window)
     */
    public MoveVideoFramesCmd(String targetVideoArchiveName,
                              Collection<VideoFrame> originalVideoFrames, boolean refreshVideoArchive) {
        if (targetVideoArchiveName == null || originalVideoFrames == null) {
            throw new IllegalArgumentException("null parameters are not allowed in the constructor");
        }
        this.refreshVideoArchive = refreshVideoArchive;
        this.targetVideoArchiveName = targetVideoArchiveName;
        for (VideoFrame videoFrame : originalVideoFrames) {
            originalData.add(new DataBean(videoFrame.getVideoArchive().getName(), videoFrame.getPrimaryKey()));
        }
    }

    @Override
    public void apply(ToolBelt toolBelt) {
        Collection<VideoFrame> modifiedVideoFrames = new ArrayList<VideoFrame>(originalData.size());

        VideoArchiveDAO dao = toolBelt.getAnnotationDAOFactory().newVideoArchiveDAO();
        VideoFrameDAO videoFrameDao = toolBelt.getAnnotationDAOFactory().newVideoFrameDAO(dao.getEntityManager());
        dao.startTransaction();
        VideoArchive videoArchive = dao.findByName(targetVideoArchiveName);
        for (DataBean bean : originalData) {
            VideoFrame videoFrame = videoFrameDao.findByPrimaryKey(bean.videoFramePrimaryKey); // Pull existing copy from db and bring into transation
            VideoArchive sourceVideoArchive = videoFrame.getVideoArchive();
            if (sourceVideoArchive != null && !sourceVideoArchive.getName().equals(videoArchive.getName())) {
                sourceVideoArchive.removeVideoFrame(videoFrame);

                /*
                 * Check to see if the target VideoArchive already has a
                 * VideoFrame with that timecode.
                 */
                VideoFrame targetVideoFrame = videoArchive.findVideoFrameByTimeCode(videoFrame.getTimecode());
                if (targetVideoFrame != null) {
                    mergeFrames(videoFrame, targetVideoFrame);
                    dao.remove(videoFrame);
                    modifiedVideoFrames.add(targetVideoFrame);
                }
                else {
                    videoArchive.addVideoFrame(videoFrame);
                    modifiedVideoFrames.add(videoFrame);
                }

            }
        }
        dao.endTransaction();

        EventBus.publish(new VideoFramesChangedEvent(null, modifiedVideoFrames));

        if (refreshVideoArchive) {
            VideoArchive va = StateLookup.getVideoArchive();
            va = dao.find(va);
            EventBus.publish(new VideoArchiveChangedEvent(MoveVideoFramesCmd.this, va));
        }
    }

    @Override
    public void unapply(ToolBelt toolBelt) {
        Collection<VideoFrame> modifiedVideoFrames = new ArrayList<VideoFrame>(originalData.size());

        VideoArchiveDAO dao = toolBelt.getAnnotationDAOFactory().newVideoArchiveDAO();
        VideoFrameDAO videoFrameDao = toolBelt.getAnnotationDAOFactory().newVideoFrameDAO(dao.getEntityManager());
        dao.startTransaction();
        for (DataBean bean : originalData) {
            VideoArchive videoArchive = dao.findByName(bean.originalVideoArchiveName);
            if (videoArchive != null) {
                VideoFrame videoFrame = videoFrameDao.findByPrimaryKey(bean.videoFramePrimaryKey); // Pull existing copy from db and bring into transation
                VideoArchive sourceVideoArchive = videoFrame.getVideoArchive();
                if (sourceVideoArchive != null && !sourceVideoArchive.getName().equals(videoArchive.getName())) {
                    sourceVideoArchive.removeVideoFrame(videoFrame);

                    /*
                     * Check to see if the target VideoArchive already has a
                     * VideoFrame with that timecode.
                     */
                    VideoFrame targetVideoFrame = videoArchive.findVideoFrameByTimeCode(videoFrame.getTimecode());
                    if (targetVideoFrame != null) {
                        mergeFrames(videoFrame, targetVideoFrame);
                        dao.remove(videoFrame);
                        modifiedVideoFrames.add(targetVideoFrame);
                    }
                    else {
                        videoArchive.addVideoFrame(videoFrame);
                        modifiedVideoFrames.add(videoFrame);
                    }

                }
            }
        }
        dao.endTransaction();

        EventBus.publish(new VideoFramesChangedEvent(null, modifiedVideoFrames));

        if (refreshVideoArchive) {
            VideoArchive va = StateLookup.getVideoArchive();
            va = dao.find(va);
            EventBus.publish(new VideoArchiveChangedEvent(MoveVideoFramesCmd.this, va));
        }
    }

    @Override
    public String getDescription() {
        return "Move " + originalData.size() + " VideoFrames to VideoArchive named " + targetVideoArchiveName;
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


    private class DataBean {
        String originalVideoArchiveName;
        Object videoFramePrimaryKey;

        private DataBean(String originalVideoArchiveName, Object videoFramePrimaryKey) {
            this.originalVideoArchiveName = originalVideoArchiveName;
            this.videoFramePrimaryKey = videoFramePrimaryKey;
        }
    }

}
