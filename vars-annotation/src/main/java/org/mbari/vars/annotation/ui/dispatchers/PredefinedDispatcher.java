/*
 * Copyright 2007 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/*
Dispatchers.java
 *
Created on March 27, 2007, 2:18 PM
 *
To change this template, choose Tools | Template Manager
and open the template in the editor.
 */

package org.mbari.vars.annotation.ui.dispatchers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.prefs.Preferences;
import org.mbari.framegrab.IGrabber;
import org.mbari.framegrab.FakeGrabber;
import org.mbari.qt.QT4JException;
import org.mbari.qt.awt.QTMovieFrame;
import org.mbari.util.Dispatcher;
import org.mbari.util.Person;
import org.mbari.vars.annotation.model.Observation;
import org.mbari.vars.annotation.model.VideoArchive;
import org.mbari.vars.annotation.ui.actions.CloseVideoArchiveAction;
import org.mbari.vars.annotation.ui.table.ObservationTable;
import org.mbari.vars.dao.DAOEventQueue;
import org.mbari.vars.dao.DAOException;
import org.mbari.vars.util.AppFrameDispatcher;
import org.mbari.vars.util.VARSPreferencesFactory;
import org.mbari.vcr.IVCR;
import org.mbari.vcr.VCRAdapter;
import org.mbari.vcr.qt.TimeSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Explicitly define dispatchers used by the annotation applications. This class 
 * defines behaviors and interactions between components such as VideoArchives,
 * Movies, Movie Frames and Frame Grabbers</p>
 * 
 * <p>In order to remove all QuickTime classes from the VARS build. I abstracted
 * all the QT4J code out of VARS; the result is a a morass of convoluted code
 * in this class. A thousand apologies to any developer who tries to wade into
 * this mess</p>
 * @author brian
 */

public enum PredefinedDispatcher {
    
    /**
     * References a String indication the direction of the CameraPlatform
     */
    CAMERA_DIRECTION("Camera Direction", null),
    /**
     *
     */
    DATABASE_STATUS("Database Status", null),
    /**
     * Reference to the IGrabber used for grabbing images from the
     * QuickTime source.
     */
    GRABBER(IGrabber.class, new FakeGrabber()),
    /**
     * Reference to a Boolean that indicates the ability to grab a frame
     * True = grabber is working, False means it's not.
     */
    GRABBER_STATUS("Image Grabber Status", Boolean.FALSE),
    /**
     * Reference to a quicktime movie if one is being annotated. null if no
     * quicktime movie is currently being annotated.
     */
    MOVIE("QuickTime Movie", null),
    /**
     * Reference to the observation currently referenced by the annotation ui
     */
    OBSERVATION(Observation.class, null),
    /**
     * The ObservationTable object used by the annotation ui
     */
    OBSERVATIONTABLE(ObservationTable.class, null),
    /**
     * A person object representing the current annotator
     */
    PERSON(Person.class, null),
    /**
     * Reference to the preferences object for the current annotator
     */
    PREFERENCES(Preferences.class, new VARSPreferencesFactory().systemRoot()),
    /**
     * A reference to the QTMovieFrame object, if a QuickTime movie is being
     * annotated.
     */
    QTMOVIEFRAME(QTMovieFrame.class, null),
    /**
     * Contains a Boolean. TRUE = the system has a working quickTime for java
     * installation. FALSE means QuickTIme functions are not available.
     */
    QUICKTIME_STATUS("QuickTime for Java", Boolean.FALSE),
    
    /**
     * Points to a reference for the Timecode represetation used for movie files.
     */
    TIMESOURCE(TimeSource.class, TimeSource.AUTO),
    /**
     * A reference to the VCR object used by the annotation UI.
     */
    VCR(IVCR.class, new VCRAdapter()),
    /**
     * Reference to the currently open VideoArchive
     */
    VIDEOARCHIVE(VideoArchive.class, null);
    
    private static final Logger log = LoggerFactory.getLogger(PredefinedDispatcher.class);
    
    
    // Initialize all the enumerations for the Annotation Aplication
    static {
        new DispatcherIntializer();
    }
    
    private Object defaultValue;
    private Dispatcher dispatcher;
    
    PredefinedDispatcher(Object key, Object defaultValue) {
        dispatcher = Dispatcher.getDispatcher(key);
        this.defaultValue = defaultValue;
    }
    
    /**
     * Method description
     *
     *
     * @return
     */
    public Object getDefaultValue() {
        return defaultValue;
    }
    
    /**
     * @return The Dispatcher used for each enumerated value.
     */
    public Dispatcher getDispatcher() {
        return dispatcher;
    }
    
