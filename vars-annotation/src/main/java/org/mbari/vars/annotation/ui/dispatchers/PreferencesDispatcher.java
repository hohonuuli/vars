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
import java.util.prefs.Preferences;
import org.mbari.util.Dispatcher;
import org.mbari.util.IObservable;
import org.mbari.util.IObserver;
import org.mbari.util.ObservableSupport;
import org.mbari.vars.util.VARSPreferencesFactory;

/**
 * <p>A single point to retrieve ui preferences from. This will update whenever
 * the name stored in the <code>PersonDispathcer</code> is changed</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: PreferencesDispatcher.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class PreferencesDispatcher implements IObservable {

    /** Field description */
    public static final Dispatcher DISPATCHER = PredefinedDispatcher.PREFERENCES.getDispatcher();
    private static PreferencesDispatcher pd;

    /**
     */
    private ObservableSupport oc = new ObservableSupport();

    /**
     * Singleton
     *
     */
    PreferencesDispatcher() {
        super();
        DISPATCHER.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                notifyObservers();
            }

        });
    }

    /**
     *  Adds a feature to the Observer attribute of the PreferencesDispatcher object
     *
     * @param  observer The feature to be added to the Observer attribute
     */
    public void addObserver(IObserver observer) {
        oc.add(observer);
    }

    /**
     * @return   Returns the preferences.
     * @uml.property  name="preferences"
     */
    public Preferences getPreferences() {
        return (Preferences) DISPATCHER.getValueObject();
    }

    /**
     * Notifies the application that the preferences has changed. The Observing
     * object will recieve a reference to the new preferences.
     */
    void notifyObservers() {
        oc.notify(getPreferences(), "new");
    }

    /**
     *  Description of the Method
     */
    public void removeAllObservers() {
        oc.clear();
    }

    /**
     *  Description of the Method
     *
     * @param  observer Description of the Parameter
     */
    public void removeObserver(IObserver observer) {
        oc.remove(observer);
    }

    /**
     * @param preferences  the preferences to set
     * @uml.property  name="preferences"
     */
    private void setPreferences(Preferences preferences) {
        DISPATCHER.setValueObject(preferences);
    }


    /**
     * @return  An IObservable object
     */
    public static PreferencesDispatcher getInstance() {
        if (pd == null) {
            synchronized (PreferencesDispatcher.class) {
                pd = new PreferencesDispatcher();
                VARSPreferencesFactory pf = new VARSPreferencesFactory();
                pd.setPreferences(pf.userRoot());
            }
        }

        return pd;
    }
}
