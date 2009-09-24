/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.query;

import java.util.HashSet;
import java.util.Set;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.IConcept;
import vars.knowledgebase.IConceptMetadata;
import vars.knowledgebase.IConceptName;

/**
 *
 * @author brian
 */
public class SimpleConcept implements IConcept {

    private Set<IConceptName> conceptNames = new HashSet<IConceptName>();
    private Set<IConcept> childConcepts = new HashSet<IConcept>();

    private IConcept parentConcept;

    public SimpleConcept() {
    }

    public SimpleConcept(IConceptName conceptName) {
        conceptName.setNameType(ConceptNameTypes.PRIMARY.toString());
        addConceptName(conceptName);
    }


    public void addChildConcept(IConcept child) {
        childConcepts.add(child);
    }

    public void addConceptName(IConceptName conceptName) {
        conceptNames.add(conceptName);
    }

    public Set<IConcept> getChildConcepts() {
        return childConcepts;
    }

    public IConceptMetadata getConceptMetadata() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public IConceptName getConceptName(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set<IConceptName> getConceptNames() {
        return conceptNames;
    }

    public String getNodcCode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getOriginator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public IConcept getParentConcept() {
        return parentConcept;
    }

    public IConceptName getPrimaryConceptName() {
        IConceptName primaryName = null;
        for (IConceptName cn : conceptNames) {
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

    public IConcept getRootConcept() {
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

    public void removeChildConcept(IConcept childConcept) {
        childConcepts.remove(childConcept);
        ((SimpleConcept) childConcept).setParentConcept(null);
    }

    public void removeConceptName(IConceptName conceptName) {
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

    protected void setParentConcept(IConcept parentConcept) {
        this.parentConcept = parentConcept;
    }

}
