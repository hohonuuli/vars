/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.query;

import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import org.mbari.util.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.query.ui.Lookup;

/**
 *
 * @author brian
 */
public class DAOTests {

    private final Logger log = LoggerFactory.getLogger(getClass());
    KnowledgebaseDAOFactory knowledgebaseDAOFactory;
    QueryDAO queryDAO;

    @Before
    public void setup() {
        Dispatcher dispatcher = Lookup.getGuiceInjectorDispatcher();
        Injector injector = (Injector) dispatcher.getValueObject();
        knowledgebaseDAOFactory = injector.getInstance(KnowledgebaseDAOFactory.class);
        queryDAO = injector.getInstance(QueryDAO.class);
    }

    @Test
    public void test1() {

        ConceptDAO dao = knowledgebaseDAOFactory.newConceptDAO();
        Concept concept = dao.findRoot();
        log.info("Found root concept: " + concept);

    }

}
