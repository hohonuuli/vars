package vars.jpa;

import com.google.inject.Module;
import com.google.inject.Binder;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.AnnotationFactory;
import vars.annotation.jpa.AnnotationDAOFactoryImpl;
import vars.annotation.jpa.AnnotationFactoryImpl;
import vars.annotation.jpa.AnnotationEAO;
import vars.knowledgebase.jpa.KnowledgebaseDAOFactoryImpl;
import vars.knowledgebase.jpa.KnowledgebaseFactoryImpl;
import vars.knowledgebase.jpa.KnowledgebaseEAO;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.MiscDAOFactory;
import vars.MiscFactory;
import org.mbari.jpax.EAO;


/**
 * This Guice Module sets up the Factories needed
 */
public class VarsJpaTestModule implements Module {

    public void configure(Binder binder) {

        // Bind the names of the persistence units
	    binder.bindConstant().annotatedWith(Names.named("annotationPersistenceUnit")).to("test");
        binder.bindConstant().annotatedWith(Names.named("knowledgebasePersistenceUnit")).to("test");
        binder.bindConstant().annotatedWith(Names.named("miscPersistenceUnit")).to("test");

        // Bind annotation DAO
        binder.bind(MiscDAOFactory.class).to(MiscDAOFactoryImpl.class);
        binder.bind(MiscFactory.class).to(MiscFactoryImpl.class);
        binder.bind(AnnotationDAOFactory.class).to(AnnotationDAOFactoryImpl.class);
        binder.bind(AnnotationFactory.class).to(AnnotationFactoryImpl.class);
        binder.bind(KnowledgebaseDAOFactory.class).to(KnowledgebaseDAOFactoryImpl.class);
        binder.bind(KnowledgebaseFactory.class).to(KnowledgebaseFactoryImpl.class);
        binder.bind(EAO.class).annotatedWith(Names.named("annotationEAO")).to(AnnotationEAO.class).in(Scopes.SINGLETON);
        binder.bind(EAO.class).annotatedWith(Names.named("knowledgebaseEAO")).to(KnowledgebaseEAO.class).in(Scopes.SINGLETON);
        binder.bind(EAO.class).annotatedWith(Names.named("miscEAO")).to(MiscEAO.class).in(Scopes.SINGLETON);

    }


}
