package vars.knowledgebase.ui.persistence;

import vars.knowledgebase.LinkTemplate;
import vars.knowledgebase.LinkTemplateDAO;
import vars.knowledgebase.ui.Lookup;

/**
 * This subscriber deletes lists of LinkTemplates from the database.
 */
class DeleteLinkTemplateSubscriber extends DeleteSubscriber<LinkTemplate> {


    public DeleteLinkTemplateSubscriber(LinkTemplateDAO linkTemplateDAO) {
        super(Lookup.TOPIC_DELETE_LINK_TEMPLATE, linkTemplateDAO);
    }

    @Override
    String getLookupName(LinkTemplate obj) {
        return obj.getConceptMetadata().getConcept().getPrimaryConceptName().getName();
    }

    @Override
    LinkTemplate prepareForTransaction(LinkTemplate linkTemplate) {
        linkTemplate.getConceptMetadata().removeLinkTemplate(linkTemplate);
        return linkTemplate;
    }
}
