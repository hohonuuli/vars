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
import vars.knowledgebase.ISectionInfo
import vars.knowledgebase.IMedia
import vars.knowledgebase.ILinkTemplate
import vars.knowledgebase.ILinkRealization
import vars.knowledgebase.IConcept
import vars.knowledgebase.IConceptDelegate
import vars.jpa.JPAEntity;

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
@Entity(name = "ConceptDelegate")
@Table(name = "ConceptDelegate")
@NamedQueries( value = [
    @NamedQuery(name = "ConceptDelegate.findById",
                query = "SELECT v FROM ConceptDelegate v WHERE v.id = :id")
])
class ConceptDelegate implements Serializable, IConceptDelegate, JPAEntity {

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

    @OneToOne(mappedBy = "conceptDelegate", fetch = FetchType.EAGER, cascade = CascadeType.ALL, targetEntity = Usage.class)
    IUsage usage

    @OneToMany(targetEntity = History.class,
            mappedBy = "conceptDelegate",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    List<IHistory> histories

    @OneToMany(targetEntity = SectionInfo.class,
            mappedBy = "conceptDelegate",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    Set<ISectionInfo> sectionInfos

    @OneToMany(targetEntity = Media.class,
            mappedBy = "conceptDelegate",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    Set<IMedia> medias

    @OneToMany(targetEntity = LinkTemplate.class,
            mappedBy = "conceptDelegate",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    Set<ILinkTemplate> linkTemplates

    @OneToMany(targetEntity = LinkRealization.class,
            mappedBy = "conceptDelegate",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    Set<ILinkRealization> linkRealizations

    @OneToOne(targetEntity = Concept.class)
    @JoinColumn(name = "ConceptID_FK")
    IConcept concept

    boolean hasPrimaryImage() {
        return false;  // TODO implement this method.
    }

    IMedia getPrimaryImage() {
        return null;  // TODO implement this method.
    }

    boolean addHistory(IHistory history) {
        return false;  // TODO implement this method.
    }

    boolean addLinkRealization(ILinkRealization linkRealization) {
        return false;  // TODO implement this method.
    }

    boolean addLinkTemplate(ILinkTemplate linkTemplate) {
        return false;  // TODO implement this method.
    }

    boolean addMedia(IMedia media) {
        return false;  // TODO implement this method.
    }

    boolean addSectionInfo(ISectionInfo sectionInfo) {
        return false;  // TODO implement this method.
    }

    boolean removeHistory(IHistory history) {
        return false;  // TODO implement this method.
    }

    boolean removeLinkRealization(ILinkRealization linkRealization) {
        return false;  // TODO implement this method.
    }

    boolean removeLinkTemplate(ILinkTemplate linkTemplate) {
        return false;  // TODO implement this method.
    }

    boolean removeMedia(IMedia media) {
        return false;  // TODO implement this method.
    }

    boolean removeSectionInfo(ISectionInfo sectionInfo) {
        return false;  // TODO implement this method.
    }
}
