/*
 * @(#)ConceptNameImpl.java   2009.11.10 at 03:26:40 PST
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
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import vars.jpa.JPAEntity;
import vars.jpa.KeyNullifier;
import vars.jpa.TransactionLogger;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptName;

/**
 * <pre>
 * CREATE TABLE CONCEPTNAME (
 *   CONCEPTNAME        VARCHAR(50) NOT NULL,
 *   CONCEPTID_FK       BIGINT,
 *   AUTHOR             VARCHAR(255),
 *   NAMETYPE           VARCHAR(10),
 *   ID                 BIGINT NOT NULL,
 *   CONSTRAINT PK_CONCEPTNAME PRIMARY KEY(ID)
 * )
 * GO
 * CREATE INDEX IDX_CONCEPTNAME_CONCEPTID
 *   ON CONCEPTNAME(CONCEPTID_FK)
 * GO
 * CREATE INDEX IDX_CONCEPTNAME2
 *   ON CONCEPTNAME(CONCEPTNAME)
 * GO
 * </pre>
 */
@Entity(name = "ConceptName")
@Table(name = "ConceptName", uniqueConstraints = { @UniqueConstraint(columnNames = { "ConceptName" }) })
@EntityListeners({ TransactionLogger.class, KeyNullifier.class })
@NamedQueries( {

    @NamedQuery(name = "ConceptName.findById", query = "SELECT v FROM ConceptName v WHERE v.id = :id") ,
    @NamedQuery(name = "ConceptName.findByName", query = "SELECT c FROM ConceptName c WHERE c.name = :name") ,
    @NamedQuery(name = "ConceptName.findByAuthor", query = "SELECT c FROM ConceptName c WHERE c.author = :author") ,
    @NamedQuery(name = "ConceptName.findByNameType",
                query = "SELECT c FROM ConceptName c WHERE c.nameType = :nameType") ,
    @NamedQuery(name = "ConceptName.findAll", query = "SELECT c FROM ConceptName c") ,
    @NamedQuery(name = "ConceptName.findByNameLike", query = "SELECT c FROM ConceptName c WHERE lower(c.name) LIKE :name ORDER BY c.name")

})
public class ConceptNameImpl implements Serializable, ConceptName, JPAEntity {


    @Column(name = "Author", length = 255)
    String author;

    @ManyToOne(
        optional = false,
        targetEntity = ConceptImpl.class,
        fetch = FetchType.EAGER,
        cascade = {CascadeType.MERGE, CascadeType.REFRESH}
    )
    @JoinColumn(name = "ConceptID_FK")
    Concept concept;

    @Id
    @Column(
        name = "id",
        nullable = false,
        updatable = false
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "ConceptName_Gen")
    @TableGenerator(
        name = "ConceptName_Gen",
        table = "UniqueID",
        pkColumnName = "TableName",
        valueColumnName = "NextID",
        pkColumnValue = "ConceptName",
        allocationSize = 1
    )
    Long id;

    @Column(
        name = "ConceptName",
        nullable = false,
        length = 128,
        unique = true
    )
    String name;
    
    @Column(
        name = "NameType",
        nullable = false,
        length = 10
    )
    String nameType;

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

        final ConceptNameImpl other = (ConceptNameImpl) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }

        return true;
    }

    public String getAuthor() {
        return author;
    }

    public Concept getConcept() {
        return concept;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNameType() {
        return nameType;
    }
    
    public Object getPrimaryKey() {
    	return getId();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + ((this.name != null) ? this.name.hashCode() : 0);

        return hash;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setConcept(Concept concept) {
        this.concept = concept;
    }
    
    public void setId(Long id) {
    	this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNameType(String nameType) {
        this.nameType = nameType;
    }

    public String stringValue() {
        return name;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append(" ([id=").append(id).append("] name=").append(name).append(")");

        return sb.toString();
    }
}
