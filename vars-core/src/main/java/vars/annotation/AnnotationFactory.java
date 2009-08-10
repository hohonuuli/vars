package vars.annotation;

import vars.annotation.IAssociation;
import vars.annotation.IObservation;
import vars.annotation.IVideoFrame;
import vars.annotation.IVideoArchive;
import vars.annotation.IVideoArchiveSet;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 6, 2009
 * Time: 2:50:48 PM
 * To change this template use File | Settings | File Templates.
 */
public interface AnnotationFactory {

    /* --- Annotation --- */

    IAssociation newAssociation();

    IObservation newObservation();

    IVideoFrame newVideoFrame();

    IVideoArchive newVideoArchive();

    IVideoArchiveSet newVideoArchiveSet();

    ICameraDeployment newCameraDeployment();
    
}
