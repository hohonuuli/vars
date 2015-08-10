package vars.queryfx.ui.db;

        import vars.queryfx.beans.ResultsCustomization;

        import java.util.List;
        import java.util.stream.Collectors;

/**
 * Created by brian on 8/5/15.
 */
public class SQLStatementGenerator {

    public String getSQLStatement(List<String> queryReturns,
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
                .map(ConceptConstraint::toSQLClause)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(" OR "));

        if (!conceptClause.isEmpty()) {
            sb.append("(").append(conceptClause).append(")");
        }


        // Add other clauses
        String otherClause = constraints.stream()
                .map(IConstraint::toSQLClause)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(" AND "));

        if (!otherClause.isEmpty()) {
            if (!conceptClause.isEmpty()) {
                sb.append(" AND ");
            }
            sb.append(otherClause);
        }

        return sb.toString();
    }

}
