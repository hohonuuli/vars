package vars.knowledgebase.ui.persistence;

import vars.knowledgebase.LinkTemplate;
import vars.knowledgebase.LinkTemplateDAO;
import vars.knowledgebase.ui.Lookup;

/**
 * This subscriber deletes lists of LinkTemplates from the database.
 */
class UpdateLinkTemplateSubscriber extends UpdateSubscriber<LinkTemplate> {


    public UpdateLinkTemplateSubscriber(LinkTemplateDAO linkTemplateDAO) {
        super(Lookup.TOPIC_UPDATE_LINK_TEMPLATE, linkTemplateDAO);
    }

    @Override
    String getLookupName(LinkTemplate obj) {
        return obj.getConceptMetadata().getConcept().getPrimaryConceptName().getName();
    }
}
