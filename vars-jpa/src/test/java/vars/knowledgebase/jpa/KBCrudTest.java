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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
import vars.knowledgebase.IConceptName;
import vars.knowledgebase.IHistory;
import vars.knowledgebase.ILinkRealization;
import vars.knowledgebase.ILinkTemplate;
import vars.knowledgebase.IMedia;
import vars.knowledgebase.IUsage;
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

    @Test
    public void bigTest() {

        IConcept c = testObjectFactory.makeObjectGraph("BIG-TEST", 2);
        IConceptDAO dao = daoFactory.newConceptDAO();
        EntityUtilities eu = new EntityUtilities(new NonManagedEAOImpl("test"));

        log.info("KNOWLEDGEBASE TREE BEFORE TEST:\n" + eu.buildTextTree(c));
        c = dao.makePersistent(c);

        Long cId = ((JPAEntity) c).getId();

        Assert.assertNotNull("Primary Key [ID] was not set!", cId);
        c = dao.findByPrimaryKey(c.getClass(), ((JPAEntity) c).getId());
        Assert.assertTrue("Not all objects were inserted",
                          PrimaryKeyUtilities.checkDbForAllPks(PrimaryKeyUtilities.primaryKeyMap(c), (DAO) dao));
        log.info("KNOWLEDGEBASE TREE AFTER INSERT:\n" + eu.buildTextTree(c));
        c = dao.makeTransient(c);
        log.info("KNOWLEDGEBASE TREE AFTER DELETE:\n" + eu.buildTextTree(c));
        c = dao.findByPrimaryKey(c.getClass(), cId);
        Assert.assertNull("Whoops!! We can still lookup the entity after deleteing it", c);

    }

    @Before
    public void setup() {
        Injector injector = Guice.createInjector(new VarsJpaTestModule());

        kbFactory = injector.getInstance(KnowledgebaseFactory.class);
        testObjectFactory = new KnowledgebaseTestObjectFactory(kbFactory);
        daoFactory = injector.getInstance(KnowledgebaseDAOFactory.class);
        eao = new NonManagedEAOImpl("test");
    }

    @Test
    public void bottomUpDelete() {
        IConcept concept = testObjectFactory.makeObjectGraph("BIG-TEST", 2);
        IConceptDAO dao = daoFactory.newConceptDAO();
        concept = dao.makePersistent(concept);
        EntityUtilities eu = new EntityUtilities(new NonManagedEAOImpl("test"));

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

        log.info("KNOWLEDGEBASE TREE AFTER INITIAL INSERT:\n" + eu.buildTextTree(concept));

        for (ILinkRealization linkRealization : linkRealizations) {
            linkRealization.getConceptMetadata().removeLinkRealization(linkRealization);
            linkRealization = dao.makeTransient(linkRealization);
        }
        log.info("KNOWLEDGEBASE TREE AFTER LINKREALIZATION DELETE:\n" + eu.buildTextTree(concept));

        for (ILinkTemplate linkTemplate : linkTemplates) {
            linkTemplate.getConceptMetadata().removeLinkTemplate(linkTemplate);
            dao.makeTransient(linkTemplate);
        }
        log.info("KNOWLEDGEBASE TREE AFTER LINKTEMPLATE DELETE:\n" + eu.buildTextTree(concept));


        for (IMedia media : medias) {
            media.getConceptMetadata().removeMedia(media);
            dao.makeTransient(media);
        }
        log.info("KNOWLEDGEBASE TREE AFTER MEDIA DELETE:\n" + eu.buildTextTree(concept));

        for (IHistory history : histories) {
            history.getConceptMetadata().removeHistory(history);
            dao.makeTransient(history);
        }
        log.info("KNOWLEDGEBASE TREE AFTER HISTORY DELETE:\n" + eu.buildTextTree(concept));

        dao.makeTransient(concept);

    }


}
