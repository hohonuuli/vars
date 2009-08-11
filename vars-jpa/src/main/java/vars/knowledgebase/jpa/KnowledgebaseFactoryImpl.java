package vars.knowledgebase.jpa;

import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.IConcept;
import vars.knowledgebase.IConceptName;
import vars.knowledgebase.IHistory;
import vars.knowledgebase.ILinkRealization;
import vars.knowledgebase.ILinkTemplate;
import vars.knowledgebase.IMedia;
import vars.knowledgebase.IUsage;

public class KnowledgebaseFactoryImpl implements KnowledgebaseFactory {

    public IConcept newConcept() {
        return new Concept();
    }

    public IConceptName newConceptName() {
        return new ConceptName();
    }

    public IHistory newHistory() {
        return new History();
    }

    public ILinkRealization newLinkRealization() {
        return new LinkRealization();
    }

    public ILinkTemplate newLinkTemplate() {
        return new LinkTemplate();
    }

    public IMedia newMedia() {
        return new Media();
    }

    public IUsage newUsage() {
        return new Usage();
    }
}
