package vars.knowledgebase;

import vars.DAO;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 3:07:36 PM
 * To change this template use File | Settings | File Templates.
 */
public interface HistoryDAO extends DAO {
    
    Set<History> findAll();

    Set<History> findPendingHistories();

    Set<History> findApprovedHistories();
}
