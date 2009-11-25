package vars.knowledgebase.ui.actions;

import vars.knowledgebase.History;
import vars.DAO;
import vars.UserAccount;


public interface IApproveHistoryTask {
    
	/**
	 * Approve method should be called within a DAO transaction
	 * @param userAccount
	 * @param history
	 * @param dao
	 */
    void approve(UserAccount userAccount, History history, DAO dao);

}
