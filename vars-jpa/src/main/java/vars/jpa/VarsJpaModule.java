package vars.jpa;

import com.google.inject.Module;
import com.google.inject.Binder;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import org.mbari.jpax.EAO;
import vars.MiscDAOFactory;
import vars.MiscFactory;
import vars.annotation.AnnotationDAOFactory;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.jpa.KnowledgebaseFactoryImpl;
import vars.annotation.AnnotationFactory;
import vars.annotation.jpa.AnnotationDAOFactoryImpl;
import vars.annotation.jpa.AnnotationEAO;
import vars.annotation.jpa.AnnotationFactoryImpl;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.jpa.KnowledgebaseDAOFactoryImpl;
import vars.knowledgebase.jpa.KnowledgebaseEAO;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 10, 2009
 * Time: 3:35:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class VarsJpaModule implements Module {


    private final String annotationPersistenceUnit;
    private final String knowledgebasePersistenceUnit;
    private final String miscPersistenceUnit;

    public VarsJpaModule(String annotationPersistenceUnit, String knowledgebasePersistenceUnit, String miscPersistenceUnit) {
        this.annotationPersistenceUnit = annotationPersistenceUnit;
        this.knowledgebasePersistenceUnit = knowledgebasePersistenceUnit;
        this.miscPersistenceUnit = miscPersistenceUnit;
    }

    public void configure(Binder binder) {

        // Bind the names of the persistence units
	binder.bindConstant().annotatedWith(Names.named("annotationPersistenceUnit")).to(annotationPersistenceUnit);
        binder.bindConstant().annotatedWith(Names.named("knowledgebasePersistenceUnit")).to(knowledgebasePersistenceUnit);
        binder.bindConstant().annotatedWith(Names.named("miscPersistenceUnit")).to(miscPersistenceUnit);

        // Bind annotation object and DAO factories
        binder.bind(VarsUserPreferencesFactory.class).to(VarsUserPreferencesFactoryImpl.class);
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
