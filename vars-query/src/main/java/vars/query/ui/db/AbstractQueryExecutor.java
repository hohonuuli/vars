package vars.query.ui.db;

import com.google.common.collect.ImmutableList;
import vars.query.ui.ConceptConstraints;
import vars.query.ui.ValuePanel;
import vars.query.ui.db.sql.SQLGenerator;

import java.util.Collection;

/**
 * Query's are built from the UI components. This class encapsulates the database queries
 * and returns a {@link org.mbari.sql.QueryResults} object with the results based on the
 * UI's settings.
 * 
 * @author Brian Schlining
 * @since Nov 10, 2010
 */
public abstract class AbstractQueryExecutor implements QueryExecutor {

    private final Collection<ConceptConstraints> conceptConstraints;
    private final Collection<ValuePanel> valuePanels;
    private final boolean allInterpretations;
    private final boolean allAssociations;

    public AbstractQueryExecutor(Collection<ConceptConstraints> conceptConstraints,
            Collection<ValuePanel> valuePanels, boolean allInterpretations,
                boolean allAssociations) {
        this.conceptConstraints = ImmutableList.copyOf(conceptConstraints);
        this.valuePanels = ImmutableList.copyOf(valuePanels);
        this.allInterpretations = allInterpretations;
        this.allAssociations = allAssociations;
    }

    public Collection<ConceptConstraints> getConceptConstraints() {
        return conceptConstraints;
    }

    public Collection<ValuePanel> getValuePanels() {
        return valuePanels;
    }

    public boolean isAllInterpretations() {
        return allInterpretations;
    }

    public boolean isAllAssociations() {
        return allAssociations;
    }

    public String getSQL() {
        return SQLGenerator.getSQL(conceptConstraints, valuePanels, allInterpretations, allAssociations);
    }
    
}
