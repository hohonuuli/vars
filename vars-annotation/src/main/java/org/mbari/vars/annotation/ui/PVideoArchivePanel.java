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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import javax.swing.JDialog;
import javax.swing.JTextField;

import org.bushe.swing.event.EventBus;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.swing.PropertyPanel;
import org.mbari.vars.annotation.locale.OpenVideoArchiveSetUsingParamsDialog;
import org.mbari.vars.annotation.ui.actions.ChangeVideoArchiveNameAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveSet;
import vars.annotation.VideoFrame;
import vars.annotation.Observation;
import vars.annotation.CameraDeployment;
import vars.annotation.Observation;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.PersistenceService;

/**
 * <p>
 * Displays properties of a VideoArchive
 * </p>
 *
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class PVideoArchivePanel extends PropertiesPanel {


    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private ActionAdapter changeNameAction;


    private JDialog changeNameDialog;
    
    private final PersistenceService persistenceService;

    /**
     * Constructs ...
     *
     */
    PVideoArchivePanel(PersistenceService persistenceService) {
        super();
        this.persistenceService = persistenceService;
        setPropertyNames(new String[] {
            "recordedDate", "videoArchiveName", "shipName", "platformName", "formatCode", "startDate", "endDate",
            "chiefScientist", "diveNumber", "trackingNumber"
        });
        addListeners();
    }

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
                
                Collection<Observation> observations = (Collection<Observation>) Lookup.getSelectedObservationsDispatcher().getValueObject();
                observations = new ArrayList<Observation>(observations); // Make a copy to avoid sync issues.
                if (observations.size() == 1) {
                    
                    final Observation obs = observations.iterator().next();
                    final VideoFrame vf = obs.getVideoFrame();
                    if (vf != null) {
                        final VideoArchive va = vf.getVideoArchive();
                        if (va != null) {
                            final VideoArchiveSet vas = va.getVideoArchiveSet();
                            vas.setShipName(f2.getText());
    
                            try {
                                persistenceService.updateVideoArchiveSet(vas);
                            }
                            catch (final Exception e1) {
                                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e1);
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
                Collection<Observation> observations = (Collection<Observation>) Lookup.getSelectedObservationsDispatcher().getValueObject();
                observations = new ArrayList<Observation>(observations); // Make a copy to avoid sync issues.
                if (observations.size() == 1) {
                    final Observation obs = observations.iterator().next();
                    final VideoFrame vf = obs.getVideoFrame();
                    if (vf != null) {
                        final VideoArchive va = vf.getVideoArchive();
                        if (va != null) {
                            final VideoArchiveSet vas = va.getVideoArchiveSet();
                            vas.setPlatformName(f3.getText());
    
                            try {
                                persistenceService.updateVideoArchiveSet(vas);
                            }
                            catch (final Exception e1) {
                                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e1);
                            }
                        }
                    }
                }
            }

        });
        p.setEditable(true);
    }

    private ActionAdapter getChangeNameAction() {
        if (changeNameAction == null) {
            changeNameAction = new ActionAdapter() {

                public void doAction() {
                    getChangeNameDialog().setVisible(true);
                }
            };
        }

        return changeNameAction;
    }


    private JDialog getChangeNameDialog() {
        if (changeNameDialog == null) {
            changeNameDialog = new OpenVideoArchiveSetUsingParamsDialog() {

                /**
                 *  @see org.mbari.vars.annotation.ui.dialogs.OpenVideoArchiveSetUsingParamsDialog#getOkButtonAction()
                 */
                public ActionAdapter getOkButtonAction() {
                    if (okButtonAction == null) {
                        okButtonAction = new ActionAdapter() {
                            public void doAction() {
                                final int seqNumber = Integer.parseInt(getTfDiveNumber().getText());
                                final String platform = (String) getCbCameraPlatform().getSelectedItem();
                                final int tapeNumber = Integer.parseInt(getTfTapeNumber().getText());
                                action.setPlatform(platform);
                                action.setSeqNumber(seqNumber);
                                action.setTapeNumber(tapeNumber);
                                action.doAction();
                                dispose();
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
        PVideoArchive(final VideoFrame videoFrame) {
            recordedDate = videoFrame.getRecordedDate();
            final VideoArchive va = videoFrame.getVideoArchive();
            if (va != null) {
                videoArchiveName = va.getName();
                final VideoArchiveSet vas = va.getVideoArchiveSet();
                if (vas != null) {
                    formatCode = vas.getFormatCode() + "";
                    shipName = vas.getShipName();
                    platformName = vas.getPlatformName();
                    startDate = vas.getStartDate();
                    endDate = vas.getEndDate();
                    trackingNumber = vas.getTrackingNumber();
                    final Collection<CameraDeployment> cpd = new ArrayList<CameraDeployment>(vas.getCameraDeployments());
                    if ((cpd != null) && (cpd.size() > 0)) {
                        String sep = "";
                        if (cpd.size() > 1) {
                            sep = " | ";
                        }

                        final StringBuffer d = new StringBuffer();
                        final StringBuffer s = new StringBuffer();
                        for (final Iterator i = cpd.iterator(); i.hasNext(); ) {
                            final CameraDeployment pd = (CameraDeployment) i.next();
                            d.append(pd.getSequenceNumber()).append(sep);
                            s.append(pd.getChiefScientistName()).append(sep);
                        }

                        chiefScientist = s.toString();
                        diveNumber = d.toString();
                    }
                }
            }
        }

        public String getChiefScientist() {
            return chiefScientist;
        }

        public String getDiveNumber() {
            return diveNumber;
        }

        public Date getEndDate() {
            return endDate;
        }

        public String getFormatCode() {
            return formatCode;
        }

        public String getPlatformName() {
            return platformName;
        }

        public Date getRecordedDate() {
            return recordedDate;
        }

        public String getShipName() {
            return shipName;
        }

        /**
         *         @return   Returns the startDate.
         */
        public Date getStartDate() {
            return startDate;
        }

        /**
         *         Gets the trackingNumber attribute of the PVideoArchive object
         *         @return   The trackingNumber value
         */
        public String getTrackingNumber() {
            return trackingNumber;
        }

        /**
         *         @return   Returns the videoArchiveName
         */
        public String getVideoArchiveName() {
            return videoArchiveName;
        }
    }
}
