package vars.knowledgebase.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;
import org.junit.Assert;
import org.mbari.jpax.NonManagedEAOImpl;
import com.google.inject.Injector;
import com.google.inject.Guice;
import vars.annotation.jpa.VideoArchiveSet;
import vars.testing.KnowledgebaseTestObjectFactory;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.IConcept;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.IConceptDAO;
import vars.jpa.VarsJpaTestModule;
import vars.jpa.EntityUtilities;
import vars.jpa.JPAEntity;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 12, 2009
 * Time: 11:42:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class KBCrudTest {

    public final Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void bigTest() {
        Injector injector = Guice.createInjector(new VarsJpaTestModule());
        KnowledgebaseFactory af = injector.getInstance(KnowledgebaseFactory.class);
        KnowledgebaseTestObjectFactory factory = new KnowledgebaseTestObjectFactory(af);
        IConcept c = factory.makeObjectGraph("BIG-TEST", 2);

        KnowledgebaseDAOFactory adf = injector.getInstance(KnowledgebaseDAOFactory.class);
        IConceptDAO dao = adf.newConceptDAO();

        EntityUtilities eu = new EntityUtilities(new NonManagedEAOImpl("test"));
        log.info("KNOWLEDGEBASE TREE BEFORE TEST:\n" + eu.buildTextTree(c));

        c = dao.makePersistent(c);
        Assert.assertNotNull("Primary Key [ID] was not set!", ((JPAEntity) c).getId());

        c = dao.findByPrimaryKey(c.getClass(), ((JPAEntity) c).getId());

        log.info("KNOWLEDGEBASE TREE AFTER TEST:\n" + eu.buildTextTree(c));

    }
}
