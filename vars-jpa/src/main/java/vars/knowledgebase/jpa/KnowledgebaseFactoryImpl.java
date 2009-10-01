package vars.knowledgebase.jpa;

import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.History;
import vars.knowledgebase.LinkRealization;
import vars.knowledgebase.LinkTemplate;
import vars.knowledgebase.Media;
import vars.knowledgebase.Usage;

public class KnowledgebaseFactoryImpl implements KnowledgebaseFactory {

    public Concept newConcept() {
        return new ConceptImpl();
    }

    public ConceptName newConceptName() {
        return new GConceptName();
    }

    public History newHistory() {
        return new GHistory();
    }

    public LinkRealization newLinkRealization() {
        return new GLinkRealization();
    }

    public LinkTemplate newLinkTemplate() {
        return new GLinkTemplate();
    }

    public Media newMedia() {
        return new GMedia();
    }

    public Usage newUsage() {
        return new GUsage();
    }

}
