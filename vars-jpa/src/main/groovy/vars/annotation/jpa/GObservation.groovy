package vars.annotation.jpa

import javax.persistence.Id
import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.NamedQueries
import javax.persistence.NamedQuery
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import javax.persistence.Temporal
import javax.persistence.TemporalType
import javax.persistence.OneToMany
import javax.persistence.FetchType
import javax.persistence.CascadeType
import javax.persistence.Version
import java.sql.Timestamp
import groovy.beans.Bindable
import javax.persistence.TableGenerator
import vars.annotation.Observation
import vars.annotation.Association
import vars.annotation.VideoFrame
import vars.jpa.JPAEntity
import vars.EntitySupportCategory
import javax.persistence.EntityListeners
import org.mbari.jpaxx.TransactionLogger
import vars.jpa.KeyNullifier
import vars.jpa.KeyNullifier
import vars.EntitySupportCategory
import javax.persistence.Transient
import vars.annotation.VideoFrame
import vars.annotation.Association
import vars.annotation.Observation

@Entity(name = "Observation")
@Table(name = "Observation")
@EntityListeners( value = [TransactionLogger.class, KeyNullifier.class] )
@NamedQueries( value = [
    @NamedQuery(name = "Observation.findById",
                query = "SELECT v FROM Observation v WHERE v.id = :id"),
    @NamedQuery(name = "Observation.findByConceptName",
                query = "SELECT v FROM Observation v WHERE v.conceptName = :conceptName"),
    @NamedQuery(name = "Observation.findByNotes", query = "SELECT o FROM Observation o WHERE o.notes = :notes"),
    @NamedQuery(name = "Observation.findByObservationDate",
                query = "SELECT o FROM Observation o WHERE o.observationDate = :observationDate") ,
    @NamedQuery(name = "Observation.findByObserver",
                query = "SELECT o FROM Observation o WHERE o.observer = :observer")
])
class GObservation implements Serializable, Observation, JPAEntity {

    @Transient
    private static final PROPS = Collections.unmodifiableList([Observation.PROP_CONCEPT_NAME,
            Observation.PROP_OBSERVATION_DATE, Observation.PROP_OBSERVER])

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Observation_Gen")
    @TableGenerator(name = "Observation_Gen", table = "UniqueID",
            pkColumnName = "TableName", valueColumnName = "NextID",
            pkColumnValue = "Observation", allocationSize = 1)
    Long id

    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime

    @ManyToOne(optional = false, targetEntity = GVideoFrame.class)
    @JoinColumn(name = "VideoFrameID_FK")
    @Bindable
    VideoFrame videoFrame

    @Column(name = "ObservationDTG")
    @Temporal(value = TemporalType.TIMESTAMP)
    @Bindable
    Date observationDate

    @Column(name = "Observer", length = 50)
    @Bindable
    String observer

    @Column(name = "ConceptName", nullable = false, length = 50)
    @Bindable
    String conceptName

    @Column(name = "Notes", length = 200)
    @Bindable
    String notes

    @OneToMany(targetEntity = GAssociation.class,
            mappedBy = "observation",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    Set<Association> associations

    Set<Association> getAssociations() {
        if (associations == null) {
            associations = new HashSet<Association>();
        }
        return associations
    }

    void addAssociation(Association association) {
        getAssociations() << association
        ((GAssociation) association).observation = this
        firePropertyChange(PROP_ASSOCIATIONS, null, associations) // This method is added by @Bindable
    }

    void removeAssociation(Association association) {
        if (getAssociations().remove(association)) {
            ((GAssociation) association).observation = null
            firePropertyChange(PROP_ASSOCIATIONS, null, associations) // This method is added by @Bindable
        }
    }

    public boolean hasSample() {
        return false;  // TODO implement this.
    }

    String toString() {
        return EntitySupportCategory.basicToString(this, PROPS)
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