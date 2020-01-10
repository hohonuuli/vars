/*
 * @(#)ObservationImpl.java   2009.11.12 at 10:27:21 PST
 *
 * Copyright 2009 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.annotation.jpa;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

import vars.annotation.Association;
import vars.annotation.Observation;
import vars.annotation.VideoFrame;
import vars.jpa.JPAEntity;
import vars.jpa.KeyNullifier;
import vars.jpa.TransactionLogger;

/**
 * @author brian
 */

@Entity(name = "Observation")
@Table(name = "Observation",
        indexes = {@Index(name = "idx_Observation_FK1", columnList = "VideoFrameID_FK"),
                @Index(name = "idx_Observation_concept", columnList = "ConceptName"),
                @Index(name = "idx_Observation_LUT", columnList = "LAST_UPDATED_TIME")})
@Cacheable(false)
@EntityListeners({ TransactionLogger.class, KeyNullifier.class })
@NamedQueries( {

    @NamedQuery(name = "Observation.findById",
                query = "SELECT v FROM Observation v WHERE v.id = :id") ,
    @NamedQuery(name = "Observation.findByConceptName",
                query = "SELECT v FROM Observation v WHERE v.conceptName = :conceptName") ,
    @NamedQuery(name = "Observation.findByNotes",
                query = "SELECT o FROM Observation o WHERE o.notes = :notes") ,
    @NamedQuery(name = "Observation.findByObservationDate",
                query = "SELECT o FROM Observation o WHERE o.observationDate = :observationDate") ,
    @NamedQuery(name = "Observation.findByObserver",
                query = "SELECT o FROM Observation o WHERE o.observer = :observer")
//    @NamedQuery(name = "Observation.findAllByConceptNameAndAssociation",
//                query = "SELECT o FROM Observation, IN (o.associations) AS a WHERE a.linkName = :linkName AND a.linkValue = :linkValue AND o.conceptName = :conceptName")
//

})
public class ObservationImpl implements Serializable, Observation, JPAEntity {

    @Transient
    final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    @OneToMany(
        targetEntity = AssociationImpl.class,
        mappedBy = "observation",
        fetch = FetchType.EAGER,
        cascade = CascadeType.ALL
    )
    Set<Association> associations;
    
    @Column(
        name = "ConceptName",
        nullable = false,
        length = 50
    )
    String conceptName;
    
