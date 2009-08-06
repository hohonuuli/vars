/*
 * @(#)UserTest.java   2009.08.06 at 10:01:22 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars;

import java.io.InputStream;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.EntityManager;

import junit.framework.TestCase;
import org.hibernate.ejb.HibernateEntityManager;
import org.mbari.jpax.EAO;

/**
 * Class description
 *
 *
 * @version        $date$, 2009.08.06 at 10:01:22 PDT
 * @author         Brian Schlining [brian@mbari.org]
 */
public class UserTest extends TestCase {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("user-test");

    public void setUp() throws Exception {
        InputStream testData = User.class.getResourceAsStream("/user.db.xml");

        HibernateEntityManager em = (HibernateEntityManager) emf.createEntityManager();

        DbUnitDataLoader loader = new DbUnitDataLoader(testData, em.getSession().connection());

        loader.populateTestData();
    }

    public void testFindAll() {
        EntityManager em = emf.createEntityManager();

        User user = em.find(User.class, new Long(1));

        assertNotNull(user);
        assertEquals(1, user.getId());
        assertEquals("John Doe", user.getName());
        em.close();
    }

    public void testFindAll2() {
        EAO eao = new EAO(emf);
        Long key = 1L;
        User user = eao.find(User.class, key);

        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("John Doe", user.getName());

        // Repeat for grins
        user = eao.find(User.class, key);

        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("John Doe", user.getName());

    }
}
