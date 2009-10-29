package vars.knowledgebase.ui.persistence;

import vars.knowledgebase.LinkRealization;
import vars.knowledgebase.LinkRealizationDAO;
import vars.knowledgebase.ui.Lookup;

/**
 * This subscriber deletes lists of observations from the database.
 */
class DeleteLinkRealizationSubscriber extends DeleteSubscriber<LinkRealization> {

    public DeleteLinkRealizationSubscriber(LinkRealizationDAO linkRealizationDAO) {
        super(Lookup.TOPIC_DELETE_LINK_REALIZATION, linkRealizationDAO);
    }

    @Override
    String getLookupName(LinkRealization obj) {
        return obj.getConceptMetadata().getConcept().getPrimaryConceptName().getName();
    }

    @Override
    LinkRealization prepareForTransaction(LinkRealization linkRealization) {
        linkRealization.getConceptMetadata().removeLinkRealization(linkRealization);
        return linkRealization;
    }
}
