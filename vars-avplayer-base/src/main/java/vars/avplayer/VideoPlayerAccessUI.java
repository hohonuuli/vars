package vars.avplayer;

import org.mbari.util.Tuple2;
import vars.ToolBelt;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.VideoArchive;


import java.awt.*;
import java.util.Optional;

/**
 * Provides access to 2 key methods that are specific to the implementation of a videoplayer.
 * <ul>
 *     <li>getOpenDialog - A method to grab a dialog that can be used to open a movie</li>
 *     <li>openMoviePlayer - A method to open a VideoArchive and  VideoPlayerController that can be used
 *     to control the display of a movie file.</li>
 * </ul>
 */
public interface VideoPlayerAccessUI {

    /**
     *
     * @param parent The dialog UI may be modal, so it needs a parent window to reference
     * @param toolBelt A VARS god object containing need factories.
     * @return A reference to a object that can be used to display an interactive dialog for opening movie files or
     *      videoarchives that reference a movie file.
     */
    VideoPlayerDialogUI getOpenDialog(Window parent, ToolBelt toolBelt);

    /**
     * Open a movie player based on provided parameters. If no VideoArchive exists that matches the movie location,
     * then a new VideoArchive is created.
     * @param videoParams The parameters needed to generate the movieplayer and videoarchive
     * @return A tuple of the VideoArchive that references the movie location as well as a VideoPlayerController that
     * can be used to control the display of the movie file
     */
    Tuple2<VideoArchive, VideoPlayerController> openMoviePlayer(VideoParams videoParams, AnnotationDAOFactory daoFactory);

    /**
     *
     * @param location A URL (usually) that references a movie file
     * @return The matching VideoArchive, based on the movie URL. If no match is found the returned Optional will be
     *      empty
     */
    Optional<VideoArchive> findByLocation(String location, AnnotationDAOFactory daoFactory);

    /**
     * Creates a new VideoArchive based on the videoParams
     * @param videoParams
     * @return a new VideoArchive based on the videoParams
     */
    VideoArchive createVideoArchive(VideoParams videoParams, AnnotationDAOFactory daoFactory);

    String getName();

}
