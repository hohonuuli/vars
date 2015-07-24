package vars.queryfx;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import vars.ILink;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.query.QueryPersistenceService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2015-07-22T10:42:00
 */
public class QueryServiceImpl implements QueryService {

    private final Executor executor;
    private final QueryPersistenceService queryPersistenceService;
    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;
    private final Function<Concept, Collection<String>> asNames = c ->
            c.getConceptNames().stream().map(ConceptName::getName).collect(Collectors.toList());


    @Inject
    public QueryServiceImpl(Executor executor,
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
        return CompletableFuture.supplyAsync(() -> {
            List<String> names;
            ConceptDAO conceptDAO = knowledgebaseDAOFactory.newConceptDAO();
            conceptDAO.startTransaction();
            Concept concept = conceptDAO.findByName(conceptName);
            if (concept == null) {
                names = Lists.newArrayList();
            } else {
                names = conceptDAO.findDescendentNames(concept).stream()
                        .map(ConceptName::getName)
                        .sorted()
                        .collect(Collectors.toList());
            }
            conceptDAO.endTransaction();
            return names;
        }, executor);
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

        return CompletableFuture.supplyAsync(() -> {
            Collection<Concept> concepts = new HashSet<>();

            ConceptDAO conceptDAO = knowledgebaseDAOFactory.newConceptDAO();
            conceptDAO.startTransaction();
            Concept c = conceptDAO.findByName(name);

            if (c != null) {
                concepts.add(c);

                if (extendToParent && c.getParentConcept() != null) {
                    concepts.add(c.getParentConcept());
                }

                if (extendToSiblings && c.getParentConcept() != null) {
                    concepts.addAll(c.getParentConcept().getChildConcepts());
                }

                if (extendToChildren && !extendToDescendants) {
                    concepts.addAll(c.getChildConcepts());
                }

                if (extendToDescendants) {
                    concepts.addAll(conceptDAO.findDescendents(c));
                }
            }
            conceptDAO.endTransaction();
            conceptDAO.close();

            return concepts;
        }, executor);

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
                new ArrayList<>(queryPersistenceService.findByConceptNames(conceptNames)), executor);
    }
}
