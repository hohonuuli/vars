/*
 * @(#)QueryDAOImpl.java   2009.09.23 at 01:36:42 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.query;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.TreeMap;
import org.mbari.sql.QueryResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.ILink;
import vars.VARSException;
import vars.QueryableImpl;

/**
 * DAO for use by the query app. This drops out of hibernate and uses a lot of
 * SQL internally for speed reasons.
 * @author brian
 */
public class QueryDAOImpl extends QueryableImpl implements QueryDAO {

    private static final String jdbcPassword;
    private static final String jdbcUrl;
    private static final String jdbcUsername;
    private static final String jdbcDriver;

    static {
        ResourceBundle bundle = ResourceBundle.getBundle("query-dao");
        jdbcUrl = bundle.getString("jdbc.url");
        jdbcUsername = bundle.getString("jdbc.username");
        jdbcPassword = bundle.getString("jdbc.password");
        jdbcDriver = bundle.getString("jdbc.driver");
    }

    /**
     * Constructs ...
     */
    public QueryDAOImpl() {
        super(jdbcUrl, jdbcUsername, jdbcPassword, jdbcDriver);
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

        return (Collection<ILink>) executeQueryFunction(sb.toString(), queryFunction);
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

        final QueryFunction queryFunction = new QueryFunction() {

            public Object apply(ResultSet resultSet) throws SQLException {
                Collection<String> names = new HashSet<String>();
                while (resultSet.next()) {
                    names.add(resultSet.getString(1));
                }

                return names;
            }
        };

        String query = "SELECT DISTINCT ConceptName FROM Observation WHERE ConceptName IS NOT NULL" +
                       " UNION SELECT DISTINCT ToConcept FROM Association WHERE ToConcept IS NOT NULL" +
                       " UNION SELECT DISTINCT ConceptName From ConceptName WHERE ConceptName IS NOT NULL";

        return (Collection<String>) executeQueryFunction(query, queryFunction);
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

        return (Collection<ILink>) executeQueryFunction(sb.toString(), queryFunction);
    }

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

        return (List<String>) executeQueryFunction(query, queryFunction);
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

        return (Integer) executeQueryFunction(query, queryFunction);

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

        return (Map<String, String>) executeQueryFunction(query, queryFunction);
    }

    public String getURL() {
        return jdbcUrl;
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

        return (Collection) executeQueryFunction(query, queryFunction);


    }


}
