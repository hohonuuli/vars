package vars.annotation.jpa;

import vars.jpa.DAO;
import vars.annotation.IPhysicalData;
import vars.annotation.IPhysicalDataDAO;
import org.mbari.jpax.IEAO;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:40:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class PhysicalDataDAO extends DAO implements IPhysicalDataDAO {

    public PhysicalDataDAO(IEAO eao) {
        super(eao);
    }

}
