package vars.queryfx.ui.controllers;

import com.typesafe.config.Config;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.VARSException;
import vars.queryfx.Lookup;
import vars.queryfx.QueryService;
import vars.queryfx.RXEventBus;
import vars.queryfx.beans.ConceptSelection;
import vars.queryfx.beans.QueryParams;
import vars.queryfx.beans.ResolvedConceptSelection;
import vars.queryfx.beans.ResultsCustomization;
import vars.queryfx.messages.ExecuteSearchMsg;
import vars.queryfx.messages.NewConceptSelectionMsg;
import vars.queryfx.messages.NewQueryResultsMsg;
import vars.queryfx.messages.NewResolvedConceptSelectionMsg;
import vars.queryfx.ui.QueryResultsTableView;
import vars.queryfx.ui.db.ConceptConstraint;
import vars.queryfx.ui.db.IConstraint;
import vars.queryfx.ui.db.PreparedStatementGenerator;
import vars.queryfx.ui.db.results.AssociationColumnRemappingDecorator;
import vars.queryfx.ui.db.results.CoalescingDecorator;
import vars.queryfx.ui.db.results.QueryResults;
import vars.queryfx.ui.db.results.QueryResultsDecorator;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author Brian Schlining
 * @since 2015-07-26T11:29:00
 */
public class AppController {

    private final QueryService queryService;
    private final RXEventBus eventBus;
    private final Executor excutor;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public AppController(QueryService queryService, RXEventBus eventBus, Executor executor) {
        this.queryService = queryService;
        this.eventBus = eventBus;
        this.excutor = executor;

        eventBus.toObserverable()
                .filter(msg -> msg instanceof NewConceptSelectionMsg)
                .map(msg -> (NewConceptSelectionMsg) msg)
                .subscribe(msg -> addConceptSelection(msg.getConceptSelection()));

        eventBus.toObserverable()
                .filter(msg -> msg instanceof ExecuteSearchMsg)
                .map(msg -> (ExecuteSearchMsg) msg)
                .subscribe(msg -> executeSearch(msg));

        eventBus.toObserverable()
                .filter(msg -> msg instanceof NewQueryResultsMsg)
                .map(msg -> (NewQueryResultsMsg) msg)
                .subscribe(msg -> showQueryResults(msg));
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

    protected void executeSearch(ExecuteSearchMsg msg) {
        // TODO create a stage that shows elapse time. Reuse it to display results.

        runQuery(msg.getQueryReturns(),
                msg.getConceptConstraints(),
                msg.getQueryConstraints(),
                msg.getResultsCustomization()).thenAccept(queryResults -> {

            eventBus.send(new NewQueryResultsMsg(queryResults));
        });
    }

    public CompletableFuture<QueryResults> runQuery(List<String> queryReturns,
            List<ConceptConstraint> conceptConstraints,
            List<IConstraint> queryConstraints,
            ResultsCustomization resultsCustomization) {

        return CompletableFuture.supplyAsync(() -> {

            PreparedStatementGenerator psg = new PreparedStatementGenerator();

            try {
                String template = psg.getPreparedStatementTemplate(queryReturns,
                        conceptConstraints,
                        queryConstraints,
                        resultsCustomization);

                if (log.isDebugEnabled()) {
                    log.debug("PreparedStatement Template: " + template);
                }
                Connection connection = queryService.getAnnotationConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(template);
                psg.bind(preparedStatement, conceptConstraints, queryConstraints);
                QueryResults queryResults = QueryResults.fromResultSet(preparedStatement.executeQuery());

                if (resultsCustomization.isCategorizeAssociations()) {
                    queryResults = AssociationColumnRemappingDecorator.apply(queryResults);
                }

                queryResults = CoalescingDecorator.coalesce(queryResults, "ObservationID_FK");

                QueryResultsDecorator queryResultsDecorator = new QueryResultsDecorator(queryService);
                if (resultsCustomization.isConceptHierarchy()) {
                    queryResults = queryResultsDecorator.addHierarchy(queryResults);
                }

                if (resultsCustomization.isDetailedPhylogeny()) {
                    queryResults = queryResultsDecorator.addFullPhylogeny(queryResults);
                }
                else if (resultsCustomization.isBasicPhylogeny()) {
                    queryResults = queryResultsDecorator.addBasicPhylogeny(queryResults);
                }

                return queryResults;

            }
            catch (SQLException e) {
                throw new VARSException("Failed to execute prepared statement", e);
            }
        }, excutor);

    }

    protected void showQueryResults(NewQueryResultsMsg msg) {
        Platform.runLater(() -> {
            TableView<String[]> tableView = QueryResultsTableView.newTableView(msg.getQueryResults());
            BorderPane borderPane = new BorderPane(tableView);
            Scene scene = new Scene(borderPane);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        });
    }


}
