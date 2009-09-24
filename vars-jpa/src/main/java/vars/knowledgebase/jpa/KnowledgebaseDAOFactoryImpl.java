package vars.knowledgebase.jpa;

import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptMetadataDAO;
import vars.knowledgebase.ConceptNameDAO;
import vars.knowledgebase.HistoryDAO;
import vars.knowledgebase.LinkRealizationDAO;
import vars.knowledgebase.LinkTemplateDAO;
import vars.knowledgebase.MediaDAO;
import vars.knowledgebase.UsageDAO;
import org.mbari.jpax.EAO;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 11, 2009
 * Time: 9:37:10 AM
 * To change this template use File | Settings | File Templates.
 */
    public class KnowledgebaseDAOFactoryImpl implements KnowledgebaseDAOFactory {

    private final EAO eao;

    @Inject
    public KnowledgebaseDAOFactoryImpl(@Named("knowledgebaseEAO") EAO eao) {
        this.eao = eao;
    }

    public ConceptDAO newConceptDAO() {
        return new ConceptDAOImpl(eao, newConceptNameDAO());
    }

    public ConceptMetadataDAO newConceptMetadataDAO() {
        return new ConceptMetadataDAOImpl(eao);
    }

    public ConceptNameDAO newConceptNameDAO() {
        return new ConceptNameDAOImpl(eao);
    }

    public HistoryDAO newHistoryDAO() {
        return new HistoryDAOImpl(eao);
    }

    public LinkRealizationDAO newLinkRealizationDAO() {
        return new LinkRealizationDAOImpl(eao, newConceptDAO());
    }

    public LinkTemplateDAO newLinkTemplateDAO() {
        return new LinkTemplateDAOImpl(eao, newConceptDAO());
    }

    public MediaDAO newMediaDAO() {
        return new MediaDAOImpl(eao);
    }

    public UsageDAO newUsageDAO() {
        return new UsageDAOImpl(eao);
    }
}
