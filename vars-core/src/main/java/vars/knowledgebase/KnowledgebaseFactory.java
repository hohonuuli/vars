package vars.knowledgebase;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 6, 2009
 * Time: 2:50:58 PM
 * To change this template use File | Settings | File Templates.
 */
public interface KnowledgebaseFactory {

    /* --- Knowledgebase --- */

    Artifact newArtifact();

    Concept newConcept();

    ConceptName newConceptName();

    History newHistory();

    LinkRealization newLinkRealization();

    LinkTemplate newLinkTemplate();

    Media newMedia();

    Usage newUsage();
}
