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
import vars.annotation.IObservation
import vars.annotation.IAssociation
import vars.annotation.IVideoFrame
import vars.jpa.JPAEntity

@Entity(name = "Observation")
@Table(name = "Observation")
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
class Observation implements Serializable, IObservation, JPAEntity {

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

    @ManyToOne(optional = false, targetEntity = VideoFrame.class)
    @JoinColumn(name = "VideoArchiveSetID_FK")
    @Bindable
    IVideoFrame videoFrame

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

    @OneToMany(targetEntity = Association.class,
            mappedBy = "observation",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    Set<IAssociation> associations

    Set<IAssociation> getAssociations() {
        if (associations == null) {
            associations = new HashSet<Association>();
        }
        return associations
    }

    void addAssociation(IAssociation association) {
        getAssociations() << association
        ((Association) association).observation = this
        firePropertyChange(PROP_ASSOCIATIONS, null, associations) // This method is added by @Bindable
    }

    void removeAssociation(IAssociation association) {
        if (getAssociations().remove(association)) {
            ((Association) association).observation = null
            firePropertyChange(PROP_ASSOCIATIONS, null, associations) // This method is added by @Bindable
        }
    }

    public boolean hasSample() {
        return false;  // TODO implement this.
    }


}