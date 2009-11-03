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
import vars.knowledgebase.ConceptMetadata
import vars.knowledgebase.Media
import vars.jpa.JPAEntity
import vars.EntitySupportCategory
import javax.persistence.EntityListeners;
import vars.jpa.TransactionLogger
import vars.jpa.KeyNullifier
import javax.persistence.Transient

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Jun 19, 2009
 * Time: 10:02:15 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "Media")
@Table(name = "Media")
@EntityListeners( value = [TransactionLogger.class, KeyNullifier.class] )
@NamedQueries( value = [
    @NamedQuery(name = "Media.findById",
                query = "SELECT v FROM Media v WHERE v.id = :id"),
    @NamedQuery(name = "Media.findByUrl", query = "SELECT m FROM Media m WHERE m.url = :url") ,
    @NamedQuery(name = "Media.findByType", query = "SELECT m FROM Media m WHERE m.type = :type") ,
    @NamedQuery(name = "Media.findByPrimaryMedia",
                query = "SELECT m FROM Media m WHERE m.primaryMedia = :primaryMedia") ,
    @NamedQuery(name = "Media.findByCredit", query = "SELECT m FROM Media m WHERE m.credit = :credit") ,
    @NamedQuery(name = "Media.findByCaption", query = "SELECT m FROM Media m WHERE m.caption = :caption")
])
public class GMedia implements Serializable, Media, JPAEntity {

    @Transient
    private static final PROPS = Collections.unmodifiableList([Media.PROP_URL])

    @Id
    @Column(name = "id", nullable = false, updatable=false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Media_Gen")
    @TableGenerator(name = "Media_Gen", table = "UniqueID",
            pkColumnName = "TableName", valueColumnName = "NextID",
            pkColumnValue = "Media", allocationSize = 1)
    Long id

    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime

    @Column(name = "Url", length = 1024)
    String url

    @Column(name = "MediaType", length = 5)
    String type

    @Column(name = "PrimaryMedia")
    Boolean primaryMedia

    @Column(name = "Credit", length = 255)
    String credit

    @Column(name = "Caption", length = 1000)
    String caption

    @ManyToOne(optional = false, targetEntity = ConceptMetadataImpl.class)
    @JoinColumn(name = "ConceptDelegateID_FK")
    ConceptMetadata conceptMetadata

    public boolean isPrimary() {
        return false;  // TODO implement this method.
    }

    public void setPrimary(boolean primary) {
        // TODO implement this method.
    }

    public String stringValue() {
        return null;  // TODO implement this method.
    }

    @Override
    String toString() {
        return EntitySupportCategory.basicToString(this, [PROP_URL, PROP_TYPE])
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
