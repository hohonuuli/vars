/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui;

import org.mbari.util.Dispatcher;
import vars.annotation.VideoArchive;
import vars.shared.ui.GlobalLookup;

/**
 *
 * @author brian
 */
public class Lookup extends GlobalLookup {

    protected static final Object KEY_DISPATCHER_OBSERVATION_TABLE = "ObjservationTable";
    protected static final Object KEY_DISPATCHER_VIDEO_ARCHIVE = VideoArchive.class;

    public static Dispatcher getVideoArchiveDispatcher() {
        return Dispatcher.getDispatcher(KEY_DISPATCHER_VIDEO_ARCHIVE);
    }

    /**
     * Stores a reference to the {@link ConceptTree} so that other componenets
     * can reference it as needed.
     */
    public static Dispatcher getObservationTableDispatcher() {
        return Dispatcher.getDispatcher(KEY_DISPATCHER_OBSERVATION_TABLE);
    }

}
