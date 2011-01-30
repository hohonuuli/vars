package vars.annotation;


import vars.ILink;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 6, 2009
 * Time: 2:50:48 PM
 * To change this template use File | Settings | File Templates.
 */
public interface AnnotationFactory {

    /* --- Annotation --- */

    Association newAssociation();

    Association newAssociation(String linkName, String toConcept, String linkValue);

    Association newAssociation(ILink link);

    Observation newObservation();

    VideoFrame newVideoFrame();

    VideoArchive newVideoArchive();

    VideoArchiveSet newVideoArchiveSet();

    CameraDeployment newCameraDeployment();
    
}
