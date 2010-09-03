/*
 * @(#)DAO.java   2009.11.30 at 11:49:51 PST
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



package vars;

import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;

/**
 *
 * @author brian
 */
public interface DAO {

    void commit();

    void close();

    void endTransaction();

    /**
     * True if the to objects represent the same object in the datastore. (e.g.
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
     * @return A list of objects returned by the query.
     */
    List findByNamedQuery(String name, Map<String, Object> namedParameters);

    /**
     * Executes a named query that does not take any parameters
     *
     * @param name The name of the JPL query
     * @return A list of objects returned by the query
     */
    List findByNamedQuery(String name);

    <T> T findByPrimaryKey(Class<T> clazz, Object primaryKey);

    /**
     * Retrieves the object from the datastore. This ignores all state changes
     * in the provided object and returns the copy as found in the data store.
     */
    <T> T find(T object);

    EntityManager getEntityManager();

    /**
     * Checks to see if the given object is persisted in the databas
     * @param entity The object of interest
     * @return true if it's in the database. False if it is not
     */
    boolean isPersistent(Object entity);

    /**
     * Many one-to-many relations are lazy loaded in JPA. For convenience, this
     * method will load all lazy relations of an IEntity object. This method has
     * no effect on objects that are not persistent
     *
     * @param entity
     *            The persistent object who's children will be loaded from the
     *            database.
     */
    void loadLazyRelations(Object entity);

    <T> T merge(T object);

    /**
     *
     * @param object
     */
    void persist(Object object);

    /**
     *
     * @param object
     */
    void remove(Object object);

    void startTransaction();
}