    @Id
    @Column(
        name = "id",
        nullable = false,
        updatable = false
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Observation_Gen")
    @TableGenerator(
        name = "Observation_Gen",
        table = "UniqueID",
        pkColumnName = "TableName",
        valueColumnName = "NextID",
        pkColumnValue = "Observation",
        allocationSize = 1
    )
    Long id;
    
    @Column(name = "Notes", length = 200)
    String notes;
    
    @Column(name = "ObservationDTG")
    @Temporal(value = TemporalType.TIMESTAMP)
    Date observationDate;
    
    @Column(name = "Observer", length = 50)
    String observer;

    /** Optimistic lock to prevent concurrent overwrites */
    @SuppressWarnings("unused")
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime;
    
    @ManyToOne(optional = false, targetEntity = VideoFrameImpl.class)
    @JoinColumn(name = "VideoFrameID_FK")
    VideoFrame videoFrame;
    
    Double x;
    Double y;

    /**
     *
     * @param association
     */
    public void addAssociation(Association association) {
        if (getAssociations().add(association)) {
            ((AssociationImpl) association).setObservation(this);
        }

        propertyChangeSupport.firePropertyChange(PROP_ASSOCIATIONS, null, associations);  
    }

    /**
     *
     * @param string
     * @param listener
     */
    public void addPropertyChangeListener(String string, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(string, listener);
    }

    /**
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final ObservationImpl other = (ObservationImpl) obj;
        return this.hashCode() == other.hashCode();
    }

    /**
     * @return
     */
    public Set<Association> getAssociations() {
        if (associations == null) {
            associations = new HashSet<Association>();
        }

        return associations;
    }

    /**
     * @return
     */
    public String getConceptName() {
        return conceptName;
    }

    /**
     * @return
     */
    public Long getId() {
        return id;
    }

    /**
     * @return
     */
    public String getNotes() {
        return notes;
    }

    /**
     * @return
     */
    public Date getObservationDate() {
        return observationDate;
    }

    /**
     * @return
     */
    public String getObserver() {
        return observer;
    }

    /**
     * @return
     */
    public PropertyChangeListener[] getPropertyChangeListeners() {
        return propertyChangeSupport.getPropertyChangeListeners();
    }

    /**
     *
     * @param string
     * @return
     */
    public PropertyChangeListener[] getPropertyChangeListeners(String string) {
        return propertyChangeSupport.getPropertyChangeListeners(string);
    }

    /**
     * @return
     */
    public VideoFrame getVideoFrame() {
        return videoFrame;
    }

    /**
     * @return
     */
    public Double getX() {
        return x;
    }

    /**
     * @return
     */
    public Double getY() {
        return y;
    }

    /**
     * @return
     */
    public boolean hasSample() {
        // TODO this contains a hard coded reference. Needs to be moved to properties file
        boolean b = false;
        for (Association association : new ArrayList<Association>(getAssociations())) {
            if ("sample-reference".equals(association.getLinkName()) || "sampled-by".equals(association.getLinkName())) {
                b = true;
                break;
            }
        }
        return b;
    }

    /**
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 3;

        // I had to use the ID as a hashcode. Due to the second precision of observationDate
        // it is not uncommon to have several different observations that generate the
        // same hashcode when using fields as the hash. This is VERY annoying as the
        // annotation interface doesn't know that a different line has been selected
        if (id != null) {
            hash = id.intValue();
        }
        else {
            hash = 59 * hash + ((this.observationDate != null) ? this.observationDate.hashCode() : 0);
            hash = 59 * hash + ((this.observer != null) ? this.observer.hashCode() : 0);
            hash = 59 * hash + ((this.conceptName != null) ? this.conceptName.hashCode() : 0);
            hash = 59 * hash + ((this.x != null) ? this.x.hashCode() : 0);
            hash = 59 * hash + ((this.y != null) ? this.y.hashCode() : 0);
        }

        return hash;
    }

    /**
     *
     * @param association
     */
    public void removeAssociation(Association association) {
        if (getAssociations().remove(association)) {
            ((AssociationImpl) association).setObservation(null);
            propertyChangeSupport.firePropertyChange(PROP_ASSOCIATIONS, null, associations);    // This method is added by @Bindable
        }
    }

    /**
     *
     * @param string
     * @param listener
     */
    public void removePropertyChangeListener(String string, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(string, listener);
    }

    /**
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     *
     * @param conceptName
     */
    public void setConceptName(String conceptName) {
        final String oldConceptName = this.conceptName;
        this.conceptName = conceptName;
        propertyChangeSupport.firePropertyChange(PROP_CONCEPT_NAME, oldConceptName, conceptName);
    }

    /**
     *
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     *
     * @param string
     */
    public void setNotes(String string) {
        final String oldNotes = this.notes;
        this.notes = string;
        propertyChangeSupport.firePropertyChange(PROP_NOTES, oldNotes, notes);
    }

    /**
     *
     * @param dtg
     */
    public void setObservationDate(Date dtg) {
        final Date oldDate = this.observationDate;
        this.observationDate = dtg;
        propertyChangeSupport.firePropertyChange(PROP_OBSERVATION_DATE, oldDate, observationDate);
    }

    /**
     *
     * @param observer
     */
    public void setObserver(String observer) {
        final String oldObserver = this.observer;
        this.observer = observer;
        propertyChangeSupport.firePropertyChange(PROP_OBSERVER, oldObserver, this.observer);
    }

    void setVideoFrame(VideoFrame videoFrame) {
        this.videoFrame = videoFrame;
    }

    /**
     *
     * @param x
     */
    public void setX(Double x) {
        final Double oldX = this.x;
        this.x = x;
        propertyChangeSupport.firePropertyChange(PROP_X, oldX, this.x);
    }

    /**
     *
     * @param y
     */
    public void setY(Double y) {
        final Double oldY = this.y;
        this.y = y;
        propertyChangeSupport.firePropertyChange(PROP_Y, oldY, this.y);
    }

    /**
     * @return
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());

        sb.append(" ([id=").append(getId()).append("] conceptName=");
        sb.append(conceptName).append(", observer=").append(observer);
        sb.append(", observationDate=").append(observationDate).append(")");

        return sb.toString();
    }
    
    public Object getPrimaryKey() {
    	return getId();
    }


}
