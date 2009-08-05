package vars;

import java.io.InputStream;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.TestCase;

import org.hibernate.ejb.HibernateEntityManager;

public class UserTest extends TestCase
{
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("vars-jpa-test");

    public void setUp() throws Exception
    {
        InputStream testData = User.class.getResourceAsStream("/user.db.xml");

        HibernateEntityManager em = (HibernateEntityManager) emf.createEntityManager();

        DbUnitDataLoader loader = new DbUnitDataLoader(testData, em.getSession().connection());

        loader.populateTestData();
    }

    public void testFindAll()
    {
        User.setEntityManager(emf.createEntityManager());

        User user = User.find(1);

        assertNotNull(user);
        assertEquals(1, user.getId());
        assertEquals("John Doe", user.getName());
    }
}
