package vars.knowledgebase.ui.persistence;

import vars.knowledgebase.Media;
import vars.knowledgebase.MediaDAO;
import vars.knowledgebase.ui.Lookup;

/**
 * This subscriber deletes lists of observations from the database.
 */
class UpdateMediaSubscriber extends UpdateSubscriber<Media> {


    public UpdateMediaSubscriber(MediaDAO mediaDAO) {
        super(Lookup.TOPIC_UPDATE_MEDIA, mediaDAO);
    }

    @Override
    String getLookupName(Media obj) {
        return obj.getConceptMetadata().getConcept().getPrimaryConceptName().getName();
    }
}
