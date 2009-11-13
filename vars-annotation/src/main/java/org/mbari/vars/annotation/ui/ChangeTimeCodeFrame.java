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
Created on Sep 10, 2004
 */
package org.mbari.vars.annotation.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.mbari.movie.Timecode;
import org.mbari.util.IObserver;
import org.mbari.vars.annotation.ui.actions.ChangeTimeCodeAction;
import org.mbari.vars.annotation.ui.dispatchers.ObservationDispatcher;
import org.mbari.vcr.ui.TimeCodeSelectionFrame;
import org.mbari.vcr.ui.TimeSelectPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.Observation;
import vars.annotation.VideoFrame;

/**
 * <p>A dialog that allows the annotator to edit the time-code for the currently
 * selected observation.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: ChangeTimeCodeFrame.java 314 2006-07-10 02:38:46Z hohonuuli $
 */
public class ChangeTimeCodeFrame extends TimeCodeSelectionFrame implements IObserver {

    /**
     *
     */
    private static final long serialVersionUID = -3207616146049862321L;
    private static final Logger log = LoggerFactory.getLogger(ChangeTimeCodeFrame.class);

    /**
     * Constructor for the ChangeTimeCodeFrame object
     */
    public ChangeTimeCodeFrame() {
        super();
        ObservationDispatcher.getInstance().addObserver(this);

        if (log.isDebugEnabled()) {
            log.debug("Initializing " + getClass().getName());
        }
    }

    /**
     *  Gets the okActionListener attribute of the ChangeTimeCodeFrame object
     *
     * @return  The okActionListener value
     * @see org.mbari.vcr.ui.TimeCodeSelectionFrame#getOkActionListener()
     */
    public ActionListener getOkActionListener() {
        if (okActionListener == null) {
            okActionListener = new ActionListener() {

                public void actionPerformed(final ActionEvent e) {
                    final ObservationDispatcher dispatcher = ObservationDispatcher.getInstance();
                    final Observation obs = dispatcher.getObservation();
                    if (obs != null) {
                        final VideoFrame vf = obs.getVideoFrame();
                        synchronized (vf) {
                            action.setVideoFrame(vf);
                            action.setTimeCode(getTimePanel().getTimeAsString());
                            action.doAction();
                        }
                    }

                    setVisible(false);
                    dispatcher.setObservation(dispatcher.getObservation());
                }
                private final ChangeTimeCodeAction action = new ChangeTimeCodeAction();
            };
        }

        return okActionListener;
    }

    /**
     *  Recieves notifications when the selected observation changes.
     *
     * @param  observedObj The selected observation, could be null
     * @param  changeCode No usedr
     * @see org.mbari.util.IObserver#update(java.lang.Object, java.lang.Object)
     */
    public void update(final Object observedObj, final Object changeCode) {
        if (observedObj == null) {
            return;
        }

        final Observation obs = (Observation) observedObj;
        final VideoFrame vf =  obs.getVideoFrame();
        if ((obs != null) && (vf != null)) {
            final String stc = vf.getTimecode();
            if (stc != null) {
                final TimeSelectPanel tp = getTimePanel();
                try {
                    final Timecode tc = new Timecode(stc);
                    tp.getHourWidget().setTime(tc.getHour());
                    tp.getMinuteWidget().setTime(tc.getMinute());
                    tp.getSecondWidget().setTime(tc.getSecond());
                    tp.getFrameWidget().setTime(tc.getFrame());
                }
                catch (final NumberFormatException e) {
                    tp.getHourWidget().setTime(0);
                    tp.getMinuteWidget().setTime(0);
                    tp.getSecondWidget().setTime(0);
                    tp.getFrameWidget().setTime(0);
                }

                tp.getHourWidget().getTextField().requestFocus();
            }
        }
    }
}
