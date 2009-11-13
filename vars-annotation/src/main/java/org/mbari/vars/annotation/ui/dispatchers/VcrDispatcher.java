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
import org.mbari.vcr.IVCR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>A singleton source to retrieve the reference to the currently
 * used <code>IVCR</code>. This is actually a decorator on a
 * <code>Dispatcher</code> object that adds IObservable methods. You can access
 * the Dispatcher directly by using:</p>
 * <pre>
 * Dispatcher dispatcher = Dispatcher.getDispatcher(IVCR.class);
 * </pre>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: VcrDispatcher.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class VcrDispatcher implements IObservable {

    /** Field description */
    public static final Dispatcher DISPATCHER = PredefinedDispatcher.VCR.getDispatcher();
    private static final VcrDispatcher instance = new VcrDispatcher();
    private static final Logger log = LoggerFactory.getLogger(VcrDispatcher.class);

    /**
     *     @uml.property  name="os"
     *     @uml.associationEnd
     */
    private ObservableSupport os = new ObservableSupport();

    /**
     * Singleton
     *
     */
    VcrDispatcher() {
        super();
        DISPATCHER.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                notifyObservers();
            }
        });
    }

    /**
     *  Adds a feature to the Observer attribute of the VcrDispatcher object
     *
     * @param  observer The feature to be added to the Observer attribute
     */
    public void addObserver(IObserver observer) {
        os.add(observer);
    }

    /**
     * @return  Returns the observation.
     */
    public IVCR getVcr() {
        return (IVCR) DISPATCHER.getValueObject();
    }

    void notifyObservers() {
        os.notify(getVcr(), null);
    }

    /**
     *  Description of the Method
     */
    public void removeAllObservers() {
        os.clear();
    }

    /**
     *  Description of the Method
     *
     * @param  observer Description of the Parameter
     */
    public void removeObserver(IObserver observer) {
        os.remove(observer);
    }

    /**
     * @param  vcr The new vcr value
     */
    public void setVcr(IVCR vcr) {

        // The dispatcher will invoke notifyObservers so we don't need to.
        DISPATCHER.setValueObject(vcr);
    }

    /**
     * @return  An IObservable object
     */
    public static VcrDispatcher getInstance() {
        return instance;
    }
}
