/*
 * @(#)DAO.java   2009.11.06 at 07:58:58 PST
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars;

import java.util.List;
import java.util.Map;

/**
 *
 * @author brian
 */

/**
 *
 * @author brian
 */
public interface DAO {

    void commit();

    void endTransaction();

    /**
     * True if the to bojects represnt the same object in the datastore. (e.g.
     * It basically compares the primary key)
     * @return
     */
    boolean equalInDatastore(Object thisObj, Object thatObj);

    /**
     * Executes a named query using a map of named parameters
     *
     * @param name
     *            The name of the query to execute
     * @param namedParameters
     *            A Map<String, Object> of the 'named' parameters to assign in
     *            the query
     * @param endTransaction if true the transaction wll be ended when the method exits. If
     *     false then the transaction will be kept open and can be reused by the current thread.
     * @return A list of objects returned by the query.
     */
    List findByNamedQuery(String name, Map<String, Object> namedParameters);

    <T> T findByPrimaryKey(Class<T> clazz, Object primaryKey);

    /**
     * Many one-to-many relations are lazy loaded in JPA. For convience, this
     * method will load all lazy relations of an IEntity object. This method has
     * no effect on objects that are not persistant
     *
     * @param entity
     *            The persistent object whos children will be loaded from the
     *            database.
     */
    void loadLazyRelations(Object entity);

    <T> T merge(T object);

    /**
     * TODO: Add JavaDoc
     *
     * @param object
     */
    void persist(Object object);

    /**
     * TODO: Add JavaDoc
     *
     * @param object
     */
    void remove(Object object);

    void startTransaction();
}
