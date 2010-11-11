/*
 * @(#)QueryPersistenceServiceImpl.java   2010.01.26 at 03:22:56 PST
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



package vars.query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

import org.mbari.sql.QueryFunction;
import org.mbari.sql.QueryResults;
import org.mbari.sql.QueryableImpl;
import vars.ILink;
import vars.LinkBean;
import vars.VARSException;

/**
 * DAO for use by the query app. This drops out of hibernate and uses a lot of
 * SQL internally for speed reasons.
 * @author brian
 */
public class QueryPersistenceServiceImpl implements QueryPersistenceService {

    private final QueryableImpl annoQueryable;
    private final QueryableImpl kbQueryable;
    private final String url;

    /**
     * Constructs ...
     */
    public QueryPersistenceServiceImpl() {
        ResourceBundle bundle = ResourceBundle.getBundle("annotation-jdbc");
        String jdbcUrl = bundle.getString("jdbc.url");
        url = jdbcUrl;
        String jdbcUsername = bundle.getString("jdbc.username");
        String jdbcPassword = bundle.getString("jdbc.password");
        String jdbcDriver = bundle.getString("jdbc.driver");
        annoQueryable = new QueryableImpl(jdbcUrl, jdbcUsername, jdbcPassword, jdbcDriver);

        bundle = ResourceBundle.getBundle("knowledgebase-jdbc");
        jdbcUrl = bundle.getString("jdbc.url");
        jdbcUsername = bundle.getString("jdbc.username");
        jdbcPassword = bundle.getString("jdbc.password");
        jdbcDriver = bundle.getString("jdbc.driver");
        kbQueryable = new QueryableImpl(jdbcUrl, jdbcUsername, jdbcPassword, jdbcDriver);

    }

    /**
     * @return
     */
    public List<String> findAllConceptNamesAsStrings() {
        final QueryFunction queryFunction = new QueryFunction() {

            public Object apply(ResultSet resultSet) throws SQLException {
                List<String> conceptNamesAsStrings = new ArrayList<String>();
                while (resultSet.next()) {
                    conceptNamesAsStrings.add(resultSet.getString(1));
                }

                return conceptNamesAsStrings;
            }
        };


        String query = "SELECT ConceptName FROM ConceptName ORDER BY ConceptName";

        return (List<String>) kbQueryable.executeQueryFunction(query, queryFunction);
    }

    /**
     * Similar to findByConceptNames. However, this looksup all LinkTemplates rather
     * that Associations.
     *
     * @return A Collection of <code>AssociationBean</code>s
     */
    public Collection<ILink> findAllLinkTemplates() {

        /*
         * Assemble a query to search for all annotations used for the respective
         * conceptnames.
         */
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT DISTINCT linkName, toConcept, linkValue ");
        sb.append("FROM LinkTemplate");

        final QueryFunction<Collection<ILink>> queryFunction = new QueryFunction<Collection<ILink>>() {

            public Collection<ILink> apply(ResultSet resultSet) throws SQLException {
                Collection<ILink> associationBeans = new ArrayList<ILink>();
                while (resultSet.next()) {
                    LinkBean bean = new LinkBean();
                    bean.setLinkName(resultSet.getString(1));
                    bean.setToConcept(resultSet.getString(2));
                    bean.setLinkValue(resultSet.getString(3));
                    associationBeans.add(bean);
                }

                return associationBeans;
            }
        };

        return kbQueryable.executeQueryFunction(sb.toString(), queryFunction);
    }

    /**
     * Retrives all conceptnames actually used in annotations. This query
     * searches the Observation.conceptName, Association.toConcept, and
     * ConceptName.name fields
     * fields
     *
     * @return A Collection<String> of all ConceptNames that were actually used
     *      in Annotations as well as all conceptNames defined in the knowledgebase
     */
    public Collection<String> findAllNamesUsedInAnnotations() {

        // All the conceptnames will be stored here
        final Set<String> allNames = new HashSet<String>();

        final QueryFunction queryFunction = new QueryFunction() {

            public Object apply(ResultSet resultSet) throws SQLException {
                while (resultSet.next()) {
                    allNames.add(resultSet.getString(1));
                }

                return allNames;
            }
        };

        // Fetch all conceptnames from Knowledgebase
        String sql = "SELECT DISTINCT ConceptName From ConceptName WHERE ConceptName IS NOT NULL";
        kbQueryable.executeQueryFunction(sql, queryFunction);

        // Fetch all conceptnames from annotations
        String query = "SELECT DISTINCT ConceptName FROM Observation WHERE ConceptName IS NOT NULL" +
                       " UNION SELECT DISTINCT ToConcept FROM Association WHERE ToConcept IS NOT NULL";
        annoQueryable.executeQueryFunction(query, queryFunction);

        // Turn names into a sorted list
        List<String> sortedNames = new ArrayList<String>(allNames);
        Collections.sort(sortedNames);

        return sortedNames;
    }

