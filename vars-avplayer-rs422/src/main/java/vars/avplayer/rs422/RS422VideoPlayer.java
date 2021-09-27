package vars.avplayer.rs422;

import org.bushe.swing.event.EventBus;
import mbarix4j.util.Tuple2;
import org.mbari.vcr4j.SimpleVideoIO;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.decorators.SchedulerVideoIO;
import org.mbari.vcr4j.decorators.VCRSyncDecorator;
import org.mbari.vcr4j.rs422.RS422Error;
import org.mbari.vcr4j.rs422.RS422State;
import org.mbari.vcr4j.rs422.RS422VideoIO;
import org.mbari.vcr4j.rs422.decorators.RS422StatusDecorator;
import org.mbari.vcr4j.rs422.decorators.UserbitsAsTimeDecorator;
import org.mbari.vcr4j.rxtx.RXTX;
import org.mbari.vcr4j.rxtx.RXTXVideoIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.ToolBelt;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Brian Schlining
 * @since 2016-04-05T12:32:00
 */
public class RS422VideoPlayer implements VideoPlayer<RS422State, RS422Error> {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final AtomicReference<VideoIO<RS422State, RS422Error>> videoIORef = new AtomicReference<>();
    private ImageCaptureService imageCaptureService = ImageCaptureServiceRef.getImageCaptureService();
    private RS422VideoPlayerDialogUI dialogUI;

    public RS422VideoPlayer() {
       try {
         
         RXTX.setup();
       } 
       catch (UnsatisfiedLinkError e) {
          log.warn("Failed to setup RXTX native libraries", e);
       }
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
    public CompletableFuture<Tuple2<VideoArchive, VideoController<RS422State, RS422Error>>> openVideoArchive(ToolBelt toolBelt, Object... args) {
        String serialPort = (String) args[0];
        String platformName = (String) args[1];
        Integer sequenceNumber = (Integer) args[2];
        Integer tapeNumber = (Integer) args[3];
        Boolean isHD = (Boolean) args[4];
        RS422VideoParams videoParams = new RS422VideoParams(serialPort, platformName, sequenceNumber, tapeNumber, isHD);
        return CompletableFuture.supplyAsync(() -> openVideoArchive(toolBelt, videoParams));
    }

    public Tuple2<VideoArchive, VideoController<RS422State, RS422Error>> openVideoArchive(ToolBelt toolBelt, RS422VideoParams videoParams) {
        VideoArchiveDAO dao = toolBelt.getAnnotationDAOFactory().newVideoArchiveDAO();
        VideoArchive videoArchive = dao.findOrCreateByParameters(videoParams.getPlatformName(),
                videoParams.getSequenceNumber(),
                videoParams.getVideoArchiveName());
        dao.close();

        // Close the port if it's different
        setSerialPort(videoParams.getSerialPortName());

        return new Tuple2<>(videoArchive, getVideoController());
    }

    private void setSerialPort(String serialPortName) {
        // Close the port if it's different
        if (videoIORef.get() != null) {
            VideoIO<RS422State, RS422Error> videoIO = videoIORef.get();
            String connectionID = videoIO.getConnectionID();
            if (!connectionID.equalsIgnoreCase(serialPortName)) {
                videoIO.close();
                videoIORef.set(null);
            }
        }

        // Connect to the port if needed
        if (videoIORef.get() == null) {
            videoIORef.set(newVideoIO(serialPortName));
        }
    }

    private VideoIO<RS422State, RS422Error> newVideoIO(String serialPortName) {
        VideoIO<RS422State, RS422Error> io = null;
        try {
            RS422VideoIO rawIO = RXTXVideoIO.open(serialPortName);
            //RS422VideoIO rawIO = SerialCommVideoIO.open(serialPortName);
            // Keep UI in sync by scheduling status/time requests
            new VCRSyncDecorator<>(rawIO);
            // Keep UI in sync by sending status/timecode requests after commands
            new RS422StatusDecorator(rawIO);
            // Listen to time stored in userbits and decode it.
            UserbitsAsTimeDecorator timeDecorator = new UserbitsAsTimeDecorator(rawIO);
            // Hide all the complexity.
            VideoIO<RS422State, RS422Error> simpleIO = new SimpleVideoIO<>(rawIO.getConnectionID(),
                    rawIO.getCommandSubject(),
                    rawIO.getStateObservable(),
                    rawIO.getErrorObservable(),
                    timeDecorator.getIndexObservable());
            // Move IO off of current thread
            io = new SchedulerVideoIO<>(simpleIO, Executors.newCachedThreadPool());
            io.send(VideoCommands.REQUEST_INDEX);
            io.send(VideoCommands.REQUEST_STATUS);
        }
        catch (Exception e) {
            EventBus.publish(GlobalStateLookup.TOPIC_NONFATAL_ERROR, e);
        }
        return io;
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
                //eventBus.send(new SetVideoArchiveMsg(null));
                // Do lookup off of Swing's thread
//                Runnable r = ()  -> {
                    VideoArchive videoArchive = dialogUI.openVideoArchive();
                    setSerialPort(dialogUI.getSerialPortName());
                    eventBus.send(new SetVideoArchiveMsg(videoArchive));
                    eventBus.send(new SetVideoControllerMsg<>(getVideoController()));
//                };
//                new Thread(r).run();
            });
        }
        return dialogUI;
    }

    @Override
    public String getName() {
        return "VCR via RS422";
    }


}
