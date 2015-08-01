package vars.queryfx.ui.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.VARSException;
import vars.queryfx.beans.ResultsCustomization;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2015-07-29T13:09:00
 */
public class PreparedStatementGenerator {

    private final Logger log = LoggerFactory.getLogger(getClass());


    public void bind(PreparedStatement statement, List<ConceptConstraint> conceptConstraints, List<IConstraint> constraints) {
        List<IConstraint> allConstraints = new ArrayList<>(conceptConstraints);
        allConstraints.addAll(constraints);

        int idx = 1;
        try {
            for (IConstraint c: allConstraints) {
                idx = c.bind(statement, idx);
            }
        }
        catch (SQLException e) {
            throw new VARSException("Failed to bind parameters to prepared statement", e);
        }
        log.debug("Bound " + (idx - 1) + " items to the preparedStatement");
    }

    public String getPreparedStatementTemplate(List<String> queryReturns,
            List<ConceptConstraint> conceptConstraints,
            List<IConstraint> queryConstraints,
            ResultsCustomization resultsCustomization) {

        StringBuilder sb = new StringBuilder(getSelectClause(queryReturns));
        sb.append(" FROM Annotations");

        String whereClause = getWhereClause(conceptConstraints, queryConstraints);
        if (!whereClause.isEmpty()) {
            if (resultsCustomization.isConcurrentObservations()
                    && resultsCustomization.isRelatedAssociations()) {

                sb.append(" WHERE ObservationID_FK IN")
                        .append(" (SELECT ObservationID_FK FROM Annotations")
                        .append(" WHERE VideoFrameID_FK IN")
                        .append(" (SELECT VideoFrameID_FK FROM Annotations")
                        .append(" WHERE ").append(whereClause).append("))");


            }
            else if (resultsCustomization.isConcurrentObservations()) {
                sb.append(" WHERE VideoFrameID_FK IN")
                        .append(" (SELECT VideoFrameID_FK FROM Annotations")
                        .append(" WHERE ").append(whereClause).append(")");
            }
            else if (resultsCustomization.isRelatedAssociations()) {
                sb.append(" WHERE ObservationID_FK IN")
                        .append(" (SELECT ObservationID_FK FROM Annotations")
                        .append(" WHERE ").append(whereClause).append(")");
            }
            else {
                sb.append(" WHERE ").append(whereClause);
            }
        }
        return sb.toString();
    }

    private String getSelectClause(List<String> queryReturns) {
        StringBuilder sb = new StringBuilder("SELECT ObservationID_FK");
        if (!queryReturns.isEmpty()) {
            sb.append(", ");
            sb.append(queryReturns.stream()
                    .collect(Collectors.joining(", ")));
        }
        return sb.toString();
    }

    private String getWhereClause(List<ConceptConstraint> conceptConstraints, List<IConstraint> constraints) {

        StringBuilder sb = new StringBuilder();

        // Add WHERE clauses for conceptNames first
        String conceptClause = conceptConstraints.stream()
                .map(ConceptConstraint::toPreparedStatementTemplate)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(" OR "));

        if (!conceptClause.isEmpty()) {
            sb.append("(").append(conceptClause).append(")");
        }


        // Add other clauses
        String otherClause = constraints.stream()
                .map(IConstraint::toPreparedStatementTemplate)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(" AND "));

        if (!otherClause.isEmpty()) {
            sb.append(" AND ").append(otherClause);
        }

        return sb.toString();
    }


}
