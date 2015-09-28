package vars.queryfx.beans;

import vars.ILink;

/**
 * @author Brian Schlining
 * @since 2015-07-22T11:38:00
 */
public class ConceptSelection {

    private final String conceptName;

    private final boolean extendToParent;
    private final boolean extendToSiblings;
    private final boolean extendToChildren;
    private final boolean extendToDescendants;
    private final ILink link;

    public ConceptSelection(String conceptName, ILink link, boolean extendToParent, boolean extendToSiblings,
                            boolean extendToChildren, boolean extendToDescendants) {
        this.conceptName = conceptName;
        this.link = link;
        this.extendToParent = extendToParent;
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

    public boolean isExtendToParent() {
        return extendToParent;
    }

    public boolean isExtendToSiblings() {
        return extendToSiblings;
    }

    public ILink getLink() {
        return link;
    }

    @Override
    public String toString() {
        return "ConceptSelection{" +
                "conceptName='" + conceptName + '\'' +
                ", extendToParent=" + extendToParent +
                ", extendToSiblings=" + extendToSiblings +
                ", extendToChildren=" + extendToChildren +
                ", extendToDescendants=" + extendToDescendants +
                '}';
    }
}
