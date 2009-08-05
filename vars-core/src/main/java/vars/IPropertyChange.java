/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars;

import java.beans.PropertyChangeListener;
import java.util.Collection;

/**
 *
 * @author brian
 */
public interface IPropertyChange {

    void addPropertyChangeListener(String string, PropertyChangeListener listener);

    void removePropertyChangeListener(String string, PropertyChangeListener listener);

    void addPropertyChangeListener(PropertyChangeListener listener);

    void removePropertyChangeListener(PropertyChangeListener listener);

    Collection<PropertyChangeListener> getPropertyChangeListeners();
}
