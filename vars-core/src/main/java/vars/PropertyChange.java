/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;

/**
 * Interface defining classes that have property change listeners
 */
public interface PropertyChange {

    void addPropertyChangeListener(String string, PropertyChangeListener listener);

    void removePropertyChangeListener(String string, PropertyChangeListener listener);

    void addPropertyChangeListener(PropertyChangeListener listener);

    void removePropertyChangeListener(PropertyChangeListener listener);


}
