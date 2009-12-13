/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.SwingUtilities;
import org.mbari.swing.LabeledSpinningDialWaitIndicator;
import org.mbari.swing.WaitIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.CacheClearedEvent;
import vars.CacheClearedListener;
import vars.annotation.Observation;

/**
 *
 * @author brian
 */
public class AnnotationFrameController {

    private final AnnotationFrame annotationFrame;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ToolBelt toolBelt;

    public AnnotationFrameController(AnnotationFrame annotationFrame, final ToolBelt toolBelt) {
        this.annotationFrame = annotationFrame;
        this.toolBelt = toolBelt;
        
        // Make sure we save the last observations we annotated to the database
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                log.debug("Saving last Observations to persistent storage during JVM shutdown");
                Collection<Observation> observations = (Collection<Observation>) Lookup.getSelectedObservationsDispatcher().getValueObject();
                toolBelt.getPersistenceController().updateAndValidate(new ArrayList<Observation>(observations));
            }
        }));

        toolBelt.getPersistenceCache().addCacheClearedListener(new CacheClearedListener() {

            private WaitIndicator waitIndicator;

            public void afterClear(CacheClearedEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        waitIndicator.dispose();
                        waitIndicator = null;
                    }
                });
                
            }

            public void beforeClear(CacheClearedEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        waitIndicator = new LabeledSpinningDialWaitIndicator(AnnotationFrameController.this.annotationFrame, "Refreshing");
                    }
                });
                
            }
        });

        Lookup.getSelectedObservationsDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                PersistenceController persistenceController = toolBelt.getPersistenceController();
                Collection<Observation> oldObservations = (Collection<Observation>) evt.getOldValue();
                if (oldObservations != null) {
                    oldObservations = new ArrayList<Observation>(oldObservations);
                    oldObservations = persistenceController.updateAndValidate(oldObservations);
                    persistenceController.updateUI(oldObservations, false);
                }
            }
        });

    }
    

}
