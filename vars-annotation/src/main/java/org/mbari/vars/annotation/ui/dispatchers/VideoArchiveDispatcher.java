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


package org.mbari.vars.annotation.ui.dispatchers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.mbari.util.Dispatcher;
import org.mbari.util.IObservable;
import org.mbari.util.IObserver;
import org.mbari.util.ObservableSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.IVideoArchive;

/**
 * <p>
 * A singleton source to retrieve the reference to the currently
 * used <code>VideoArchive</code>
 * </p>
 *
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: VideoArchiveDispatcher.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class VideoArchiveDispatcher implements IObservable {

    /** Field description */
    public static final Dispatcher DISPATCHER = PredefinedDispatcher.VIDEOARCHIVE.getDispatcher();
    private static VideoArchiveDispatcher vad = new VideoArchiveDispatcher();
    private static final Logger log = LoggerFactory.getLogger(VideoArchiveDispatcher.class);

    /**
     * @uml.property  name="oc"
     * @uml.associationEnd  
     */
    private ObservableSupport oc = new ObservableSupport();

    /**
     * Singleton
     *
     */
    VideoArchiveDispatcher() {
        super();
        DISPATCHER.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                notifyObservers();
            }
        });
    }

    /**
     *  Adds a feature to the Observer attribute of the VideoArchiveDispatcher object
     *
     * @param  observer The feature to be added to the Observer attribute
     */
    public void addObserver(final IObserver observer) {
        oc.add(observer);
    }

    /**
     * Add a listener to receive notification of changes to all the bound
     * properties in this class.
     *
     *
     * @param  l             The listener which will receive <tt>PropertyChangeEvent</tt>
     *            s
     */
    public void addPropertyChangeListener(final PropertyChangeListener l) {
        DISPATCHER.addPropertyChangeListener(l);
    }

    /**
     * Add a listener to receive notification of changes to the bound property
     * whose name matches the string provided.
     *
     *
     * @param  propertyName             The name of the property to receive change events on.
     * @param  l             The listener which will receive <tt>PropertyChangeEvent</tt>
     *            s
     */
    public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener l) {
        DISPATCHER.addPropertyChangeListener(l);
    }

    /**
     * @return  Returns the videoArchive.
     */
    public IVideoArchive getVideoArchive() {
        return (IVideoArchive) DISPATCHER.getValueObject();
    }

    void notifyObservers() {
        oc.notify(getVideoArchive(), "new");
    }

    /**
     *  Description of the Method
     */
    public void removeAllObservers() {
        oc.clear();
    }

    /**
     *  Description of the Method
     *
     * @param  observer Description of the Parameter
     */
    public void removeObserver(final IObserver observer) {
        oc.remove(observer);
    }

    /**
     * Remove a listener from property change notification.
     *
     *
     * @param  l             The listener to be removed.
     */
    public void removePropertyChangeListener(final PropertyChangeListener l) {
        DISPATCHER.removePropertyChangeListener(l);
    }

    /**
     * @param  va The new videoArchive value
     */
    public void setVideoArchive(final IVideoArchive va) {
        DISPATCHER.setValueObject(va);
    }

    /**
     * @return  An IObservable object
     */
    public static VideoArchiveDispatcher getInstance() {
        return vad;
    }
}
