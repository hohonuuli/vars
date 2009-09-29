/*
 * @(#)ConceptMetadataImpl.java   2009.09.28 at 09:18:07 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.knowledgebase.jpa;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import javax.persistence.*;
import org.mbari.jpaxx.TransactionLogger;
import vars.EntitySupportCategory;
import vars.jpa.JPAEntity;
import vars.jpa.KeyNullifier;
import vars.knowledgebase.*;

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
@EntityListeners({ TransactionLogger.class, KeyNullifier.class })
@NamedQueries({ @NamedQuery(name = "ConceptMetadata.findById",
                            query = "SELECT v FROM ConceptMetadata v WHERE v.id = :id") })
public class ConceptMetadataImpl implements Serializable, ConceptMetadata, JPAEntity {

    @OneToOne(targetEntity = ConceptImpl.class)
    @JoinColumn(name = "ConceptID_FK")
    private Concept concept;

    @OneToMany(
        targetEntity = GHistory.class,
        mappedBy = "conceptMetadata",
        fetch = FetchType.EAGER,
        cascade = CascadeType.ALL
    )
    @OrderBy(value = "creationDate")
    private Set<History> histories;

    @Id
    @Column(
        name = "id",
        nullable = false,
        updatable = false
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "ConceptDelegate_Gen")
    @TableGenerator(
        name = "ConceptDelegate_Gen",
        table = "UniqueID",
        pkColumnName = "TableName",
        valueColumnName = "NextID",
        pkColumnValue = "ConceptDelegate",
        allocationSize = 1
    )
    private Long id;

    @OneToMany(
        targetEntity = GLinkRealization.class,
        mappedBy = "conceptMetadata",
        fetch = FetchType.EAGER,
        cascade = CascadeType.ALL
    )
    private Set<LinkRealization> linkRealizations;

    @OneToMany(
        targetEntity = GLinkTemplate.class,
        mappedBy = "conceptMetadata",
        fetch = FetchType.EAGER,
        cascade = CascadeType.ALL
    )
    private Set<LinkTemplate> linkTemplates;

    @OneToMany(
        targetEntity = GMedia.class,
        mappedBy = "conceptMetadata",
        fetch = FetchType.EAGER,
        cascade = CascadeType.ALL
    )
    private Set<Media> medias;

    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime;
    
    @OneToOne(
        mappedBy = "conceptMetadata",
        fetch = FetchType.EAGER,
        cascade = CascadeType.ALL,
        targetEntity = GUsage.class
    )
    private Usage usage;

    public void addHistory(History history) {
        if (getHistories().add(history)) {
            ((GHistory) history).setConceptMetadata(this);
        }
    }

    public void addLinkRealization(LinkRealization linkRealization) {
        if (getLinkRealizations().add(linkRealization)) {
            ((GLinkRealization) linkRealization).setConceptMetadata(this);
        }
    }

    public void addLinkTemplate(LinkTemplate linkTemplate) {
        if (getLinkTemplates().add(linkTemplate)) {
            ((GLinkTemplate) linkTemplate).setConceptMetadata(this);
        }
    }

    public void addMedia(Media media) {
        if (getMedias().add(media)) {
            ((GMedia) media).setConceptMetadata(this);
        }
    }

    @Override
    public boolean equals(Object that) {

        boolean isEqual = true;

        if (this == that) {

            // Do nothing isEqual is already true
            //isEqual = true
        }
        else if ((that == null) || (this.getClass() != that.getClass())) {
            isEqual = false;
        }
        else {

            /*
             * Check ID. If they are both null use concept id
             */
            JPAEntity thatCm = (JPAEntity) that;
            if ((this.id != null) && (thatCm.getId() != null)) {
                isEqual = this.id.equals(thatCm.getId());
            }
            else {
                Concept thisConcept = getConcept();
                Concept thatConcept = ((ConceptMetadata) that).getConcept();
                if ((thisConcept == null) || (thatConcept == null)) {
                    isEqual = false;
                }
                else {
                    isEqual = thisConcept.hashCode() == thatConcept.hashCode();
                }
            }
        }

        return isEqual;

    }

    public Concept getConcept() {
        return concept;
    }

    public Set<History> getHistories() {
        if (histories == null) {
            histories = new TreeSet<History>(new HistoryCreationDateComparator());
        }

        return histories;
    }

    public Long getId() {
        return id;
    }

    public Set<LinkRealization> getLinkRealizations() {
        if (linkRealizations == null) {
            linkRealizations = new HashSet<LinkRealization>();
        }

        return linkRealizations;
    }

    public Set<LinkTemplate> getLinkTemplates() {
        if (linkTemplates == null) {
            linkTemplates = new HashSet<LinkTemplate>();
        }

        return linkTemplates;
    }

    public Set<Media> getMedias() {
        if (medias == null) {
            medias = new HashSet<Media>();
        }

        return medias;
    }

    public Media getPrimaryImage() {
        Media media = null;
        Collection<Media> m = new ArrayList<Media>(getMedias());
        for (Media media1 : m) {
            if (media1.isPrimary() && media1.getType().equalsIgnoreCase(MediaTypes.IMAGE.toString())) {
                media = media1;

                break;
            }
        }

        return media;
    }

    public Usage getUsage() {
        return usage;
    }

    public boolean hasPrimaryImage() {
        return (getPrimaryImage() != null);
    }

    @Override
    public int hashCode() {
        int result = 0;

        /*
         * Use id has hash. If it's null use the concept id hash instead
         */
        if (id != null) {
            result = 3 * id.intValue();
        }
        else {
            result = (concept == null) ? 0 : concept.hashCode();
        }

        return result;

    }

    public void removeHistory(History history) {
        if (getHistories().remove(history)) {
            ((GHistory) history).setConceptMetadata(null);
        }
    }

    public void removeLinkRealization(LinkRealization linkRealization) {
        if (getLinkRealizations().remove(linkRealization)) {
            ((GLinkRealization) linkRealization).setConceptMetadata(null);
        }
    }

    public void removeLinkTemplate(LinkTemplate linkTemplate) {
        if (getLinkTemplates().remove(linkTemplate)) {
            ((GLinkTemplate) linkTemplate).setConceptMetadata(this);
        }
    }

    public void removeMedia(Media media) {
        if (getMedias().remove(media)) {
            ((GMedia) media).setConceptMetadata(null);
        }
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsage(Usage usage) {
        if (this.usage != null) {
            GUsage thisUsage = (GUsage) this.usage;
            thisUsage.setConceptMetadata(null);
        }

        this.usage = usage;

        if (usage != null) {
            ((GUsage) usage).setConceptMetadata(this);
        }
    }

    @Override
    public String toString() {
        return EntitySupportCategory.basicToString(this, new ArrayList());
    }
}
