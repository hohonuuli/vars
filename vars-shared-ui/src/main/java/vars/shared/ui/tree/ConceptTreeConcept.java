/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.shared.ui.tree;

import java.util.HashSet;
import java.util.Set;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptMetadata;
import vars.knowledgebase.ConceptName;

/**
 *
 * @author brian
 */
public class ConceptTreeConcept implements Concept {

    private final Set<Concept> childConcepts = new HashSet<Concept>();
    private final int fakeHashCode;
    private Concept parentConcept;

    public ConceptTreeConcept(int fakeHashCode) {
        this.fakeHashCode = fakeHashCode;
    }

    public void addChildConcept(Concept child) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addConceptName(ConceptName conceptName) {
        throw new UnsupportedOperationException("Not supported yet.");
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
        throw new UnsupportedOperationException("Not supported yet.");
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
        throw new UnsupportedOperationException("Not supported yet.");
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
        return parentConcept != null;
    }

    public void removeChildConcept(Concept childConcept) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeConceptName(ConceptName conceptName) {
        throw new UnsupportedOperationException("Not supported yet.");
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

    public void setParentConcept(Concept parentConcept) {
        this.parentConcept = parentConcept;
    }

    public void setStructureType(String structureType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setTaxonomyType(String taxonomyType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getTaxonomyType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void loadLazyRelations() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int hashCode() {
        return fakeHashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConceptTreeConcept other = (ConceptTreeConcept) obj;
        if (this.fakeHashCode != other.fakeHashCode) {
            return false;
        }
        return true;
    }



}
