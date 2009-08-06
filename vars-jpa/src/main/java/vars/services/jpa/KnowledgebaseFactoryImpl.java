package vars.services.jpa;

import vars.services.KnowlegebaseFactory;
import vars.knowledgebase.IConcept;
import vars.knowledgebase.IConceptDelegate;
import vars.knowledgebase.IConceptName;
import vars.knowledgebase.IHistory;
import vars.knowledgebase.ILinkRealization;
import vars.knowledgebase.ILinkTemplate;
import vars.knowledgebase.IMedia;
import vars.knowledgebase.ISectionInfo;
import vars.knowledgebase.IUsage;
import vars.knowledgebase.jpa.Concept;
import vars.knowledgebase.jpa.ConceptName;
import vars.knowledgebase.jpa.History;
import vars.knowledgebase.jpa.LinkRealization;
import vars.knowledgebase.jpa.LinkTemplate;
import vars.knowledgebase.jpa.Media;
import vars.knowledgebase.jpa.SectionInfo;
import vars.knowledgebase.jpa.Usage;


public class KnowledgebaseFactoryImpl implements KnowlegebaseFactory {

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

    public ISectionInfo newSectionInfo() {
        return new SectionInfo();
    }

    public IUsage newUsage() {
        return new Usage();
    }
}
