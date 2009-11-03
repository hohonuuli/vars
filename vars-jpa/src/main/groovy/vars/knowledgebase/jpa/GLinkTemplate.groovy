package vars.knowledgebase.jpa

import javax.persistence.Version
import javax.persistence.Column
import java.sql.Timestamp
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.TableGenerator
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.NamedQueries
import javax.persistence.NamedQuery
import javax.persistence.GenerationType
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import vars.ILink
import vars.LinkCategory
import vars.knowledgebase.ConceptMetadata
import vars.knowledgebase.LinkTemplate
import vars.jpa.JPAEntity
import javax.persistence.EntityListeners;
import vars.jpa.TransactionLogger
import vars.jpa.KeyNullifier
import javax.persistence.Transient
import vars.EntitySupportCategory

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Jun 19, 2009
 * Time: 10:01:50 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "LinkTemplate")
@Table(name = "LinkTemplate")
@EntityListeners( value = [TransactionLogger.class, KeyNullifier.class] )
@NamedQueries( value = [
    @NamedQuery(name = "LinkTemplate.findById",
                query = "SELECT v FROM LinkTemplate v WHERE v.id = :id"),
    @NamedQuery(name = "LinkTemplate.findByLinkName",
                query = "SELECT l FROM LinkTemplate l WHERE l.linkName = :linkName") ,
    @NamedQuery(name = "LinkTemplate.findByToConcept",
                query = "SELECT l FROM LinkTemplate l WHERE l.toConcept = :toConcept") ,
    @NamedQuery(name = "LinkTemplate.findByLinkValue",
                query = "SELECT l FROM LinkTemplate l WHERE l.linkValue = :linkValue"),
    @NamedQuery(name = "LinkTemplate.findByFields",
                query = "SELECT l FROM LinkTemplate l WHERE l.linkName = :linkName AND l.toConcept = :toConcept AND l.linkValue = :linkValue")
])
class GLinkTemplate implements Serializable, LinkTemplate, JPAEntity {

    @Transient
    private static final PROPS = Collections.unmodifiableList([LinkTemplate.PROP_LINKNAME,
            LinkTemplate.PROP_TOCONCEPT, LinkTemplate.PROP_LINKVALUE])

    @Id
    @Column(name = "id", nullable = false, updatable=false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "LinkTemplate_Gen")
    @TableGenerator(name = "LinkTemplate_Gen", table = "UniqueID",
            pkColumnName = "TableName", valueColumnName = "NextID",
            pkColumnValue = "LinkTemplate", allocationSize = 1)
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

    @ManyToOne(optional = false, targetEntity = ConceptMetadataImpl.class)
    @JoinColumn(name = "ConceptDelegateID_FK")
    ConceptMetadata conceptMetadata


   public String getFromConcept() {
        return conceptMetadata.concept.primaryConceptName.name
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
        if (this.getClass() == that?.getClass()) {
            return EntitySupportCategory.equals(this, that, PROPS)
        }
        else {
            return false;
        }
    }

    @Override
    int hashCode() {
        return EntitySupportCategory.hashCode(this, PROPS)
    }

 
}
