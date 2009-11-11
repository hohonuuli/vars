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
        return new AssociationImpl();
    }

    public Association newAssociation(String linkName, String toConcept, String linkValue) {
        return new AssociationImpl(linkName, toConcept, linkValue);
    }

    public Observation newObservation() {
        return new ObservationImpl();
    }

    public VideoFrame newVideoFrame() {
        return new VideoFrameImpl();
    }

    public VideoArchive newVideoArchive() {
        return new VideoArchiveImpl();
    }

    public VideoArchiveSet newVideoArchiveSet() {
        return new VideoArchiveSetImpl();
    }

    public CameraDeployment newCameraDeployment() {
        return new CameraDeploymentImpl();
    }

}
