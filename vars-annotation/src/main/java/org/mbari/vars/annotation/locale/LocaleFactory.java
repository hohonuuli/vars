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
 * Created on Dec 16, 2004
 */
package org.mbari.vars.annotation.locale;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.util.IObserver;
import org.mbari.vars.annotation.ui.dispatchers.VcrDispatcher;
import vars.util.VARSProperties;
import org.mbari.vcr.IVCR;
import org.mbari.vcr.timer.DefaultMonitoringVCR;
import org.mbari.vcr.timer.Monitor;
import org.mbari.vcr.timer.MonitoringVCR;
import org.mbari.vcr.timer.TimecodeMonitor;
import org.mbari.vcr.udp01.VCR;

//~--- classes ----------------------------------------------------------------

/**
 * Acquires the deployment location (Shore, Point Lobos, Western Flyer) from a
 * properties file, vars.propertes. Attempts to set certain behaviors base on
 * location. These behaviors include:
 * <ul>
 * <li>Initializing the VCR on COM1 and calling the play method so that it
 * acquires the time-code when on the ships.</li>
 * </ul>
 *
 * @author brian
 *
 */
public class LocaleFactory {

    private static final Logger log = LoggerFactory.getLogger(LocaleFactory.class);
    private static String cameraPlatform;
    private static boolean isInitialized;
    private static String locale;

    //~--- constructors -------------------------------------------------------

    /**
     *
     */
    private LocaleFactory() {
        super();
        // No external instantiation allowed
    }

    //~--- get methods --------------------------------------------------------

    /**
     *
     * @return The name of the camera platform that VARS is configured for. This
     *         is configured by in the vars.properties file. The platforms are
     *         "Ventana" and "Tiburon"
     */
    public static String getCameraPlatform() {
        initialize();
        return cameraPlatform;
    }

    /**
     *
     * @return The location that vars is installed at. This is configured in the
     *         vars.properties file. This is "Shore", "Western Flyer", "Point
     *         Lobos". If none was specified in the vars.properties file then this
     *         returns null.
     */
    public static String getLocale() {
        initialize();
        return locale;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * <p><!-- Method description --></p>
     *
     */
    private static void initialize() {
        if (!isInitialized) {
            if (locale == null) {
                locale = VARSProperties.getDeploymentLocale();

                if (locale != null) {
                    cameraPlatform = VARSProperties.getCameraPlatform(locale);
                }
            }

            /*
             * If VARS is running on one of the ships, we want the VCR to 'play' by
             * default. On this ships, since we don't actually control the VCR with
             * VARS, we use play to query the navproc for time-code.
             *
             */
            if (locale != null) {
                    
                final VcrDispatcher vcrDispatcher = VcrDispatcher.getInstance();
                    
                /*
                 * Automatically start 'playing' when VARS is started
                 */
                try {
                    vcrDispatcher.addObserver(new IObserver() {

                        public void update(Object obj, Object changeCode) {
                            if ((obj != null) && (obj instanceof IVCR)) {
                                ((IVCR) obj).play();
                            }
                        }

                    });

                } catch (Exception e) {
                    log.error("Auto-play on " + cameraPlatform + "failed.", e);
                }
                
            }
            
            /*
             * If a remote URL for the VCR is specified try to connect to it.
             */
            String vcrUrl = VARSProperties.getVcrUrl();
            int vcrPort = VARSProperties.getVcrPort();
            if (vcrUrl != null) {
                MonitoringVCR vcr = null;
                try {
                    // Slow down the timecode sampling rate when working over the network
                    vcr = new DefaultMonitoringVCR(new VCR(vcrUrl, VARSProperties.getVcrPort()));
                    Set<Monitor> monitors = vcr.getMonitors();
                    for (Monitor monitor : monitors) {
                        if (monitor.getName().equals(TimecodeMonitor.MONITOR_NAME)) {
                            monitor.setIntervalMin(350);
                            break;
                        }
                    }
                    VcrDispatcher.DISPATCHER.setValueObject(vcr);
                } catch (Exception ex) {
                    if (log.isWarnEnabled()) {
                        log.warn("Failed to connect to VCR on " + vcrUrl + ":" + vcrPort);
                    }
                } 
            }

            isInitialized = true;
        }
    }
}
