package vars.annotation.jpa;

import vars.annotation.*;


/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 6, 2009
 * Time: 2:59:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class AnnotationFactoryImpl implements AnnotationFactory {

    public Association newAssociation() {
        return new GAssociation();
    }

    public Association newAssociation(String linkName, String toConcept, String linkValue) {
        return new GAssociation(linkName, toConcept, linkValue);
    }

    public Observation newObservation() {
        return new GObservation();
    }

    public VideoFrame newVideoFrame() {
        return new GVideoFrame();
    }

    public VideoArchive newVideoArchive() {
        return new GVideoArchive();
    }

    public VideoArchiveSet newVideoArchiveSet() {
        return new GVideoArchiveSet();
    }

    public CameraDeployment newCameraDeployment() {
        return new GCameraDeployment();
    }

}
