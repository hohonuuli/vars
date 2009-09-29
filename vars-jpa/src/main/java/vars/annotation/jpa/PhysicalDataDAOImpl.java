package vars.annotation.jpa;

import vars.jpa.DAO;
import vars.annotation.PhysicalDataDAO;
import org.mbari.jpaxx.EAO;
import com.google.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:40:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class PhysicalDataDAOImpl extends DAO implements PhysicalDataDAO {

    @Inject
    public PhysicalDataDAOImpl(EAO eao) {
        super(eao);
    }

}
