package vars.jpa;

import com.google.inject.Module;
import com.google.inject.Binder;
import com.google.inject.name.Names;
import vars.annotation.IAssociationDAO;
import vars.annotation.ICameraDataDAO;
import vars.annotation.jpa.AssociationDAO;
import vars.annotation.jpa.CameraDataDAO;
import vars.annotation.jpa.AnnotationEAO;
import org.mbari.jpax.EAO;


/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 10, 2009
 * Time: 4:11:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class VarsJpaTestModule implements Module {

    public void configure(Binder binder) {

        // Bind the names of the persistence units
		binder.bindConstant().annotatedWith(Names.named("annotationPersistenceUnit")).to("test");
        binder.bindConstant().annotatedWith(Names.named("knowledgebasePersistenceUnit")).to("test");

        // Bind annotation DAO
        //binder.bind(EAO.class).to() // TODO figure out how to bind 2 different EAOs to different trees
        binder.bind(IAssociationDAO.class).to(AssociationDAO.class);
        binder.bind(ICameraDataDAO.class).to(CameraDataDAO.class);

        
    }
}
