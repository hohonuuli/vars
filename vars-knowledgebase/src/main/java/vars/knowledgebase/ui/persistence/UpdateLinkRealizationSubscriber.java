package vars.knowledgebase.ui.persistence;

import vars.knowledgebase.LinkRealization;
import vars.knowledgebase.LinkRealizationDAO;
import vars.knowledgebase.ui.Lookup;

/**
 * This subscriber deletes lists of observations from the database.
 */
class UpdateLinkRealizationSubscriber extends UpdateSubscriber<LinkRealization> {


    public UpdateLinkRealizationSubscriber(LinkRealizationDAO linkRealizationDAO) {
        super(Lookup.TOPIC_UPDATE_LINK_REALIZATION, linkRealizationDAO);
    }

    @Override
    String getLookupName(LinkRealization obj) {
        return obj.getConceptMetadata().getConcept().getPrimaryConceptName().getName();
    }
}
