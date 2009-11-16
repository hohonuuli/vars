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

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.swing.IPopup;
import org.mbari.vars.annotation.ui.actions.AddNewRefNumPropAction;
import org.mbari.vars.annotation.ui.actions.UpdateNewRefNumAction;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;

/**
 * <p>
 * Adds a 'new reference number' assotiation to the currently selected observations.
 * </p>
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: NewRefNumPropButton.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class NewRefNumPropButton extends PropButton implements IPopup {


    private JPopupMenu jPopupMenu;

    private ActionAdapter refNumAction;

    private final ToolBelt toolBelt;

    /**
     *  Constructor
     */
    public NewRefNumPropButton(final ToolBelt toolBelt) {
        super();
        this.toolBelt = toolBelt;
        setAction(getRefNumAction());
        initialize();
    }

    /**
     *     <p><!-- Method description --></p>
     *     @return
     *     @uml.property  name="jPopupMenu"
     */
    private JPopupMenu getJPopupMenu() {
        if (jPopupMenu == null) {

            // Add popup
            jPopupMenu = new JPopupMenu();

            // Set to the last knwo value + 1
            final JMenuItem auto = new JMenuItem("Set to maximum reference number + 1");
            auto.addActionListener(getRefNumAction());
            jPopupMenu.add(auto);

            // Add reset
            final JMenuItem reset = new JMenuItem("Reset to one");
            reset.addActionListener(new ActionListener() {

                public void actionPerformed(final ActionEvent ae) {
                    AddNewRefNumPropAction.setRefNumber(1);
                }
            });
            jPopupMenu.add(reset);

            // Add set to refNumber
            final JMenuItem set = new JMenuItem("Set value");
            set.addActionListener(new ActionListener() {

                public void actionPerformed(final ActionEvent ae) {
                    VideoArchiveDAO videoArchiveDAO = toolBelt.getAnnotationDAOFactory().newVideoArchiveDAO();
                    VideoArchive videoArchive = (VideoArchive) Lookup.getVideoArchiveDispatcher().getValueObject();

                    // TODO 'identity-reference' should be pulled out and put into a properties file
                    final Collection<String> refNums = videoArchiveDAO.findAllLinkValues(videoArchive, "identity-reference");
                    // REturned as string convert to integers:
                    final Collection<Integer> refInts = new ArrayList<Integer>(refNums.size());
                    for (String object : refNums) {
                        refInts.add(Integer.valueOf(object));
                    }

                    // TO help the user out let's put the max used number in the dialog
                    final Integer maxNum = (Integer) Collections.max(refInts);
                    Frame frame = (Frame) Lookup.getApplicationFrameDispatcher().getValueObject();
                    final String s = (String) JOptionPane.showInputDialog(frame,
                            "Enter a reference number", "VARS - Input",
                            JOptionPane.PLAIN_MESSAGE, null,
                            null, maxNum.toString());
                    if (s == null) {

                        // do nothing
                    }
                    else {
                        try {
                            AddNewRefNumPropAction.setRefNumber(Integer.parseInt(s));
                        }
                        catch (final Exception e) {
                            e.printStackTrace();
                            actionPerformed(ae);
                        }
                    }
                }

            });
            jPopupMenu.add(set);
        }

        return jPopupMenu;
    }

    /**
     *  Gets the popupMenu used by this button
     *
     * @return  The popupMenu value
     *  @see org.mbari.swing.IPopup#getPopupMenu()
     */
    public JPopupMenu getPopupMenu() {
        return getJPopupMenu();
    }

    /**
     *     <p><!-- Method description --></p>
     *     @return
     *     @uml.property  name="refNumAction"
     */
    private ActionAdapter getRefNumAction() {
        if (refNumAction == null) {
            refNumAction = new RefNumAction();
        }

        return refNumAction;
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    private void initialize() {

        // Set some properties
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/newnum.png")));
        setToolTipText("reference #");
        setEnabled(false);
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(final MouseEvent e) {
                maybeShowPopup(e);
            }
            @Override
            public void mouseReleased(final MouseEvent e) {
                maybeShowPopup(e);
            }
            private void maybeShowPopup(final MouseEvent e) {
                if (e.isPopupTrigger()) {
                    getJPopupMenu().show(e.getComponent(), e.getX(), e.getY());
                }
            }

        });
    }

    /**
     *     This class combines 2 actions together and also sets up one of the actions to listen to changes in the VideoArchive.
     *     @author  brian
     */
    private class RefNumAction extends ActionAdapter {

        /**
         *
         */
        private static final long serialVersionUID = 1870783910768380309L;

        /**
         * This keeps the reference number incremented to one more than the largest
         * value found in the database when a VideoArchive is opened.
         */
        private final UpdateNewRefNumAction updateAction = new UpdateNewRefNumAction();

        /**
         * This action adds the association to the selected observation
         */
        private final AddNewRefNumPropAction newRefNumAction = new AddNewRefNumPropAction();

        /**
         * Constructs ...
         *
         */
        RefNumAction() {
            super();

            /*
             * Listen to changes in the videoArchive. When the archive is changed
             * the new reference number initial value will be set.
             */
            Lookup.getVideoArchiveDispatcher().addPropertyChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    updateAction.setVideoArchive((VideoArchive) evt.getNewValue());
                }
            });
 
        }

        /**
         * <p><!-- Method description --></p>
         *
         */
        public void doAction() {

            /*
             * First we need to make sure that
             */
            updateAction.doAction();
            newRefNumAction.doAction();
        }
    }
}