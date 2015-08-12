package vars.shared.javafx.application;

import javafx.application.Platform;
import javafx.stage.Stage;
import vars.shared.javafx.stage.ImageStage;

import java.util.concurrent.CompletableFuture;

/**
 * @author Brian Schlining
 * @since 2015-08-11T11:57:00
 */
public class ImageFXDemo {

    public static void main(String[] args) {
        final CompletableFuture<ImageStage> w = ImageFX.namedWindow("Foo", "http://www.mbari.org/staff/brian/images/storage/brian-schlining5_sm.jpg");
        w.thenAccept(stage -> {
            Platform.runLater(() -> stage.show());
        });
    }
}
