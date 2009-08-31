package vars.annotation;


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

    IAssociation newAssociation(String linkName, String toConcept, String linkValue);

    IObservation newObservation();

    IVideoFrame newVideoFrame();

    IVideoArchive newVideoArchive();

    IVideoArchiveSet newVideoArchiveSet();

    ICameraDeployment newCameraDeployment();
    
}
