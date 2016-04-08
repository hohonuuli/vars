package vars.avplayer.rs422;

import org.mbari.util.Tuple2;
import org.mbari.vcr4j.rs422.RS422Error;
import org.mbari.vcr4j.rs422.RS422State;
import org.mbari.vcr4j.rs422.RS422VideoIO;
import org.mbari.vcr4j.rxtx.RXTX;
import org.mbari.vcr4j.rxtx.RXTXVideoIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.ToolBelt;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.avfoundation.AVFImageCaptureServiceImpl;
import vars.avplayer.ImageCaptureService;
import vars.avplayer.VideoController;
import vars.avplayer.VideoPlayer;
import vars.avplayer.VideoPlayerDialogUI;
import vars.avplayer.rx.SetVideoArchiveMsg;
import vars.avplayer.rx.SetVideoControllerMsg;
import vars.shared.rx.RXEventBus;
import vars.shared.ui.GlobalStateLookup;

import java.awt.*;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Brian Schlining
 * @since 2016-04-05T12:32:00
 */
public class RS422VideoPlayer implements VideoPlayer<RS422State, RS422Error> {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final AtomicReference<RS422VideoIO> videoIORef = new AtomicReference<>();
    private ImageCaptureService imageCaptureService = ImageCaptureServiceRef.getImageCaptureService();
    private RS422VideoPlayerDialogUI dialogUI;

    public RS422VideoPlayer() {
        RXTX.setup();
    }

    @Override
    public boolean canPlay(String mimeType) {
        return false;
    }



    /**
     *
     * @param toolBelt
     * @param args The arguments need to connect to you video control service.
     *             [serialPort: String,
     *             platformName: String,
     *             sequenceNumber: Integer,
     *             tapeNumber: Integer,
     *             isHD: Boolean]
     * @return
     */
    @Override
    public Optional<Tuple2<VideoArchive, VideoController<RS422State, RS422Error>>> openVideoArchive(ToolBelt toolBelt, Object... args) {
        String serialPort = (String) args[0];
        String platformName = (String) args[1];
        Integer sequenceNumber = (Integer) args[2];
        Integer tapeNumber = (Integer) args[3];
        Boolean isHD = (Boolean) args[4];
        RS422VideoParams videoParams = new RS422VideoParams(serialPort, platformName, sequenceNumber, tapeNumber, isHD);
        return openVideoArchive(toolBelt, videoParams);
    }

    public Optional<Tuple2<VideoArchive, VideoController<RS422State, RS422Error>>> openVideoArchive(ToolBelt toolBelt, RS422VideoParams videoParams) {
        VideoArchiveDAO dao = toolBelt.getAnnotationDAOFactory().newVideoArchiveDAO();
        VideoArchive videoArchive = dao.findOrCreateByParameters(videoParams.getPlatformName(),
                videoParams.getSequenceNumber(),
                videoParams.getVideoArchiveName());
        dao.close();

        // Close the port if it's different
        setSerialPort(videoParams.getSerialPortName());

        return Optional.of(new Tuple2<>(videoArchive, getVideoController()));
    }

    private void setSerialPort(String serialPortName) {
        // Close the port if it's different
        if (videoIORef.get() != null) {
            RS422VideoIO videoIO = videoIORef.get();
            String connectionID = videoIO.getConnectionID();
            if (!connectionID.equalsIgnoreCase(serialPortName)) {
                videoIO.close();
                videoIORef.set(null);
            }
        }

        // Connect to the port if needed
        if (videoIORef.get() == null) {
            RS422VideoIO videoIO = RXTXVideoIO.open(serialPortName);
            videoIORef.set(videoIO);
        }
    }

    private VideoController<RS422State, RS422Error> getVideoController() {
        return new VideoController<>(imageCaptureService, videoIORef.get());
    }

    @Override
    public VideoPlayerDialogUI<RS422State, RS422Error> getConnectionDialog(ToolBelt toolBelt, RXEventBus eventBus) {
        if (dialogUI == null) {
            Window window = GlobalStateLookup.getSelectedFrame();
            dialogUI = new RS422VideoPlayerDialogUI(window, toolBelt);
            dialogUI.onOkay(() -> {
                dialogUI.setVisible(false);
                VideoArchive videoArchive = dialogUI.openVideoArchive();
                setSerialPort(dialogUI.getSerialPortName());
                eventBus.send(new SetVideoArchiveMsg(videoArchive));
                eventBus.send(new SetVideoControllerMsg<>(getVideoController()));
            });
        }
        return dialogUI;
    }

    @Override
    public String getName() {
        return "RS422";
    }


}
