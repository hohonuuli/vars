package vars.knowledgebase.ui.persistence;

import vars.knowledgebase.LinkRealization;
import vars.knowledgebase.LinkRealizationDAO;
import vars.knowledgebase.ui.Lookup;

/**
 * This subscriber inserts lists of observations from the database.
 */
class InsertLinkRealizationSubscriber extends InsertSubscriber<LinkRealization> {


    public InsertLinkRealizationSubscriber(LinkRealizationDAO linkRealizationDAO) {
        super(Lookup.TOPIC_INSERT_LINK_REALIZATION, linkRealizationDAO);
    }

    @Override
    String getLookupName(LinkRealization obj) {
        return obj.getConceptMetadata().getConcept().getPrimaryConceptName().getName();
    }
}
