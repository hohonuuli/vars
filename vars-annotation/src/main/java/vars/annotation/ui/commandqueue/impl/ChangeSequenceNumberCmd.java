package vars.annotation.ui.commandqueue.impl;

import org.bushe.swing.event.EventBus;
import vars.VARSPersistenceException;
import vars.annotation.AnnotationFactory;
import vars.annotation.CameraDeployment;
import vars.annotation.FormatCodes;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.annotation.VideoArchiveSet;
import vars.annotation.VideoArchiveSetDAO;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.eventbus.VideoArchiveChangedEvent;
import vars.annotation.ui.eventbus.VideoArchiveSetChangedEvent;

import java.util.Collection;
import java.util.Set;

/**
 * @author Brian Schlining
 * @since 2015-11-04T19:39:00
 */
public class ChangeSequenceNumberCmd implements Command {

    private final int newSequenceNumber;
    private final int oldSequenceNumber;
    private final Long videoArchiveID;

    public ChangeSequenceNumberCmd(int newSequenceNumber, int oldSequenceNumber, Long videoArchiveID) {
        this.newSequenceNumber = newSequenceNumber;
        this.oldSequenceNumber = oldSequenceNumber;
        this.videoArchiveID = videoArchiveID;
    }

    @Override
    public void apply(ToolBelt toolBelt) {
        doChange(toolBelt, true);
    }

    @Override
    public void unapply(ToolBelt toolBelt) {
        doChange(toolBelt, false);
    }

    private void doChange(ToolBelt toolBelt, boolean isApply) {
        int newNumber = isApply ? newSequenceNumber : oldSequenceNumber;
        int oldNumber = isApply ? oldSequenceNumber : newSequenceNumber;
        VideoArchiveDAO dao = toolBelt.getAnnotationDAOFactory().newVideoArchiveDAO();
        VideoArchiveSetDAO vasDao = toolBelt.getAnnotationDAOFactory().newVideoArchiveSetDAO(dao.getEntityManager());
        dao.startTransaction();
        VideoArchive va = dao.findByPrimaryKey(videoArchiveID);
        VideoArchiveSet videoArchiveSet = null;
        if (va != null) {
            String platformName = va.getVideoArchiveSet().getPlatformName();
            Collection<VideoArchiveSet> vasList = vasDao.findAllByPlatformAndSequenceNumber(platformName, newNumber);
            if (vasList.isEmpty()) {
                // No match. Create a new one and move VideoArchive to it.
                // DO NOT Just change the seqnumber of currently one as that
                // will change it for all attached videoarchives
                AnnotationFactory factory = toolBelt.getAnnotationFactory();
                videoArchiveSet = factory.newVideoArchiveSet();
                videoArchiveSet.setPlatformName(platformName);
                videoArchiveSet.setFormatCode(FormatCodes.UNKNOWN.getCode());
                vasDao.persist(videoArchiveSet);
                CameraDeployment cameraDeployment = factory.newCameraDeployment();
                cameraDeployment.setSequenceNumber(newNumber);
                videoArchiveSet.addCameraDeployment(cameraDeployment);
                dao.persist(cameraDeployment);
                va.getVideoArchiveSet().removeVideoArchive(va);
                videoArchiveSet.addVideoArchive(va);
            }
            else if (vasList.size() == 1){
                // A match was found. Move VideoArchive to it
                videoArchiveSet = vasList.iterator().next();
                va.getVideoArchiveSet().removeVideoArchive(va);
                videoArchiveSet.addVideoArchive(va);
            }
            else {
                throw new VARSPersistenceException("There's a problem!! More than one VideoArchiveSet " +
                        "with platform = " + platformName + " and sequenceNumber = " + newNumber +
                        " exists in the database");
            }
        }

        dao.commit();
        dao.endTransaction();
        dao.close();

        if (videoArchiveSet != null) {
            EventBus.publish(new VideoArchiveSetChangedEvent(null, videoArchiveSet));
        }
    }

    @Override
    public String getDescription() {
        return "Change SequenceNumber of VideoArchive with id = " +
                videoArchiveID + " from " + oldSequenceNumber +
                " to " + newSequenceNumber;
    }
}
