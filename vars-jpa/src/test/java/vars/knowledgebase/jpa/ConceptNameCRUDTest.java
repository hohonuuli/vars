package vars.knowledgebase.jpa;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.jpaxx.NonManagedEAO;
import vars.knowledgebase.*;
import vars.testing.KnowledgebaseTestObjectFactory;
import vars.jpa.EntityUtilities;
import vars.jpa.VarsJpaTestModule;
import com.google.inject.Injector;
import com.google.inject.Guice;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Oct 30, 2009
 * Time: 2:02:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConceptNameCRUDTest {

    public final Logger log = LoggerFactory.getLogger(getClass());
    KnowledgebaseDAOFactory daoFactory;
    NonManagedEAO eao;
    KnowledgebaseFactory kbFactory;
    KnowledgebaseTestObjectFactory testObjectFactory;
    EntityUtilities entityUtilities;

    @Before
    public void setup() {
        Injector injector = Guice.createInjector(new VarsJpaTestModule());

        kbFactory = injector.getInstance(KnowledgebaseFactory.class);
        testObjectFactory = new KnowledgebaseTestObjectFactory(kbFactory);
        daoFactory = injector.getInstance(KnowledgebaseDAOFactory.class);
        eao = injector.getInstance(KnowledgebaseEAO.class);
        entityUtilities = new EntityUtilities(eao);
    }

    @After
    public void report() {
        Collection<Concept> badData = daoFactory.newConceptDAO().findAll();

        if (badData.size() > 0) {

            String s = "Concepts that shouldn't still be in the database:\n";
            for (Concept c : badData) {
                s += "\n" + entityUtilities.buildTextTree(c) + "\n";
            }
            log.info(s);
        }
    }

    @Test
     public void conceptNameCRUD() {

         log.info("---------- TEST: conceptNameCRUD ----------");
         ConceptDAO dao = daoFactory.newConceptDAO();
         ConceptNameDAO conceptNameDAO = daoFactory.newConceptNameDAO();
         Concept concept = testObjectFactory.makeObjectGraph("conceptNameCRUD", 2);
         dao.makePersistent(concept);
         log.info("INITIAL KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(concept));

         String name1 = "FOO";
         String name2 = "BAR";

         insert: {

             // Using correct JPA form
             concept = dao.findRoot();
             ConceptName conceptName = new GConceptName();
             conceptName.setName(name1);
             conceptName.setNameType(ConceptNameTypes.SYNONYM.toString());
             concept.addConceptName(conceptName);
             conceptName = conceptNameDAO.makePersistent(conceptName);
             concept = dao.findRoot();
             log.info("INSERTED " + conceptName + " in KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(concept));
             concept.removeConceptName(conceptName);
             conceptNameDAO.makeTransient(conceptName);
             log.info("DELETED " + conceptName + " in KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(concept));

             // Using old reference to conceptName
             concept.addConceptName(conceptName);
             conceptNameDAO.makePersistent(conceptName);
             log.info("INSERTED " + conceptName + " in KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(concept));
             concept.removeConceptName(conceptName);
             conceptName = conceptNameDAO.makeTransient(conceptName);
             log.info("DELETED " + conceptName + " in KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(concept));

             // Insert by inserting a new concept
             Concept childConcept = new ConceptImpl();
             conceptName.setNameType(ConceptNameTypes.PRIMARY.getName());
             childConcept.addConceptName(conceptName);
             concept.addChildConcept(childConcept);
             dao.makePersistent(childConcept);
             log.info("UPDATED " + concept + " in KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(concept));

         }


         update: {
             concept = dao.findByName(name1);
             ConceptName conceptName = concept.getConceptName(name1);
             conceptName.setName(name2);
             conceptNameDAO.update(conceptName);
             Concept root = dao.findRoot();
             log.info("UPDATED KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(root));
             conceptName = new GConceptName();
             conceptName.setName(name1);
             conceptName.setNameType(ConceptNameTypes.SYNONYM.toString());
             concept.addConceptName(conceptName);
             dao.update(concept);
             root = dao.findRoot();
             log.info("UPDATED KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(root));
         }

         cleanup: {
             concept = dao.findRoot();
             dao.makeTransient(concept);
             log.info("DELETED KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(concept));
         }

     }

}
