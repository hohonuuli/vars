package vars.knowledgebase.jpa;

import vars.knowledgebase.HistoryDAO;
import vars.knowledgebase.History;
import vars.jpa.DAO;
import org.mbari.jpaxx.EAO;

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
public class HistoryDAOImpl extends DAO implements HistoryDAO {

    @Inject
    public HistoryDAOImpl(EAO eao) {
        super(eao);
    }

    public Set<History> findPendingHistories() {
        Map<String, Object> params = new HashMap<String, Object>();
        Set<History> histories = new HashSet<History>();
        histories.addAll(getEAO().findByNamedQuery("History.findPendingApproval", params));
        return histories;
    }

    public Set<History> findApprovedHistories() {
        Map<String, Object> params = new HashMap<String, Object>();
        Set<History> histories = new HashSet<History>();
        histories.addAll(getEAO().findByNamedQuery("History.findApproved", params));
        return histories;
    }
}
