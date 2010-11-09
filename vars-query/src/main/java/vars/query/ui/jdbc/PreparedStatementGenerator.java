package vars.query.ui.jdbc;

import org.mbari.sql.QueryableImpl;
import vars.query.ui.ConceptConstraints;
import vars.query.ui.ValuePanel;

import java.util.Collection;

/**
 * @author Brian Schlining
 * @since Nov 9, 2010
 */
public class PreparedStatementGenerator {

    private final QueryableImpl queryable;

    public PreparedStatementGenerator(QueryableImpl queryable) {
        this.queryable = queryable;
    }

    public String getPreparedStatement(Collection<ConceptConstraints> conceptConstraints,
            Collection<ValuePanel> valuePanels, boolean allInterpretations,
                boolean allAssociations) {

    }

    private String getSQLSelect(Collection<ValuePanel> valuePanels) {
        StringBuffer sb = new StringBuffer("SELECT ObservationID_FK, ");
        for (ValuePanel vp : valuePanels) {
            if (vp.getReturnCheckBox().isSelected()) {
                sb.append(" ").append(vp.getValueName()).append(", ");
            }
        }
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }

    private String getSQLWhere(Collection<ConceptConstraints> conceptConstraints, Collection<ValuePanel> valuePanels) {
        StringBuffer sb = new StringBuffer();

        /*
         * Add the where clauses for conceptnames first
         */
        for (ConceptConstraints cc : conceptConstraints) {
            ConceptConstraintsWrapper ccConstraint = new ConceptConstraintsWrapper(cc);
            String sql = ccConstraint.toSQL();
            if (!sql.equals(ConceptConstraints.EMPTY_SQL)) {
                sb.append(sql).append(" OR ");
            }
        }

        /*
         * Add all the subsetting by non- ConceptName or toConcept fields
         *
         * Wrap concept constrains with a parenthesis. If we don't do this then
         * only the last concept constrain will be constrained by the
         * Ancillary data fields added below.
         */
        if (sb.length() > 8) {
            sb.insert(0, "(");
            sb.delete(sb.length() - 4, sb.length()).append(") AND ");
        }

        for (ValuePanel vp : valuePanels) {
            if (vp.getConstrainCheckBox().isSelected()) {
                String sql = vp.getSQL();
                if (sql.length() > 0) {
                    sb.append(" ").append(vp.getSQL()).append(" AND ");
                }
            }
        }

        if (sb.length() > 4) {
            sb.delete(sb.length() - 5, sb.length());
        }

        return sb.toString();

        
    }

}
