package vars.annotation.jpa;

import vars.annotation.PhysicalData;
import vars.jpa.DAO;
import vars.annotation.PhysicalDataDAO;
import com.google.inject.Inject;
import javax.persistence.EntityManager;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:40:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class PhysicalDataDAOImpl extends DAO implements PhysicalDataDAO {

    @Inject
    public PhysicalDataDAOImpl(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    public PhysicalData findByPrimaryKey(Object primaryKey) {
        return findByPrimaryKey(PhysicalDataImpl.class, primaryKey);
    }

}
