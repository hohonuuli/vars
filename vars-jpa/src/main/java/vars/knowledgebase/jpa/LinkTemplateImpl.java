/*
 * @(#)LinkTemplateImpl.java   2009.11.09 at 04:57:26 PST
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
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;
import vars.LinkUtilities;
import vars.jpa.JPAEntity;
import vars.jpa.KeyNullifier;
import vars.jpa.TransactionLogger;
import vars.knowledgebase.ConceptMetadata;
import vars.knowledgebase.LinkTemplate;

/**
 * Class description
 *
 *
 * @version        $date$, 2009.11.09 at 04:57:26 PST
 * @author         Brian Schlining [brian@mbari.org]
 */
@Entity(name = "LinkTemplate")
@Table(name = "LinkTemplate")
@EntityListeners({ TransactionLogger.class, KeyNullifier.class })
@NamedQueries( {

    @NamedQuery(name = "LinkTemplate.findById", query = "SELECT v FROM LinkTemplate v WHERE v.id = :id") ,
    @NamedQuery(name = "LinkTemplate.findByLinkName",
                query = "SELECT l FROM LinkTemplate l WHERE l.linkName = :linkName") ,
    @NamedQuery(name = "LinkTemplate.findByToConcept",
                query = "SELECT l FROM LinkTemplate l WHERE l.toConcept = :toConcept") ,
    @NamedQuery(name = "LinkTemplate.findByLinkValue",
                query = "SELECT l FROM LinkTemplate l WHERE l.linkValue = :linkValue") ,
    @NamedQuery(name = "LinkTemplate.findByFields",
                query = "SELECT l FROM LinkTemplate l WHERE l.linkName = :linkName AND l.toConcept = :toConcept AND l.linkValue = :linkValue")

})
public class LinkTemplateImpl implements Serializable, LinkTemplate, JPAEntity {


    @ManyToOne(optional = false, targetEntity = ConceptMetadataImpl.class, fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "ConceptDelegateID_FK")
    ConceptMetadata conceptMetadata;

    @Id
    @Column(
        name = "id",
        nullable = false,
        updatable = false
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "LinkTemplate_Gen")
    @TableGenerator(
        name = "LinkTemplate_Gen",
        table = "UniqueID",
        pkColumnName = "TableName",
        valueColumnName = "NextID",
        pkColumnValue = "LinkTemplate",
        allocationSize = 1
    )
    Long id;

    @Column(name = "LinkName", length = 50)
    String linkName;

    @Column(name = "LinkValue", length = 255)
    String linkValue;
    
    @Column(name = "ToConcept", length = 128)
    String toConcept;

    /** Optimistic lock to prevent concurrent overwrites */
    @SuppressWarnings("unused")
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime;

    public ConceptMetadata getConceptMetadata() {
        return conceptMetadata;
    }

    void setConceptMetadata(ConceptMetadata conceptMetadata) {
        this.conceptMetadata = conceptMetadata;
    }
    
    public String getFromConcept() {
        return (conceptMetadata == null) ? null : conceptMetadata.getConcept().getPrimaryConceptName().getName();
    }

    public Long getId() {
        return id;
    }

    public String getLinkName() {
        return linkName;
    }

    public String getLinkValue() {
        return linkValue;
    }

    public String getToConcept() {
        return toConcept;
    }
    
    public Object getPrimaryKey() {
    	return getId();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public void setLinkValue(String linkValue) {
        this.linkValue = linkValue;
    }

    public void setToConcept(String toConcept) {
        this.toConcept = toConcept;
    }

    public String stringValue() {
        return LinkUtilities.formatAsString(this);
    }

    

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((linkName == null) ? 0 : linkName.hashCode());
        result = prime * result
                + ((linkValue == null) ? 0 : linkValue.hashCode());
        result = prime * result
                + ((toConcept == null) ? 0 : toConcept.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LinkTemplateImpl other = (LinkTemplateImpl) obj;
        if (linkName == null) {
            if (other.linkName != null)
                return false;
        }
        else if (!linkName.equals(other.linkName))
            return false;
        if (linkValue == null) {
            if (other.linkValue != null)
                return false;
        }
        else if (!linkValue.equals(other.linkValue))
            return false;
        if (toConcept == null) {
            if (other.toConcept != null)
                return false;
        }
        else if (!toConcept.equals(other.toConcept))
            return false;
        return true;
    }

    @Override
	public String toString() {
		return "LinkTemplateImpl ([id=" + id + "] linkName=" + linkName
				+ ", toConcept=" + toConcept + ", linkValue=" + linkValue + ")";
	}
    
    
    

}
