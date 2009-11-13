/*
 * Copyright 2005 MBARI
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/*
Created on Oct 30, 2003
 */
package org.mbari.vars.annotation.ui.dispatchers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.mbari.util.Dispatcher;
import org.mbari.util.IObservable;
import org.mbari.util.IObserver;
import org.mbari.util.ObservableSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.IObservation;

/**
 * <p>
 * A singleton Observable object that notifes objects when the currently
 * selected observation propertyChangeSupport. The currently selected observation is
 * represented by the highlighted row in the ObservationTable. This interacts
 * with the database so that a call to setObservation first updates the
 * previously held observation in the database before setting the reference
 * to the new observation
 * </p>
 *
 * <h2><u>UML</u></h2>
 *
 * <pre>
 *      [ObservationDispatcher]-->[ObservationDAO]
 * </pre>
 *
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: ObservationDispatcher.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class ObservationDispatcher implements IObservable {

    private static final Logger log = LoggerFactory.getLogger(ObservationDispatcher.class);

    /** Field description */
    public static final Dispatcher DISPATCHER = PredefinedDispatcher.OBSERVATION.getDispatcher();
    private static ObservationDispatcher cod;

    /**
     * @uml.property  name="observableSupport"
     * @uml.associationEnd
     */
    private final transient ObservableSupport observableSupport = new ObservableSupport();

    /**
     * @uml.property  name="observation"
     * @uml.associationEnd
     */
    private IObservation observation;

    /**
     * Singleton
     *
     */
    ObservationDispatcher() {
        super();
        DISPATCHER.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                notifyObservers();
            }
        });
    }

    /**
     *  Adds an <code>IObserver</code> object to this dispatcher. Observers will
     * be notified when the Observation propertyChangeSupport. Observers will receive an
     * <code>Observation</code> as the object in their update methods.
     *
     * @param  observer The observer to add.
     */
    public void addObserver(final IObserver observer) {
        observableSupport.add(observer);
    }

    /**
     * Add a listener to receive notification of propertyChangeSupport to all the bound
     * properties in this class.
     *
     * @param  l   The listener which will receive <tt>PropertyChangeEvent</tt>s
     */
    public void addPropertyChangeListener(final PropertyChangeListener l) {
        DISPATCHER.addPropertyChangeListener(l);
    }

    /**
     * @return   Returns the observation.
     * @uml.property  name="observation"
     */
    public IObservation getObservation() {
        return (IObservation) DISPATCHER.getValueObject();
    }

    void notifyObservers() {
        observableSupport.notify(getObservation(), "new");
    }

    /**
     *  Description of the Method
     */
    public void removeAllObservers() {
        observableSupport.clear();
    }

    /**
     *  Description of the Method
     *
     * @param  observer Description of the Parameter
     */
    public void removeObserver(final IObserver observer) {
        observableSupport.remove(observer);
    }

    /**
     * <p>Set the currently edited observation. Then the new observation is set.</p> <p>IMPORTANT: Before deleting the currently held observation using <code>ObservationDAO</code> it's important to set the referenced observation to null or another observation. When setting a new observation the old one has it's conceptname validated and is updated in  the database via the  {@link org.mbari.vars.dao.DAOEventQueue}
     * @param newObservation  The new observation value
     * @uml.property  name="observation"
     */
    public void setObservation(final IObservation newObservation) {

        // Moved to PredefinedDispatcher
//      if (observation != null) {
//          try {
//              observation.validateConceptName();
//          } catch (DAOException e) {
//              log.error("Failed to validate " + observation, e);
//              AppFrameDispatcher.showErrorDialog("Failed to validate '" + observation.getConceptName() +
//                      "'. There may be a problem with the database connection.");
//          }
//          DAOEventQueue.update(observation);
//      }

        DISPATCHER.setValueObject(newObservation);

    }

    /**
     * @return  An IObservable object
     */
    public static ObservationDispatcher getInstance() {
        if (cod == null) {
            log.debug("Initializing ObservationDispatcher");
            cod = new ObservationDispatcher();
        }

        return cod;
    }
}
