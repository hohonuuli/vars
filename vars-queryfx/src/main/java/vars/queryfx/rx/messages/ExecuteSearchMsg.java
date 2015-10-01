package vars.queryfx.rx.messages;

import vars.queryfx.beans.ResultsCustomization;
import vars.queryfx.ui.db.ConceptConstraint;
import vars.queryfx.ui.db.IConstraint;
import vars.shared.rx.messages.Msg;

import java.util.List;

/**
 * @author Brian Schlining
 * @since 2015-07-30T13:57:00
 */
public class ExecuteSearchMsg implements Msg {

    private final List<String> queryReturns;
    private final List<ConceptConstraint> conceptConstraints;
    private final List<IConstraint> queryConstraints;
    private final ResultsCustomization resultsCustomization;

    public ExecuteSearchMsg(List<ConceptConstraint> conceptConstraints,
            List<String> queryReturns,
            List<IConstraint> queryConstraints,
            ResultsCustomization resultsCustomization) {
        this.conceptConstraints = conceptConstraints;
        this.queryReturns = queryReturns;
        this.queryConstraints = queryConstraints;
        this.resultsCustomization = resultsCustomization;
    }

    public List<ConceptConstraint> getConceptConstraints() {
        return conceptConstraints;
    }

    public List<IConstraint> getQueryConstraints() {
        return queryConstraints;
    }

    public List<String> getQueryReturns() {
        return queryReturns;
    }

    public ResultsCustomization getResultsCustomization() {
        return resultsCustomization;
    }
}
