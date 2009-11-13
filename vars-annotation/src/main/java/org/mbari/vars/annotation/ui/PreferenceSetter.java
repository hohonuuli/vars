/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mbari.vars.annotation.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.prefs.Preferences;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.Timer;
import org.mbari.util.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.ui.Lookup;

/**
 * <p>Sets preferences in various UI components.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: PreferenceSetter.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class PreferenceSetter {

    private final static String COMPONENT_HEIGHT = "Component Height";
    private final static String COMPONENT_WIDTH = "Component Width";
    private final static String SPLIT_DIVIDER_LOCATION = "JSplitPane divider location";

    // the infamous SINGLETON!!!
    private static PreferenceSetter _this = null;
    private static final Logger log = LoggerFactory.getLogger(PreferenceSetter.class);

    /**
     * Constructs ...
     *
     */
    private PreferenceSetter() {
        super();
    }

    /**
     *  Sets the resizePreference attribute of the PreferenceSetter object
     *
     * @param  comp The new resizePreference value
     * @param  uniqueName The new resizePreference value
     */
    public void setResizePreference(final Component comp, final String uniqueName) {
        final Dispatcher dispatcher = Lookup.getPreferencesDispatcher();
        dispatcher.addPropertyChangeListener(new ResizeObserver(comp, uniqueName));
    }

    /**
     *  Sets the splitPanePreference attribute of the PreferenceSetter object
     *
     * @param  splitPane The new splitPanePreference value
     * @param  uniqueName The new splitPanePreference value
     */
    public void setSplitPanePreference(final JSplitPane splitPane, final String uniqueName) {
        final Dispatcher dispatcher = Lookup.getPreferencesDispatcher();
        dispatcher.addPropertyChangeListener(new SplitPaneObserver(splitPane, uniqueName));
    }

    /**
     *  Gets the instance attribute of the PreferenceSetter class
     *
     * @return  The instance value
     */
    public static PreferenceSetter getInstance() {
        if (_this == null) {
            _this = new PreferenceSetter();
        }

        return _this;
    }

    /**
     *     @author  brian
     */
    class ResizeObserver implements PropertyChangeListener {

        private ActionListener actionListener;
        private final Component component;
        private ComponentListener componentListener;
        private final Timer delayTimer;
        private final String uniqueName;

        /**
         * Constructor for the ResizeObserver object
         *
         * @param component
         * @param uniqueName
         */
        public ResizeObserver(final Component component, final String uniqueName) {
            this.component = component;
            this.uniqueName = uniqueName;
            component.addComponentListener(getComponentListener());
            delayTimer = new Timer(1500, getActionListener());
            delayTimer.setRepeats(false);
        }

        /**
         *         This is the action used by the delayTimer.
         *         @return
         *         @uml.property  name="actionListener"
         */
        private ActionListener getActionListener() {
            if (actionListener == null) {
                actionListener = new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        Preferences prefs = (Preferences) Lookup.getPreferencesDispatcher().getValueObject();
                        if (prefs == null) {
                            return;
                        }

                        prefs.putInt(uniqueName + COMPONENT_WIDTH, component.getWidth());
                        prefs.putInt(uniqueName + COMPONENT_HEIGHT, component.getHeight());

                        if (log.isDebugEnabled()) {
                            log.debug("Component size set to " + component.getWidth() + " " + component.getHeight());
                        }
                    }
                };
            }

            return actionListener;
        }

        /**
         *         When a component is resized we don't store it in the database right away. Instead we wait until it stops being resized then we send info to the database. This cuts down on DB traffic.
         *         @return
         *         @uml.property  name="componentListener"
         */
        private ComponentListener getComponentListener() {
            if (componentListener == null) {
                componentListener = new ComponentAdapter() {

                    @Override
                    public void componentResized(ComponentEvent evt) {
                        delayTimer.restart();
                    }
                };
            }

            return componentListener;
        }

        /**
         *  Sets the preferences when a new user is logged in.
         *
         * @param  preference Description of the Parameter
         * @param  unused Description of the Parameter
         */
        public void update(Object preference, Object unused) {

            // only work with Preferences objects
            if (!(preference instanceof Preferences)) {
                return;
            }

            Preferences prefs = (Preferences) preference;
            int width = prefs.getInt(uniqueName + COMPONENT_WIDTH, component.getWidth());
            int height = prefs.getInt(uniqueName + COMPONENT_HEIGHT, component.getHeight());
            if (log.isDebugEnabled()) {
                log.debug("Setting size to " + width + " " + height);
            }

            Component parent = this.component.getParent();
            while (parent != null) {
                if ((parent.getWidth() < width) || (parent.getHeight() < height)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Setting parent size");
                    }

                    parent.setSize(width, height);
                    parent.repaint();
                }

                parent = parent.getParent();
            }

            component.setSize(width, height);
            ((JComponent) component).revalidate();
            component.validate();
            component.repaint();
        }

        public void propertyChange(PropertyChangeEvent evt) {
            update(evt.getNewValue(), null);
        }
    }

    /**
     *     @author  brian
     */
    class SplitPaneObserver implements PropertyChangeListener {

        private ActionListener actionListener;
        private final Timer delayTimer;
        private PropertyChangeListener propertyChangeListener;
        private final JSplitPane splitPane;
        private final String uniqueName;

        /**
         * Constructor for the ResizeObserver object
         *
         * @param splitPane
         * @param uniqueName
         */
        public SplitPaneObserver(final JSplitPane splitPane, final String uniqueName) {
            this.splitPane = splitPane;
            this.uniqueName = uniqueName;
            delayTimer = new Timer(1500, getActionListener());
            delayTimer.setRepeats(false);
            splitPane.addPropertyChangeListener(getPropertyChangeListener());
        }

        /**
         *         @return  the actionListener
         *         @uml.property  name="actionListener"
         */
        private ActionListener getActionListener() {
            if (actionListener == null) {
                actionListener = new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        Preferences prefs = (Preferences) Lookup.getPreferencesDispatcher().getValueObject();
                        if (prefs == null) {
                            return;
                        }

                        int location = splitPane.getDividerLocation();
                        prefs.putInt(uniqueName + SPLIT_DIVIDER_LOCATION, location);

                        if (log.isDebugEnabled()) {
                            log.debug("Divider locatin set to " + location + "for " + uniqueName);
                        }
                    }
                };
            }

            return actionListener;
        }

        /**
         *         @return  the propertyChangeListener
         *         @uml.property  name="propertyChangeListener"
         */
        private PropertyChangeListener getPropertyChangeListener() {
            if (propertyChangeListener == null) {
                propertyChangeListener = new PropertyChangeListener() {

                    public void propertyChange(PropertyChangeEvent evt) {
                        if (!(evt.getPropertyName().equals(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY)) &&
                                !(evt.getPropertyName().equals(JSplitPane.DIVIDER_LOCATION_PROPERTY))) {

                            // Do nothing
                        }
                        else {
                            delayTimer.restart();
                        }
                    }
                };
            }

            return propertyChangeListener;
        }

        /**
         *  Description of the Method
         *
         * @param  preference Description of the Parameter
         * @param  unused Description of the Parameter
         */
        public void update(Object preference, Object unused) {

            // only work with Preferences objects
            if (!(preference instanceof Preferences)) {
                return;
            }

            Preferences prefs = (Preferences) preference;
            int location = prefs.getInt(uniqueName + SPLIT_DIVIDER_LOCATION, splitPane.getDividerLocation());
            splitPane.setDividerLocation(location);
            splitPane.validate();

            if (log.isDebugEnabled()) {
                log.debug("Divider location set to " + location + " for " + uniqueName);
            }
        }

        public void propertyChange(PropertyChangeEvent evt) {
            update(evt.getNewValue(), null);
        }
    }
}
