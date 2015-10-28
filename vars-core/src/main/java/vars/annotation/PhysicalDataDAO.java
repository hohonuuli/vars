package vars.annotation;

import vars.DAO;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 2:38:28 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PhysicalDataDAO extends DAO {

    PhysicalData findByPrimaryKey(Object primaryKey);
}
