package vars.knowledgebase.jpa;

import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptName;
import vars.jpa.VarsJpaDevelopmentModule;

import java.util.Collection;

import com.google.inject.Injector;
import com.google.inject.Guice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;
import org.junit.Ignore;

/**
 * Integration testing. You need to use this against a development database that actually contains data
 */
public class ConceptDaoTest {

    public final Logger log = LoggerFactory.getLogger(getClass());
    private final ConceptDAO conceptDAO;

    public ConceptDaoTest() {
        Injector injector = Guice.createInjector(new VarsJpaDevelopmentModule());
        KnowledgebaseDAOFactory knowledgebaseDAOFactory = injector.getInstance(KnowledgebaseDAOFactory.class);
        conceptDAO = knowledgebaseDAOFactory.newConceptDAO();
    }

    @Test
    @Ignore
    public void testFindByName() {
        String name = "Nanomia";
        Concept concept = conceptDAO.findByName(name);
        log.info("conceptDAO.findByName('" + name + "') returns: " + concept);
    }

    @Test
    @Ignore
    public void testFindRoot() {
        Concept concept = conceptDAO.findRoot();
        log.info("conceptDAO.findRoot() returns: " + concept);
    }

    @Test
    @Ignore
    public void testAll() {
        Collection<Concept> concept = conceptDAO.findAll();
        log.info("conceptDAO.findAll() returns " + concept.size() + " concepts");
    }

    @Test
    @Ignore
    public void testFindDescendentNames() {
        Collection<ConceptName> names = conceptDAO.findDescendentNames(conceptDAO.findByName("Nanomia"));
        log.info("conceptDAO.findDescendentNames('Nanomia') returned: " + names);
    }

}
