package vars.knowledgebase;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 11, 2009
 * Time: 9:28:24 AM
 * To change this template use File | Settings | File Templates.
 */
public interface KnowledgebaseDAOFactory {

    IConceptDAO newConceptDAO();
    IConceptMetadataDAO newConceptMetadataDAO();
    IConceptNameDAO newConceptNameDAO();
    IHistoryDAO newHistoryDAO();
    ILinkRealizationDAO newLinkRealizationDAO();
    ILinkTemplateDAO newLinkTemplateDAO();
    IMediaDAO newMediaDAO();
    IUsageDAO newUsageDAO();

}
