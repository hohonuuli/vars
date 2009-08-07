package vars.annotation.jpa;

import vars.annotation.AnnotationFactory;
import vars.annotation.IAssociation;
import vars.annotation.IObservation;
import vars.annotation.IVideoFrame;
import vars.annotation.IVideoArchive;
import vars.annotation.IVideoArchiveSet;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 6, 2009
 * Time: 2:59:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class AnnotationFactoryImpl implements AnnotationFactory {

    public IAssociation newAssociation() {
        return new Association();
    }

    public IObservation newObservation() {
        return new Observation();
    }

    public IVideoFrame newVideoFrame() {
        return new VideoFrame();
    }

    public IVideoArchive newVideoArchive() {
        return new VideoArchive();
    }

    public IVideoArchiveSet newVideoArchiveSet() {
        return new VideoArchiveSet();
    }
}
