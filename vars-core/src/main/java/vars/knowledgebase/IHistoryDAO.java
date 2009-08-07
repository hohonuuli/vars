package vars.knowledgebase;

import vars.IDAO;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 3:07:36 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IHistoryDAO extends IDAO {

    Set<IHistory> findPendingHistories();

    Set<IHistory> findApprovedHistories();
}
