package vars.knowledgebase.ui.actions;

import vars.knowledgebase.History;
import vars.DAO;
import vars.UserAccount;


public interface IApproveHistoryTask {
    
    void approve(UserAccount userAccount, History history, DAO dao);

}
