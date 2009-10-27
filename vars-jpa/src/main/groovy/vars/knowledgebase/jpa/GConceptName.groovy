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
import groovy.beans.Bindable
import vars.knowledgebase.Concept
import vars.knowledgebase.ConceptName
import vars.jpa.JPAEntity
import vars.EntitySupportCategory
import javax.persistence.EntityListeners;
import org.mbari.jpaxx.TransactionLogger
import vars.jpa.KeyNullifier
import vars.jpa.KeyNullifier
import javax.persistence.Transient

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
@EntityListeners( value = [TransactionLogger.class, KeyNullifier.class] )
@NamedQueries( value = [
    @NamedQuery(name = "ConceptName.findById",
                query = "SELECT v FROM ConceptName v WHERE v.id = :id"),
    @NamedQuery(name = "ConceptName.findByName",
                query = "SELECT c FROM ConceptName c WHERE c.name = :name"),
    @NamedQuery(name = "ConceptName.findByAuthor", query = "SELECT c FROM ConceptName c WHERE c.author = :author"),
    @NamedQuery(name = "ConceptName.findByNameType",
                query = "SELECT c FROM ConceptName c WHERE c.nameType = :nameType"),
    @NamedQuery(name = "ConceptName.findAll",
                query = "SELECT c FROM ConceptName c"),
    @NamedQuery(name = "ConceptName.findByNameLike",
                query = "SELECT c FROM ConceptName c WHERE c.name LIKE :name")
])
public class GConceptName implements Serializable, ConceptName, JPAEntity {

    @Transient
    private static final PROPS = Collections.unmodifiableList([ConceptName.PROP_NAME])

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

    @Column(name = "ConceptName", nullable = false, length = 64, unique = true)
    @Bindable
    String name

    @Column(name = "NameType", nullable = false, length = 10)
    @Bindable
    String nameType

    @Column(name = "Author", length = 255)
    @Bindable
    String author

    @ManyToOne(optional = false, targetEntity = ConceptImpl.class)
    @JoinColumn(name = "ConceptID_FK")
    Concept concept

    public String stringValue() {
        return name
    }

    @Override
    String toString() {
        return EntitySupportCategory.basicToString(this, [PROP_NAME, PROP_NAME_TYPE])
    }

    @Override
    boolean equals(that) {
        return EntitySupportCategory.equals(this, that, PROPS)
    }

    @Override
    int hashCode() {
        return EntitySupportCategory.hashCode(this, PROPS)
    }


}
