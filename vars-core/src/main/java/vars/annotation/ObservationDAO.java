/*
 * @(#)ObservationDAO.java   2010.01.26 at 02:11:25 PST
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



package vars.annotation;

import java.util.List;
import vars.DAO;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 2:33:04 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ObservationDAO extends DAO, ConceptNameValidator<Observation> {

    /**
     * Finds all Observations in the database that use this Concept OR a child of this
     * Concept (if cascade = true)
     *
     * @param concept
     * @param cascade false = use only the concepts names to locate observations. true = Use the concepts names
     *      AND all its descendants names too.
     * @return A Set<IObservation> containing all matching observations. If none are found an empty collection
     *      is returned
     */
    List<Observation> findAllByConcept(Concept concept, boolean cascade, ConceptDAO conceptDAO);

    List<Observation> findAllByConceptName(String conceptName);

    /**
    * Retrieves all conceptnames actually used in annotations. This query
    * searches the Observation.conceptName and Association.toConcept fields
    * fields
    *
    * @return Set of Strings
    */
    List<String> findAllConceptNamesUsedInAnnotations();

    /**
     * Updates the fields of an observation in the database. This is used by the
     * annotation UI since we don't know when the observation was last modified
     * in the database so it's a workaround for concurrent modifications. NOTE:
     * it does not modify the parent {@link VideoFrame} or the child
     * {@link Association}s; only the fields of the observation.
     *
     * @param observation
     * @return
     */
    Observation updateFields(Observation observation);

    Observation findByPrimaryKey(Object primaryKey);
}
