package vars.jpa;

import com.google.inject.Module;
import com.google.inject.Binder;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.jpa.KnowledgebaseFactoryImpl;
import vars.annotation.AnnotationFactory;
import vars.annotation.jpa.AnnotationFactoryImpl;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 10, 2009
 * Time: 3:35:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class VarsJpaModule implements Module {

    public void configure(Binder binder) {
        binder.bind(KnowledgebaseFactory.class).to(KnowledgebaseFactoryImpl.class);
        binder.bind(AnnotationFactory.class).to(AnnotationFactoryImpl.class);
    }
//        binder.bind(DataIngestorService.class).to(DataIngestorServiceImpl03.class);
//        binder.bind(AnnotationLookupService.class).to(VARSAnnotationLookupService.class);
//        binder.bind(AnnotationGeneratorService.class).to(VARSAnnotationGeneratorService.class);
//        binder.bind(AnnotationPersistenceService.class).to(VARSAnnotationPersistenceService.class);
//        binder.bind(UserLookupService.class).to(VARSUserLookupService.class);
//        binder.bind(VideoControlService.class).to(RS422VideoControlService.class).in(Scopes.SINGLETON);
//        binder.bind(ImageCaptureService.class).to(QTVideoChannelImageCaptureService.class);

}
