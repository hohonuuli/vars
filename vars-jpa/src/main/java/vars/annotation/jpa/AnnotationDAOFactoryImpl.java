/*
 * @(#)AnnotationDAOFactoryImpl.java   2010.05.20 at 09:29:22 PDT
 *
 * Copyright 2009 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.annotation.jpa;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import vars.DAO;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.AnnotationFactory;
import vars.annotation.AssociationDAO;
import vars.annotation.CameraDataDAO;
import vars.annotation.CameraDeploymentDAO;
import vars.annotation.ObservationDAO;
import vars.annotation.PhysicalDataDAO;
import vars.annotation.VideoArchiveDAO;
import vars.annotation.VideoArchiveSetDAO;
import vars.annotation.VideoFrameDAO;
import vars.jpa.EntityManagerFactoryAspect;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 11, 2009
 * Time: 9:30:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class AnnotationDAOFactoryImpl implements AnnotationDAOFactory, EntityManagerFactoryAspect {

    private final AnnotationFactory annotationFactory;
    private final EntityManagerFactory entityManagerFactory;

    /**
     * Constructs ...
     *
     * @param entityManagerFactory
     * @param annotationFactory
     */
    @Inject
    public AnnotationDAOFactoryImpl(@Named("annotationPersistenceUnit") EntityManagerFactory entityManagerFactory,
                                    AnnotationFactory annotationFactory) {
        this.entityManagerFactory = entityManagerFactory;
        this.annotationFactory = annotationFactory;
    }

    /**
     * @return
     */
    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    /**
     * @return
     */
    public AssociationDAO newAssociationDAO() {
        return new AssociationDAOImpl(entityManagerFactory.createEntityManager());
    }

    /**
     *
     * @param entityManager
     * @return
     */
    public AssociationDAO newAssociationDAO(EntityManager entityManager) {
        return new AssociationDAOImpl(entityManager);
    }

    /**
     * @return
     */
    public CameraDataDAO newCameraDataDAO() {
        return new CameraDataDAOImpl(entityManagerFactory.createEntityManager());
    }

    /**
     *
     * @param entityManager
     * @return
     */
    public CameraDataDAO newCameraDataDAO(EntityManager entityManager) {
        return new CameraDataDAOImpl(entityManager);
    }

    /**
     * @return
     */
    public CameraDeploymentDAO newCameraDeploymentDAO() {
        return new CameraDeploymentDAOImpl(entityManagerFactory.createEntityManager());
    }

    /**
     *
     * @param entityManager
     * @return
     */
    public CameraDeploymentDAO newCameraDeploymentDAO(EntityManager entityManager) {
        return new CameraDeploymentDAOImpl(entityManager);
    }

    /**
     * @return
     */
    public DAO newDAO() {
        return new vars.jpa.DAO(entityManagerFactory.createEntityManager());
    }

    /**
     *
     * @param entityManager
     * @return
     */
    public DAO newDAO(EntityManager entityManager) {
        return new vars.jpa.DAO(entityManager);
    }

    /**
     * @return
     */
    public ObservationDAO newObservationDAO() {
        return new ObservationDAOImpl(entityManagerFactory.createEntityManager());
    }

    /**
     *
     * @param entityManager
     * @return
     */
    public ObservationDAO newObservationDAO(EntityManager entityManager) {
        return new ObservationDAOImpl(entityManager);
    }

    /**
     * @return
     */
    public PhysicalDataDAO newPhysicalDataDAO() {
        return new PhysicalDataDAOImpl(entityManagerFactory.createEntityManager());
    }

    /**
     *
     * @param entityManager
     * @return
     */
    public PhysicalDataDAO newPhysicalDataDAO(EntityManager entityManager) {
        return new PhysicalDataDAOImpl(entityManager);
    }

    /**
     * @return
     */
    public VideoArchiveDAO newVideoArchiveDAO() {
        return new VideoArchiveDAOImpl(entityManagerFactory.createEntityManager(), annotationFactory);
    }

    /**
     *
     * @param entityManager
     * @return
     */
    public VideoArchiveDAO newVideoArchiveDAO(EntityManager entityManager) {
        return new VideoArchiveDAOImpl(entityManager, annotationFactory);
    }

    /**
     * @return
     */
    public VideoArchiveSetDAO newVideoArchiveSetDAO() {
        return new VideoArchiveSetDAOImpl(entityManagerFactory.createEntityManager(), annotationFactory);
    }

    /**
     *
     * @param entityManager
     * @return
     */
    public VideoArchiveSetDAO newVideoArchiveSetDAO(EntityManager entityManager) {
        return new VideoArchiveSetDAOImpl(entityManager, annotationFactory);
    }

    /**
     * @return
     */
    public VideoFrameDAO newVideoFrameDAO() {
        return new VideoFrameDAOImpl(entityManagerFactory.createEntityManager());
    }

    /**
     *
     * @param entityManager
     * @return
     */
    public VideoFrameDAO newVideoFrameDAO(EntityManager entityManager) {
        return new VideoFrameDAOImpl(entityManager);
    }
}
