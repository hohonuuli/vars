package vars.queryfx.rx.messages;

import vars.queryfx.beans.ResolvedConceptSelection;
import vars.shared.rx.messages.Msg;

/**
 * @author Brian Schlining
 * @since 2015-07-26T11:49:00
 */
public class NewResolvedConceptSelectionMsg implements Msg {

    private final ResolvedConceptSelection resolvedConceptSelection;

    public NewResolvedConceptSelectionMsg(ResolvedConceptSelection resolvedConceptSelection) {
        this.resolvedConceptSelection = resolvedConceptSelection;
    }

    public ResolvedConceptSelection getResolvedConceptSelection() {
        return resolvedConceptSelection;
    }
}
