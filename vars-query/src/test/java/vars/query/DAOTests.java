/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.query;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.KnowledgebaseDAOFactory;


/**
 *
 * @author brian
 */
public class DAOTests {

    private final Logger log = LoggerFactory.getLogger(getClass());
    KnowledgebaseDAOFactory knowledgebaseDAOFactory;
    QueryPersistenceService queryDAO;

    @Before
    public void setup() {
        Injector injector = Guice.createInjector(new VarsJpaTestModule());
        knowledgebaseDAOFactory = injector.getInstance(KnowledgebaseDAOFactory.class);
        queryDAO = injector.getInstance(QueryPersistenceService.class);
    }

    @Test
    public void test1() {

        ConceptDAO dao = knowledgebaseDAOFactory.newConceptDAO();
        Concept concept = dao.findRoot();
        log.info("Found root concept: " + concept);

    }

}
