package vars.queryfx;

import com.google.common.base.Preconditions;
import mbarix4j.sql.QueryResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.ILink;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.Media;
import vars.query.QueryPersistenceService;
import vars.queryfx.beans.ConceptSelection;
import vars.queryfx.beans.ResolvedConceptSelection;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2015-07-22T10:42:00
 */
public class AsyncQueryServiceImpl implements AsyncQueryService {


    private final Executor executor;
    private final QueryPersistenceService queryPersistenceService;
    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;
    private final Function<Concept, Collection<String>> asNames = c ->
            c.getConceptNames().stream().map(ConceptName::getName).collect(Collectors.toList());

    private final Logger log = LoggerFactory.getLogger(getClass());


    @Inject
    public AsyncQueryServiceImpl(Executor executor,
            KnowledgebaseDAOFactory knowledgebaseDAOFactory,
            QueryPersistenceService queryPersistenceService) {
        this.executor = executor;
        this.knowledgebaseDAOFactory = knowledgebaseDAOFactory;
        this.queryPersistenceService = queryPersistenceService;
    }

    @Override
    public CompletableFuture<List<String>> findAllConceptNamesAsStrings() {
        return CompletableFuture.supplyAsync(queryPersistenceService::findAllConceptNamesAsStrings, executor);
    }

    @Override
    public CompletableFuture<List<String>> findDescendantNamesAsStrings(String conceptName) {
        return CompletableFuture.supplyAsync(() -> queryPersistenceService.findDescendantNamesAsStrings(conceptName), executor);
    }

    @Override
    public CompletableFuture<Optional<Concept>> findConcept(String name) {
        return CompletableFuture.supplyAsync(
                () -> Optional.ofNullable(knowledgebaseDAOFactory.newConceptDAO().findByName(name)),
                executor);
    }

    public CompletableFuture<Collection<Concept>> findConcepts(String name,
            boolean extendToParent,
            boolean extendToSiblings,
            boolean extendToChildren,
            boolean extendToDescendants) {

        Preconditions.checkArgument(name != null, "You must supply a concept name");

        return CompletableFuture.supplyAsync(() -> queryPersistenceService.findConcepts(name, extendToParent,
                extendToSiblings, extendToChildren, extendToDescendants), executor);

    }

    @Override
    public CompletableFuture<List<String>> findConceptNamesAsStrings(String name,
            boolean extendToParent,
            boolean extendToSiblings,
            boolean extendToChildren,
            boolean extendToDescendants) {

        return findConcepts(name, extendToParent, extendToSiblings, extendToChildren, extendToDescendants).handleAsync((cs, t) -> {
            List<String> names;
            if (cs != null) {
                names = cs.stream()
                        .flatMap(c -> asNames.apply(c).stream())
                        .sorted()
                        .collect(Collectors.toList());
            }
            else {
                names = new ArrayList<>();
            }
            return names;

        }, executor);
    }

    @Override
    public CompletableFuture<List<ILink>> findAllLinks() {
        return CompletableFuture.supplyAsync(() ->
                        new ArrayList<>(queryPersistenceService.findAllLinkTemplates()),
                executor);
    }

    @Override
    public CompletableFuture<List<ILink>> findLinksByConceptNames(Collection<String> conceptNames) {
        return CompletableFuture.supplyAsync(() ->
                new ArrayList<>(queryPersistenceService.findLinksByConceptNames(conceptNames)), executor);
    }


    @Override
    public CompletableFuture<Optional<URL>> resolveImageURL(String conceptName) {
        return findConcept(conceptName).thenApplyAsync(co ->
                co.flatMap(c -> {
                    Media primaryImage = c.getConceptMetadata().getPrimaryImage();
                    Optional<URL> url = Optional.empty();
                    if (primaryImage != null) {
                        try {
                            url = Optional.of(new URL(primaryImage.getUrl()));
                        }
                        catch (MalformedURLException e) {
                            log.info("Invalid URL for '" + conceptName + "': " + primaryImage.getUrl());
                        }
                    }
                    return url;
                }), executor);
    }

    @Override
    public CompletableFuture<Map<String, String>> getAnnotationViewMetadata() {
        return CompletableFuture.supplyAsync(queryPersistenceService::getMetaData, executor);
    }

    @Override
    public CompletableFuture<Collection<?>> getAnnotationViewsUniqueValuesForColumn(String columnName) {
        return CompletableFuture.supplyAsync(() -> queryPersistenceService.getUniqueValuesByColumn(columnName), executor);
    }

    @Override
    public CompletableFuture<List<Number>> getAnnotationViewsMinAndMaxForColumn(String columnName) {
        final String sql = "SELECT MIN(" + columnName + ") AS minValue, MAX(" + columnName +
                ") AS maxValue FROM Annotations WHERE " + columnName + " IS NOT NULL";

        return CompletableFuture.supplyAsync(() -> {

            List<Number> minMax = new ArrayList<>();
            try {
                QueryResults queryResults = queryPersistenceService.executeQuery(sql);
                minMax.add((Number) queryResults.getResults("minValue").get(0));
                minMax.add((Number) queryResults.getResults("maxValue").get(0));
            }
            catch (Exception e) {
                log.error("An error occurred while executing the SQL statement: '" + sql + "'", e);
            }

            return minMax;
        }, executor);
    }

    @Override
    public CompletableFuture<List<Date>> getAnnotationViewsMinAndMaxDatesforColumn(String columnName) {
        final String sql = "SELECT MIN(" + columnName + ") AS minValue, MAX(" + columnName +
                ") AS maxValue FROM Annotations WHERE " + columnName + " IS NOT NULL";

        return CompletableFuture.supplyAsync(() -> {

            List<Date> minMax = new ArrayList<>();
            try {
                QueryResults queryResults = queryPersistenceService.executeQuery(sql);
                minMax.add((Date) queryResults.getResults("minValue").get(0));
                minMax.add((Date) queryResults.getResults("maxValue").get(0));
            }
            catch (Exception e) {
                log.error("An error occurred while executing the SQL statement: '" + sql + "'", e);
            }

            return minMax;
        }, executor);
    }

    @Override
    public Connection getAnnotationConnection() throws SQLException {
        return queryPersistenceService.getAnnotationQueryable().getConnection();
    }

    /**
     *
     * @param conceptName The concept whos ancestorw we will look up
     * @return A list of ancestors from the root down to, and including, the named concept
     */
    @Override
    public CompletableFuture<List<Concept>> findAncestors(String conceptName) {
        return CompletableFuture.supplyAsync(() -> queryPersistenceService.findAncestors(conceptName), executor);
    }


    @Override
    public CompletableFuture<ResolvedConceptSelection> resolveConceptSelection(ConceptSelection conceptSelection) {
        return findConceptNamesAsStrings(conceptSelection.getConceptName(),
                conceptSelection.isExtendToParent(),
                conceptSelection.isExtendToSiblings(),
                conceptSelection.isExtendToChildren(),
                conceptSelection.isExtendToDescendants())
                .thenApplyAsync(list -> new ResolvedConceptSelection(conceptSelection, list), executor);
    }

}

