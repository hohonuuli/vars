package vars.avplayer.sharktopoda;

import org.bushe.swing.event.EventBus;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.sharktopoda.SharktopodaVideoIO;
import org.mbari.vcr4j.sharktopoda.commands.FramecaptureCmd;
import org.mbari.vcr4j.sharktopoda.commands.SharkCommands;
import org.mbari.vcr4j.sharktopoda.decorators.FramecaptureDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.avplayer.ImageCaptureException;
import vars.avplayer.ImageCaptureService;
import vars.shared.ui.GlobalStateLookup;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by brian on 9/1/16.
 */
public class SharktopodaImageCaptureService implements ImageCaptureService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final SharktopodaVideoIO io;
    private final FramecaptureDecorator decorator;

    public SharktopodaImageCaptureService(SharktopodaVideoIO io, int port) {
        this.io = io;
        decorator = new FramecaptureDecorator(io, port);
    }

    @Override
    public Optional<Image> capture(File file) throws ImageCaptureException {

        CompletableFuture<Optional<Image>> future = new CompletableFuture<>();

        // Sharktopoda will send a response once the image is written
        decorator.getFramecaptureObservable()
                .first()
                .forEach(r -> {
                    // -- Read file as image
                    BufferedImage image = null;
                    try {
                        image = ImageIO.read(r.getImageLocation());
                    } catch (Exception e) {
                        EventBus.publish(GlobalStateLookup.TOPIC_WARNING, e);
                    }
                    future.complete(Optional.ofNullable(image));
                });

        io.send(new FramecaptureCmd(UUID.randomUUID(), file));

        Optional<Image> image;
        try {
            image = future.get(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException | TimeoutException | ExecutionException e) {
            image = Optional.empty();
        }

        return image;
    }

    @Override
    public void dispose() {
        decorator.unsubscribe();
    }

    @Override
    public void showSettingsDialog() {
        // TODO allow user to configure the port number
    }

}
