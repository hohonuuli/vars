package vars.query.ui.db.preparedstatement;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.mbari.sql.QueryableImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.VARSException;
import vars.query.ui.ConceptConstraints;
import vars.query.ui.ValuePanel;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author Brian Schlining
 * @since Nov 9, 2010
 */
public class PreparedStatementGenerator {

    private final Collection<ConceptConstraintsWrapper> conceptConstraintsWrappers;
    private final Collection<ValuePanel> valuePanels;
    private final Collection<ValuePanelWrapper> valuePanelWrappers;
    private final boolean allInterpretations;
    private final boolean allAssociations;
    private final Logger log = LoggerFactory.getLogger(getClass());


    public PreparedStatementGenerator(Collection<ConceptConstraints> conceptConstraints,
            Collection<ValuePanel> valuePanels, boolean allInterpretations,
                boolean allAssociations) {

        this.valuePanels = valuePanels;

        conceptConstraintsWrappers = Collections2.transform(conceptConstraints,
                new Function<ConceptConstraints, ConceptConstraintsWrapper>() {
                    public ConceptConstraintsWrapper apply(ConceptConstraints from) {
                        return new ConceptConstraintsWrapper(from);
                    }
                });

        final ValuePanelWrapperFactory vpFactory = new ValuePanelWrapperFactory();
        valuePanelWrappers = Collections2.transform(valuePanels, new Function<ValuePanel, ValuePanelWrapper>(){
            public ValuePanelWrapper apply(ValuePanel from) {
                return vpFactory.wrap(from);  //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        this.allInterpretations = allInterpretations;
        this.allAssociations = allAssociations;
    }

    public String getStatementTemplate() {

        String whereClause = getSQLWhere(conceptConstraintsWrappers, valuePanelWrappers);
        StringBuffer sb = new StringBuffer(getSQLSelect(valuePanels));
        sb.append(" FROM Annotations");

        if (allInterpretations && allAssociations) {
            if (whereClause.length() > 0) {
                sb.append(
                        " WHERE ObservationID_FK IN (SELECT ObservationID_FK FROM Annotations");
                sb.append(
                        " WHERE VideoFrameID_FK IN (SELECT VideoFrameID_FK FROM Annotations");
                sb.append(" WHERE ").append(whereClause);
                sb.append(" ))");
            }
        } else if (allInterpretations) {
            if (whereClause.length() > 0) {
                sb.append(
                        " WHERE VideoFrameID_FK IN (SELECT VideoFrameID_FK FROM Annotations");
                sb.append(" WHERE ").append(whereClause);
                sb.append(" )");
            }
        } else if (allAssociations) {
            if (whereClause.length() > 0) {
                sb.append(
                        " WHERE ObservationID_FK IN (SELECT ObservationID_FK FROM Annotations");
                sb.append(" WHERE ").append(whereClause);
                sb.append(" )");
            }
        } else {
            /*
             * This is the default if no 'all associations' or 'all
             * interpretations' is checked.
             */
            if (whereClause.length() > 0) {
                sb.append(" WHERE ").append(whereClause);
            }
        }

        return sb.toString();

    }

    public void bind(PreparedStatement preparedStatement) {
        int idx = 1;
        try {
            for (ConceptConstraintsWrapper wrapper : conceptConstraintsWrappers) {
                idx = wrapper.bind(preparedStatement, idx);
            }
            for (ValuePanelWrapper wrapper: valuePanelWrappers) {
                idx = wrapper.bind(preparedStatement, idx);
            }
        } catch (SQLException e) {
            throw new VARSException("Failed to bind parameters to prepared statement", e);
        }
        log.debug("Bound " + (idx - 1) + " items to the preparedStatement");

    }

    private String getSQLSelect(Collection<ValuePanel> valuePanels) {
        StringBuffer sb = new StringBuffer("SELECT ObservationID_FK, ");
        for (ValuePanel vp : valuePanels) {
            if (vp.isReturned()) {
                sb.append(" ").append(vp.getValueName()).append(", ");
            }
        }
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }

    private String getSQLWhere(Collection<ConceptConstraintsWrapper> conceptConstraintsWrappers, 
                               Collection<ValuePanelWrapper> valuePanelWrappers) {
        StringBuffer sb = new StringBuffer();

        /*
         * Add the where clauses for conceptnames first
         */
        for (ConceptConstraintsWrapper wrapper : conceptConstraintsWrappers) {
            String sql = wrapper.toSQL();
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

        for (ValuePanelWrapper vp : valuePanelWrappers) {
            String sql = vp.toSQL();
            if (sql.length() > 0) {
                sb.append(" ").append(sql).append(" AND ");
            }
        }

        if (sb.length() > 4) {
            sb.delete(sb.length() - 5, sb.length());
        }

        return sb.toString();

        
    }

}
