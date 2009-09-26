package vars.knowledgebase.jpa;

import vars.knowledgebase.*;
import vars.jpa.KeyNullifier;
import vars.jpa.JPAEntity;

import javax.persistence.*;

import org.mbari.jpaxx.TransactionLogger;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;
import java.util.TreeSet;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Sep 25, 2009
 * Time: 8:57:31 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "ConceptMetadata")
@Table(name = "ConceptDelegate")
@EntityListeners( {TransactionLogger.class, KeyNullifier.class })
@NamedQueries( {
    @NamedQuery(name = "ConceptMetadata.findById",
                query = "SELECT v FROM ConceptMetadata v WHERE v.id = :id")
})
public class ConceptMetadataImpl implements Serializable, ConceptMetadata, JPAEntity {

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
    private Timestamp updatedTime;

    @OneToOne(mappedBy = "conceptMetadata", fetch = FetchType.EAGER, cascade = CascadeType.ALL, targetEntity = GUsage.class)
    private Usage usage;

    @OneToMany(targetEntity = GHistory.class,
            mappedBy = "conceptMetadata",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    @OrderBy(value = "creationDate")
    private Set<History> histories;

    @OneToMany(targetEntity = GMedia.class,
            mappedBy = "conceptMetadata",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    private Set<Media> medias;

    @OneToMany(targetEntity = GLinkTemplate.class,
            mappedBy = "conceptMetadata",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    private Set<LinkTemplate> linkTemplates;

    @OneToMany(targetEntity = GLinkRealization.class,
            mappedBy = "conceptMetadata",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    private Set<LinkRealization> linkRealizations;

    @OneToOne(targetEntity = ConceptImpl.class)
    @JoinColumn(name = "ConceptID_FK")
    private Concept concept;

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

