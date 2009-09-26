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

import org.junit.*;
import org.mbari.jpaxx.NonManagedEAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.jpa.DAO;
import vars.jpa.EntityUtilities;
import vars.jpa.JPAEntity;
import vars.jpa.PrimaryKeyUtilities;
import vars.jpa.VarsJpaTestModule;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptMetadata;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.History;
import vars.knowledgebase.LinkRealization;
import vars.knowledgebase.LinkTemplate;
import vars.knowledgebase.Media;
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
    public void bigTest() {
        log.info("---------- TEST: bigTest ----------");

        Concept c = testObjectFactory.makeObjectGraph("BIG-TEST", 4);
        ConceptDAO dao = daoFactory.newConceptDAO();

        log.info("KNOWLEDGEBASE TREE BEFORE TEST:\n" + entityUtilities.buildTextTree(c));
        c = dao.makePersistent(c);

        Long cId = ((JPAEntity) c).getId();

        Assert.assertNotNull("Primary Key [ID] was not set!", cId);
        c = dao.findByPrimaryKey(c.getClass(), ((JPAEntity) c).getId());
        Assert.assertTrue("Not all objects were inserted",
                          PrimaryKeyUtilities.checkDbForAllPks(PrimaryKeyUtilities.primaryKeyMap(c), (DAO) dao));
        log.info("KNOWLEDGEBASE TREE AFTER INSERT:\n" + entityUtilities.buildTextTree(c));

        // Exercise the DAO methods
        Concept root = dao.findRoot();
        Assert.assertNotNull("Whoops, couldn't get root", root);

        //Collection<IConcept> allConcepts = dao.findAll();
        //log.info("All concepts: " + allConcepts);

        //Collection<IConceptName> names = dao.findDescendentNames(c);
        //log.info("Descendent names from root:" + names);

        c = dao.makeTransient(c);
        log.info("KNOWLEDGEBASE TREE AFTER DELETE:\n" + entityUtilities.buildTextTree(c));
        c = dao.findByPrimaryKey(c.getClass(), cId);
        Assert.assertNull("Whoops!! We can still lookup the entity after deleteing it", c);

    }

    

    @Test
    @Ignore
    public void incrementalBuildAndDeleteByConcept() {

        log.info("---------- TEST: incrementalBuildAndDeleteByConcept ----------");
        ConceptDAO dao = daoFactory.newConceptDAO();

        setup: {
            Concept root = testObjectFactory.makeConcept("__ROOT__");
            root.getPrimaryConceptName().setName("__ROOT__");
            root = dao.makePersistent(root);
            log.info("BUILDING KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(root));

            // ---- Step 1: Build up each node in the database
            log.info("---------- Add 2A ----------");
            Concept concept2A = testObjectFactory.makeConcept("LEVEL 2 A");
            concept2A.getPrimaryConceptName().setName("2A");
            root.addChildConcept(concept2A);
            concept2A = dao.makePersistent(concept2A);
            log.info("BUILDING KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(root));

            log.info("---------- Add 3AA and 3AB ----------");
            Concept concept3AA = testObjectFactory.makeConcept("LEVEL 3 A A");
            concept3AA.getPrimaryConceptName().setName("3AA");
            concept2A.addChildConcept(concept3AA);
            Concept concept3AB = testObjectFactory.makeConcept("LEVEL 3 A B");
            concept3AB.getPrimaryConceptName().setName("3AB");
            concept2A.addChildConcept(concept3AB);
            concept2A = dao.update(concept2A);
            log.info("BUILDING KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(root));

            log.info("---------- Add 4A ----------");
            Concept concept4A = testObjectFactory.makeConcept("LEVEL 4 A");
            concept4A.getPrimaryConceptName().setName("4A");
            concept3AA = dao.findByName("3AA");
            concept3AA.addChildConcept(concept4A);
            concept4A = dao.makePersistent(concept4A);
            log.info("BUILDING KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(root));

            log.info("---------- Add 2B ----------");
            Concept concept2B = testObjectFactory.makeConcept("LEVEL 2 B");
            concept2B.getPrimaryConceptName().setName("2B");
            root.addChildConcept(concept2B);
            concept2B = dao.makePersistent(concept2B);
            log.info("BUILDING KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(root));
        }

        // ---- Step 2: Let's look up the references form the database so that we have the correct entities

        execute: {

            Concept root = dao.findByName("__ROOT__");
            Concept concept2B = dao.findByName("2B");
            Concept concept3AB = dao.findByName("3AB");
            Concept concept3AA = dao.findByName("3AA");
            Concept concept2A = dao.findByName("2A");

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

            log.info("---------- Remove __ROOT__ ----------");
            dao.makeTransient(root);
        }

    }



    @Test
    @Ignore
    public void bottomUpDelete() {

        log.info("---------- TEST: bottomUpDelete ----------");

        Concept concept = testObjectFactory.makeObjectGraph("BIG-TEST", 1);
        ConceptDAO dao = daoFactory.newConceptDAO();
        concept = dao.makePersistent(concept);

        final Collection<History> histories = new ArrayList<History>();
        final Collection<Media> medias = new ArrayList<Media>();
        final Collection<LinkTemplate> linkTemplates = new ArrayList<LinkTemplate>();
        final Collection<LinkRealization> linkRealizations = new ArrayList<LinkRealization>();

        /*
         * Handy 'Closure' to do all the dirty work of recursively filling in
         * the collections for us
         */
        class Collector {
            
            void collect(Concept c) {
                ConceptMetadata metadata = c.getConceptMetadata();
                histories.addAll(metadata.getHistories());
                medias.addAll(metadata.getMedias());
                linkTemplates.addAll(metadata.getLinkTemplates());
                linkRealizations.addAll(metadata.getLinkRealizations());
                for (Concept child : c.getChildConcepts()) {
                    collect(child);
                }
            }
            
        }

        Collector collector = new Collector();
        collector.collect(concept);

        log.info("KNOWLEDGEBASE TREE AFTER INITIAL INSERT:\n" + entityUtilities.buildTextTree(concept));

        for (LinkRealization linkRealization : linkRealizations) {
            linkRealization.getConceptMetadata().removeLinkRealization(linkRealization);
            dao.makeTransient(linkRealization);
        }
        log.info("KNOWLEDGEBASE TREE AFTER LINKREALIZATION DELETE:\n" + entityUtilities.buildTextTree(concept));

        for (LinkTemplate linkTemplate : linkTemplates) {
            linkTemplate.getConceptMetadata().removeLinkTemplate(linkTemplate);
            dao.makeTransient(linkTemplate);
        }
        log.info("KNOWLEDGEBASE TREE AFTER LINKTEMPLATE DELETE:\n" + entityUtilities.buildTextTree(concept));


        for (Media media : medias) {
            media.getConceptMetadata().removeMedia(media);
            dao.makeTransient(media);
        }
        log.info("KNOWLEDGEBASE TREE AFTER MEDIA DELETE:\n" + entityUtilities.buildTextTree(concept));

        for (History history : histories) {
            history.getConceptMetadata().removeHistory(history);
            dao.makeTransient(history);
        }
        log.info("KNOWLEDGEBASE TREE AFTER HISTORY DELETE:\n" + entityUtilities.buildTextTree(concept));

        dao.makeTransient(concept);

    }


}
