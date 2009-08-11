package vars.jpa;

import com.google.inject.Module;
import com.google.inject.Binder;
import com.google.inject.PrivateModule;
import com.google.inject.Injector;
import com.google.inject.Guice;
import com.google.inject.name.Names;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.AnnotationFactory;
import vars.annotation.jpa.AnnotationDAOFactoryImpl;
import vars.annotation.jpa.AnnotationFactoryImpl;
import vars.annotation.jpa.AnnotationEAO;
import vars.annotation.jpa.VARSAnnotation;
import vars.knowledgebase.jpa.KnowledgebaseDAOFactoryImpl;
import vars.knowledgebase.jpa.KnowledgebaseFactoryImpl;
import vars.knowledgebase.jpa.KnowledgebaseEAO;
import vars.knowledgebase.jpa.VARSKnowledgebase;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.KnowledgebaseFactory;
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
        binder.bind(AnnotationDAOFactory.class).to(AnnotationDAOFactoryImpl.class);
        binder.bind(AnnotationFactory.class).to(AnnotationFactoryImpl.class);
        binder.bind(KnowledgebaseDAOFactory.class).to(KnowledgebaseDAOFactoryImpl.class);
        binder.bind(KnowledgebaseFactory.class).to(KnowledgebaseFactoryImpl.class);
        binder.bind(EAO.class).annotatedWith(Names.named("annotationEAO")).to(AnnotationEAO.class);
        binder.bind(EAO.class).annotatedWith(Names.named("knowledgebaseEAO")).to(KnowledgebaseEAO.class);

    }


}
