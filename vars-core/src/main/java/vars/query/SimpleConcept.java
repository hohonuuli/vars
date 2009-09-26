/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.query;

import java.util.HashSet;
import java.util.Set;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptMetadata;
import vars.knowledgebase.ConceptName;

/**
 *
 * @author brian
 */
public class SimpleConcept implements Concept {

    private Set<ConceptName> conceptNames = new HashSet<ConceptName>();
    private Set<Concept> childConcepts = new HashSet<Concept>();

    private Concept parentConcept;

    public SimpleConcept() {
    }

    public SimpleConcept(ConceptName conceptName) {
        conceptName.setNameType(ConceptNameTypes.PRIMARY.toString());
        addConceptName(conceptName);
    }


    public void addChildConcept(Concept child) {
        childConcepts.add(child);
    }

    public void addConceptName(ConceptName conceptName) {
        conceptNames.add(conceptName);
    }

    public Set<Concept> getChildConcepts() {
        return childConcepts;
    }

    public ConceptMetadata getConceptMetadata() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ConceptName getConceptName(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set<ConceptName> getConceptNames() {
        return conceptNames;
    }

    public String getNodcCode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getOriginator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Concept getParentConcept() {
        return parentConcept;
    }

    public ConceptName getPrimaryConceptName() {
        ConceptName primaryName = null;
        for (ConceptName cn : conceptNames) {
            if (cn.getNameType().equalsIgnoreCase(ConceptNameTypes.PRIMARY.toString())) {
                primaryName = cn;
                break;
            }
        }
        return primaryName;
    }

    public String getRankLevel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getRankName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getReference() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Concept getRootConcept() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getStructureType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasChildConcepts() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasDescendent(String child) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasDetails() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasParent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeChildConcept(Concept childConcept) {
        childConcepts.remove(childConcept);
        ((SimpleConcept) childConcept).setParentConcept(null);
    }

    public void removeConceptName(ConceptName conceptName) {
        conceptNames.remove(conceptName);
        conceptName.setConcept(null);
    }

    public void setNodcCode(String nodcCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setOriginator(String originator) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setRankLevel(String rankLevel) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setRankName(String rankName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setReference(String reference) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setStructureType(String structureType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void loadLazyRelations() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected void setParentConcept(Concept parentConcept) {
        this.parentConcept = parentConcept;
    }

    public void setTaxonomyType(String taxonomyType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getTaxonomyType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
