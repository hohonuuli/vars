/*
 * ConceptButtonOverviewPanel.java
 * 
 * Created on Oct 9, 2007, 5:06:47 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mbari.vars.annotation.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.mbari.vars.annotation.ui.dispatchers.PreferencesDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author brian
 */
public class ConceptButtonOverviewPanel extends JPanel {
    
    private static final Logger log = LoggerFactory.getLogger(ConceptButtonOverviewPanel.class);
    
    private JScrollPane scrollPane = null;
    private JPanel overviewPanel = null;
    

    public ConceptButtonOverviewPanel() {
        initialize();
        
        // Update the overview tab when the users are switched.
        PreferencesDispatcher.DISPATCHER.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                loadPreferences();
            }
        });
        loadPreferences();
    }

    void initialize() {
        setLayout(new BorderLayout());
        add(getScrollPane(), BorderLayout.CENTER);
    }
    
    
    public void loadPreferences() {
        getOverviewPanel().removeAll();
        Preferences userPreferences = PreferencesDispatcher.getInstance().getPreferences();
        final Preferences cpPreferences = userPreferences.node("CP");
        if (cpPreferences != null) {
            String[] tabNames = null;
            try {
                tabNames = cpPreferences.childrenNames();
            } catch (final BackingStoreException bse) {
                log.error("Problem loading user tabs.", bse);
            }
            
            if (tabNames != null) {
                
                for (int i = 0; i < tabNames.length; i++) {
                    String tabName = tabNames[i];
                    JPanel panel = new ConceptButtonDropPanelWithHighlights(cpPreferences.node(tabName));
                    //panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(javax.swing.BorderFactory.createTitledBorder(cpPreferences.node(tabName).get("tabName","")));
                    getOverviewPanel().add(panel);
                }

            }
        }
    }
    
    protected JPanel getOverviewPanel() {
        if (overviewPanel == null) {
            overviewPanel = new JPanel();
            overviewPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
            //overviewPanel.setLayout(new BoxLayout(scrollPane, BoxLayout.X_AXIS));
        }
        return overviewPanel;
        
    }
    
    protected JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane();
            scrollPane.setViewportView(getOverviewPanel());
        }
        return scrollPane;
    }
    
}
