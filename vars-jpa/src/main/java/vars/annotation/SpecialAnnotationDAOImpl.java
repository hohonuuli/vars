/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import vars.QueryableImpl;

/**
 *
 * @author brian
 */
public class SpecialAnnotationDAOImpl extends QueryableImpl implements SpecialAnnotationDAO{

    private static final String jdbcPassword;
    private static final String jdbcUrl;
    private static final String jdbcUsername;
    private static final String jdbcDriver;

    static {
        ResourceBundle bundle = ResourceBundle.getBundle("annotation-jdbc");
        jdbcUrl = bundle.getString("jdbc.url");
        jdbcUsername = bundle.getString("jdbc.username");
        jdbcPassword = bundle.getString("jdbc.password");
        jdbcDriver = bundle.getString("jdbc.driver");
    }

    /**
     * Constructs ...
     */
    public SpecialAnnotationDAOImpl() {
        super(jdbcUrl, jdbcUsername, jdbcPassword, jdbcDriver);
    }

    public boolean doesConceptNameExist(String conceptname) {

        String sql = "SELECT count(*) FROM ConceptName WHERE ConceptName = '" +
                conceptname + "'";

        final QueryFunction queryFunction = new QueryFunction() {

            public Object apply(ResultSet resultSet) throws SQLException {
                Integer n = 0;

                while (resultSet.next()) {
                    n = resultSet.getInt(1);
                }

                return new Boolean(n > 0);
            }
        };

        return ((Boolean) executeQueryFunction(sql, queryFunction)).booleanValue();
    }

}
