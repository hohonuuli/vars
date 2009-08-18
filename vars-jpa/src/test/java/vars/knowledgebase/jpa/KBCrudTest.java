/*
 * @(#)KBCrudTest.java   2009.08.17 at 11:38:50 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.knowledgebase.jpa;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.mbari.jpax.NonManagedEAO;
import org.mbari.jpax.NonManagedEAOImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.jpa.DAO;
import vars.jpa.EntityUtilities;
import vars.jpa.JPAEntity;
import vars.jpa.PrimaryKeyUtilities;
import vars.jpa.VarsJpaTestModule;
import vars.knowledgebase.IConcept;
import vars.knowledgebase.IConceptDAO;
import vars.knowledgebase.IConceptMetadata;
import vars.knowledgebase.IHistory;
import vars.knowledgebase.ILinkRealization;
import vars.knowledgebase.ILinkTemplate;
import vars.knowledgebase.IMedia;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.testing.KnowledgebaseTestObjectFactory;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 12, 2009
 * Time: 11:42:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class KBCrudTest {

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
        eao = new NonManagedEAOImpl("test");
        entityUtilities = new EntityUtilities(eao);
    }

    @After
    public void report() {
        Collection<IConcept> badData = daoFactory.newConceptDAO().findAll();

        if (badData.size() > 0) {

            String s = "Concepts that shouldn't still be in the database:\n";
            for (IConcept c : badData) {
                s += "\n" + entityUtilities.buildTextTree(c) + "\n";
            }
            log.info(s);
        }
    }

    @Test
    public void bigTest() {
        log.info("---------- TEST: bigTest ----------");

        IConcept c = testObjectFactory.makeObjectGraph("BIG-TEST", 2);
        IConceptDAO dao = daoFactory.newConceptDAO();

        log.info("KNOWLEDGEBASE TREE BEFORE TEST:\n" + entityUtilities.buildTextTree(c));
        c = dao.makePersistent(c);

        Long cId = ((JPAEntity) c).getId();

        Assert.assertNotNull("Primary Key [ID] was not set!", cId);
        c = dao.findByPrimaryKey(c.getClass(), ((JPAEntity) c).getId());
        Assert.assertTrue("Not all objects were inserted",
                          PrimaryKeyUtilities.checkDbForAllPks(PrimaryKeyUtilities.primaryKeyMap(c), (DAO) dao));
        log.info("KNOWLEDGEBASE TREE AFTER INSERT:\n" + entityUtilities.buildTextTree(c));
        c = dao.makeTransient(c);
        log.info("KNOWLEDGEBASE TREE AFTER DELETE:\n" + entityUtilities.buildTextTree(c));
        c = dao.findByPrimaryKey(c.getClass(), cId);
        Assert.assertNull("Whoops!! We can still lookup the entity after deleteing it", c);

    }

    

    @Test
    public void incrementalBuildAndDeleteByConcept() {

        log.info("---------- TEST: incrementalBuildAndDeleteByConcept ----------");
        IConceptDAO dao = daoFactory.newConceptDAO();

        setup: {
            IConcept root = testObjectFactory.makeConcept("ROOT");
            root.getPrimaryConceptName().setName("ROOT");
            root = dao.makePersistent(root);
            log.info("BUILDING KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(root));

            // ---- Step 1: Build up each node in the database
            log.info("---------- Add 2A ----------");
            IConcept concept2A = testObjectFactory.makeConcept("LEVEL 2 A");
            concept2A.getPrimaryConceptName().setName("2A");
            root.addChildConcept(concept2A);
            concept2A = dao.makePersistent(concept2A);
            log.info("BUILDING KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(root));

            log.info("---------- Add 3AA and 3AB ----------");
            IConcept concept3AA = testObjectFactory.makeConcept("LEVEL 3 A A");
            concept3AA.getPrimaryConceptName().setName("3AA");
            concept2A.addChildConcept(concept3AA);
            IConcept concept3AB = testObjectFactory.makeConcept("LEVEL 3 A B");
            concept3AB.getPrimaryConceptName().setName("3AB");
            concept2A.addChildConcept(concept3AB);
            concept2A = dao.update(concept2A);
            log.info("BUILDING KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(root));

            log.info("---------- Add 4A ----------");
            IConcept concept4A = testObjectFactory.makeConcept("LEVEL 4 A");
            concept4A.getPrimaryConceptName().setName("4A");
            concept3AA = dao.findByName("3AA");
            concept3AA.addChildConcept(concept4A);
            concept4A = dao.makePersistent(concept4A);
            log.info("BUILDING KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(root));

            log.info("---------- Add 2B ----------");
            IConcept concept2B = testObjectFactory.makeConcept("LEVEL 2 B");
            concept2B.getPrimaryConceptName().setName("2B");
            root.addChildConcept(concept2B);
            concept2B = dao.makePersistent(concept2B);
            log.info("BUILDING KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(root));
        }

        // ---- Step 2: Let's look up the references form the database so that we have the correct entities

        execute: {

            IConcept root = dao.findByName("ROOT");
            IConcept concept2B = dao.findByName("2B");
            IConcept concept3AB = dao.findByName("3AB");
            IConcept concept3AA = dao.findByName("3AA");
            IConcept concept2A = dao.findByName("2A");

            // Tear down each node in the database
            log.info("---------- Remove 2B ----------");
            root.removeChildConcept(concept2B);
            dao.makeTransient(concept2B);

            log.info("---------- Remove 3AB ----------");
            concept2A.removeChildConcept(concept3AB);
            dao.makeTransient(concept3AB);

            log.info("---------- Remove 3AA ----------");
            concept2A.removeChildConcept(concept3AA);
            dao.makeTransient(concept3AA);

            root = dao.findByPrimaryKey(root.getClass(), ((JPAEntity) root).getId());
            log.info("KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(root));

            log.info("---------- Remove ROOT ----------");
            dao.makeTransient(root);
        }

    }



    @Test
    @Ignore
    public void bottomUpDelete() {

        log.info("---------- TEST: bottomUpDelete ----------");

        IConcept concept = testObjectFactory.makeObjectGraph("BIG-TEST", 1);
        IConceptDAO dao = daoFactory.newConceptDAO();
        concept = dao.makePersistent(concept);

        final Collection<IHistory> histories = new ArrayList<IHistory>();
        final Collection<IMedia> medias = new ArrayList<IMedia>();
        final Collection<ILinkTemplate> linkTemplates = new ArrayList<ILinkTemplate>();
        final Collection<ILinkRealization> linkRealizations = new ArrayList<ILinkRealization>();

        /*
         * Handy 'Closure' to do all the dirty work of recursively filling in
         * the collections for us
         */
        class Collector {
            
            void collect(IConcept c) {
                IConceptMetadata metadata = c.getConceptMetadata();
                histories.addAll(metadata.getHistories());
                medias.addAll(metadata.getMedias());
                linkTemplates.addAll(metadata.getLinkTemplates());
                linkRealizations.addAll(metadata.getLinkRealizations());
                for (IConcept child : c.getChildConcepts()) {
                    collect(child);
                }
            }
            
        }

        Collector collector = new Collector();
        collector.collect(concept);

        log.info("KNOWLEDGEBASE TREE AFTER INITIAL INSERT:\n" + entityUtilities.buildTextTree(concept));

        for (ILinkRealization linkRealization : linkRealizations) {
            linkRealization.getConceptMetadata().removeLinkRealization(linkRealization);
            dao.makeTransient(linkRealization);
        }
        log.info("KNOWLEDGEBASE TREE AFTER LINKREALIZATION DELETE:\n" + entityUtilities.buildTextTree(concept));

        for (ILinkTemplate linkTemplate : linkTemplates) {
            linkTemplate.getConceptMetadata().removeLinkTemplate(linkTemplate);
            dao.makeTransient(linkTemplate);
        }
        log.info("KNOWLEDGEBASE TREE AFTER LINKTEMPLATE DELETE:\n" + entityUtilities.buildTextTree(concept));


        for (IMedia media : medias) {
            media.getConceptMetadata().removeMedia(media);
            dao.makeTransient(media);
        }
        log.info("KNOWLEDGEBASE TREE AFTER MEDIA DELETE:\n" + entityUtilities.buildTextTree(concept));

        for (IHistory history : histories) {
            history.getConceptMetadata().removeHistory(history);
            dao.makeTransient(history);
        }
        log.info("KNOWLEDGEBASE TREE AFTER HISTORY DELETE:\n" + entityUtilities.buildTextTree(concept));

        dao.makeTransient(concept);

    }


}
