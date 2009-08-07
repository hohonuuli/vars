package vars.knowledgebase.jpa;

import vars.knowledgebase.IHistoryDAO;
import vars.knowledgebase.IHistory;
import vars.jpa.DAO;
import org.mbari.jpax.IEAO;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:45:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class HistoryDAO extends DAO implements IHistoryDAO {

    public HistoryDAO(IEAO eao) {
        super(eao);
    }

    public Set<IHistory> findPendingHistories() {
        return null;  // TODO implement this method.
    }

    public Set<IHistory> findApprovedHistories() {
        return null;  // TODO implement this method.
    }
}
