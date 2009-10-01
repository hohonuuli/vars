package vars.knowledgebase.ui.actions;

import vars.knowledgebase.History;
import vars.UserAccount;


public interface IApproveHistoryTask {
    
    void approve(UserAccount userAccount, History history);

}
