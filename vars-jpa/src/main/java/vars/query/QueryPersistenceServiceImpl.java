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

import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import javafx.util.Pair;
import org.mbari.sql.QueryFunction;
import org.mbari.sql.QueryResults;
import org.mbari.sql.QueryableImpl;
import vars.ILink;
import vars.LinkBean;
import vars.VARSException;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.KnowledgebaseDAOFactory;

import javax.inject.Inject;

/**
 * DAO for use by the query app. This drops out of hibernate and uses a lot of
 * SQL internally for speed reasons.
 * @author brian
 */
public class QueryPersistenceServiceImpl implements QueryPersistenceService {

    private final QueryableImpl annoQueryable;
    private final QueryableImpl kbQueryable;
    private final String url;
    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;
    protected final Function<Concept, Collection<String>> asNames = c ->
            c.getConceptNames().stream().map(ConceptName::getName).collect(Collectors.toList());

    /**
     * Constructs ...
     */
    @Inject
    public QueryPersistenceServiceImpl(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
        this.knowledgebaseDAOFactory = knowledgebaseDAOFactory;
        ResourceBundle bundle = ResourceBundle.getBundle("annotation-jdbc", Locale.US);
        String jdbcUrl = bundle.getString("jdbc.url");
        url = jdbcUrl;
        String jdbcUsername = bundle.getString("jdbc.username");
        String jdbcPassword = bundle.getString("jdbc.password");
        String jdbcDriver = bundle.getString("jdbc.driver");
        annoQueryable = new QueryableImpl(jdbcUrl, jdbcUsername, jdbcPassword, jdbcDriver);

        bundle = ResourceBundle.getBundle("knowledgebase-jdbc", Locale.US);
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
        final QueryFunction queryFunction = resultSet -> {
            List<String> conceptNamesAsStrings = new ArrayList<>();
            while (resultSet.next()) {
                conceptNamesAsStrings.add(resultSet.getString(1));
            }

            return conceptNamesAsStrings;
        };


        String query = "SELECT ConceptName FROM ConceptName ORDER BY ConceptName";

        return (List<String>) kbQueryable.executeQueryFunction(query, queryFunction);
    }

    /**
     * Similar to findLinksByConceptNames. However, this looksup all LinkTemplates rather
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

        final QueryFunction<Collection<ILink>> queryFunction = resultSet -> {
            Collection<ILink> associationBeans = new ArrayList<>();
            while (resultSet.next()) {
                LinkBean bean = new LinkBean();
                bean.setLinkName(resultSet.getString(1));
                bean.setToConcept(resultSet.getString(2));
                bean.setLinkValue(resultSet.getString(3));
                associationBeans.add(bean);
            }

            return associationBeans;
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
        final Set<String> allNames = new HashSet<>();

        final QueryFunction queryFunction = resultSet -> {
            while (resultSet.next()) {
                allNames.add(resultSet.getString(1));
            }

            return allNames;
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
     * Looks up all associations in a database that were used with Observations
     * containing the specified conceptNames
     *
     * @param conceptNames A collection of Strings representing the conceptnames to
     *  lookup
     * @return A collection of <code>AssociationBean</code>s representing the
     *  associations actually used to annotate Observations with the specifed
     *  conceptNames.
     */
    public Collection<ILink> findLinksByConceptNames(Collection<String> conceptNames) {

        // Here's the function that extracts the contents of a results set
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
         * Assemble a preparedStatement query to search for all annotations used for the respective
         * conceptnames.
         */
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT DISTINCT linkName, toConcept, linkValue ");
        sb.append("FROM Association JOIN Observation ON Observation.id = Association.ObservationID_FK ");
        sb.append("WHERE ");
        for (Iterator i = conceptNames.iterator(); i.hasNext(); ) {
            String conceptName = (String) i.next();
            sb.append("ConceptName = ?");
            if (i.hasNext()) {
                sb.append(" OR ");
            }
        }
        sb.append(" ORDER BY LinkName, toConcept, linkValue");


        // Execute Query
        Collection<ILink> links = new ArrayList<ILink>();
        try {
            Connection connection = annoQueryable.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sb.toString());
            int idx = 1;
            for (String name : conceptNames) {
                preparedStatement.setString(idx, name);
                idx++;
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            links.addAll((Collection<ILink>) queryFunction.apply(resultSet));
            preparedStatement.close();
        }
        catch (Exception e) {
            throw new VARSException("Failed to execute PreparedStatement of " + sb.toString(), e);
        }

        return links;
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

        String query = "SELECT DISTINCT count(" + columnName + ") FROM Annotations";

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

    @Override
    public List<Concept> findAncestors(String conceptName) {
        List<Concept> names = new ArrayList<>();
        ConceptDAO conceptDAO = knowledgebaseDAOFactory.newConceptDAO();
        conceptDAO.startTransaction();
        Concept concept = conceptDAO.findByName(conceptName);
        if (concept != null) {
            while (concept != null) {
                names.add(concept);
                concept = concept.getParentConcept();
            }
        }
        conceptDAO.endTransaction();
        Collections.reverse(names);
        return names;
    }

    @Override
    public Collection<Concept> findConcepts(String name,
            boolean extendToParent,
            boolean extendToSiblings,
            boolean extendToChildren,
            boolean extendToDescendants) {
        Collection<Concept> concepts = new HashSet<>();

        ConceptDAO conceptDAO = knowledgebaseDAOFactory.newConceptDAO();
        conceptDAO.startTransaction();
        Concept c = conceptDAO.findByName(name);

        if (c != null) {
            concepts.add(c);

            if (extendToParent && c.getParentConcept() != null) {
                concepts.add(c.getParentConcept());
            }

            if (extendToSiblings && c.getParentConcept() != null) {
                concepts.addAll(c.getParentConcept().getChildConcepts());
            }

            if (extendToChildren && !extendToDescendants) {
                concepts.addAll(c.getChildConcepts());
            }

            if (extendToDescendants) {
                concepts.addAll(conceptDAO.findDescendents(c));
            }
        }
        conceptDAO.endTransaction();
        conceptDAO.close();

        return concepts;
    }

    public List<String> findConceptNamesAsStrings(String name,
            boolean extendToParent,
            boolean extendToSiblings,
            boolean extendToChildren,
            boolean extendToDescendants) {
        Collection<Concept> concepts = findConcepts(name, extendToParent, extendToSiblings, extendToChildren, extendToDescendants);
        List<String> names;
        if (concepts != null) {
            names = concepts.stream()
                    .flatMap(c -> asNames.apply(c).stream())
                    .sorted()
                    .collect(Collectors.toList());
        }
        else {
            names = new ArrayList<>();
        }
        return names;
    }

    @Override
    public List<String> findDescendantNamesAsStrings(String conceptName) {
        List<String> names;
        ConceptDAO conceptDAO = knowledgebaseDAOFactory.newConceptDAO();
        conceptDAO.startTransaction();
        Concept concept = conceptDAO.findByName(conceptName);
        if (concept == null) {
            names = Lists.newArrayList();
        } else {
            names = conceptDAO.findDescendentNames(concept).stream()
                    .map(ConceptName::getName)
                    .sorted()
                    .collect(Collectors.toList());
        }
        conceptDAO.endTransaction();
        return names;
    }

    public QueryResults executeQuery(String query) throws Exception {
        return annoQueryable.executeQuery(query);
    }

    public <T> T executeQueryFunction(String query, QueryFunction<T> queryFunction) {
        return annoQueryable.executeQueryFunction(query, queryFunction);
    }
}
