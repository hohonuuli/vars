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
import vars.knowledgebase.Concept
import vars.knowledgebase.ConceptMetadata
import vars.knowledgebase.History
import vars.knowledgebase.Media
import vars.knowledgebase.LinkRealization
import vars.knowledgebase.LinkTemplate
import vars.knowledgebase.Usage
import vars.jpa.JPAEntity
import javax.persistence.OrderBy
import vars.knowledgebase.HistoryCreationDateComparator
import vars.knowledgebase.MediaTypes
import vars.EntitySupportCategory
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
class GConceptMetadata implements Serializable, ConceptMetadata, JPAEntity {

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

    @OneToOne(mappedBy = "conceptMetadata", fetch = FetchType.EAGER, cascade = CascadeType.ALL, targetEntity = GUsage.class)
    Usage usage

    @OneToMany(targetEntity = GHistory.class,
            mappedBy = "conceptMetadata",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    @OrderBy(value = "creationDate")
    Set<History> histories

    @OneToMany(targetEntity = GMedia.class,
            mappedBy = "conceptMetadata",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    Set<Media> medias

    @OneToMany(targetEntity = GLinkTemplate.class,
            mappedBy = "conceptMetadata",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    Set<LinkTemplate> linkTemplates

    @OneToMany(targetEntity = GLinkRealization.class,
            mappedBy = "conceptMetadata",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    Set<LinkRealization> linkRealizations

    @OneToOne(targetEntity = GConcept.class)
    @JoinColumn(name = "ConceptID_FK")
    Concept concept

    boolean hasPrimaryImage() {
        return (primaryImage != null)
    }

    Media getPrimaryImage() {
        return medias?.find { Media m -> m.primary && m.type == MediaTypes.IMAGE.type }
    }

    Set<History> getHistories() {
        if (histories == null) {
            histories = new TreeSet<History>(new HistoryCreationDateComparator())
        }
        return histories
    }

    Set<Media> getMedias() {
        if (medias == null) {
            medias = new HashSet<Media>()
        }
        return medias
    }

    Set<LinkRealization> getLinkRealizations() {
        if (linkRealizations == null) {
            linkRealizations = new HashSet<LinkRealization>()
        }
        return linkRealizations
    }

    Set<LinkTemplate> getLinkTemplates() {
        if (linkTemplates == null) {
            linkTemplates = new HashSet<LinkTemplate>()
        }
        return linkTemplates
    }

    public void addHistory(History history) {
        if (getHistories().add(history)) {
            history.conceptMetadata = this
        }
    }

    public void addLinkRealization(LinkRealization linkRealization) {
        if (getLinkRealizations().add(linkRealization)) {
            linkRealization.conceptMetadata = this
        }
    }

    public void addLinkTemplate(LinkTemplate linkTemplate) {
        if (getLinkTemplates().add(linkTemplate)) {
            linkTemplate.conceptMetadata = this
        }
    }

    public void addMedia(Media media) {
        if (getMedias().add(media)) {
            media.conceptMetadata = this
        }
    }

    public void removeHistory(History history) {
        if (getHistories().remove(history)) {
            history?.conceptMetadata = null
        }
    }

    public void removeLinkRealization(LinkRealization linkRealization) {
        if (getLinkRealizations().remove(linkRealization)) {
            linkRealization?.conceptMetadata = null
        }
    }

    public void removeLinkTemplate(LinkTemplate linkTemplate) {
        if (getLinkTemplates().remove(linkTemplate)) {
            linkTemplate?.conceptMetadata = null
        }
    }

    public void removeMedia(Media media) {
        if (getMedias().remove(media)) {
            media?.conceptMetadata = null
        }
    }


    void setUsage(Usage usage) {
        if (this.usage != null) {
            this.usage.conceptMetadata = null
        }
        this.usage = usage;
        usage?.conceptMetadata = this
    }

    @Override
    String toString() {
        return EntitySupportCategory.basicToString(this, [])
    }

    boolean equals(that) {

        def isEqual = true

        if (this.is(that)) {
            // Do nothing isEqual is already true
            //isEqual = true
        }
        else if (!that || this.getClass() != that.getClass()) {
            isEqual = false
        }
        else {

            /*
             * Check ID. If they are both null use concept id
             */
            if(this.id ? !this.id.equals(that.id) : that.id != null) {
                isEqual = false
            }

            if (isEqual &&
                    (this.concept?.id ? !this.concept.id.equals(that.concept?.id) : that.concept.id != null)) {
                isEqual = false
            }
        }

        return isEqual

    }

    int hashCode() {
        int result

        /*
         * Use id has hash. If it's null use the concept id hash instead
         */
        if (id) {
            result = 3 * id
        }
        else  {
            result = concept?.hashCode() ?: 0
        }

        return result

    }


}
