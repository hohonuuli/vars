package vars.knowledgebase.jpa;

import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.IConceptDAO;
import vars.knowledgebase.IConceptMetadataDAO;
import vars.knowledgebase.IConceptNameDAO;
import vars.knowledgebase.IHistoryDAO;
import vars.knowledgebase.ILinkRealizationDAO;
import vars.knowledgebase.ILinkTemplateDAO;
import vars.knowledgebase.IMediaDAO;
import vars.knowledgebase.IUsageDAO;
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

    public IConceptDAO newConceptDAO() {
        return new ConceptDAO(eao);
    }

    public IConceptMetadataDAO newConceptMetadataDAO() {
        return new ConceptMetadataDAO(eao);
    }

    public IConceptNameDAO newConceptNameDAO() {
        return new ConceptNameDAO(eao);
    }

    public IHistoryDAO newHistoryDAO() {
        return new HistoryDAO(eao);
    }

    public ILinkRealizationDAO newLinkRealizationDAO() {
        return new LinkRealizationDAO(eao, newConceptDAO());
    }

    public ILinkTemplateDAO newLinkTemplateDAO() {
        return new LinkTemplateDAO(eao, newConceptDAO());
    }

    public IMediaDAO newMediaDAO() {
        return new MediaDAO(eao);
    }

    public IUsageDAO newUsageDAO() {
        return new UsageDAO(eao);
    }
}
