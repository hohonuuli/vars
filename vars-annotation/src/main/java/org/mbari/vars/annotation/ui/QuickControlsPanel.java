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
Created on Dec 3, 2003
 */
package org.mbari.vars.annotation.ui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.mbari.vars.annotation.model.VideoArchiveSet;
import org.mbari.vars.annotation.ui.actions.ChangeAnnotationModeAction;
import org.mbari.vars.annotation.ui.dispatchers.PredefinedDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.VideoArchiveDispatcher;
import vars.annotation.ICameraData;
import vars.annotation.IVideoArchive;
import vars.annotation.IVideoArchiveSet;

/**
 * <p><!--Insert summary here--></p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: QuickControlsPanel.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class QuickControlsPanel extends JPanel {

 
    private final ChangeAnnotationModeAction action = new ChangeAnnotationModeAction();


    private JLabel cameraLabel;

    private JComboBox cbCameraDirection;


    private javax.swing.JComboBox modeChoiceBox;

    private javax.swing.JLabel modeLabel;

    private ModeObject[] modeObjects;

    public QuickControlsPanel() {
        super();
        initialize();
    }

    /**
     *     <p><!-- Method description --></p>
     *     @return
     *     @uml.property  name="cameraLabel"
     */
    private JLabel getCameraLabel() {
        if (cameraLabel == null) {
            cameraLabel = new JLabel();
            cameraLabel.setText(" Camera Direction: ");
        }

        return cameraLabel;
    }

    /**
     *     <p><!-- Method description --></p>
     *     @return
     */
    private JComboBox getCbCameraDirection() {
        if (cbCameraDirection == null) {
            cbCameraDirection = new CameraDirectionComboBox();

            // Forward selections to CameraDirectionDispatcher
            cbCameraDirection.addItemListener(new ItemListener() {

                public void itemStateChanged(final ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        PredefinedDispatcher.CAMERA_DIRECTION.getDispatcher().setValueObject(
                            (String) cbCameraDirection.getSelectedItem());

                    }
                }

            });
            cbCameraDirection.setSelectedItem(ICameraData.DIR_DESCEND);
            final String dir = (String) cbCameraDirection.getSelectedItem();
            PredefinedDispatcher.CAMERA_DIRECTION.getDispatcher().setValueObject(dir);
        }

        return cbCameraDirection;
    }

    /**
     *     This method initializes modeChoiceBox
     *     @return   javax.swing.JComboBox
     */
    private javax.swing.JComboBox getModeChoiceBox() {
        if (modeChoiceBox == null) {
            modeChoiceBox = new ModeComboBox();
        }

        return modeChoiceBox;
    }

    /**
     *     This method initializes modeLabel
     *     @return   javax.swing.JLabel
     */
    private javax.swing.JLabel getModeLabel() {
        if (modeLabel == null) {
            modeLabel = new javax.swing.JLabel();
            modeLabel.setText(" Annotation Mode: ");
        }

        return modeLabel;
    }

    /**
     *     Gets the modeObjects attribute of the QuickControls object
     *     @return   The modeObjects value
     */
    public ModeObject[] getModeObjects() {
        if (modeObjects == null) {
            modeObjects = new ModeObject[] { new ModeObject(IVideoArchiveSet.FORMAT_CODE_OUTLINE),
                                             new ModeObject(IVideoArchiveSet.FORMAT_CODE_DETAILED) };
        }

        return modeObjects;
    }

    /**
     * This method initializes this
     *
     *
     */
    private void initialize() {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.add(getModeLabel(), null);
        this.add(getModeChoiceBox(), null);
        this.add(getCameraLabel(), null);
        this.add(getCbCameraDirection(), null);
        getModeChoiceBox();
    }

    class ModeComboBox extends JComboBox implements PropertyChangeListener {

        /**
         *
         */
        private static final long serialVersionUID = 5554180629594135416L;

        /**
         * Constructs ...
         *
         */
        ModeComboBox() {
            super();
            initialize();
        }

        /**
         * <p><!-- Method description --></p>
         *
         */
        void initialize() {
            setEditable(false);
            setComponentOrientation(java.awt.ComponentOrientation.UNKNOWN);
            setMaximumRowCount(5);
            setName("Annotation Mode");
            final ModeObject[] mos = getModeObjects();
            for (int i = 0; i < mos.length; i++) {
                addItem(mos[i]);
            }

            setPreferredSize(new java.awt.Dimension(120, 25));
            addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(final java.awt.event.ActionEvent e) {
                    final Object selectedItem = getSelectedItem();
                    if (!(selectedItem instanceof ModeObject)) {
                        return;
                    }

                    final ModeObject mode = (ModeObject) modeChoiceBox.getSelectedItem();
                    action.setFormatCode(mode.code);
                    action.doAction();
                }

            });
            VideoArchiveDispatcher.getInstance().addPropertyChangeListener(this);
        }

        /**
         * Registered to the VideoArchiveDispatcher
         *
         * @param  evt Description of the Parameter
         * @see  java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        public void propertyChange(final PropertyChangeEvent evt) {
            final IVideoArchive va = (IVideoArchive) evt.getNewValue();
            if (va != null) {
                final char formatCode = va.getVideoArchiveSet().getFormatCode();
                final ModeObject[] modes = getModeObjects();
                for (int i = 0; i < modes.length; i++) {
                    if (modes[i].code == formatCode) {
                        final JComboBox cb = getModeChoiceBox();
                        final ModeObject mo = (ModeObject) cb.getSelectedItem();
                        if ((mo != null) &&!mo.equals(modes[i])) {
                            cb.setSelectedItem(modes[i]);
                        }

                        return;
                    }
                }
            }
        }
    }

    /**
     * Get the character codes allowed for setting the mode
     *
     * @author  brian
     * @version
     * @return  An array of the character codes allowed for
     */
    class ModeObject {

        private final char code;
        private final String codeString;

        /**
         * Constructs ...
         *
         *
         * @param c
         */
        ModeObject(final char c) {
            code = c;
            codeString = VideoArchiveSet.getFormatCodeDescriptiveName(c);
        }

        /**
         *  Description of the Method
         *
         * @param  obj Description of the Parameter
         * @return  Description of the Return Value
         */
        public boolean equals(final Object obj) {
            boolean ok = false;
            if ((obj == null) ||!obj.getClass().equals(getClass())) {
                ok = false;
            }
            else if (obj == this) {
                ok = true;
            }
            else {
                final ModeObject that = (ModeObject) obj;
                if (this.code == that.code) {
                    ok = true;
                }
            }

            return ok;
        }

        /**
         *  Description of the Method
         *
         * @return  Description of the Return Value
         */
        public String toString() {
            return codeString;
        }
    }
}
