package vars.annotation.ui.commandqueue.impl;

import org.bushe.swing.event.EventBus;
import vars.annotation.AnnotationFactory;
import vars.annotation.CameraDeployment;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.annotation.VideoArchiveSet;
import vars.annotation.VideoArchiveSetDAO;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.eventbus.VideoArchiveSetChangedEvent;

import java.util.Collection;

/**
 * @author Brian Schlining
 * @since 2011-10-18
 */
public class ChangeVideoArchiveNameCmd implements Command {

    private final Object primaryKey;
    private final DataBean originalData;
    private final DataBean modifiedData;

    public ChangeVideoArchiveNameCmd(VideoArchive videoArchive, String newName, String newPlatformName,
                                     int newSequenceNumber) {
        this.primaryKey = videoArchive.getPrimaryKey();
        String platformName = videoArchive.getVideoArchiveSet().getPlatformName();
        int sequenceNumber = videoArchive.getVideoArchiveSet().getCameraDeployments().iterator().next().getSequenceNumber();
        originalData = new DataBean(platformName, sequenceNumber, videoArchive.getName());
        modifiedData = new DataBean(newPlatformName, newSequenceNumber, newName);
    }

    @Override
    public void apply(ToolBelt toolBelt) {
        doCommand(toolBelt, true);
    }

    @Override
    public void unapply(ToolBelt toolBelt) {
        doCommand(toolBelt, false);
    }

    private void doCommand(ToolBelt toolBelt, boolean isApply) {
        DataBean data = isApply ? modifiedData : originalData;
        VideoArchiveDAO dao = toolBelt.getAnnotationDAOFactory().newVideoArchiveDAO();
        dao.startTransaction();
        VideoArchive matchingVideoArchive = dao.findByName(data.videoArchiveName);
        if (matchingVideoArchive != null) {
            DataBean oldData = isApply ? originalData : modifiedData;
            EventBus.publish(Lookup.TOPIC_WARNING, "A VideoArchive named " + data.videoArchiveName +
                    " already exists. Unable to rename " + oldData.videoArchiveName);
        }
        else {
            VideoArchiveSetDAO videoArchiveSetDAO = toolBelt.getAnnotationDAOFactory().newVideoArchiveSetDAO(dao.getEntityManager());
            VideoArchive videoArchive = dao.findByPrimaryKey(primaryKey);
            if (videoArchive != null) {
                /*
                 *  Check the database for an existing matching VideoArchiveSet
                 */
                VideoArchiveSet videoArchiveSet = null;
                Collection<VideoArchiveSet> videoArchiveSets = videoArchiveSetDAO.findAllByPlatformAndSequenceNumber(data.platformName,
                    data.sequenceNumber);
                if (videoArchiveSets.size() > 0) {
                    videoArchiveSet = videoArchiveSets.iterator().next();
                }
                else {
                    /*
                     * No matching VideoArchiveSet was found so create one
                     */
                    AnnotationFactory annotationFactory = toolBelt.getAnnotationFactory();
                    videoArchiveSet = annotationFactory.newVideoArchiveSet();
                    videoArchiveSet.setPlatformName(data.platformName);
                    dao.persist(videoArchiveSet);
                    CameraDeployment cameraDeployment = annotationFactory.newCameraDeployment();
                    cameraDeployment.setSequenceNumber(data.sequenceNumber);
                    videoArchiveSet.addCameraDeployment(cameraDeployment);
                    dao.persist(cameraDeployment);
                }

                // Move the VideoArchive form the old set to the new one
                videoArchive.getVideoArchiveSet().removeVideoArchive(videoArchive);
                videoArchive.setName(data.videoArchiveName);
                videoArchiveSet.addVideoArchive(videoArchive);

                EventBus.publish(new VideoArchiveSetChangedEvent(null, videoArchiveSet));
            }
        }
        dao.endTransaction();
        dao.close();


    }

    @Override
    public String getDescription() {
        return "Rename VideoArchive from " + originalData.videoArchiveName + " to " + modifiedData.videoArchiveName;
    }

    private class DataBean {
        final String platformName;
        final int sequenceNumber;
        final String videoArchiveName;

        private DataBean(String platformName, int sequenceNumber, String videoArchiveName) {
            this.platformName = platformName;
            this.sequenceNumber = sequenceNumber;
            this.videoArchiveName = videoArchiveName;
        }
    }
}
