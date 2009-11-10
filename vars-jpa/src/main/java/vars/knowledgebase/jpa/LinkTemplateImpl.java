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

import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.Version;
import vars.LinkUtilites;
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

    @Transient
    private static final Collection<String> PROPS = ImmutableList.of(LinkTemplate.PROP_LINKNAME,
        LinkTemplate.PROP_TOCONCEPT, LinkTemplate.PROP_LINKVALUE);

    @ManyToOne(optional = false, targetEntity = ConceptMetadataImpl.class)
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
    
    @Column(name = "ToConcept", length = 50)
    String toConcept;

    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime;

    public ConceptMetadata getConceptMetadata() {
        return conceptMetadata;
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
        return LinkUtilites.formatAsString(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LinkTemplateImpl other = (LinkTemplateImpl) obj;
        if ((this.linkName == null) ? (other.linkName != null) : !this.linkName.equals(other.linkName)) {
            return false;
        }
        if ((this.linkValue == null) ? (other.linkValue != null) : !this.linkValue.equals(other.linkValue)) {
            return false;
        }
        if ((this.toConcept == null) ? (other.toConcept != null) : !this.toConcept.equals(other.toConcept)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.linkName != null ? this.linkName.hashCode() : 0);
        hash = 79 * hash + (this.linkValue != null ? this.linkValue.hashCode() : 0);
        return hash;
    }

    

}
