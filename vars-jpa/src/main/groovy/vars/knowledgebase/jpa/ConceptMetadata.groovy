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
import javax.persistence.OneToOne
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.FetchType
import javax.persistence.CascadeType
import vars.knowledgebase.IUsage
import vars.knowledgebase.IHistory
import vars.knowledgebase.IMedia
import vars.knowledgebase.ILinkTemplate
import vars.knowledgebase.ILinkRealization
import vars.knowledgebase.IConcept
import vars.knowledgebase.IConceptMetadata
import vars.jpa.JPAEntity
import javax.persistence.OrderBy
import vars.knowledgebase.HistoryCreationDateComparator
import vars.knowledgebase.MediaTypes
import vars.EntityToStringCategory
import javax.persistence.EntityListeners;
import org.mbari.jpax.TransactionLogger
import vars.jpa.KeyNullifier

/**
 * <pre>
 * CREATE TABLE CONCEPTDELEGATE (
 *   ID          	BIGINT NOT NULL,
 *   CONCEPTID_FK	BIGINT,
 *   USAGEID_FK  	BIGINT,
 *   CONSTRAINT PK_CONCEPTDELEGATE PRIMARY KEY(ID)
 * )
 * GO
 * CREATE INDEX IDX_USAGEID
 *   ON CONCEPTDELEGATE(USAGEID_FK)
 * GO
 * CREATE INDEX IDX_CONCEPTID
 *   ON CONCEPTDELEGATE(CONCEPTID_FK)
 * GO
 * </pre>
 */
@Entity(name = "ConceptMetadata")
@Table(name = "ConceptDelegate")
@EntityListeners( value = [TransactionLogger.class, KeyNullifier.class] )
@NamedQueries( value = [
    @NamedQuery(name = "ConceptMetadata.findById",
                query = "SELECT v FROM ConceptMetadata v WHERE v.id = :id")
])
class ConceptMetadata implements Serializable, IConceptMetadata, JPAEntity {

    @Id
    @Column(name = "id", nullable = false, updatable=false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "ConceptDelegate_Gen")
    @TableGenerator(name = "ConceptDelegate_Gen", table = "UniqueID",
            pkColumnName = "TableName", valueColumnName = "NextID",
            pkColumnValue = "ConceptDelegate", allocationSize = 1)
    Long id


    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime

    @OneToOne(mappedBy = "conceptMetadata", fetch = FetchType.EAGER, cascade = CascadeType.ALL, targetEntity = Usage.class)
    IUsage usage

    @OneToMany(targetEntity = History.class,
            mappedBy = "conceptMetadata",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    @OrderBy(value = "creationDate")
    Set<IHistory> histories

    @OneToMany(targetEntity = Media.class,
            mappedBy = "conceptMetadata",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    Set<IMedia> medias

    @OneToMany(targetEntity = LinkTemplate.class,
            mappedBy = "conceptMetadata",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    Set<ILinkTemplate> linkTemplates

    @OneToMany(targetEntity = LinkRealization.class,
            mappedBy = "conceptMetadata",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    Set<ILinkRealization> linkRealizations

    @OneToOne(targetEntity = Concept.class)
    @JoinColumn(name = "ConceptID_FK")
    IConcept concept

    boolean hasPrimaryImage() {
        return (primaryImage != null)
    }

    IMedia getPrimaryImage() {
        return medias?.find { IMedia m -> m.primary && m.type == MediaTypes.IMAGE.type }
    }

    Set<IHistory> getHistories() {
        if (histories == null) {
            histories = new TreeSet<IHistory>(new HistoryCreationDateComparator())
        }
        return histories
    }

    Set<IMedia> getMedias() {
        if (medias == null) {
            medias = new HashSet<IMedia>()
        }
        return medias
    }

    Set<ILinkRealization> getLinkRealizations() {
        if (linkRealizations == null) {
            linkRealizations = new HashSet<ILinkRealization>()
        }
        return linkRealizations
    }

    Set<ILinkTemplate> getLinkTemplates() {
        if (linkTemplates == null) {
            linkTemplates = new HashSet<ILinkTemplate>()
        }
        return linkTemplates
    }

    public void addHistory(IHistory history) {
        if (getHistories().add(history)) {
            history.conceptMetadata = this
        }
    }

    public void addLinkRealization(ILinkRealization linkRealization) {
        if (getLinkRealizations().add(linkRealization)) {
            linkRealization.conceptMetadata = this
        }
    }

    public void addLinkTemplate(ILinkTemplate linkTemplate) {
        if (getLinkTemplates().add(linkTemplate)) {
            linkTemplate.conceptMetadata = this
        }
    }

    public void addMedia(IMedia media) {
        if (getMedias().add(media)) {
            media.conceptMetadata = this
        }
    }

    public void removeHistory(IHistory history) {
        if (getHistories().remove(history)) {
            history?.conceptMetadata = null
        }
    }

    public void removeLinkRealization(ILinkRealization linkRealization) {
        if (getLinkRealizations().remove(linkRealization)) {
            linkRealization?.conceptMetadata = null
        }
    }

    public void removeLinkTemplate(ILinkTemplate linkTemplate) {
        if (getLinkTemplates().remove(linkTemplate)) {
            linkTemplate?.conceptMetadata = null
        }
    }

    public void removeMedia(IMedia media) {
        if (getMedias().remove(media)) {
            media?.conceptMetadata = null
        }
    }


    void setUsage(IUsage usage) {
        if (this.usage != null) {
            this.usage.conceptMetadata = null
        }
        this.usage = usage;
        usage?.conceptMetadata = this
    }

    @Override
    String toString() {
        return EntityToStringCategory.basicToString(this, [])
    }


}
