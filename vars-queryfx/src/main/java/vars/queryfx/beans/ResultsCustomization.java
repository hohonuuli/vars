package vars.queryfx.beans;

/**
 * @author Brian Schlining
 * @since 2015-07-27T17:26:00
 */
public class ResultsCustomization {

    /*
    relatedAssociationsCheckbox;
    private EditorFormRow<CheckBox> concurrentObservationsCheckbox;
    private EditorFormRow<CheckBox> conceptHierarchyCheckbox;
    private EditorFormRow<CheckBox> basicPhylogenyCheckbox;
    private EditorFormRow<CheckBox> detailedPhylogenyCheckbox;
    private EditorFormRow<CheckBox> categorizeAssociationsCheckbox;
    */

    private final boolean concurrentObservations;
    private final boolean conceptHierarchy;
    private final boolean basicPhylogeny;
    private final boolean detailedPhylogeny;
    private final boolean categorizeAssociations;

    public ResultsCustomization(boolean basicPhylogeny, boolean concurrentObservations,
            boolean conceptHierarchy, boolean detailedPhylogeny, boolean categorizeAssociations) {
        this.basicPhylogeny = basicPhylogeny;
        this.concurrentObservations = concurrentObservations;
        this.conceptHierarchy = conceptHierarchy;
        this.detailedPhylogeny = detailedPhylogeny;
        this.categorizeAssociations = categorizeAssociations;
    }

    public boolean isBasicPhylogeny() {
        return basicPhylogeny;
    }

    public boolean isCategorizeAssociations() {
        return categorizeAssociations;
    }

    public boolean isConceptHierarchy() {
        return conceptHierarchy;
    }

    public boolean isConcurrentObservations() {
        return concurrentObservations;
    }

    public boolean isDetailedPhylogeny() {
        return detailedPhylogeny;
    }
}
