package vars.knowledgebase.ui.persistence;

import vars.knowledgebase.Media;
import vars.knowledgebase.MediaDAO;
import vars.knowledgebase.ui.Lookup;

/**
 * This subscriber deletes lists of observations from the database.
 */
class DeleteMediaSubscriber extends DeleteSubscriber<Media> {

    public DeleteMediaSubscriber(MediaDAO mediaDAO) {
        super(Lookup.TOPIC_DELETE_MEDIA, mediaDAO);
    }

    @Override
    String getLookupName(Media obj) {
        return obj.getConceptMetadata().getConcept().getPrimaryConceptName().getName();
    }

    @Override
    Media prepareForTransaction(Media media) {
        media.getConceptMetadata().removeMedia(media);
        return media;
    }
}
