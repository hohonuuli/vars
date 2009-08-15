package vars.annotation.jpa;

import vars.annotation.AnnotationFactory;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.IAssociationDAO;
import vars.annotation.ICameraDataDAO;
import vars.annotation.ICameraDeploymentDAO;
import vars.annotation.IObservationDAO;
import vars.annotation.IPhysicalDataDAO;
import vars.annotation.IVideoArchiveDAO;
import vars.annotation.IVideoArchiveSetDAO;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import org.mbari.jpax.EAO;
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

    public IAssociationDAO newAssociationDAO() {
        return new AssociationDAO(eao, kbFactory.newConceptDAO());
    }

    public ICameraDataDAO newCameraDataDAO() {
        return new CameraDataDAO(eao);
    }

    public ICameraDeploymentDAO newCameraDeploymentDAO() {
        return new CameraDeploymentDAO(eao);
    }

    public IObservationDAO newObservationDAO() {
        return new ObservationDAO(eao, kbFactory.newConceptDAO());
    }

    public IPhysicalDataDAO newPhysicalDataDAO() {
        return new PhysicalDataDAO(eao);
    }

    public IVideoArchiveDAO newVideoArchiveDAO() {
        return new VideoArchiveDAO(eao, annotationFactory);
    }

    public IVideoArchiveSetDAO newVideoArchiveSetDAO() {
        return new VideoArchiveSetDAO(eao, newVideoArchiveDAO());
    }
}
