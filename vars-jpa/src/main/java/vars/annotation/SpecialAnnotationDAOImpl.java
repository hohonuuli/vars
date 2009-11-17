/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import vars.CacheClearedEvent;
import vars.CacheClearedListener;
import vars.PersistenceCache;
import vars.QueryableImpl;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.KnowledgebaseDAOFactory;

/**
 *
 * @author brian
 */
public class SpecialAnnotationDAOImpl extends QueryableImpl implements SpecialAnnotationDAO{

    private static final String jdbcPassword;
    private static final String jdbcUrl;
    private static final String jdbcUsername;
    private static final String jdbcDriver;
    
    /**
     * Never close this transaction!! Closing will clear the L1 cache and slow things down
     */
    private final ConceptDAO conceptDAO;

    static {
        ResourceBundle bundle = ResourceBundle.getBundle("annotation-jdbc");
        jdbcUrl = bundle.getString("jdbc.url");
        jdbcUsername = bundle.getString("jdbc.username");
        jdbcPassword = bundle.getString("jdbc.password");
        jdbcDriver = bundle.getString("jdbc.driver");
    }

    /**
     * Constructs ...
     */
    @Inject
    public SpecialAnnotationDAOImpl(KnowledgebaseDAOFactory knowledgebaseDAOFactory, PersistenceCache persistenceCache) {
        super(jdbcUrl, jdbcUsername, jdbcPassword, jdbcDriver);
        conceptDAO = knowledgebaseDAOFactory.newConceptDAO();
        conceptDAO.startTransaction();
        persistenceCache.addCacheClearedListener(new CacheClearedListener() {
            
            public void beforeClear(CacheClearedEvent evt) {
                conceptDAO.endTransaction(); // Close the transaction 
                
            }
            
            public void afterClear(CacheClearedEvent evt) {
                conceptDAO.startTransaction();
                
            }
        });
    }

    public boolean doesConceptNameExist(String conceptname) {

        String sql = "SELECT count(*) FROM ConceptName WHERE ConceptName = '" +
                conceptname + "'";

        final QueryFunction queryFunction = new QueryFunction() {

            public Object apply(ResultSet resultSet) throws SQLException {
                Integer n = 0;

                while (resultSet.next()) {
                    n = resultSet.getInt(1);
                }

                return new Boolean(n > 0);
            }
        };

        return ((Boolean) executeQueryFunction(sql, queryFunction)).booleanValue();
    }

    /**
     * Yes this duplicates functionality in {@link ConceptDAO}. But this version keeps
     * a transaction open so that the L1 cache never gets cleared. This greatly speeds
     * up lookups!!
     */
    public Concept findConceptByName(String name) {
        Concept concept = conceptDAO.findByName(name);
        
        // Let's load the children and grandchildren into our transaction
        for (Concept child : concept.getChildConcepts()) {
            child.getChildConcepts();
        }
        return concept;
    }

    public ConceptDAO getConceptDAO() {
        return conceptDAO;
    }
    
    public Concept findRootConcept() {
        Concept concept = conceptDAO.findRoot();
        
        // Let's load the children and grandchildren into our transaction
        for (Concept child : concept.getChildConcepts()) {
            child.getChildConcepts();
        }
        return concept;
    }

}
