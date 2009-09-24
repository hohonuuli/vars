package vars.knowledgebase;

import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.History;
import vars.knowledgebase.LinkRealization;
import vars.knowledgebase.LinkTemplate;
import vars.knowledgebase.Media;
import vars.knowledgebase.Usage;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 6, 2009
 * Time: 2:50:58 PM
 * To change this template use File | Settings | File Templates.
 */
public interface KnowledgebaseFactory {

    /* --- Knowledgebase --- */

    Concept newConcept();

    ConceptName newConceptName();

    History newHistory();

    LinkRealization newLinkRealization();

    LinkTemplate newLinkTemplate();

    Media newMedia();


    Usage newUsage();
}
