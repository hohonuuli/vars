package vars.knowledgebase.jpa;

import vars.knowledgebase.*;

public class KnowledgebaseFactoryImpl implements KnowledgebaseFactory {

    public Artifact newArtifact() {
        return new ArtifactImpl();
    }

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
