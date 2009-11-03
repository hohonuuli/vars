package vars.knowledgebase;

import vars.DAO;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 11, 2009
 * Time: 9:28:24 AM
 * To change this template use File | Settings | File Templates.
 */
public interface KnowledgebaseDAOFactory {

    DAO newDAO();
    ConceptDAO newConceptDAO();
    ConceptMetadataDAO newConceptMetadataDAO();
    ConceptNameDAO newConceptNameDAO();
    HistoryDAO newHistoryDAO();
    LinkRealizationDAO newLinkRealizationDAO();
    LinkTemplateDAO newLinkTemplateDAO();
    MediaDAO newMediaDAO();
    UsageDAO newUsageDAO();

}
