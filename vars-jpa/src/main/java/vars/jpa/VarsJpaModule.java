/*
 * @(#)VarsJpaModule.java   2009.09.21 at 09:03:15 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.jpa;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import org.mbari.jpaxx.EAO;
import vars.ExternalDataDAO;
import vars.ExternalDataDaoExpdImpl;
import vars.MiscDAOFactory;
import vars.MiscFactory;
import vars.PersistenceCacheProvider;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.AnnotationFactory;
import vars.annotation.jpa.AnnotationDAOFactoryImpl;
import vars.annotation.jpa.AnnotationEAO;
import vars.annotation.jpa.AnnotationFactoryImpl;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.jpa.KnowledgebaseDAOFactoryImpl;
import vars.knowledgebase.jpa.KnowledgebaseEAO;
import vars.knowledgebase.jpa.KnowledgebaseFactoryImpl;
import vars.query.QueryDAO;
import vars.query.QueryDAOImpl;

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

    /**
     * Constructs ...
     *
     * @param annotationPersistenceUnit
     * @param knowledgebasePersistenceUnit
     * @param miscPersistenceUnit
     */
    public VarsJpaModule(String annotationPersistenceUnit, String knowledgebasePersistenceUnit,
                         String miscPersistenceUnit) {
        this.annotationPersistenceUnit = annotationPersistenceUnit;
        this.knowledgebasePersistenceUnit = knowledgebasePersistenceUnit;
        this.miscPersistenceUnit = miscPersistenceUnit;
    }

    public void configure(Binder binder) {

        /*
         * Binding an iso8601 data format
         */
        final DateFormat dateFormatISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") {{
            setTimeZone(TimeZone.getTimeZone("UTC"));
        }};
        binder.bind(DateFormat.class).toInstance(dateFormatISO);


        // Bind the names of the persistence units
        binder.bindConstant().annotatedWith(Names.named("annotationPersistenceUnit")).to(annotationPersistenceUnit);
        binder.bindConstant().annotatedWith(Names.named("knowledgebasePersistenceUnit")).to(
            knowledgebasePersistenceUnit);
        binder.bindConstant().annotatedWith(Names.named("miscPersistenceUnit")).to(miscPersistenceUnit);

        // Bind annotation object and DAO factories
        binder.bind(AnnotationDAOFactory.class).to(AnnotationDAOFactoryImpl.class);
        binder.bind(AnnotationFactory.class).to(AnnotationFactoryImpl.class);
        binder.bind(EAO.class).annotatedWith(Names.named("annotationEAO")).to(AnnotationEAO.class).in(Scopes.SINGLETON);
        binder.bind(EAO.class).annotatedWith(Names.named("knowledgebaseEAO")).to(KnowledgebaseEAO.class).in(Scopes.SINGLETON);
        binder.bind(EAO.class).annotatedWith(Names.named("miscEAO")).to(MiscEAO.class).in(Scopes.SINGLETON);
        binder.bind(ExternalDataDAO.class).to(ExternalDataDaoExpdImpl.class);
        binder.bind(KnowledgebaseDAOFactory.class).to(KnowledgebaseDAOFactoryImpl.class);
        binder.bind(KnowledgebaseFactory.class).to(KnowledgebaseFactoryImpl.class);
        binder.bind(MiscDAOFactory.class).to(MiscDAOFactoryImpl.class);
        binder.bind(MiscFactory.class).to(MiscFactoryImpl.class);
        binder.bind(QueryDAO.class).to(QueryDAOImpl.class);
        binder.bind(VarsUserPreferencesFactory.class).to(VarsUserPreferencesFactoryImpl.class).in(Scopes.SINGLETON);
        binder.bind(PersistenceCacheProvider.class).to(JPACacheProvider.class).in(Scopes.SINGLETON);

    }
}
