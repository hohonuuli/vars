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
import vars.knowledgebase.IConceptDelegate
import vars.knowledgebase.IHistory;

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
@NamedQueries( value = [
    @NamedQuery(name = "History.findById",
                query = "SELECT v FROM History v WHERE v.id = :id"),
    @NamedQuery(name = "History.findByApprovalDate",
                query = "SELECT h FROM History h WHERE h.approvalDate = :approvalDate") ,
    @NamedQuery(name = "History.findByCreationDate",
                query = "SELECT h FROM History h WHERE h.creationDate = :creationDate") ,
    @NamedQuery(name = "History.findByDescription",
                query = "SELECT h FROM History h WHERE h.description = :description") ,
    @NamedQuery(name = "History.findByCreatorName",
                query = "SELECT h FROM History h WHERE h.creatorName = :creatorName") ,
    @NamedQuery(name = "History.findByApproverName",
                query = "SELECT h FROM History h WHERE h.approverName = :approverName") ,
    @NamedQuery(name = "History.findByField", query = "SELECT h FROM History h WHERE h.field = :field") ,
    @NamedQuery(name = "History.findByOldValue", query = "SELECT h FROM History h WHERE h.oldValue = :oldValue") ,
    @NamedQuery(name = "History.findByNewValue", query = "SELECT h FROM History h WHERE h.newValue = :newValue") ,
    @NamedQuery(name = "History.findByAction", query = "SELECT h FROM History h WHERE h.action = :action") ,
    @NamedQuery(name = "History.findByComment", query = "SELECT h FROM History h WHERE h.comment = :comment") ,
    @NamedQuery(name = "History.findByRejected", query = "SELECT h FROM History h WHERE h.rejected = :rejected")
])
class History implements Serializable, IHistory {

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

    @Column(name = "ApprovalDTG")
    @Temporal(value = TemporalType.TIMESTAMP)
    Date approvalDate

    @Column(name = "CreationDTG", nullable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    Date creationDate

    @Column(name = "Description", length = 1000)
    String description

    @Column(name = "CreatorName", nullable = false, length = 50)
    String creatorName

    @Column(name = "ApproverName", length = 50)
    String approverName

    @Column(name = "Field", length = 2048)
    String field

    @Column(name = "OldValue", length = 2048)
    String oldValue

    @Column(name = "NewValue", length = 2048)
    String newValue

    @Column(name = "Action", length = 16)
    String action

    @Column(name = "Commnet", length = 2048)
    String comment

    @Column(name = "Rejected")
    private Short rejected

    @ManyToOne(optional = false, targetEntity = ConceptDelegate.class)
    @JoinColumn(name = "ConceptDelegateID_FK")
    IConceptDelegate conceptDelegate

    public boolean isAdd() {
        return false;  // TODO implement this method.
    }

    public boolean isApproved() {
        return false;  // TODO implement this method.
    }

    public boolean isDelete() {
        return false;  // TODO implement this method.
    }

    public boolean isReplace() {
        return false;  // TODO implement this method.
    }

    public Boolean isRejected() {
        return rejected == 0
    }

    public void setRejected(Boolean rejected) {
        this.rejected = rejected ? 1 : 0
    }

    public String stringValue() {
        return null;  // TODO implement this method.
    }
}
