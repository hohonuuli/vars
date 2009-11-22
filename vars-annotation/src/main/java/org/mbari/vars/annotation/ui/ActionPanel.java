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


package org.mbari.vars.annotation.ui;

import java.awt.FlowLayout;
import java.util.Enumeration;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.mbari.awt.layout.WrappingFlowLayout;
import org.slf4j.LoggerFactory;

import vars.annotation.ui.ToolBelt;

/**
 * <p> This panel contains buttons for actions that can modify the contents of the
 *  <code>ObservatonTable</code></p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @created  February 17, 2004
 */
public class ActionPanel extends JPanel {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ActionPanel.class);


    JButton btnDeepCopy;

  
    JButton btnDelete;


    JButton btnNew;


    JButton btnShallowCopy;

 
    JButton btnFramegrab;


    FlowLayout flowLayout = new WrappingFlowLayout();

    /**
     *  Constructor for the ActionPanel object
     */
    public ActionPanel(ToolBelt toolBelt) {
        super();
        btnDeepCopy = new CopyObservationButton(toolBelt);
        btnDelete = new DeleteSelectedObservationsButton(toolBelt);
        btnNew = new NewVideoFrameButton(toolBelt);
        btnShallowCopy = new NewObservationButton(toolBelt);
        btnFramegrab = new FrameCaptureButton(toolBelt);
        initialize();
        registerHotKeys();
    }

    void initialize() {
        setLayout(flowLayout);
        flowLayout.setAlignment(FlowLayout.LEFT);

        // setBounds(new java.awt.Rectangle(0, 0, 405, 80));
        add(btnNew, null);
        add(btnShallowCopy, null);
        add(btnDeepCopy, null);
        add(btnFramegrab, null);

        /*
         * The majority of buttons are read from the vars-annotation property
         * file and insantiated at runtime. This allows location specifc 
         * customization with requiring the agency to rebuild VARS from scratch.
         */
        ResourceBundle bundle = ResourceBundle.getBundle("vars-annotation");
        Enumeration<String> enumeration = bundle.getKeys();
        Map<String, JButton> map = new TreeMap<String, JButton>();
        while (enumeration.hasMoreElements()) {
            String element = enumeration.nextElement();
            if (element.startsWith("actionpanel.button")) {
                try {
                    map.put(element, (JButton) Class.forName(bundle.getString(element)).newInstance());
                }
                catch (Exception ex) {
                    log.warn("Unable to add button class specified by '" + element + 
                            "' in vars-annotation.properties", ex);
                }
            }
        }

        for (String key : map.keySet()) {
            add(map.get(key));
        }

        add(btnDelete, null);
    }

    /**
     *  Register
     */
    public void registerHotKeys() {
        final Action[] as = { btnDelete.getAction(), btnNew.getAction(), btnDeepCopy.getAction(),
                              btnShallowCopy.getAction(), btnFramegrab.getAction() };
        for (int i = 0; i < as.length; i++) {
            final Action a = as[i];
            getActionMap().put(a.getValue(Action.ACTION_COMMAND_KEY), a);
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put((KeyStroke) a.getValue(Action.ACCELERATOR_KEY),
                        a.getValue(Action.ACTION_COMMAND_KEY));
        }
    }
}
