package vars.knowledgebase.jpa;

import com.google.inject.Injector;
import com.google.inject.Guice;
import vars.jpa.VarsJpaTestModule;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.IConceptDAO;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.IObservationDAO;
import org.junit.Test;
import org.junit.Assert;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 11, 2009
 * Time: 9:56:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class DaoFactoryTest {

    @Test
    public void test01() {
        Injector injector = Guice.createInjector(new VarsJpaTestModule());
        KnowledgebaseDAOFactory factory = injector.getInstance(KnowledgebaseDAOFactory.class);
        IConceptDAO conceptDAO = factory.newConceptDAO();
        Assert.assertNotNull("Failed to create DAO", conceptDAO);

        AnnotationDAOFactory afactory = injector.getInstance(AnnotationDAOFactory.class);
        IObservationDAO obsDAO = afactory.newObservationDAO();
        Assert.assertNotNull("Failed to create annotation DAO", obsDAO);
    }

}
