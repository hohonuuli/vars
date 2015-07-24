package vars.queryfx.beans;

/**
 * @author Brian Schlining
 * @since 2015-07-22T11:38:00
 */
public class ConceptSelection {

    private final String conceptName;

    private final boolean extendToParents;
    private final boolean extendToSiblings;
    private final boolean extendToChildren;
    private final boolean extendToDescendants;

    public ConceptSelection(String conceptName, boolean extendToParents, boolean extendToSiblings,
                            boolean extendToChildren, boolean extendToDescendants) {
        this.conceptName = conceptName;
        this.extendToParents = extendToParents;
        this.extendToSiblings = extendToSiblings;
        this.extendToChildren = extendToChildren;
        this.extendToDescendants = extendToDescendants;
    }

    public String getConceptName() {
        return conceptName;
    }

    public boolean isExtendToChildren() {
        return extendToChildren;
    }

    public boolean isExtendToDescendants() {
        return extendToDescendants;
    }

    public boolean isExtendToParents() {
        return extendToParents;
    }

    public boolean isExtendToSiblings() {
        return extendToSiblings;
    }

    @Override
    public String toString() {
        return "ConceptSelection{" +
                "conceptName='" + conceptName + '\'' +
                ", extendToParents=" + extendToParents +
                ", extendToSiblings=" + extendToSiblings +
                ", extendToChildren=" + extendToChildren +
                ", extendToDescendants=" + extendToDescendants +
                '}';
    }
}
