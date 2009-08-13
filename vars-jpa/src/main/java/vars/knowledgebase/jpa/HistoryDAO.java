package vars.knowledgebase.jpa;

import vars.knowledgebase.IHistoryDAO;
import vars.knowledgebase.IHistory;
import vars.jpa.DAO;
import org.mbari.jpax.EAO;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:45:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class HistoryDAO extends DAO implements IHistoryDAO {

    @Inject
    public HistoryDAO(EAO eao) {
        super(eao);
    }

    public Set<IHistory> findPendingHistories() {
        Map<String, Object> params = new HashMap<String, Object>();
        Set<IHistory> histories = new HashSet<IHistory>();
        histories.addAll(getEAO().findByNamedQuery("History.findPendingApproval", params));
        return histories;
    }

    public Set<IHistory> findApprovedHistories() {
        Map<String, Object> params = new HashMap<String, Object>();
        Set<IHistory> histories = new HashSet<IHistory>();
        histories.addAll(getEAO().findByNamedQuery("History.findApproved", params));
        return histories;
    }
}
