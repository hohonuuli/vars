/*
 * @(#)ConceptImpl.java   2009.09.24 at 08:47:23 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.knowledgebase.jpa;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import vars.jpa.JPAEntity;
import vars.jpa.KeyNullifier;
import vars.jpa.TransactionLogger;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptMetadata;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameTypes;

/**
 *
 * @author brian
 */
@Entity(name = "Concept")
@Table(name = "Concept")
@EntityListeners({ TransactionLogger.class, KeyNullifier.class})
@NamedQueries( {
    @NamedQuery(name = "Concept.findById", query = "SELECT v FROM Concept v WHERE v.id = :id") ,
    @NamedQuery(name = "Concept.findByOriginator", query = "SELECT c FROM Concept c WHERE c.originator = :originator") ,
    @NamedQuery(name = "Concept.findByStructureType",
                query = "SELECT c FROM Concept c WHERE c.structureType = :structureType") ,
    @NamedQuery(name = "Concept.findByReference", query = "SELECT c FROM Concept c WHERE c.reference = :reference") ,
    @NamedQuery(name = "Concept.findByNodcCode", query = "SELECT c FROM Concept c WHERE c.nodcCode = :nodcCode") ,
    @NamedQuery(name = "Concept.findByRankName", query = "SELECT c FROM Concept c WHERE c.rankName = :rankName") ,
    @NamedQuery(name = "Concept.findByRankLevel", query = "SELECT c FROM Concept c WHERE c.rankLevel = :rankLevel") ,
    @NamedQuery(name = "Concept.findByTaxonomyType",
                query = "SELECT c FROM Concept c WHERE c.taxonomyType = :taxonomyType") ,
    @NamedQuery(name = "Concept.findRoot", query = "SELECT c FROM Concept c WHERE c.parentConcept IS NULL") ,
    @NamedQuery(name = "Concept.findAll", query = "SELECT c FROM Concept c"),
    @NamedQuery(name = "Concept.findByName", query = "SELECT c FROM Concept c, IN (c.conceptNames) AS n WHERE n.name = :name"),
    @NamedQuery(name = "Concept.findAllByNameGlob", query = "SELECT DISTINCT c FROM Concept c, IN (c.conceptNames) AS n WHERE lower(n.name) LIKE :name ORDER BY n.name")
})
public class ConceptImpl implements Serializable, Concept, JPAEntity {
	


    @OneToMany(
        targetEntity = ConceptImpl.class,
        mappedBy = "parentConcept",
        fetch = FetchType.LAZY,
        cascade = { CascadeType.ALL }
    )
    private List<Concept> childConcepts;

    @OneToOne(
        mappedBy = "concept",
        fetch = FetchType.LAZY,
        cascade = { CascadeType.ALL },
        targetEntity = ConceptMetadataImpl.class
    )
    private ConceptMetadata conceptMetadata;

    @OneToMany(
        targetEntity = ConceptNameImpl.class,
        mappedBy = "concept",
        fetch = FetchType.EAGER,
        cascade = { CascadeType.ALL }
    )
    private Set<ConceptName> conceptNames;

    @Id
    @Column(
        name = "id",
        nullable = false,
        updatable = false
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Concept_Gen")
    @TableGenerator(
        name = "Concept_Gen",
        table = "UniqueID",
        pkColumnName = "TableName",
        valueColumnName = "NextID",
        pkColumnValue = "Concept",
        allocationSize = 1
    )
    private Long id;

    @Column(name = "NodcCode", length = 20)
    private String nodcCode;

    @Column(name = "Originator", length = 255)
    private String originator;

    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = true,
        targetEntity = ConceptImpl.class
    )
    @JoinColumn(name = "ParentConceptID_FK")
    private Concept parentConcept;

    @Column(name = "RankLevel", length = 20)
    private String rankLevel;

    @Column(name = "RankName", length = 20)
    private String rankName;

    @Column(name = "Reference", length = 1024)
    private String reference;

    @Column(name = "StructureType", length = 10)
    private String structureType;

    @Column(name = "TaxonomyType", length = 20)
    private String taxonomyType;

    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime;

    public void addChildConcept(Concept child) {
        ConceptImpl ci = (ConceptImpl) child;
        if (getChildConcepts().add(child)) {
            ci.setParentConcept(this);
        }
    }

    public void addConceptName(ConceptName conceptName) {

        // Check that there isn't already a primary name if this one is primary
        if (conceptName.getNameType().equalsIgnoreCase(ConceptNameTypes.PRIMARY.toString()) &&
                    getPrimaryConceptName() != null) {
            throw new IllegalArgumentException("Can't add a second primay conceptname to a concept");
        }

        // Check for matching name
        Collection<ConceptName> names = new ArrayList<ConceptName>(getConceptNames());
        for (ConceptName cn : names) {
            if (cn.getName().equals(conceptName.getName())) {
                throw new IllegalArgumentException(
                    "A ConceptName with the name '${conceptName.name}' already exists in " + this);
            }
        }

        getConceptNames().add(conceptName);
        conceptName.setConcept(this);
    }

