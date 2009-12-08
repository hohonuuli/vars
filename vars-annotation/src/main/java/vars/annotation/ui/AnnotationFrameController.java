/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui;

import java.util.ArrayList;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.Observation;

/**
 *
 * @author brian
 */
public class AnnotationFrameController {

    private final AnnotationFrame annotationFrame;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final PersistenceController persistenceController;

    public AnnotationFrameController(AnnotationFrame annotationFrame, final PersistenceController persistenceController) {
        this.annotationFrame = annotationFrame;
        this.persistenceController = persistenceController;
        
        // Make sure we save the last observations we annotated to the database
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                log.debug("Saving last Observations to persistent storage during JVM shutdown");
                Collection<Observation> observations = (Collection<Observation>) Lookup.getSelectedObservationsDispatcher().getValueObject();
                persistenceController.updateObservations(new ArrayList<Observation>(observations));
            }
        }));
    }

}
