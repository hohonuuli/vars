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
        return new ConceptNameImpl();
    }

    public History newHistory() {
        return new HistoryImpl();
    }

    public LinkRealization newLinkRealization() {
        return new LinkRealizationImpl();
    }

    public LinkTemplate newLinkTemplate() {
        return new LinkTemplateImpl();
    }

    public Media newMedia() {
        return new MediaImpl();
    }

    public Usage newUsage() {
        return new UsageImpl();
    }

}
