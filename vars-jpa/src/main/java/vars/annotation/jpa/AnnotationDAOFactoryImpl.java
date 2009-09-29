package vars.annotation.jpa;

import vars.annotation.CameraDataDAO;
import vars.annotation.*;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import org.mbari.jpaxx.EAO;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 11, 2009
 * Time: 9:30:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class AnnotationDAOFactoryImpl implements AnnotationDAOFactory {

    private final EAO eao;
    private final AnnotationFactory annotationFactory;
    private final KnowledgebaseDAOFactory kbFactory;

    @Inject
    public AnnotationDAOFactoryImpl(@Named("annotationEAO") EAO eao,
            AnnotationFactory annotationFactory,
            KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
        this.eao = eao;
        this.annotationFactory = annotationFactory;
        this.kbFactory = knowledgebaseDAOFactory;
    }

    public AssociationDAO newAssociationDAO() {
        return new AssociationDAOImpl(eao, kbFactory.newConceptDAO());
    }

    public CameraDataDAO newCameraDataDAO() {
        return new CameraDataDAOImpl(eao);
    }

    public CameraDeploymentDAO newCameraDeploymentDAO() {
        return new CameraDeploymentDAOImpl(eao);
    }

    public ObservationDAO newObservationDAO() {
        return new ObservationDAOImpl(eao, kbFactory.newConceptDAO());
    }

    public PhysicalDataDAO newPhysicalDataDAO() {
        return new PhysicalDataDAOImpl(eao);
    }

    public VideoArchiveDAO newVideoArchiveDAO() {
        return new VideoArchiveDAOImpl(eao, annotationFactory);
    }

    public VideoArchiveSetDAO newVideoArchiveSetDAO() {
        return new VideoArchiveSetDAOImpl(eao, newVideoArchiveDAO());
    }

    public VideoFrameDAO newVideoFrameDAO() {
        return new VideoFrameDAOImpl(eao);
    }

}
