package vars.queryfx.ui.controllers;

import com.typesafe.config.Config;
import javafx.application.Platform;
import javafx.scene.image.Image;
import vars.queryfx.Lookup;
import vars.queryfx.QueryService;
import vars.queryfx.RXEventBus;
import vars.queryfx.beans.ConceptSelection;
import vars.queryfx.beans.ResolvedConceptSelection;
import vars.queryfx.messages.NewConceptSelectionMsg;
import vars.queryfx.messages.NewResolvedConceptSelectionMsg;

import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author Brian Schlining
 * @since 2015-07-26T11:29:00
 */
public class AppController {

    private final QueryService queryService;
    private final RXEventBus eventBus;

    public AppController(QueryService queryService, RXEventBus eventBus) {
        this.queryService = queryService;
        this.eventBus = eventBus;

        eventBus.toObserverable()
                .filter(msg -> msg instanceof NewConceptSelectionMsg)
                .map(msg -> (NewConceptSelectionMsg) msg)
                .subscribe(msg -> addConceptSelection(msg.getConceptSelection()));
    }

    protected void addConceptSelection(ConceptSelection conceptSelection) {

        CompletableFuture<Optional<URL>> urlF = queryService.resolveImageURL(conceptSelection.getConceptName());

        queryService.resolveConceptSelection(conceptSelection)
                .thenCombineAsync(urlF, (rcs, urlOpt) -> {
                    if (urlOpt.isPresent()) {
                        Platform.runLater(() -> {
                            Image image = new Image(urlOpt.get().toExternalForm(), true);
                            rcs.imageProperty().set(image);
                        });
                    }
                    return rcs;

                })
                .thenAccept(rcs -> eventBus.send(new NewResolvedConceptSelectionMsg(rcs)));

    }
    

}