    /**
     * Method description
     *
     */
    public void reset() {
        if (log.isDebugEnabled()) {
            log.debug("Resetting PredefinedDispatcher." + name());
        }
        getDispatcher().setValueObject(getDefaultValue());
    }
}

/**
 * This class sets up each dispatcher for the Annotation Application
 */
class DispatcherIntializer {
    
    private static final Logger log = LoggerFactory.getLogger(DispatcherIntializer.class);
    
    DispatcherIntializer() {
        log.debug("Initializing PredefinedDispatchers");
        
        for (PredefinedDispatcher d : PredefinedDispatcher.values()) {
            // Add logging to all the dispatchers
            d.getDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
                
                public void propertyChange(PropertyChangeEvent evt) {
                    if (log.isDebugEnabled()) {
                        Object newObj = evt.getNewValue();
                        Object oldObj = evt.getOldValue();
                        String oldName = (oldObj == null) ? "No Object" : oldObj.toString();
                        String newName = (newObj == null) ? "No Object" : newObj.toString();
                        log.debug("Object Changed in PredefinedDispatcher: OLD = " + oldName + ", NEW = " + newName);
                    }
                }
                
            });
        }
        initVideoArchive();
        initMovie();
        initVcr();
        initQTMovieFrame();
        initObservation();
        initPerson();
        initPreferences();
        initQuicktimeStatus();
        initGrabberStatus();
        initGrabber();
        
        
    }
    
    void initMovie() {
        Dispatcher dispatcher = PredefinedDispatcher.MOVIE.getDispatcher();
        
        
        // If a movie is set open a VCR
        dispatcher.addPropertyChangeListener(new MovieChangeListener4VCR(
                PredefinedDispatcher.VCR.getDispatcher(),
                (IVCR) PredefinedDispatcher.VCR.getDefaultValue(), 
                PredefinedDispatcher.TIMESOURCE.getDispatcher()));
        
        
        // If a movie is set open QTMOVIEFRAME.
        dispatcher.addPropertyChangeListener(new MovieChangeListener4QTMOVIEFRAME(
                PredefinedDispatcher.QTMOVIEFRAME.getDispatcher(),
                PredefinedDispatcher.QTMOVIEFRAME.getDefaultValue()));

        
        // Create a grabber to capture images
        dispatcher.addPropertyChangeListener(new MovieChangeListener4GRABBER(
                PredefinedDispatcher.GRABBER.getDispatcher(),
                PredefinedDispatcher.GRABBER.getDefaultValue()));
    }
    
    void initObservation() {
        Dispatcher dispatcher = PredefinedDispatcher.OBSERVATION.getDispatcher();
        
        // Update the concept name
        dispatcher.addPropertyChangeListener(new PropertyChangeListener() {
            
            public void propertyChange(final PropertyChangeEvent evt) {
                final Observation oldObs = (Observation) evt.getOldValue();
                if (oldObs != null) {
                    
                    synchronized (DAOEventQueue.getInstance()) {
                        DAOEventQueue.flush();
                        
                        try {
                            if (log.isDebugEnabled()) {
                                log.debug("Validating " + oldObs);
                            }
                            oldObs.validateConceptName();
                        } catch (final DAOException e) {
                            log.error("Failed to validate " + oldObs, e);
                            AppFrameDispatcher.showErrorDialog(
                                    "Failed to validate '" + oldObs.getConceptName() +
                                    "'. There may be a problem with the database connection.");
                        }
                        
                        //ObservationDAO.getInstance().update(oldObs);
                        DAOEventQueue.update(oldObs);
                    }
                }
            }
            
        });
    }
    
    void initQTMovieFrame() {
        
        // Initialize QTMOVIEFRAME ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        Dispatcher dispatcher = PredefinedDispatcher.QTMOVIEFRAME.getDispatcher();
        
        // Close any old frames
        dispatcher.addPropertyChangeListener(new PropertyChangeListener() {
            
            public void propertyChange(PropertyChangeEvent evt) {
                QTMovieFrame f = (QTMovieFrame) evt.getOldValue();
                if (f != null) {
                    f.setVisible(false);
                    f.dispose();
                }
            }
        });
    }
    
    void initVcr() {
        
        // Initialize VCR ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        Dispatcher dispatcher = PredefinedDispatcher.VCR.getDispatcher();
        
        // Close the old VCR
        dispatcher.addPropertyChangeListener(new PropertyChangeListener() {
            
            public void propertyChange(PropertyChangeEvent evt) {
                IVCR oldVcr = (IVCR) evt.getOldValue();
                if (oldVcr != null) {
                    oldVcr.disconnect();
                }
            }
            
        });
    }
    
    void initVideoArchive() {
        
        // Initialize VIDEOARCHIVE ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        Dispatcher dispatcher = PredefinedDispatcher.VIDEOARCHIVE.getDispatcher();
        
        dispatcher.addPropertyChangeListener(new OldVideoArchiveListener());
        
        /*
         * If the VideoArchive name is a URL attempt to open the movie it points to.
         * If it's not a URL attempt to create a IGrabber
         */
        dispatcher.addPropertyChangeListener(new NewVideoArchiveListener());

    }
    
    void initPerson() {
        Dispatcher dispatcher = PredefinedDispatcher.PERSON.getDispatcher();
        
        // If the person is changed update the preferences.
        dispatcher.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                VARSPreferencesFactory f = new VARSPreferencesFactory();
                Preferences preferences = f.userRoot((String) evt.getNewValue());
                
                if (preferences == null) {
                    preferences = f.systemRoot();
                }
                
                PredefinedDispatcher.PREFERENCES.getDispatcher().setValueObject(preferences);
            }
        });
    }
    
    void initPreferences() {
        //Dispatcher dispatcher = Predef
    }
    
    void initQuicktimeStatus() {
        Dispatcher dispatcher = PredefinedDispatcher.QUICKTIME_STATUS.getDispatcher();
        Boolean ok = Boolean.FALSE;
        try {
            // Check to see if quicktime is installed first
            Class.forName("quicktime.QTSession");
            ok = Boolean.TRUE;
        } catch (final Throwable e) {
            log.warn("QuickTime for Java is not installed");
        }
        dispatcher.setValueObject(ok);
    }
    
    void initGrabberStatus() {
        final Dispatcher statusDispacher = PredefinedDispatcher.GRABBER_STATUS.getDispatcher();
        final Dispatcher dispatcher = PredefinedDispatcher.GRABBER.getDispatcher();
        dispatcher.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                IGrabber grabber = (IGrabber) evt.getNewValue();
                statusDispacher.setValueObject(new Boolean((grabber != null) && !(grabber instanceof FakeGrabber)));
            }
        });
    }
    
    void initGrabber() {
        final Dispatcher dispatcher = PredefinedDispatcher.GRABBER.getDispatcher();
        dispatcher.addPropertyChangeListener(new OldGrabberListener());
    }
    
    /**
     *     Close the old videoarchive properly
     *     @author  brian
     */
    private class OldVideoArchiveListener implements PropertyChangeListener {
        
        private final CloseVideoArchiveAction action = new CloseVideoArchiveAction();
        
        /**
         * Method description
         *
         *
         * @param evt
         */
        public void propertyChange(final PropertyChangeEvent evt) {
            final VideoArchive oldVa = (VideoArchive) evt.getOldValue();
            if (oldVa != null) {
                action.setVideoArchive(oldVa);
                action.doAction();
            }
        }
    }
    
    private class NewVideoArchiveListener implements PropertyChangeListener {
        
        private final VideoArchiveChangeAction4VIDEOARCHIVE action = 
                new VideoArchiveChangeAction4VIDEOARCHIVE(PredefinedDispatcher.MOVIE.getDispatcher(),
                PredefinedDispatcher.MOVIE.getDefaultValue(), 
                PredefinedDispatcher.GRABBER.getDispatcher(), 
                PredefinedDispatcher.GRABBER.getDefaultValue());
        
        public void propertyChange(PropertyChangeEvent evt) {
            VideoArchive videoArchive = (VideoArchive) evt.getNewValue();
            
            if (videoArchive != null) {
                URL url = null;
                try {
                    url = new URL(videoArchive.getVideoArchiveName());
                } catch (MalformedURLException ex) {
                    if (log.isDebugEnabled()) {
                        log.debug(videoArchive + " does not refer to a movie file");
                    }
                }
                
                try {
                    action.doAction(url);
                } 
                catch (QT4JException e) {
                    AppFrameDispatcher.showErrorDialog(e.getMessage());
                }
                
            } 
            else {
                PredefinedDispatcher.MOVIE.reset();
                PredefinedDispatcher.GRABBER.reset();
            }
        }
        
        
    }
    
    /**
     * Close the old grabber properly
     */
    private class OldGrabberListener implements PropertyChangeListener {
        
        public void propertyChange(PropertyChangeEvent evt) {
            final IGrabber grabber = (IGrabber) evt.getOldValue();
            if (grabber != null) {
                grabber.dispose();
            }
        }
    }
}
