package vars.knowledgebase.ui.persistence;

import vars.knowledgebase.Media;
import vars.knowledgebase.MediaDAO;
import vars.knowledgebase.ui.Lookup;

/**
 * This subscriber inserts lists of observations from the database.
 */
class InsertMediaSubscriber extends InsertSubscriber<Media> {

    public InsertMediaSubscriber(MediaDAO mediaDAO) {
        super(Lookup.TOPIC_INSERT_MEDIA, mediaDAO);
    }

    @Override
    String getLookupName(Media obj) {
        return obj.getConceptMetadata().getConcept().getPrimaryConceptName().getName();
    }
}
