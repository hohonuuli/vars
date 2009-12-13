/*
 * @(#)UsageImpl.java   2009.11.10 at 11:47:25 PST
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
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import vars.jpa.JPAEntity;
import vars.jpa.KeyNullifier;
import vars.jpa.TransactionLogger;
import vars.knowledgebase.ConceptMetadata;
import vars.knowledgebase.Usage;

/**
 * CREATE TABLE USAGE (
 *   ID                         BIGINT NOT NULL,
 *   CONCEPTDELEGATEID_FK       BIGINT,
 *   EMBARGOEXPIRATIONDATE      TIMESTAMP,
 *   SPECIFICATION              VARCHAR(1000),
 *   CONSTRAINT PK_USAGE PRIMARY KEY(ID)
 * )
 * GO
 * CREATE INDEX IDX_CONCEPTDELEGATE7
 *   ON USAGE(CONCEPTDELEGATEID_FK)
 * GO
 */
@Entity(name = "Usage")
@Table(name = "Usage")
@EntityListeners({ TransactionLogger.class, KeyNullifier.class })
@NamedQueries({ @NamedQuery(name = "Usage.findById", query = "SELECT v FROM Usage v WHERE v.id = :id") ,
                @NamedQuery(name = "Usage.findByEmbargoExpirationDate",
                            query = "SELECT u FROM Usage u WHERE u.embargoExpirationDate = :embargoExpirationDate") ,
                @NamedQuery(name = "Usage.findBySpecification",
                            query = "SELECT u FROM Usage u WHERE u.specification = :specification") })
public class UsageImpl implements Serializable, Usage, JPAEntity {

    @OneToOne(targetEntity = ConceptMetadataImpl.class, optional = false, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "ConceptDelegateID_FK")
    ConceptMetadata conceptMetadata;

    @Column(name = "EmbargoExpirationDate")
    @Temporal(value = TemporalType.TIMESTAMP)
    Date embargoExpirationDate;
    @Id
    @Column(
        name = "id",
        nullable = false,
        updatable = false
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Usage_Gen")
    @TableGenerator(
        name = "Usage_Gen",
        table = "UniqueID",
        pkColumnName = "TableName",
        valueColumnName = "NextID",
        pkColumnValue = "Usage",
        allocationSize = 1
    )
    Long id;
    
    @Column(name = "Specification", length = 1000)
    String specification;

    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final UsageImpl other = (UsageImpl) obj;
        if ((this.embargoExpirationDate != other.embargoExpirationDate) &&
                ((this.embargoExpirationDate == null) ||
                 !this.embargoExpirationDate.equals(other.embargoExpirationDate))) {
            return false;
        }

        if ((this.specification == null)
                ? (other.specification != null) : !this.specification.equals(other.specification)) {
            return false;
        }

        return true;
    }

    public ConceptMetadata getConceptMetadata() {
        return conceptMetadata;
    }

    public Date getEmbargoExpirationDate() {
        return embargoExpirationDate;
    }

    public Long getId() {
        return id;
    }

    public String getSpecification() {
        return specification;
    }
    
    public Object getPrimaryKey() {
    	return getId();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + ((this.embargoExpirationDate != null) ? this.embargoExpirationDate.hashCode() : 0);
        hash = 17 * hash + ((this.specification != null) ? this.specification.hashCode() : 0);

        return hash;
    }

    void setConceptMetadata(ConceptMetadata conceptMetadata) {
        this.conceptMetadata = conceptMetadata;
    }

    public void setEmbargoExpirationDate(Date embargoExpirationDTG) {
        this.embargoExpirationDate = embargoExpirationDTG;
    }
    
    public void setId(Long id) {
    	this.id = id;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }
}