    public List<Concept> getChildConcepts() {
        if (childConcepts == null) {
            childConcepts = new ArrayList <Concept>();
        }

        return childConcepts;
    }

    public ConceptMetadata getConceptMetadata() {
        if (conceptMetadata == null) {
            conceptMetadata = new ConceptMetadataImpl();
            ((ConceptMetadataImpl) conceptMetadata).setConcept(this);
        }

        return conceptMetadata;
    }

    /**
     * This method shouldn't be called by developers. It's added to support the
     * cascadeDelete method in the ConceptDAO
     * 
     * @param conceptMetadata
     */
    protected void setConceptMetadata(ConceptMetadata conceptMetadata) {
        ((ConceptMetadataImpl) getConceptMetadata()).setConcept(null);
        this.conceptMetadata = conceptMetadata;
        ((ConceptMetadataImpl) conceptMetadata).setConcept(this);
    }

    public ConceptName getConceptName(String name) {

        ConceptName conceptName = null;
        Collection<ConceptName> names = new ArrayList<ConceptName>(getConceptNames());
        for (ConceptName cn : names) {
            if (cn.getName().equals(name)) {
                conceptName = cn;

                break;
            }
        }

        return conceptName;
    }

    public Set<ConceptName> getConceptNames() {
        if (conceptNames == null) {
            conceptNames = new HashSet<ConceptName>();
        }

        return conceptNames;
    }

    public Long getId() {
        return id;
    }

    public String getNodcCode() {
        return nodcCode;
    }

    public String getOriginator() {
        return originator;
    }

    public Concept getParentConcept() {
        return parentConcept;
    }

    public ConceptName getPrimaryConceptName() {

        ConceptName conceptName = null;
        Collection<ConceptName> names = new ArrayList<ConceptName>(getConceptNames());
        for (ConceptName cn : names) {
            if (cn.getNameType().equalsIgnoreCase(ConceptNameTypes.PRIMARY.toString())) {
                conceptName = cn;
                break;
            }
        }

        return conceptName;
    }

    public String getRankLevel() {
        return rankLevel;
    }

    public String getRankName() {
        return rankName;
    }

    public String getReference() {
        return reference;
    }

    public Concept getRootConcept() {
        Concept concept = this;
        while (concept.getParentConcept() != null) {
            concept = concept.getParentConcept();
        }

        return concept;
    }

    public String getStructureType() {
        return structureType;
    }

    /**
     * WARNING! Due to lazy loading you will need to explicitly load the
     * childconcepts in a JPA transaction first.
     * @return
     */
    public boolean hasChildConcepts() {
        return getChildConcepts().size() > 0;
    }

    /**
     * WARNING! Due to lazy loading you will need to explicitly load the
     * childconcepts in a JPA transaction first.
     * @return
     */
    public boolean hasDescendent(String child) {
        return hasDescendent(child, this);
    }

    private boolean hasDescendent(String childName, Concept concept) {
        boolean match = false;

        // ---- Check the immediate children for a match
        Collection<Concept> children = new ArrayList<Concept>(concept.getChildConcepts());
        for (Concept child : children) {
            match = child.getConceptName(childName) != null;
            if (match) {
                break;
            }
        }

        // ---- Iterate down to the grandchildren (and so on
        for (Concept child : children) {
            match = hasDescendent(childName, child);
            if (match) {
                break;
            }
        }

        return match;
    }

    public boolean hasDetails() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public boolean hasParent() {
        return parentConcept != null;
    }

    /**
     * This needs to be called inside of a JPA transaction in order to do anything
     */
    public void loadLazyRelations() {
        for (Concept concept : childConcepts) {
            concept.getConceptNames();    // Touch each to ensure they get loaded
        }
    }

    public void removeChildConcept(Concept childConcept) {
        ConceptImpl ci = (ConceptImpl) childConcept;
        if (getChildConcepts().remove(childConcept)) {
            ci.setParentConcept(null);
        }
    }

    public void removeConceptName(ConceptName conceptName) {
        if (getConceptNames().remove(conceptName)) {
            conceptName.setConcept(null);
        }
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNodcCode(String nodcCode) {
        this.nodcCode = nodcCode;
    }

    public void setOriginator(String originator) {
        this.originator = originator;
    }

    private void setParentConcept(ConceptImpl parentConcept) {
        this.parentConcept = parentConcept;
    }

    public void setRankLevel(String rankLevel) {
        this.rankLevel = rankLevel;
    }

    public void setRankName(String rankName) {
        this.rankName = rankName;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void setStructureType(String structureType) {
        this.structureType = structureType;
    }
    
    public Object getPrimaryKey() {
    	return getId();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " ([id=" + id + "])";
    }

    public void setTaxonomyType(String taxonomyType) {
        this.taxonomyType = taxonomyType;
    }

    public String getTaxonomyType() {
        return taxonomyType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Concept other = (Concept) obj;
        if (this.hashCode() != other.hashCode()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }



}
