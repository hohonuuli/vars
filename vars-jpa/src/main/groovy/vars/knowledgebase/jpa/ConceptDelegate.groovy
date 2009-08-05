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
import javax.persistence.CascadeType;

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
class ConceptDelegate implements Serializable {

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

    @OneToOne(mappedBy = "conceptDelegate", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Usage usage

    @OneToMany(targetEntity = History.class,
            mappedBy = "conceptDelegate",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    List<History> histories

    @OneToMany(targetEntity = SectionInfo.class,
            mappedBy = "conceptDelegate",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    Set<SectionInfo> sectionInfos

    @OneToMany(targetEntity = Media.class,
            mappedBy = "conceptDelegate",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    Set<Media> medias

    @OneToMany(targetEntity = LinkTemplate.class,
            mappedBy = "conceptDelegate",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    Set<LinkTemplate> linkTemplates

    @OneToMany(targetEntity = LinkRealization.class,
            mappedBy = "conceptDelegate",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    Set<LinkRealization> linkRealizations

    @OneToOne
    @JoinColumn(name = "ConceptID_FK")
    Concept concept
}
