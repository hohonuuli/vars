/*
 * @(#)ConceptMetadataImpl.java   2009.11.10 at 10:06:20 PST
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
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;
import vars.jpa.JPAEntity;
import vars.jpa.KeyNullifier;
import vars.jpa.TransactionLogger;
import vars.knowledgebase.*;

/**
 * <pre>
 * CREATE TABLE CONCEPTDELEGATE (
 *   ID                 BIGINT NOT NULL,
 *   CONCEPTID_FK       BIGINT,
 *   USAGEID_FK         BIGINT,
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

    @OneToOne(optional = false, targetEntity = ConceptImpl.class, cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "ConceptID_FK", nullable = false)
    private Concept concept;

    @OneToMany(
        targetEntity = HistoryImpl.class,
        mappedBy = "conceptMetadata",
        fetch = FetchType.EAGER,
        cascade = {CascadeType.ALL}
    )
    @OrderBy(value = "creationDate")
    private List<History> histories;

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
        targetEntity = LinkRealizationImpl.class,
        mappedBy = "conceptMetadata",
        fetch = FetchType.EAGER,
        cascade = CascadeType.ALL
    )
    private List<LinkRealization> linkRealizations;

    @OneToMany(
        targetEntity = LinkTemplateImpl.class,
        mappedBy = "conceptMetadata",
        fetch = FetchType.EAGER,
        cascade = CascadeType.ALL
    )
    private List<LinkTemplate> linkTemplates;


    @OneToMany(
        targetEntity = MediaImpl.class,
        mappedBy = "conceptMetadata",
        fetch = FetchType.EAGER,
        cascade = CascadeType.ALL
    )
    private List<Media> medias;

    @OneToMany(
        targetEntity = ArtifactImpl.class,
        mappedBy = "conceptMetadata",
        fetch = FetchType.EAGER,
        cascade = CascadeType.ALL
    )
    private List<Artifact> artifacts;

    /** Optimistic lock to prevent concurrent overwrites */
    @SuppressWarnings("unused")
	@Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime;
    
    @OneToOne(
        mappedBy = "conceptMetadata",
        fetch = FetchType.EAGER,
        cascade = CascadeType.ALL,
        targetEntity = UsageImpl.class
    )
    private Usage usage;

    public void addArtifact(Artifact artifact) {
        if (getArtifacts().add(artifact)) {
            ((ArtifactImpl) artifact).setConceptMetadata(this);
        }
    }

    public void addHistory(History history) {
        if (getHistories().add(history)) {
            ((HistoryImpl) history).setConceptMetadata(this);
        }
    }

    public void addLinkRealization(LinkRealization linkRealization) {
        if (getLinkRealizations().add(linkRealization)) {
            ((LinkRealizationImpl) linkRealization).setConceptMetadata(this);
        }
    }

    public void addLinkTemplate(LinkTemplate linkTemplate) {
        if (getLinkTemplates().add(linkTemplate)) {
            ((LinkTemplateImpl) linkTemplate).setConceptMetadata(this);
        }
    }

    public void addMedia(Media media) {
        if (getMedias().add(media)) {
            ((MediaImpl) media).setConceptMetadata(this);
        }
    }


    public Concept getConcept() {
        return concept;
    }

    public Collection<Artifact> getArtifacts() {
        if (artifacts == null) {
            artifacts = new ArrayList<Artifact>();
        }
        return artifacts;
    }

    public Collection<History> getHistories() {
        if (histories == null) {
            histories = new ArrayList<History>();
        }
 

        return histories;
    }

    public Long getId() {
        return id;
    }

    public Collection<LinkRealization> getLinkRealizations() {
        if (linkRealizations == null) {
            linkRealizations = new ArrayList<LinkRealization>();
        }

        return linkRealizations;
    }

    public Collection<LinkTemplate> getLinkTemplates() {
        if (linkTemplates == null) {
            linkTemplates = new ArrayList<LinkTemplate>();
        }

        return linkTemplates;
    }

    public Collection<Media> getMedias() {
        if (medias == null) {
            medias = new ArrayList<Media>();
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

    public Media getPrimaryMedia(MediaTypes mediaType) {
        Media primaryMedia = null;
        Set<Media> ms = new HashSet<Media>(getMedias());
        for (Media media : ms) {
            if (media.isPrimary() && media.getType().equals(mediaType.toString())) {
                primaryMedia = media;
            }
        }

        return primaryMedia;
    }

    public Usage getUsage() {
        return usage;
    }
    
    public Object getPrimaryKey() {
    	return getId();
    }

    public boolean hasPrimaryImage() {
        return (getPrimaryImage() != null);
    }



    public boolean isPendingApproval() {
        boolean isPending = false;
        for (History history : getHistories()) {
            if (!history.isApproved() && !history.isRejected()) {
                isPending = true;

                break;
            }
        }

        return isPending;
    }

    public void removeArtifact(Artifact artifact) {
        if (getArtifacts().remove(artifact)) {
            ((ArtifactImpl) artifact).setConceptMetadata(null);
        }
    }

    public void removeHistory(History history) {
        if (getHistories().remove(history)) {
            ((HistoryImpl) history).setConceptMetadata(null);
        }
    }

    public void removeLinkRealization(LinkRealization linkRealization) {
        if (getLinkRealizations().remove(linkRealization)) {
            ((LinkRealizationImpl) linkRealization).setConceptMetadata(null);
        }
    }

    public void removeLinkTemplate(LinkTemplate linkTemplate) {
        if (getLinkTemplates().remove(linkTemplate)) {
            ((LinkTemplateImpl) linkTemplate).setConceptMetadata(null);
        }
    }

    public void removeMedia(Media media) {
        if (getMedias().remove(media)) {
            ((MediaImpl) media).setConceptMetadata(null);
        }
    }

    protected void setConcept(Concept concept) {
        this.concept = concept;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsage(Usage usage) {
        if (this.usage != null) {
            UsageImpl thisUsage = (UsageImpl) this.usage;
            thisUsage.setConceptMetadata(null);
        }

        this.usage = usage;

        if (usage != null) {
            ((UsageImpl) usage).setConceptMetadata(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConceptMetadataImpl other = (ConceptMetadataImpl) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

	@Override
	public String toString() {
		return "ConceptMetadataImpl ([id=" + id + "] updatedTime=" + updatedTime
				+ ")";
	}
    
    

    

}
