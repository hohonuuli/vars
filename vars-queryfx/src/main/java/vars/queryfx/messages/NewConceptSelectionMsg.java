package vars.queryfx.messages;

import vars.queryfx.beans.ConceptSelection;

/**
 * @author Brian Schlining
 * @since 2015-07-26T11:21:00
 */
public class NewConceptSelectionMsg implements Msg {

    private final ConceptSelection conceptSelection;

    public NewConceptSelectionMsg(ConceptSelection conceptSelection) {
        this.conceptSelection = conceptSelection;
    }

    public ConceptSelection getConceptSelection() {
        return conceptSelection;
    }
}