    /**
     * Looks up all assotations in a database that were used with Observations
     * containing the specified conceptNames
     *
     * @param conceptNames A collection of Strings representing the conceptnames to
     *  lookup
     * @return A collection of <code>AssociationBean</code>s representing the
     *  associations actually used to annotate Observations with the specifed
     *  conceptNames.
     */
    public Collection<ILink> findByConceptNames(Collection<String> conceptNames) {

        final QueryFunction queryFunction = new QueryFunction() {

            public Object apply(ResultSet resultSet) throws SQLException {
                Collection<ILink> associationBeans = new ArrayList<ILink>();
                while (resultSet.next()) {
                    LinkBean bean = new LinkBean();
                    bean.setLinkName(resultSet.getString(1));
                    bean.setToConcept(resultSet.getString(2));
                    bean.setLinkValue(resultSet.getString(3));
                    associationBeans.add(bean);
                }

                return associationBeans;
            }
        };

        /*
         * Assemble a query to search for all annotations used for the respective
         * conceptnames.
         */
        // TODO use prepared statement to escape special characters
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT DISTINCT linkName, toConcept, linkValue ");
        sb.append("FROM Association JOIN Observation ON Observation.id = Association.ObservationID_FK ");
        sb.append("WHERE ");

        for (Iterator i = conceptNames.iterator(); i.hasNext(); ) {
            String conceptName = (String) i.next();
            sb.append("ConceptName = '").append(conceptName).append("'");

            if (i.hasNext()) {
                sb.append(" OR ");
            }
        }

        sb.append(" ORDER BY LinkName, toConcept, linkValue");

        return (Collection<ILink>) annoQueryable.executeQueryFunction(sb.toString(), queryFunction);
    }

    public QueryableImpl getAnnotationQueryable() {
        return annoQueryable;
    }

    /**
     * Returns the count of unique columns found in the table for a given column
     * @param columnName
     * @return
     */
    public Integer getCountOfUniqueValuesByColumn(String columnName) {

        final QueryFunction queryFunction = new QueryFunction() {

            public Object apply(ResultSet resultSet) throws SQLException {
                Integer count = 0;
                while (resultSet.next()) {
                    count++;
                }

                return count;
            }
        };

        String query = "SELECT DISTINCT " + columnName + " FROM Annotations";

        return (Integer) annoQueryable.executeQueryFunction(query, queryFunction);

    }

    /**
     * Retrieves the metadata for the Annotation table. Returns are in
     * alphabetical order.
     *
     * @return A Map where key is the columns name as a String, value is the columns
     *  Object type as a String (e.g. "java.lang.String". (This would be the type returned by
     *  resultSet.getObject())
     */
    public Map<String, String> getMetaData() {

        final QueryFunction queryFunction = new QueryFunction() {

            public Object apply(ResultSet resultSet) throws SQLException {
                Map map = new TreeMap();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int colCount = metaData.getColumnCount();
                for (int i = 1; i <= colCount; i++) {
                    map.put(metaData.getColumnLabel(i), metaData.getColumnClassName(i));
                }

                return map;
            }
        };

        String query = "SELECT * FROM Annotations WHERE ObservationID_FK = 0";

        return (Map<String, String>) annoQueryable.executeQueryFunction(query, queryFunction);
    }

    /**
     * @return
     */
    public String getURL() {
        return url;
    }

    /**
     *
     * @param columnName
     *
     * @return
     *
     */
    public Collection<?> getUniqueValuesByColumn(String columnName) {
        String query = "SELECT DISTINCT " + columnName + " FROM Annotations WHERE " + columnName +
                       " IS NOT NULL ORDER BY " + columnName;

        final QueryFunction queryFunction = new QueryFunction() {

            public Object apply(ResultSet resultSet) throws SQLException {
                Collection values = new ArrayList();
                while (resultSet.next()) {
                    values.add(resultSet.getObject(1));
                }

                return values;
            }
        };

        return (Collection) annoQueryable.executeQueryFunction(query, queryFunction);


    }

    public QueryResults executeQuery(String query) throws Exception {
        return annoQueryable.executeQuery(query);
    }

    public <T> T executeQueryFunction(String query, QueryFunction<T> queryFunction) {
        return annoQueryable.executeQueryFunction(query, queryFunction);
    }
}
