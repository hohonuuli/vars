package vars.annotation.ui.buttons;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.annotation.ui.actions.AddNewRefNumPropAction;
import vars.annotation.ui.eventbus.VideoArchiveChangedEvent;
import vars.annotation.ui.eventbus.VideoArchiveSelectedEvent;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;

/**
 * @author Brian Schlining
 * @since 2012-06-21
 */
public class PNewRefNumButtonController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private AnnotationDAOFactory annotationDAOFactory;

    public PNewRefNumButtonController(AnnotationDAOFactory annotationDAOFactory) {
        this.annotationDAOFactory = annotationDAOFactory;
        AnnotationProcessor.process(this); // Create EventBus Proxy
    }

    public Collection<Integer> listReferenceNumbers(VideoArchive videoArchive) {
        VideoArchiveDAO videoArchiveDAO = annotationDAOFactory.newVideoArchiveDAO();

        // TODO 'identity-reference' should be pulled out and put into a properties file
        final Collection<String> refNums = videoArchiveDAO.findAllLinkValues(videoArchive,
                "identity-reference");

        // Returned as string convert to integers:
        final Collection<Integer> refInts = new ArrayList<Integer>(refNums.size());
        for (String object : refNums) {
            try {
                refInts.add(Integer.valueOf(object));
            }
            catch (Exception e) {
                log.warn(object + " is not an integer. Unable to parse identity-reference");
            }
        }
        return refInts;
    }

    /**
     *
     * @param videoArchive
     * @return the maximum integer value of the identity-references. <b>null</b> is returned if no
     *      identity-references are found.
     */
    public Integer findMaxReferenceNumber(VideoArchive videoArchive) {
        Collection<Integer> refNums = listReferenceNumbers(videoArchive);
        Integer max = null;
        try {
            max = Collections.max(refNums);
        }
        catch (NoSuchElementException e) {
            // Do nothing. The collection was empty
        }
        return max;
    }


    @EventSubscriber(eventClass = VideoArchiveSelectedEvent.class)
    public void respondTo(VideoArchiveSelectedEvent event) {
        respondTo(new VideoArchiveChangedEvent(this, event.get()));
    }

    @EventSubscriber(eventClass = VideoArchiveChangedEvent.class)
    public void respondTo(VideoArchiveChangedEvent event) {
        Integer max = findMaxReferenceNumber(event.get());
        int maxInt = max == null ? 1 : max + 1;
        AddNewRefNumPropAction.setRefNumber(maxInt);
    }


}
