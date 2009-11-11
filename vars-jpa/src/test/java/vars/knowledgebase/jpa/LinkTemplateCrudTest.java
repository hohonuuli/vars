/*
 * @(#)LinkTemplateCrudTest.java   2009.11.09 at 04:41:28 PST
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
import java.util.Collection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.jpa.EntityUtilities;
import vars.jpa.VarsJpaTestModule;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptMetadata;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.LinkTemplate;
import vars.testing.KnowledgebaseTestObjectFactory;

/**
 *
 * @author brian
 */
public class LinkTemplateCrudTest {

    public final Logger log = LoggerFactory.getLogger(getClass());
    KnowledgebaseDAOFactory daoFactory;
    EntityUtilities entityUtilities;
    KnowledgebaseFactory kbFactory;
    KnowledgebaseTestObjectFactory testObjectFactory;

    @Test
    public void crudTest01() {

        log.info("---------- TEST: crudTest01 ----------");
        ConceptDAO dao = daoFactory.newConceptDAO();
        Concept concept = testObjectFactory.makeObjectGraph("crudTest01", 1);
        dao.startTransaction();
        dao.persist(concept);
        dao.endTransaction();
        log.info("INITIAL KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(concept));

        // Insert
        LinkTemplate linkTemplate = testObjectFactory.makeLinkTemplate();
        dao.startTransaction();
        ConceptMetadata conceptMetadata = dao.merge(concept.getConceptMetadata());
        conceptMetadata.addLinkTemplate(linkTemplate);
        dao.persist(linkTemplate);
        dao.endTransaction();
        log.info("INSERTED " + linkTemplate + " in KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(concept));

        // Update
        dao.startTransaction();
        linkTemplate = dao.merge(linkTemplate);
        linkTemplate.setLinkName("WOOOGA WOOGA WOOOGA");
        dao.endTransaction();
        log.info("UPDATED " + linkTemplate + " in KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(concept));

        // Delete
        dao.startTransaction();
        linkTemplate = dao.merge(linkTemplate);
        linkTemplate.getConceptMetadata().removeLinkTemplate(linkTemplate);
        dao.endTransaction();
        log.info("DELETED " + linkTemplate + " in KNOWLEDGEBASE TREE:\n" + entityUtilities.buildTextTree(concept));

        dao.cascadeRemove(concept);

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

    @Before
    public void setup() {
        Injector injector = Guice.createInjector(new VarsJpaTestModule());
        kbFactory = injector.getInstance(KnowledgebaseFactory.class);
        testObjectFactory = new KnowledgebaseTestObjectFactory(kbFactory);
        daoFactory = injector.getInstance(KnowledgebaseDAOFactory.class);
        entityUtilities = new EntityUtilities();
    }
}
