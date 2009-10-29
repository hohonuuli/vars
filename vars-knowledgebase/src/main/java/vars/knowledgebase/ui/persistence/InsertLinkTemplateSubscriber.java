package vars.knowledgebase.ui.persistence;

import vars.knowledgebase.LinkTemplate;
import vars.knowledgebase.LinkTemplateDAO;
import vars.knowledgebase.ui.Lookup;

/**
 * This subscriber inserts lists of LinkTemplates from the database.
 */
class InsertLinkTemplateSubscriber extends InsertSubscriber<LinkTemplate> {


    public InsertLinkTemplateSubscriber(LinkTemplateDAO linkTemplateDAO) {
        super(Lookup.TOPIC_INSERT_LINK_TEMPLATE, linkTemplateDAO);
    }

    @Override
    String getLookupName(LinkTemplate obj) {
        return obj.getConceptMetadata().getConcept().getPrimaryConceptName().getName();
    }
}
