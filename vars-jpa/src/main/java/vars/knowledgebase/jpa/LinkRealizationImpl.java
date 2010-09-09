/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.knowledgebase.jpa;

import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import javax.persistence.CascadeType;
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
import vars.LinkUtilities;
import vars.jpa.JPAEntity;
import vars.jpa.KeyNullifier;
import vars.jpa.TransactionLogger;
import vars.knowledgebase.ConceptMetadata;
import vars.knowledgebase.LinkRealization;



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
@EntityListeners({TransactionLogger.class, KeyNullifier.class})
@NamedQueries({
    @NamedQuery(name = "LinkRealization.findById",
                query = "SELECT v FROM LinkRealization v WHERE v.id = :id"),
    @NamedQuery(name = "LinkRealization.findByLinkName",
                query = "SELECT l FROM LinkRealization l WHERE l.linkName = :linkName") ,
    @NamedQuery(name = "LinkRealization.findByToConcept",
                query = "SELECT l FROM LinkRealization l WHERE l.toConcept = :toConcept") ,
    @NamedQuery(name = "LinkRealization.findByLinkValue",
                query = "SELECT l FROM LinkRealization l WHERE l.linkValue = :linkValue")
})
public class LinkRealizationImpl implements Serializable, LinkRealization, JPAEntity {

    @Transient
    private static final List<String> PROPS = ImmutableList.of(LinkRealization.PROP_LINKNAME,
            LinkRealization.PROP_TOCONCEPT, LinkRealization.PROP_LINKVALUE) ;

    @Id
    @Column(name = "id", nullable = false, updatable=false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "LinkRealization_Gen")
    @TableGenerator(name = "LinkRealization_Gen", table = "UniqueID",
            pkColumnName = "TableName", valueColumnName = "NextID",
            pkColumnValue = "LinkRealization", allocationSize = 1)
    Long id;

    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime;

    @Column(name = "LinkName", length = 50)
    String linkName;

    @Column(name = "ToConcept", length = 128)
    String toConcept;

    @Column(name = "LinkValue", length = 2048)
    String linkValue;

    @ManyToOne(optional = false, targetEntity = ConceptMetadataImpl.class, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "ConceptDelegateID_FK")
    ConceptMetadata conceptMetadata;

    public LinkRealizationImpl() {}

    public LinkRealizationImpl(String linkName, String toConcept, String linkValue) {
        this.linkName = linkName;
        this.toConcept = toConcept;
        this.linkValue = linkValue;
    }

    public String getFromConcept() {
        return (conceptMetadata == null) ? null : conceptMetadata.getConcept().getPrimaryConceptName().getName();
    }

    public String stringValue() {
        return LinkUtilities.formatAsString(this);
    }

    

    @Override
    public String toString() {
        return "LinkRealizationImpl ([id=" + id + "] linkName=" + linkName
                + ", toConcept=" + toConcept + ", linkValue=" + linkValue + ")";
    }

    @Override
    public boolean equals(Object that) {

        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        return stringValue().equals(((LinkRealization) that).stringValue());
    }

    @Override
    public int hashCode() {
        return stringValue().hashCode() * 11;
    }

    public ConceptMetadata getConceptMetadata() {
        return conceptMetadata;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
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
    
    public void setToConcept(String toConcept) {
        this.toConcept = toConcept;
    }

    public String getLinkValue() {
        return linkValue;
    }

    public void setLinkValue(String linkValue) {
        this.linkValue = linkValue;
    }

    public Long getId() {
        return id;
    }

    public void setConceptMetadata(ConceptMetadata conceptMetadata) {
        this.conceptMetadata = conceptMetadata;
    }


}
