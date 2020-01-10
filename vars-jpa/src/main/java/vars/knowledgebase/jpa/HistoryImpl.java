/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.knowledgebase.jpa;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import vars.jpa.JPAEntity;
import vars.jpa.KeyNullifier;
import vars.jpa.TransactionLogger;
import vars.knowledgebase.ConceptMetadata;
import vars.knowledgebase.History;




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
@Table(name = "History",
        indexes = {@Index(name = "idx_History_FK1", columnList = "ConceptDelegateID_FK"),
                   @Index(name = "idx_History_LUT", columnList = "LAST_UPDATED_TIME")})
@EntityListeners({TransactionLogger.class, KeyNullifier.class})
@NamedQueries( {
    @NamedQuery(name = "History.findAll", 
                query = "SELECT h FROM History h"),
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
})
public class HistoryImpl implements Serializable, History, JPAEntity {

    @Id
    @Column(name = "id", nullable = false, updatable=false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "History_Gen")
    @TableGenerator(name = "History_Gen", table = "UniqueID",
            pkColumnName = "TableName", valueColumnName = "NextID",
            pkColumnValue = "History", allocationSize = 1)
    Long id;

    /** Optimistic lock to prevent concurrent overwrites */
    @SuppressWarnings("unused")
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime;

    @Column(name = "ProcessedDTG")
    @Temporal(value = TemporalType.TIMESTAMP)
    Date processedDate;

    @Column(name = "CreationDTG", nullable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    Date creationDate;

    @Column(name = "CreatorName", nullable = false, length = 50)
    String creatorName;

    @Column(name = "ProcessorName", length = 50)
    String processorName;

    @Column(name = "Field", length = 2048)
    String field;

    @Column(name = "OldValue", length = 2048)
    String oldValue;

    @Column(name = "NewValue", length = 2048)
    String newValue;

    @Column(name = "Action", length = 16)
    String action;

    //@Column(name = "HistoryComment", length = 2048) // Oracle won't allow columns named 'Comment'
    @Column(name = "Comment", length = 2048)
    String comment;

    @Column(name = "Approved")
    private Short approved = 0;

    @ManyToOne(optional = false, targetEntity = ConceptMetadataImpl.class, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "ConceptDelegateID_FK")
    ConceptMetadata conceptMetadata;

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    static {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }


    public boolean isAdd() {
        return ACTION_ADD.equalsIgnoreCase(action);
    }

    public Boolean isApproved() {
        return isProcessed() ? approved == 1 : false;
    }

    public boolean isDelete() {
        return ACTION_DELETE.equalsIgnoreCase(action);
    }

    public boolean isReplace() {
        return ACTION_REPLACE.equalsIgnoreCase(action);
    }

    public boolean isRejected() {
        return isProcessed() ? !isApproved() : false;
    }

    public boolean isProcessed() {
        return processedDate != null;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved ? Short.valueOf((short) 1) : Short.valueOf((short) 0);
    }

    public String stringValue() {

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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((action == null) ? 0 : action.hashCode());
        result = prime * result
                + ((creationDate == null) ? 0 : creationDate.hashCode());
        result = prime * result
                + ((creatorName == null) ? 0 : creatorName.hashCode());
        result = prime * result + ((field == null) ? 0 : field.hashCode());
        result = prime * result
                + ((newValue == null) ? 0 : newValue.hashCode());
        result = prime * result
                + ((oldValue == null) ? 0 : oldValue.hashCode());
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
        HistoryImpl other = (HistoryImpl) obj;
        if (action == null) {
            if (other.action != null)
                return false;
        }
        else if (!action.equals(other.action))
            return false;
        if (creationDate == null) {
            if (other.creationDate != null)
                return false;
        }
        else if (!creationDate.equals(other.creationDate))
            return false;
        if (creatorName == null) {
            if (other.creatorName != null)
                return false;
        }
        else if (!creatorName.equals(other.creatorName))
            return false;
        if (field == null) {
            if (other.field != null)
                return false;
        }
        else if (!field.equals(other.field))
            return false;
        if (newValue == null) {
            if (other.newValue != null)
                return false;
        }
        else if (!newValue.equals(other.newValue))
            return false;
        if (oldValue == null) {
            if (other.oldValue != null)
                return false;
        }
        else if (!oldValue.equals(other.oldValue))
            return false;
        return true;
    }

    public String getAction() {
        return action;
    }

    public Date getProcessedDate() {
        return processedDate;
    }

    public String getProcessorName() {
        return processorName;
    }

    public String getComment() {
        return comment;
    }

    public ConceptMetadata getConceptMetadata() {
        return conceptMetadata;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getCreatorName() {
       return creatorName;
    }

    public String getField() {
        return field;
    }
    
    public void setId(Long id) {
    	this.id = id;
    }

    public String getNewValue() {
        return newValue;
    }

    public String getOldValue() {
        return oldValue;
    }
    
    public Object getPrimaryKey() {
    	return getId();
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setProcessedDate(Date approvalDate) {
        this.processedDate = approvalDate;
    }

    public void setProcessorName(String approverName) {
        this.processorName = approverName;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public void setOldValue(String oldValue) {
       this.oldValue = oldValue;
    }

    public Long getId() {
        return id;
    }

    void setConceptMetadata(ConceptMetadata conceptMetadata) {
        this.conceptMetadata = conceptMetadata;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("HistoryImpl ([id=").append(id).append("] creatorName=")
                .append(creatorName).append(", creationDate=").append(
                        creationDate).append(", action=").append(action)
                .append(", field=").append(field).append(", oldValue=").append(
                        oldValue).append(", newValue=").append(newValue)
                .append(", approved=").append(approved).append(")");
        return builder.toString();
    }

  
    
    
    

    



}
