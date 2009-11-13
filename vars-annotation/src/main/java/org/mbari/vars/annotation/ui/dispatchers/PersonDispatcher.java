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


package org.mbari.vars.annotation.ui.dispatchers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.mbari.util.Dispatcher;
import org.mbari.util.IObservable;
import org.mbari.util.IObserver;
import org.mbari.util.ObservableSupport;
import org.mbari.util.Person;

/**
 * <p>Stores a reference to the string name of the current annotator</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: PersonDispatcher.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class PersonDispatcher implements IObservable {

    /**
     *  Description of the Field
     */
    public final static String DEFAULT_USER = "default";

    /** Field description */
    public static final Dispatcher DISPATCHER = Dispatcher.getDispatcher(Person.class);
    private static final PersonDispatcher instance = new PersonDispatcher();

    /**
     *     @uml.property  name="oc"
     *     @uml.associationEnd
     */
    private ObservableSupport oc = new ObservableSupport();

    /**
     * Singleton
     *
     */
    PersonDispatcher() {
        super();
        DISPATCHER.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                notifyObservers();
            }

        });
    }

    /**
     *  Adds an observer. Observers will be notified when the current annotator
     * has changed. They will recieve a String representing teh new person
     *
     * @param  observer The feature to be added to the Observer attribute
     */
    public void addObserver(IObserver observer) {
        oc.add(observer);
    }

    /**
     * @return  Returns the current annotator
     */
    public String getPerson() {
        return (String) DISPATCHER.getValueObject();
    }

    void notifyObservers() {
        oc.notify(getPerson(), null);
    }

    /**
     *  Remvoe all observers
     */
    public void removeAllObservers() {
        oc.clear();
    }

    /**
     *  Remove a specific obesrver. That observer will no longer recieve notification
     * if the annotator changes.
     *
     * @param  observer Description of the Parameter
     */
    public void removeObserver(IObserver observer) {
        oc.remove(observer);
    }

    /**
     * @param  person The person to set.
     */
    public void setPerson(String person) {
        DISPATCHER.setValueObject(person);
    }

    /**
     * @return  The personDispatcher object
     */
    public synchronized static PersonDispatcher getInstance() {
        return instance;
    }
}
