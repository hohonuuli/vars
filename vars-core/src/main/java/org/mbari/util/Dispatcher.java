/*
 * @(#)Dispatcher.java   2010.12.22 at 09:15:01 PST
 *
 * Copyright 2009 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package org.mbari.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>A Dispatcher provides a bucket to store Objects in. Essentially it's a
 * way to make a variable Global. In addition, a Dispatcher can notify
 * listeners when an object has been changed. Use as:</p>
 * <pre>
 * Dispatcher d = Dispatcher.getDispather("someKey");
 * d.addPropertyChangeListener(new PropertyChangeListener() {
 *     public void propertyChange(PropertyChangeEvent evt) {
 *           // Get the old value of the property
 *          Object oldValue = evt.getOldValue();
 *
 *          // Get the new value of the property
 *          Object newValue = evt.getNewValue();
 *          System.out.println("Old value = " + old + "; New value = " + new");
 *      }
 * });
 * d.setValueObject(object);
 * d.setValueObject(anotherObject);
 * Object refToAnotherObject = d.getValueObject();
 * </pre>
 *
 * Note: When Java 5.0 is more prevailent we can change this to use templates to
 * constrain the type of object that can be set.
 *
 * @author brian
 * @version $Id: Dispatcher.java 473 2007-02-01 20:00:35Z hohonuuli $
 */
public class Dispatcher {

    private static final String PROP_VALUE_OBJECT = "valueObject";
    protected static final Map map = new HashMap();

    /**
     */
    private final PropertyChangeSupport propSupport = new PropertyChangeSupport(this);

    /**
     */
    private final Object key;

    /**
     */
    private volatile Object valueObject;

    /**
     *
     *
     * @param key
     */
    protected Dispatcher(Object key) {
        super();
        this.key = key;
    }

    /**
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propSupport.addPropertyChangeListener(PROP_VALUE_OBJECT, listener);
    }

    /**
     * Returns an instance of a dispatcher for a particular class
     *
     * @param key
     * @return
     */
    public static Dispatcher getDispatcher(Object key) {
        Dispatcher dispatcher = null;

        synchronized (map) {
            if (map.containsKey(key)) {
                dispatcher = (Dispatcher) map.get(key);
            }
            else {
                dispatcher = new Dispatcher(key);
                map.put(key, dispatcher);
            }
        }

        return dispatcher;
    }

    /**
     *     @return  Returns the observedClass.
     */
    public Object getKey() {
        return key;
    }

    /**
     *
     * @return All the keys registered for the different dispatchers.
     */
    public static Collection getKeys() {
        Collection keys;

        synchronized (map) {
            keys = map.keySet();
        }

        return keys;
    }

    /**
     *     @return  Returns the valueObject.
     */
    public Object getValueObject() {
        return valueObject;
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propSupport.removePropertyChangeListener(PROP_VALUE_OBJECT, listener);
    }

    /**
     *     @param  observedObject
     */
    public synchronized void setValueObject(Object observedObject) {
        Object oldObject = this.valueObject;

        this.valueObject = observedObject;
        propSupport.firePropertyChange(PROP_VALUE_OBJECT, oldObject, observedObject);
    }
}
