/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.knowledgebase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptMetadata;
import vars.knowledgebase.ConceptName;

/**
 *
 * @author brian
 */
public class SimpleConceptBean implements Concept {

    private Set<ConceptName> conceptNames = new HashSet<ConceptName>();
    private List<Concept> childConcepts = new ArrayList<Concept>();

    private Concept parentConcept;
    private ConceptMetadata conceptMetadata = new SimpleConceptMetadataBean(this);

    public SimpleConceptBean() {
    }

    public SimpleConceptBean(ConceptName conceptName) {
        conceptName.setNameType(ConceptNameTypes.PRIMARY.toString());
        addConceptName(conceptName);
    }


    public void addChildConcept(Concept child) {
        childConcepts.add(child);
    }

    public void addConceptName(ConceptName conceptName) {
        conceptNames.add(conceptName);
    }

    public List<Concept> getChildConcepts() {
        return childConcepts;
    }

    public ConceptMetadata getConceptMetadata() {
       return conceptMetadata;
    }

    public ConceptName getConceptName(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set<ConceptName> getConceptNames() {
        return conceptNames;
    }

    public String getNodcCode() {
        return "NOT IMPLEMENTED";
    }

    public String getOriginator() {
        return "NOT IMPLEMENTED";
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
        return "NOT IMPLEMENTED";
    }

    public String getRankName() {
        return "NOT IMPLEMENTED";
    }

    public String getReference() {
        return "NOT IMPLEMENTED";
    }

    public Concept getRootConcept() {
        Concept gotRoot = this;
        while (gotRoot.getParentConcept() != null) {
            gotRoot = gotRoot.getParentConcept();
        }
        return gotRoot;
    }

    public String getStructureType() {
        return "NOT IMPLEMENTED";
    }

    public boolean hasChildConcepts() {
        return childConcepts.size() > 0;
    }

    public boolean hasDescendent(String child) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasDetails() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasParent() {
        return parentConcept != null;
    }

    public void removeChildConcept(Concept childConcept) {
        childConcepts.remove(childConcept);
        ((SimpleConceptBean) childConcept).setParentConcept(null);
    }

    public void removeConceptName(ConceptName conceptName) {
        conceptNames.remove(    conceptName);
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
        // Do Nothing
    }

    protected void setParentConcept(Concept parentConcept) {
        this.parentConcept = parentConcept;
    }

    public void setTaxonomyType(String taxonomyType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getTaxonomyType() {
        return "NOT IMPLEMENTED";
    }
    
    public Object getPrimaryKey() {
    	return null;
    }

}
