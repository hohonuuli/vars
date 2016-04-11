package vars.query.ui.db.preparedstatement;

import org.mbari.sql.QueryableImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.VARSException;
import vars.query.results.QueryResults;
import vars.query.ui.ConceptConstraints;
import vars.query.ui.ValuePanel;
import vars.query.ui.db.AbstractQueryExecutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author Brian Schlining
 * @since Nov 9, 2010
 */
public class EscapedQueryExecutorImpl extends AbstractQueryExecutor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final QueryableImpl queryable;
    private final PreparedStatementGenerator preparedStatementGenerator;

    public EscapedQueryExecutorImpl(Collection<ConceptConstraints> conceptConstraints,
            Collection<ValuePanel> valuePanels, boolean allInterpretations, boolean allAssociations,
            QueryableImpl queryable) {
        super(conceptConstraints, valuePanels, allInterpretations, allAssociations);
        this.queryable = queryable;
        this.preparedStatementGenerator = new PreparedStatementGenerator(conceptConstraints, valuePanels,
                allInterpretations, allAssociations);
    }

    public QueryResults query() {
        try {
            Connection connection = queryable.getConnection();
            String template = preparedStatementGenerator.getStatementTemplate();
            log.debug("PreparedStatement Template = \n" + template);
            PreparedStatement preparedStatement = connection.prepareStatement(template);
            preparedStatementGenerator.bind(preparedStatement);
            QueryResults queryResults = QueryResults.fromResultSet(preparedStatement.executeQuery());
            connection.close();
            return queryResults;
        } catch (SQLException e) {
            throw new VARSException("Failed to execute prepared statement", e);
        }
    }
    
}
