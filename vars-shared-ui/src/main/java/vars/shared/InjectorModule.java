/*
 * @(#)InjectorModule.java   2010.05.03 at 01:40:46 PDT
 *
 * Copyright 2009 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.shared;

import com.google.inject.Binder;
import com.google.inject.Module;
import java.util.Locale;
import java.util.ResourceBundle;
import vars.VARSException;
import vars.jpa.VarsJpaModule;

/**
 *
 * @author brian
 */
public class InjectorModule implements Module {

    private final String annotationPersistenceUnit;
    private final String knowledgebasePersistenceUnit;
    private final String miscPersistenceUnit;


    public InjectorModule(String annotationPersistenceUnit,
            String knowledgebasePersistenceUnit,
            String miscPersistenceUnit) {
        this.annotationPersistenceUnit = annotationPersistenceUnit;
        this.knowledgebasePersistenceUnit = knowledgebasePersistenceUnit;
        this.miscPersistenceUnit = miscPersistenceUnit;
    }

    /**
     *
     * @param binder
     */
    public void configure(Binder binder) {
        try {
            binder.install(new VarsJpaModule(annotationPersistenceUnit, knowledgebasePersistenceUnit,
                    miscPersistenceUnit));
        }
        catch (Exception ex) {
            throw new VARSException("Failed to intialize dependency injection", ex);
        }

    }


}
