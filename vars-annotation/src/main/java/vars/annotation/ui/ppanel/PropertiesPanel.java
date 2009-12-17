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
PPhysicalDataPanel.java
 *
Created on October 31, 2003, 9:16 AM
 */
package vars.annotation.ui.ppanel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.mbari.awt.layout.VerticalFlowLayout;
import org.mbari.swing.PropertyPanel;
import org.mbari.util.IObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vars.annotation.Observation;
import vars.annotation.ui.Lookup;

/**
 * <p>A JPanel the displays a group of related properties that can be obtained
 * via the <code>ObservationDispatcher</code>. For examples of use see
 * PPhysicalDataPanel for an example of use.</p>
 *
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public abstract class PropertiesPanel extends javax.swing.JPanel implements IObserver {

    private final Logger log = LoggerFactory.getLogger(getClass());
    String missingValue = " ";

    Map<String, PropertyPanel> propertyMap = new HashMap<String, PropertyPanel>();

    /**
     * Creates new PPhysicalDataPanel
     */
    public PropertiesPanel() {
        initialize();
        Lookup.getSelectedObservationsDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
            
            public void propertyChange(PropertyChangeEvent evt) {
                
                /*
                 * Child classes expect a single observation. If more than one has been selected then 
                 * post a null value
                 */
                Collection<Observation> observations = (Collection<Observation>) evt.getNewValue();
                Observation obs = observations.size() == 1 ? observations.iterator().next() : null;
                update(obs, "");
                
            }
        });

    }

    /**
     * Adds a tool-tip to the specified panel. The tooltip added is the contents of
     * the value-field. This is useful if the value is usually to big to view in
     * the field.
     *
     * @param name The name of the property to add the tooltip to.
     */
    void addToolTip(final String name) {
        final PropertyPanel p = getPropertyPanel(name);
        final JTextField f = p.getValueField();
        f.getDocument().addDocumentListener(new DocumentListener() {

            public void changedUpdate(final DocumentEvent e) {
                updateToolTip();
            }
            public void removeUpdate(final DocumentEvent e) {
                updateToolTip();
            }
            public void insertUpdate(final DocumentEvent e) {
                updateToolTip();
            }
            private void updateToolTip() {
                f.setToolTipText(f.getText());
            }

        });
    }

    /**
     *  Clears the values of all the value fields
     */
    void clearValues() {
        for (final Iterator<String> i = propertyMap.keySet().iterator(); i.hasNext(); ) {
            final String key = (String) i.next();
            final PropertyPanel p = (PropertyPanel) propertyMap.get(key);
            p.setProperty(key, missingValue);
        }
    }

    /**
     * <p>Get a particular <code>PropertyPanel</code>. Useful method for getting
     * a panel to register an actionlistener to for handiling edits. Typical
     * access code might be:</p>
     *
     * <pre>
     * PropertyPanel p = propertiesPanel.getPropertyPanel("latitude");
     * JTextField f = p.getValueField();
     * f.addActionListener(someActionListenerThatHandlesEdits);
     * p.setEditable(true);
     * </pre>
     *
     *
     * @param  name The name of the property.
     * @return  A propertyPanel registered to handle the property corresponding
     *             to name. <b>null</b> if no match was found.
     */
    PropertyPanel getPropertyPanel(final String name) {
        final Object obj = propertyMap.get(name);
        PropertyPanel p = null;
        if (obj != null) {
            p = (PropertyPanel) obj;
        }

        return p;
    }

    /**
     * GUI intialization
     */
    private void initialize() {

        // setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
        setLayout(new VerticalFlowLayout());
    }

    /**
     *  Sets the name-value pairs of each PropertyPanel in the PropertiesPanel.
     * The values are obtained by using reflections. For example, if the
     * PropertiesPanel contains a property of 'startDate', it will examine the
     * object passed in for a method called 'getStartDate'. If that method is
     * found then appropriate value will be displayed.
     *
     * @param  obj The object whose properties are being represented by this panel.
     */
    void setProperties(final Object obj) {
        if (obj != null) {
            final Class objClass = obj.getClass();
            final Class[] paramClass = new Class[] {};
            final Object[] args = new Object[] {};
            Object value = missingValue;
            for (final Iterator<String> i = propertyMap.keySet().iterator(); i.hasNext(); ) {
                final String key = (String) i.next();
                final String firstLetter = key.substring(0, 1).toUpperCase();
                final String name = "get" + firstLetter + key.substring(1, key.length());
                try {
                    final Method method = objClass.getMethod(name, paramClass);
                    value = method.invoke(obj, args);
                }
                catch (final Exception e) {
                    if (log.isWarnEnabled()) {
                        log.warn("Problem setting properties", e);
                    }

                    value = missingValue;
                }

                final PropertyPanel p = (PropertyPanel) propertyMap.get(key);
                try {
                    p.setProperty(key, value);
                }
                catch (final Exception e) {
                    if (log.isDebugEnabled()) {
                        log.debug("Unable to set '" + key + "' property to '" + value + " on the tabbed panel");
                    }

                    p.setProperty(key, missingValue);
                }
            }
        }
        else {
            clearValues();
        }
    }

    /**
     *  Sets the properties that will be displayed on this panel
     *
     * @param  names The new propertyNames value
     */
    void setPropertyNames(final String[] names) {
        for (int i = 0; i < names.length; i++) {

            /*
             *  Using a subclass of propertiesPanel that listens to
             *  the observation dispatcher and turns off the edit button
             *  as needed.
             */
            final PropertyPanel p = new PPanel(names[i], missingValue);
            p.setAlignmentX(LEFT_ALIGNMENT);
            p.setAlignmentY(TOP_ALIGNMENT);
            propertyMap.put(names[i], p);
            add(p);
        }
    }

    /**
     * Called by ObservationDispatcher. Developers shouldn't call this
     * directly
     *
     * @param  obj An Observation
     * @param  changeCode <b>null</b>
     * @see  org.mbari.util.IObserver#update(java.lang.Object, java.lang.Object)
     */
    public abstract void update(Object obj, Object changeCode);
}
