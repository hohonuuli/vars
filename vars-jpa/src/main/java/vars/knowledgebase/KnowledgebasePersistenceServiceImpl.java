package vars.knowledgebase;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Collection;
import java.util.ArrayList;
import org.mbari.sql.QueryFunction;

import org.mbari.sql.QueryableImpl;
import vars.VARSException;
import vars.annotation.Association;
import vars.annotation.Observation;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Sep 29, 2009
 * Time: 12:58:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class KnowledgebasePersistenceServiceImpl extends QueryableImpl implements KnowledgebasePersistenceService {

    private static final String jdbcPassword;
    private static final String jdbcUrl;
    private static final String jdbcUsername;
    private static final String jdbcDriver;

    static {
        ResourceBundle bundle = ResourceBundle.getBundle("knowledgebase-jdbc", Locale.US);
        jdbcUrl = bundle.getString("jdbc.url");
        jdbcUsername = bundle.getString("jdbc.username");
        jdbcPassword = bundle.getString("jdbc.password");
        jdbcDriver = bundle.getString("jdbc.driver");
    }

    /**
     * Constructs ...
     */
    public KnowledgebasePersistenceServiceImpl() {
        super(jdbcUrl, jdbcUsername, jdbcPassword, jdbcDriver);
    }


    /**
     * Updates all {@link Observation}s, {@link Association}s, and {@link LinkTemplate}s
     * in the database so that any that use a non-primary name for the given
     * concept are changed so that they use the primary name.
     *
     * @param concept
     */
    public void updateConceptNameUsedByLinkTemplates(Concept concept) {

        String primaryName = concept.getPrimaryConceptName().getName();

        /*
         * Update the Observation table
         */
        Collection<ConceptName> conceptNames = new ArrayList<ConceptName>(concept.getConceptNames());
        conceptNames.remove(concept.getPrimaryConceptName());

        String sql = "UPDATE LinkTemplate SET ToConcept = ? WHERE ToConcept = ?";
        try {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (ConceptName conceptName : conceptNames) {
                preparedStatement.setString(1, primaryName);
                preparedStatement.setString(2, conceptName.getName());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            //connection.commit();
            preparedStatement.close();
        }
        catch (Exception e) {
            throw new VARSException("Failed to update LinkTemplates", e);
        }

    }


    /**
     *
     * @param conceptname
     * @return
     */
    public boolean doesConceptNameExist(String conceptname) {

        String sql = "SELECT count(*) FROM ConceptName WHERE ConceptName = ?";

        final QueryFunction<Boolean> queryFunction = new QueryFunction<Boolean>() {
            public Boolean apply(ResultSet resultSet) throws SQLException {
                Integer n = 0;
                while (resultSet.next()) {
                    n = resultSet.getInt(1);
                }
                return new Boolean(n > 0);
            }
        };

        try {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, conceptname);
            boolean exists = queryFunction.apply(preparedStatement.executeQuery());
            preparedStatement.close();
            return exists;
        }
        catch (Exception e) {
            throw new VARSException("Failed to execute " + sql, e);
        }

    }


}
