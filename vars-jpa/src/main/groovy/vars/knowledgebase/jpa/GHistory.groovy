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
import javax.persistence.Temporal
import javax.persistence.GenerationType
import javax.persistence.TemporalType
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import vars.knowledgebase.ConceptMetadata
import vars.knowledgebase.History
import vars.jpa.JPAEntity
import vars.EntitySupportCategory
import java.text.DateFormat
import java.text.SimpleDateFormat
import javax.persistence.EntityListeners
import vars.jpa.TransactionLogger
import vars.jpa.KeyNullifier
import vars.jpa.KeyNullifier
import javax.persistence.Transient
import vars.UserAccount

/**
 * CREATE TABLE HISTORY (
 *   ID                  	BIGINT NOT NULL,
 *   CONCEPTDELEGATEID_FK	BIGINT,
 *   APPROVALDTG         	TIMESTAMP,
 *   CREATIONDTG         	TIMESTAMP,
 *   CREATORNAME         	VARCHAR(50),
 *   APPROVERNAME        	VARCHAR(50),
 *   FIELD               	VARCHAR(50),
 *   OLDVALUE            	VARCHAR(2048),
 *   NEWVALUE            	VARCHAR(2048),
 *   ACTION              	VARCHAR(16),
 *   COMMENT             	VARCHAR(2048),
 *   REJECTED            	SMALLINT NOT NULL,
 *   CONSTRAINT PK_HISTORY PRIMARY KEY(ID)
 * )
 * GO
 * CREATE INDEX IDX_CONCEPTDELEGATE2
 *   ON HISTORY(CONCEPTDELEGATEID_FK)
 * GO
 */
@Entity(name = "History")
@Table(name = "History")
@EntityListeners( value = [TransactionLogger.class, KeyNullifier.class] )
@NamedQueries( value = [
    @NamedQuery(name = "History.findById",
                query = "SELECT v FROM History v WHERE v.id = :id"),
    @NamedQuery(name = "History.findByProcessedDate",
                query = "SELECT h FROM History h WHERE h.processedDate = :processedDate"),
    @NamedQuery(name = "History.findByCreationDate",
                query = "SELECT h FROM History h WHERE h.creationDate = :creationDate"),
    @NamedQuery(name = "History.findByCreatorName",
                query = "SELECT h FROM History h WHERE h.creatorName = :creatorName"),
    @NamedQuery(name = "History.findByProcessorName",
                query = "SELECT h FROM History h WHERE h.processorName = :processorName") ,
    @NamedQuery(name = "History.findByField", query = "SELECT h FROM History h WHERE h.field = :field") ,
    @NamedQuery(name = "History.findByOldValue", query = "SELECT h FROM History h WHERE h.oldValue = :oldValue") ,
    @NamedQuery(name = "History.findByNewValue", query = "SELECT h FROM History h WHERE h.newValue = :newValue") ,
    @NamedQuery(name = "History.findByAction", query = "SELECT h FROM History h WHERE h.action = :action") ,
    @NamedQuery(name = "History.findByComment", query = "SELECT h FROM History h WHERE h.comment = :comment") ,
    @NamedQuery(name = "History.findByApproved", query = "SELECT h FROM History h WHERE h.approved = :approved"),
    @NamedQuery(name = "History.findPendingApproval", query = "SELECT h FROM History h WHERE h.processedDate IS NULL"),
    @NamedQuery(name = "History.findApproved", query = "SELECT h FROM History h WHERE h.processedDate IS NOT NULL")
])
class GHistory implements Serializable, History, JPAEntity {

    @Transient
    private static final PROPS = Collections.unmodifiableList([History.PROP_ACTION, History.PROP_FIELD,
            History.PROP_NEW_VALUE, History.PROP_OLD_VALUE, History.PROP_CREATOR_NAME, History.PROP_CREATION_DATE])

    @Id
    @Column(name = "id", nullable = false, updatable=false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "History_Gen")
    @TableGenerator(name = "History_Gen", table = "UniqueID",
            pkColumnName = "TableName", valueColumnName = "NextID",
            pkColumnValue = "History", allocationSize = 1)
    Long id

    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime

    @Column(name = "ProcessedDTG")
    @Temporal(value = TemporalType.TIMESTAMP)
    Date processedDate

    @Column(name = "CreationDTG", nullable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    Date creationDate

    @Column(name = "CreatorName", nullable = false, length = 50)
    String creatorName

    @Column(name = "ProcessorName", length = 50)
    String processorName

    @Column(name = "Field", length = 2048)
    String field

    @Column(name = "OldValue", length = 2048)
    String oldValue

    @Column(name = "NewValue", length = 2048)
    String newValue

    @Column(name = "Action", length = 16)
    String action

    @Column(name = "Comment", length = 2048)
    String comment

    @Column(name = "Approved")
    private Short approved = 0

    @ManyToOne(optional = false, targetEntity = ConceptMetadataImpl.class)
    @JoinColumn(name = "ConceptDelegateID_FK")
    ConceptMetadata conceptMetadata

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    static {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC")); 
    }


    boolean isAdd() {
        return ACTION_ADD.equalsIgnoreCase(action)
    }

    Boolean isApproved() {
        return approved == 1
    }

    boolean isDelete() {
        return ACTION_DELETE.equalsIgnoreCase(action)
    }

    boolean isReplace() {
        return ACTION_REPLACE.equalsIgnoreCase(action)
    }

    boolean isRejected() {
        return !isApproved()
    }

    boolean isProcessed() {
        return processedDate != null;
    }

    void setApproved(Boolean approved) {
        this.approved = approved ? 1 : 0
    }

    String stringValue() {
        
        StringBuffer sb = new StringBuffer("[").append(DATE_FORMAT.format(creationDate));
        sb.append(" by ").append(creatorName).append("] ").append(action).append(" ").append(field);
        final String newVal = (newValue == null) ? "" : newValue;
        final String oldVal = (oldValue == null) ? "" : oldValue;
        if (ACTION_ADD.equals(action)) {
            sb.append(" '").append(newVal).append("'");
        }
        else if (ACTION_DELETE.equals(action)) {
            sb.append(" '").append(oldVal).append("'");
        }
        else if (ACTION_REPLACE.equals(action)) {
            sb.append(" '").append(oldVal).append("' with '").append(newVal).append("'");
        }
        return sb.toString();
    }

    @Override
    String toString() {
        return EntitySupportCategory.basicToString(this, PROPS)
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
