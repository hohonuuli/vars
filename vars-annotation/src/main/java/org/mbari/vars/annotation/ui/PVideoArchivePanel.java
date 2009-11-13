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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import javax.swing.JDialog;
import javax.swing.JTextField;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.swing.PropertyPanel;
import org.mbari.vars.annotation.locale.OpenVideoArchiveSetUsingParamsDialog;
import org.mbari.vars.annotation.ui.actions.ChangeVideoArchiveNameAction;
import org.mbari.vars.annotation.ui.dispatchers.ObservationDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.ObservationTableDispatcher;
import org.mbari.vars.dao.DAOEventQueue;
import org.mbari.vars.dao.IDataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.IVideoArchive;
import vars.annotation.IVideoArchiveSet;
import vars.annotation.IVideoFrame;
import vars.annotation.IObservation;
import vars.annotation.ICameraPlatformDeployment;

/**
 * <p>
 * Displays properties of a VideoArchive
 * </p>
 *
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: PVideoArchivePanel.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class PVideoArchivePanel extends PropertiesPanel {

    /**
     *
     */
    private static final long serialVersionUID = -2335603516275582204L;
    private static final Logger log = LoggerFactory.getLogger(PVideoArchivePanel.class);

    /**
     *     @uml.property  name="changeNameAction"
     *     @uml.associationEnd
     */
    private ActionAdapter changeNameAction;

    /**
     *     @uml.property  name="changeNameDialog"
     *     @uml.associationEnd
     */
    private JDialog changeNameDialog;

    /**
     * Constructs ...
     *
     */
    PVideoArchivePanel() {
        super();
        setPropertyNames(new String[] {
            "recordedDate", "videoArchiveName", "shipName", "platformName", "formatCode", "startDate", "endDate",
            "chiefScientist", "diveNumber", "trackingNumber"
        });
        addListeners();
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    private void addListeners() {

        // //////////////////////////////////////////////////////////////////////
        // Listeners for the videoArchiveName
        PropertyPanel p = getPropertyPanel("videoArchiveName");
        p.setEditAction(getChangeNameAction());

        // //////////////////////////////////////////////////////////////////////
        // Listeners for the shipName
        p = getPropertyPanel("shipName");
        final JTextField f2 = p.getValueField();
        f2.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                final IObservation obs = ObservationDispatcher.getInstance().getObservation();
                final IVideoFrame vf = obs.getVideoFrame();
                if (vf != null) {
                    final IVideoArchive va = vf.getVideoArchive();
                    if (va != null) {
                        final IVideoArchiveSet vas = va.getVideoArchiveSet();
                        vas.setShipName(f2.getText());

                        try {
                            DAOEventQueue.update((IDataObject) vas);
                        }
                        catch (final Exception e1) {
                            if (log.isErrorEnabled()) {
                                log.error("Failed to update a videoarchiveset", e1);
                            }
                        }
                    }
                }
            }

        });
        p.setEditable(true);

        // //////////////////////////////////////////////////////////////////////
        // Listeners for the platformName
        p = getPropertyPanel("platformName");
        final JTextField f3 = p.getValueField();
        f3.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                final IObservation obs = ObservationDispatcher.getInstance().getObservation();
                final IVideoFrame vf = obs.getVideoFrame();
                if (vf != null) {
                    final IVideoArchive va = vf.getVideoArchive();
                    if (va != null) {
                        final IVideoArchiveSet vas = va.getVideoArchiveSet();
                        vas.setPlatformName(f3.getText());

                        try {
                            DAOEventQueue.update((IDataObject) vas);
                        }
                        catch (final Exception e1) {
                            if (log.isErrorEnabled()) {
                                log.error("Failed to updated " + vas, e1);
                            }
                        }
                    }
                }
            }

        });
        p.setEditable(true);
    }

    /**
     *     <p><!-- Method description --></p>
     *     @return
     *     @uml.property  name="changeNameAction"
     */
    private ActionAdapter getChangeNameAction() {
        if (changeNameAction == null) {
            changeNameAction = new ActionAdapter() {

                /**
                 *
                 */
                private static final long serialVersionUID = -7518619879734127810L;

                public void doAction() {
                    getChangeNameDialog().setVisible(true);
                }
            };
        }

        return changeNameAction;
    }

    /**
     *     <p><!-- Method description --></p>
     *     @return
     *     @uml.property  name="changeNameDialog"
     */
    private JDialog getChangeNameDialog() {
        if (changeNameDialog == null) {
            changeNameDialog = new OpenVideoArchiveSetUsingParamsDialog() {

                /**
                 *
                 */
                private static final long serialVersionUID = -6624844651924209285L;

                /*
                 *  (non-Javadoc)
                 *  @see org.mbari.vars.annotation.ui.dialogs.OpenVideoArchiveSetUsingParamsDialog#getOkButtonAction()
                 */
                public ActionAdapter getOkButtonAction() {
                    if (okButtonAction == null) {
                        okButtonAction = new ActionAdapter() {

                            /**
                             *
                             */
                            private static final long serialVersionUID = 3065036036641458323L;
                            public void doAction() {
                                final int seqNumber = Integer.parseInt(getTfDiveNumber().getText());
                                final String platform = (String) getCbCameraPlatform().getSelectedItem();
                                final int tapeNumber = Integer.parseInt(getTfTapeNumber().getText());
                                action.setPlatform(platform);
                                action.setSeqNumber(seqNumber);
                                action.setTapeNumber(tapeNumber);
                                action.doAction();
                                dispose();
                                ObservationTableDispatcher.getInstance().getObservationTable().redrawAll();
                            }
                            private final ChangeVideoArchiveNameAction action = new ChangeVideoArchiveNameAction();
                        };
                    }

                    return okButtonAction;
                }
            };
        }

        return changeNameDialog;
    }

    /**
     *
     * @param  obj Description of the Parameter
     * @param  changeCode Description of the Parameter
     * @see org.mbari.util.IObserver#update(java.lang.Object, java.lang.Object)
     */
    public void update(final Object obj, final Object changeCode) {
        
        final IObservation obs = (IObservation) obj;
        if (obs == null) {
            clearValues();
            return;
        }

        final IVideoFrame vf = obs.getVideoFrame();
        if (vf == null) {
            clearValues();
        }
        else {
            final PVideoArchive pvf = new PVideoArchive(vf);
            setProperties(pvf);
        }
    }

    /**
     *     @author  brian
     */
    private class PVideoArchive {

        private String chiefScientist;
        private String diveNumber;
        private Date endDate;
        private String formatCode;
        private String platformName;
        private Date recordedDate;
        private String shipName;
        private Date startDate;
        private String trackingNumber;
        private String videoArchiveName;

        /**
         * Constructs ...
         *
         *
         * @param videoFrame
         */
        PVideoArchive(final IVideoFrame videoFrame) {
            recordedDate = videoFrame.getRecordedDate();
            final IVideoArchive va = videoFrame.getVideoArchive();
            if (va != null) {
                videoArchiveName = va.getVideoArchiveName();
                final IVideoArchiveSet vas = va.getVideoArchiveSet();
                if (vas != null) {
                    formatCode = vas.getFormatCode() + "";
                    shipName = vas.getShipName();
                    platformName = vas.getPlatformName();
                    startDate = vas.getStartDate();
                    endDate = vas.getEndDate();
                    trackingNumber = vas.getTrackingNumber();
                    final Collection cpd = vas.getCameraPlatformDeployments();
                    if ((cpd != null) && (cpd.size() > 0)) {
                        String sep = "";
                        if (cpd.size() > 1) {
                            sep = " | ";
                        }

                        final StringBuffer d = new StringBuffer();
                        final StringBuffer s = new StringBuffer();
                        for (final Iterator i = cpd.iterator(); i.hasNext(); ) {
                            final ICameraPlatformDeployment pd = (ICameraPlatformDeployment) i.next();
                            d.append(pd.getSeqNumber()).append(sep);
                            s.append(pd.getChiefScientistName()).append(sep);
                        }

                        chiefScientist = s.toString();
                        diveNumber = d.toString();
                    }
                }
            }
        }

        /**
         *         @return   Returns the chiefScientist.
         *         @uml.property  name="chiefScientist"
         */
        public String getChiefScientist() {
            return chiefScientist;
        }

        /**
         *         @return   Returns the diveNumber.
         *         @uml.property  name="diveNumber"
         */
        public String getDiveNumber() {
            return diveNumber;
        }

        /**
         *         @return   Returns the endDate.
         *         @uml.property  name="endDate"
         */
        public Date getEndDate() {
            return endDate;
        }

        /**
         *         @return   Returns the formatCode.
         *         @uml.property  name="formatCode"
         */
        public String getFormatCode() {
            return formatCode;
        }

        /**
         *         @return   Returns the platformName.
         *         @uml.property  name="platformName"
         */
        public String getPlatformName() {
            return platformName;
        }

        /**
         *         @return   Returns the recordedDate.
         *         @uml.property  name="recordedDate"
         */
        public Date getRecordedDate() {
            return recordedDate;
        }

        /**
         *         @return   Returns the shipName.
         *         @uml.property  name="shipName"
         */
        public String getShipName() {
            return shipName;
        }

        /**
         *         @return   Returns the startDate.
         *         @uml.property  name="startDate"
         */
        public Date getStartDate() {
            return startDate;
        }

        /**
         *         Gets the trackingNumber attribute of the PVideoArchive object
         *         @return   The trackingNumber value
         *         @uml.property  name="trackingNumber"
         */
        public String getTrackingNumber() {
            return trackingNumber;
        }

        /**
         *         @return   Returns the videoArchiveName
         *         @uml.property  name="videoArchiveName"
         */
        public String getVideoArchiveName() {
            return videoArchiveName;
        }
    }
}
