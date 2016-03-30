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


package vars.annotation.ui.ppanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.bushe.swing.event.EventBus;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.awt.event.NonDigitConsumingKeyListener;
import org.mbari.swing.PropertyPanel;
import vars.annotation.ui.StateLookup;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.commandqueue.CommandEvent;
import vars.annotation.ui.commandqueue.impl.ChangeSequenceNumberCmd;
import vars.annotation.ui.commandqueue.impl.ChangeVideoArchiveSetCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveSet;
import vars.annotation.VideoFrame;
import vars.annotation.CameraDeployment;
import vars.annotation.Observation;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.impl.RenameVideoArchiveCmd;
import vars.annotation.ui.dialogs.RenameVideoArchiveDialog;

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


    private RenameVideoArchiveDialog changeNameDialog;
    
    private final ToolBelt toolBelt;

    /**
     * Constructs ...
     *
     */
    public PVideoArchivePanel(ToolBelt toolBelt) {
        super();
        this.toolBelt = toolBelt;
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
        f2.addActionListener(e -> {
            Collection<Observation> observations = StateLookup.getSelectedObservations();
            observations = new ArrayList<>(observations); // Make a copy to avoid sync issues.
            if (observations.size() == 1) {

                final Observation obs = observations.iterator().next();
                final VideoFrame vf = obs.getVideoFrame();
                if (vf != null) {
                    final VideoArchive va = vf.getVideoArchive();
                    if (va != null) {
                        final VideoArchiveSet vas = va.getVideoArchiveSet();
                        Command command = new ChangeVideoArchiveSetCmd(f2.getText(), vas.getPlatformName(),
                                vas.getFormatCode(), vas);
                        CommandEvent commandEvent = new CommandEvent(command);
                        EventBus.publish(commandEvent);
                    }
                }
            }
        });
        p.setEditable(true);

        // //////////////////////////////////////////////////////////////////////
        // Listeners for the platformName
        p = getPropertyPanel("platformName");
        final JTextField f3 = p.getValueField();
        f3.addActionListener(e -> {
            Collection<Observation> observations = StateLookup.getSelectedObservations();
            observations = new ArrayList<>(observations); // Make a copy to avoid sync issues.
            if (observations.size() == 1) {
                final Observation obs = observations.iterator().next();
                final VideoFrame vf = obs.getVideoFrame();
                if (vf != null) {
                    final VideoArchive va = vf.getVideoArchive();
                    if (va != null) {
                        final VideoArchiveSet vas = va.getVideoArchiveSet();
                        Command command = new ChangeVideoArchiveSetCmd(vas.getShipName(), f3.getText(),
                                vas.getFormatCode(), vas);
                        CommandEvent commandEvent = new CommandEvent(command);
                        EventBus.publish(commandEvent);
                    }
                }
            }
        });
        p.setEditable(true);


        p = getPropertyPanel("diveNumber");
        final JTextField f4 = p.getValueField();
        f4.addKeyListener(new NonDigitConsumingKeyListener());
        f4.addActionListener(e -> {

            Collection<Observation> observations = StateLookup.getSelectedObservations();
            observations = new ArrayList<>(observations); // Make a copy to avoid sync issues.
            if (observations.size() == 1) {
                final Observation obs = observations.iterator().next();
                final VideoFrame vf = obs.getVideoFrame();
                if (vf != null) {
                    final VideoArchive va = vf.getVideoArchive();
                    if (va != null) {
                        try {
                            final CameraDeployment cameraDeployment = va.getVideoArchiveSet().getCameraDeployments().iterator().next();
                            int newSeqNumber = Integer.parseInt(f4.getText());
                            int oldSeqNumber = cameraDeployment.getSequenceNumber();
                            long id = (Long) va.getPrimaryKey();
                            Command command = new ChangeSequenceNumberCmd(newSeqNumber, oldSeqNumber, id);
                            CommandEvent commandEvent = new CommandEvent(command);
                            EventBus.publish(commandEvent);
                        }
                        catch (Exception e1) {
                            log.info("Failed to set diveNumber", e1);
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


    private RenameVideoArchiveDialog getChangeNameDialog() {
        if (changeNameDialog == null) {
            changeNameDialog = new RenameVideoArchiveDialog(SwingUtilities.getWindowAncestor(this), toolBelt);
            changeNameDialog.getOkayButton().addActionListener(e -> {
                VideoArchive videoArchive = StateLookup.getVideoArchive();
                if (videoArchive != null) {
                    Command command = new RenameVideoArchiveCmd(videoArchive.getName(), getChangeNameDialog().getNewVideoArchiveName());
                    CommandEvent commandEvent = new CommandEvent(command);
                    EventBus.publish(commandEvent);
                }
            });

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
                    if (cpd.size() > 0) {
                        String sep = "";
                        if (cpd.size() > 1) {
                            sep = " | ";
                        }

                        final StringBuilder d = new StringBuilder();
                        final StringBuilder s = new StringBuilder();
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
