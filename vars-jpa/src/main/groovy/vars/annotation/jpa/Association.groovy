package vars.annotation.jpa

import javax.persistence.Id
import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Version
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import java.sql.Timestamp
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.NamedQueries
import javax.persistence.NamedQuery
import groovy.beans.Bindable
import javax.persistence.TableGenerator
import vars.LinkCategory
import vars.annotation.IAssociation
import vars.annotation.IObservation


@Entity(name = "Association")
@Table(name = "Association")
@NamedQueries( value = [
    @NamedQuery(name = "Association.findById",
                query = "SELECT v FROM Association v WHERE v.id = :id"),
    @NamedQuery(name = "Association.findByLinkName",
                query = "SELECT v FROM Association v WHERE v.linkName = :linkName"),
    @NamedQuery(name = "Association.findByToConcept",
                query = "SELECT a FROM Association a WHERE a.toConcept = :toConcept") ,
    @NamedQuery(name = "Association.findByLinkValue",
                query = "SELECT a FROM Association a WHERE a.linkValue = :linkValue")
])
class Association implements Serializable, IAssociation {

    @Id
    @Column(name = "id", nullable = false, updatable=false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Association_Gen")
    @TableGenerator(name = "Association_Gen", table = "UniqueID",
            pkColumnName = "TableName", valueColumnName = "NextID",
            pkColumnValue = "Association", allocationSize = 1)
    Long id

    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime
    
    @ManyToOne(optional = false, targetEntity = Observation.class)
    @JoinColumn(name = "ObservationID_FK")
    @Bindable
    IObservation observation

    @Column(name = "LinkName", nullable = false, length = 50)
    @Bindable
    String linkName

    @Column(name = "ToConcept", nullable = false, length = 50)
    @Bindable
    String toConcept

    @Column(name = "LinkValue", nullable = false, length = 100)
    @Bindable
    String linkValue

    String getFromConcept() {
        return observation?.getConceptName()
    }

    String stringValue() {
        use (LinkCategory) {
            return formatLinkAsString()
        }
    }

}