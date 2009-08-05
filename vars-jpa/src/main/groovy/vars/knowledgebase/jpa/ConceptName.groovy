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
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import groovy.beans.Bindable;

/**
 * <pre>
 * CREATE TABLE CONCEPTNAME (
 *   CONCEPTNAME 	VARCHAR(50) NOT NULL,
 *   CONCEPTID_FK	BIGINT,
 *   AUTHOR      	VARCHAR(255),
 *   NAMETYPE    	VARCHAR(10),
 *   ID          	BIGINT NOT NULL,
 *   CONSTRAINT PK_CONCEPTNAME PRIMARY KEY(ID)
 * )
 * GO
 * CREATE INDEX IDX_CONCEPTNAME_CONCEPTID
 *   ON CONCEPTNAME(CONCEPTID_FK)
 * GO
 * CREATE INDEX IDX_CONCEPTNAME2
 *   ON CONCEPTNAME(CONCEPTNAME)
 * GO
 * </pre>
 */
@Entity(name = "ConceptName")
@Table(name = "ConceptName")
@NamedQueries( value = [
    @NamedQuery(name = "ConceptName.findById",
                query = "SELECT v FROM ConceptName v WHERE v.id = :id"),
    @NamedQuery(name = "ConceptName.findByConceptName",
                query = "SELECT c FROM ConceptName c WHERE c.conceptName = :conceptName"),
    @NamedQuery(name = "ConceptName.findByAuthor", query = "SELECT c FROM ConceptName c WHERE c.author = :author"),
    @NamedQuery(name = "ConceptName.findByNameType",
                query = "SELECT c FROM ConceptName c WHERE c.nameType = :nameType")
])
public class ConceptName implements Serializable {

    @Id
    @Column(name = "id", nullable = false, updatable=false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "ConceptName_Gen")
    @TableGenerator(name = "ConceptName_Gen", table = "UniqueID",
            pkColumnName = "TableName", valueColumnName = "NextID",
            pkColumnValue = "ConceptName", allocationSize = 1)
    Long id

    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime

    @Column(name = "ConceptName", nullable = false, length = 50)
    @Bindable
    String name

    @Column(name = "NameType", nullable = false, length = 10)
    @Bindable
    String nameType

    @Column(name = "Author", length = 255)
    @Bindable
    String author

    @ManyToOne(optional = false)
    @JoinColumn(name = "ConceptID_FK")
    Concept concept
}
