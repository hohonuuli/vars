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
Created on October 31, 2003, 9:16 AM
 */
package vars.annotation.ui.ppanel;

import org.bushe.swing.event.EventBus;
import mbarix4j.awt.event.ActionAdapter;
import mbarix4j.swing.PropertyPanel;
import mbarix4j.util.IObserver;
import vars.CacheClearedEvent;
import vars.CacheClearedListener;
import vars.annotation.ImmutablePhysicalData;
import vars.annotation.Observation;
import vars.annotation.PhysicalData;
import vars.annotation.PhysicalDataValueEq;
import vars.annotation.VideoFrame;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.commandqueue.CommandEvent;
import vars.annotation.ui.commandqueue.impl.ChangePhysicalDataCmd;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Comparator;

/**
 * <p>Displays the properties of a PhysicalData object.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: PPhysicalDataPanel.java 314 2006-07-10 02:38:46Z hohonuuli $
 */
public class PPhysicalDataPanel extends PropertiesPanel implements IObserver {

    private static final String[] propertyNames = {
            "Latitude", "Longitude", "Depth", "Altitude", "Temperature", "Salinity", "Oxygen", "Light"
    };
    private volatile PhysicalData physicalData;
    private final PhysicalDataValueEq eq = new PhysicalDataValueEq();

    private final ActionAdapter updateAction = new ActionAdapter() {
        @Override
        public void doAction() {
            if (physicalData != null) {
                PhysicalData newPhysicalData = readDataPanels();
                if (newPhysicalData != null) {
                    Command command = new ChangePhysicalDataCmd(physicalData, newPhysicalData);
                    CommandEvent commandEvent = new CommandEvent(command);
                    EventBus.publish(commandEvent);
                }
            }
        }
    };


    /**
     * Creates new form PPhysicalDataPanel
     */
    public PPhysicalDataPanel(ToolBelt toolBelt) {
        super();
        setPropertyNames(propertyNames);
        for (String name : propertyNames) {
            PropertyPanel panel = getPropertyPanel(name);
            JTextField valueField = panel.getValueField();
            valueField.addActionListener(e -> updateAction.doAction());
            valueField.getDocument().addDocumentListener(new MyDocListener(valueField));
            panel.setEditable(true);
        }

        toolBelt.getPersistenceCache().addCacheClearedListener(new CacheClearedListener() {

            public void afterClear(CacheClearedEvent evt) {
                update(null, null);
            }

            public void beforeClear(CacheClearedEvent evt) {
                // Do nada
            }
        });
    }

    private boolean isEdited() {
        PhysicalData a = physicalData;
        PhysicalData b = readDataPanels();
        if (a == null || b == null) {
            return false;
        }
        else {
            return !eq.equal(a, b);
        }
    }

    /**
     * @param  obj Description of the Parameter
     * @param  changeCode Description of the Parameter
     * @see  mbarix4j.util.IObserver#update(java.lang.Object, java.lang.Object)
     */
    public void update(final Object obj, final Object changeCode) {
        final Observation obs = (Observation) obj;

        if (obs == null) {
            clearValues();
            return;
        }

        final VideoFrame vf = obs.getVideoFrame();
        if (vf == null) {
            clearValues();
        }
        else {
            physicalData = vf.getPhysicalData();
            if (physicalData == null) {
                clearValues();
            }
            else {
                setProperties(physicalData);
            }
            for (String name : propertyNames) {
                getPropertyPanel(name).getValueField().setForeground(Color.BLACK);
            }
        }

    }

    private PhysicalData readDataPanels() {
        if (physicalData == null) {
            return null;
        }
        else {
            return new ImmutablePhysicalData(physicalData.getPrimaryKey(),
                    readFloat("Altitude", physicalData.getAltitude()),
                    readFloat("Depth", physicalData.getDepth()),
                    readDouble("Latitude", physicalData.getLatitude()),
                    readFloat("Light", physicalData.getLight()),
                    physicalData.getLogDate(),
                    readDouble("Longitude", physicalData.getLongitude()),
                    readFloat("Oxygen", physicalData.getOxygen()),
                    readFloat("Salinity", physicalData.getSalinity()),
                    readFloat("Temperature", physicalData.getTemperature()));

        }
    }


    class MyDocListener implements DocumentListener {

        private final JTextField textField;
        public final Color defaultColor;

        public MyDocListener(JTextField textField) {
            this.textField = textField;
            this.defaultColor = textField.getForeground();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateUI();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            updateUI();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateUI();
        }

        private void updateUI() {
            Color color = isEdited() ? Color.RED : defaultColor;
            textField.setForeground(color);
        }

    }

}

