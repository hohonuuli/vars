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
import vars.knowledgebase.History;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.LinkRealization;
import vars.knowledgebase.LinkTemplate;
import vars.knowledgebase.Media;
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
    KnowledgebaseFactory kbFactory;
    KnowledgebaseTestObjectFactory testObjectFactory;
    EntityUtilities entityUtilities;

    @Before
    public void setup() {
        Injector injector = Guice.createInjector(new VarsJpaTestModule());

        kbFactory = injector.getInstance(KnowledgebaseFactory.class);
        testObjectFactory = new KnowledgebaseTestObjectFactory(kbFactory);
        daoFactory = injector.getInstance(KnowledgebaseDAOFactory.class);
        entityUtilities = new EntityUtilities();
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

        Concept c = testObjectFactory.makeObjectGraph("bigTest", 4);
        ConceptDAO dao = daoFactory.newConceptDAO();

        log.info("KNOWLEDGEBASE TREE BEFORE TEST:\n" + entityUtilities.buildTextTree(c));
        dao.startTransaction();
        dao.persist(c);
        dao.endTransaction();

        Long cId = ((JPAEntity) c).getId();

        Assert.assertNotNull("Primary Key [ID] was not set!", cId);
        dao.startTransaction();
        c = dao.findByPrimaryKey(c.getClass(), ((JPAEntity) c).getId());
        dao.endTransaction();
        Assert.assertTrue("Not all objects were inserted",
                          PrimaryKeyUtilities.checkDbForAllPks(PrimaryKeyUtilities.primaryKeyMap(c), (DAO) dao));
        log.info("KNOWLEDGEBASE TREE AFTER INSERT:\n" + entityUtilities.buildTextTree(c));

        // Exercise the DAO methods
        dao.startTransaction();
        Concept root = dao.findRoot();
        dao.endTransaction();
        Assert.assertNotNull("Whoops, couldn't get root", root);

        dao.cascadeRemove(root);
        dao.startTransaction();
        c = dao.findByPrimaryKey(c.getClass(), cId);
        dao.endTransaction();
        Assert.assertNull("Whoops!! We can still lookup the entity after deleteing it", c);

    }

    

    @Test
    public void incrementalBuildAndDeleteByConcept() {

        log.info("---------- TEST: incrementalBuildAndDeleteByConcept ----------");
        ConceptDAO dao = daoFactory.newConceptDAO();

        setup: {
            Concept root = testObjectFactory.makeConcept("__ROOT__");
            root.getPrimaryConceptName().setName("__ROOT__");
            dao.startTransaction();
            dao.persist(root);
            dao.endTransaction();
            log.info("BUILDING KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(root));

            // ---- Step 1: Build up each node in the database
            log.info("---------- Add 2A ----------");
            Concept concept2A = testObjectFactory.makeConcept("LEVEL 2 A");
            concept2A.getPrimaryConceptName().setName("2A");
            dao.startTransaction();
            root = dao.findRoot();
            root.addChildConcept(concept2A);
            dao.persist(concept2A);
            dao.endTransaction();
            log.info("BUILDING KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(root));

            log.info("---------- Add 3AA and 3AB ----------");
            Concept concept3AA = testObjectFactory.makeConcept("LEVEL 3 A A");
            concept3AA.getPrimaryConceptName().setName("3AA");
            dao.startTransaction();
            concept2A = dao.findByName(concept2A.getPrimaryConceptName().getName());
            concept2A.addChildConcept(concept3AA);
            dao.persist(concept3AA);
            Concept concept3AB = testObjectFactory.makeConcept("LEVEL 3 A B");
            concept3AB.getPrimaryConceptName().setName("3AB");
            concept2A.addChildConcept(concept3AB);
            dao.persist(concept3AB);
            dao.endTransaction();
            log.info("BUILDING KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(root));

            log.info("---------- Add 4A ----------");
            Concept concept4A = testObjectFactory.makeConcept("LEVEL 4 A");
            concept4A.getPrimaryConceptName().setName("4A");
            dao.startTransaction();
            concept3AA = dao.findByName("3AA");
            concept3AA.addChildConcept(concept4A);
            dao.persist(concept4A);
            dao.endTransaction();
            log.info("BUILDING KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(root));

            log.info("---------- Add 2B ----------");
            Concept concept2B = testObjectFactory.makeConcept("LEVEL 2 B");
            dao.startTransaction();
            root = dao.findRoot();
            concept2B.getPrimaryConceptName().setName("2B");
            root.addChildConcept(concept2B);
            dao.persist(concept2B);
            dao.endTransaction();
            log.info("BUILDING KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(root));
        }

        // ---- Step 2: Let's look up the references form the database so that we have the correct entities

        execute: {

            dao.startTransaction();
            Concept root = dao.findByName("__ROOT__");
            Concept concept2B = dao.findByName("2B");
            Concept concept3AB = dao.findByName("3AB");
            Concept concept3AA = dao.findByName("3AA");

            // Tear down each node in the database
            log.info("---------- Remove 2B ----------");
            concept2B.getParentConcept().removeChildConcept(concept2B);
            dao.remove(concept2B);
            //log.info("---------- Remove 3AB ----------");
            concept3AB.getParentConcept().removeChildConcept(concept3AB);
            dao.remove(concept3AB);
            dao.endTransaction();

            log.info("---------- Remove 3AA ----------");
            dao.startTransaction();
            dao.merge(concept3AA);
            concept3AA.getParentConcept().removeChildConcept(concept3AA);
            dao.endTransaction();
            dao.cascadeRemove(concept3AA);
            

            dao.startTransaction();
            root = dao.findByPrimaryKey(root.getClass(), ((JPAEntity) root).getId());
            dao.endTransaction();
            log.info("KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(root));

            log.info("---------- Remove __ROOT__ ----------");
            dao.cascadeRemove(root);
            
        }

    }



    @Test
    public void bottomUpDelete() {

        log.info("---------- TEST: bottomUpDelete ----------");

        Concept concept = testObjectFactory.makeObjectGraph("BIG-TEST", 1);
        ConceptDAO dao = daoFactory.newConceptDAO();
        dao.startTransaction();
        dao.persist(concept);
        dao.endTransaction();

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

        dao.startTransaction();
        for (LinkRealization linkRealization : linkRealizations) {
            linkRealization.getConceptMetadata().removeLinkRealization(linkRealization);
            dao.remove(linkRealization);
        }
        dao.endTransaction();
        log.info("KNOWLEDGEBASE TREE AFTER LINKREALIZATION DELETE:\n" + entityUtilities.buildTextTree(concept));

        dao.startTransaction();
        for (LinkTemplate linkTemplate : linkTemplates) {
            linkTemplate.getConceptMetadata().removeLinkTemplate(linkTemplate);
            dao.remove(linkTemplate);
        }
        dao.endTransaction();
        log.info("KNOWLEDGEBASE TREE AFTER LINKTEMPLATE DELETE:\n" + entityUtilities.buildTextTree(concept));


        dao.startTransaction();
        for (Media media : medias) {
            media.getConceptMetadata().removeMedia(media);
            dao.remove(media);
        }
        dao.endTransaction();
        log.info("KNOWLEDGEBASE TREE AFTER MEDIA DELETE:\n" + entityUtilities.buildTextTree(concept));

        dao.startTransaction();
        for (History history : histories) {
            history.getConceptMetadata().removeHistory(history);
            dao.remove(history);
        }
        dao.endTransaction();
        log.info("KNOWLEDGEBASE TREE AFTER HISTORY DELETE:\n" + entityUtilities.buildTextTree(concept));

        dao.cascadeRemove(concept);

    }



}
