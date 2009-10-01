package vars.knowledgebase.jpa

import javax.persistence.Id
import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.TableGenerator
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.NamedQueries
import javax.persistence.NamedQuery
import javax.persistence.GenerationType
import javax.persistence.Version
import java.sql.Timestamp
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import vars.ILink
import vars.LinkCategory
import vars.knowledgebase.ConceptMetadata
import vars.knowledgebase.LinkRealization
import vars.jpa.JPAEntity
import javax.persistence.EntityListeners;
import org.mbari.jpaxx.TransactionLogger
import vars.jpa.KeyNullifier
import vars.jpa.KeyNullifier
import javax.persistence.CascadeType
import javax.persistence.Transient
import vars.EntitySupportCategory

/**
 * <pre>
 * CREATE TABLE LINKREALIZATION (
 *   ID                        	BIGINT NOT NULL,
 *   CONCEPTDELEGATEID_FK      	BIGINT,
 *   PARENTLINKREALIZATIONID_FK	BIGINT,
 *   LINKNAME                  	VARCHAR(50),
 *   TOCONCEPT                 	VARCHAR(50),
 *   LINKVALUE                 	VARCHAR(255),
 *   CONSTRAINT PK_LINKREALIZATION PRIMARY KEY(ID)
 * )
 * GO
 * CREATE INDEX IDX_CONCEPTDELEGATE3
 *   ON LINKREALIZATION(CONCEPTDELEGATEID_FK)
 * GO
 * </pre>
 */
@Entity(name = "LinkRealization")
@Table(name = "LinkRealization")
@EntityListeners( value = [TransactionLogger.class, KeyNullifier.class] )
@NamedQueries( value = [
    @NamedQuery(name = "LinkRealization.findById",
                query = "SELECT v FROM LinkRealization v WHERE v.id = :id"),
    @NamedQuery(name = "LinkRealization.findByLinkName",
                query = "SELECT l FROM LinkRealization l WHERE l.linkName = :linkName") ,
    @NamedQuery(name = "LinkRealization.findByToConcept",
                query = "SELECT l FROM LinkRealization l WHERE l.toConcept = :toConcept") ,
    @NamedQuery(name = "LinkRealization.findByLinkValue",
                query = "SELECT l FROM LinkRealization l WHERE l.linkValue = :linkValue")
])
class GLinkRealization implements Serializable, LinkRealization, JPAEntity {

    @Transient
    private static final PROPS = Collections.unmodifiableList([LinkRealization.PROP_LINKNAME,
            LinkRealization.PROP_TOCONCEPT, LinkRealization.PROP_LINKVALUE])    

    @Id
    @Column(name = "id", nullable = false, updatable=false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "LinkRealization_Gen")
    @TableGenerator(name = "LinkRealization_Gen", table = "UniqueID",
            pkColumnName = "TableName", valueColumnName = "NextID",
            pkColumnValue = "LinkRealization", allocationSize = 1)
    Long id

    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime

    @Column(name = "LinkName", length = 50)
    String linkName

    @Column(name = "ToConcept", length = 50)
    String toConcept

    @Column(name = "LinkValue", length = 255)
    String linkValue

    @ManyToOne(optional = false, targetEntity = ConceptMetadataImpl.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "ConceptDelegateID_FK")
    ConceptMetadata conceptMetadata

    def GLinkRealization() {}

    def GLinkRealization(String linkName, String toConcept, String linkValue) {
        this.linkName = linkName;
        this.toConcept = toConcept;
        this.linkValue = linkValue;
    }

    public String getFromConcept() {
        return conceptMetadata?.concept.primaryConceptName.name
    }

    String stringValue() {
        use (LinkCategory) {
            return formatLinkAsString()
        }
    }

    @Override
    String toString() {
        return stringValue()
    }

    @Override
    boolean equals(that) {
        return EntitySupportCategory.equals(this, that, PROPS)
    }

    @Override
    int hashCode() {
        return EntitySupportCategory.hashCode(this, PROPS)
    }


}
