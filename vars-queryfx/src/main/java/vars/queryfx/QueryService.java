package vars.queryfx;

import vars.ILink;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptName;
import vars.queryfx.beans.ConceptSelection;
import vars.queryfx.beans.ResolvedConceptSelection;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * @author Brian Schlining
 * @since 2015-07-20T15:55:00
 */
public interface QueryService {

    /**
     * Lookup all names that are associated with
     * this {@link Concept} and its children. Ordered alphabetically.
     *
     *
     * @param conceptName The concept whos descendant names are returne
     * @return
     */
    CompletableFuture<List<String>> findDescendantNamesAsStrings(String conceptName);


    CompletableFuture<List<Concept>> findAncestors(String conceptName);

    CompletableFuture<Optional<Concept>> findConcept(String name);

    CompletableFuture<List<String>> findConceptNamesAsStrings(String name,
                                                              boolean extendToParent,
                                                              boolean extendToSiblings,
                                                              boolean extendToChildren,
                                                              boolean extendToDescendants);

    CompletableFuture<List<String>> findAllConceptNamesAsStrings();

    CompletableFuture<List<ILink>> findLinksByConceptNames(Collection<String> conceptNames);

    CompletableFuture<List<ILink>> findAllLinks();

    //CompletableFuture<List<ILink>> findLinksByLinkName(String linkName);

    CompletableFuture<ResolvedConceptSelection> resolveConceptSelection(ConceptSelection conceptSelection);

    CompletableFuture<Optional<URL>> resolveImageURL(String conceptName);

    CompletableFuture<Map<String, String>> getAnnotationViewMetadata();

    CompletableFuture<Collection<?>> getAnnotationViewsUniqueValuesForColumn(String columnName);

    CompletableFuture<List<Number>> getAnnotationViewsMinAndMaxForColumn(String columnName);

    CompletableFuture<List<Date>> getAnnotationViewsMinAndMaxDatesforColumn(String columnName);

    Connection getAnnotationConnection() throws SQLException;
}

/*
RXJava JDBC example

return Observable<VideoFrame>.create( o -> {
    try (Connection c = Database.getConnection()) {
        ResultSet rs = c.createStatement().executeQuery("Select * FROM VideoFrame");
        while (rs.next() && !o.isUnsubscribed()) {
            o.onNext(new VideoFrame(rs.getInt(1)));
        }
        rs.close()
        o.onCompleted();
    }
    catch (Exception e) {
        o.onError(e);
    }
}).subscribeOn(Schedulers.io());


 */