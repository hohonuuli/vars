/*
 * @(#)AssociationImpl.java   2009.11.10 at 12:28:19 PST
 *
 * Copyright 2009 MBARI
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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.Version;
import vars.LinkUtilities;
import vars.annotation.Association;
import vars.annotation.Observation;
import vars.jpa.JPAEntity;
import vars.jpa.KeyNullifier;
import vars.jpa.TransactionLogger;

/**
 *
 *
 * @version        $date$, 2009.11.10 at 12:28:19 PST
 * @author         Brian Schlining [brian@mbari.org]
 */
@Entity(name = "Association")
@Table(name = "Association")
@EntityListeners({ TransactionLogger.class, KeyNullifier.class })
@NamedQueries( {

    @NamedQuery(name = "Association.findById", query = "SELECT v FROM Association v WHERE v.id = :id") ,
    @NamedQuery(name = "Association.findByLinkName",
                query = "SELECT v FROM Association v WHERE v.linkName = :linkName") ,
    @NamedQuery(name = "Association.findByToConcept",
                query = "SELECT a FROM Association a WHERE a.toConcept = :toConcept") ,
    @NamedQuery(name = "Association.findByLinkValue",
                query = "SELECT a FROM Association a WHERE a.linkValue = :linkValue"),
    @NamedQuery(name = "Association.findByConceptNameAndLinkFields",
                query = "SELECT a FROM Association a WHERE a.observation.conceptName = :conceptName AND a.linkName = :linkName AND a.toConcept = :toConcept AND a.linkValue = :linkValue")

})
public class AssociationImpl implements Serializable, Association, JPAEntity {
    
    @Transient
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    @Id
    @Column(
        name = "id",
        nullable = false,
        updatable = false
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Association_Gen")
    @TableGenerator(
        name = "Association_Gen",
        table = "UniqueID",
        pkColumnName = "TableName",
        valueColumnName = "NextID",
        pkColumnValue = "Association",
        allocationSize = 1
    )
    Long id;

    @Column(
        name = "LinkName",
        nullable = false,
        length = 128
    )
    String linkName;

    @Column(
        name = "LinkValue",
        nullable = false,
        length = 1024
    )
    String linkValue;

    @ManyToOne(optional = false, targetEntity = ObservationImpl.class)
    @JoinColumn(name = "ObservationID_FK")
    Observation observation;

    @Column(
        name = "ToConcept",
        nullable = false,
        length = 128
    )
    String toConcept;

    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime;

    /**
     * Constructs ...
     */
    public AssociationImpl() {

        // Default constructor
    }

    /**
     * Constructs ...
     *
     * @param linkName
     * @param toConcept
     * @param linkValue
     */
    public AssociationImpl(String linkName, String toConcept, String linkValue) {
        this.linkName = linkName;
        this.toConcept = toConcept;
        this.linkValue = linkValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final AssociationImpl other = (AssociationImpl) obj;
        if ((this.linkName == null) ? (other.linkName != null) : !this.linkName.equals(other.linkName)) {
            return false;
        }

        if ((this.toConcept == null) ? (other.toConcept != null) : !this.toConcept.equals(other.toConcept)) {
            return false;
        }

        if ((this.linkValue == null) ? (other.linkValue != null) : !this.linkValue.equals(other.linkValue)) {
            return false;
        }

        return true;
    }

    public String getFromConcept() {
        return (observation == null) ? null : observation.getConceptName();
    }

    public Long getId() {
        return id;
    }

    public String getLinkName() {
        return linkName;
    }

    public String getLinkValue() {
        return linkValue;
    }

    public Observation getObservation() {
        return observation;
    }

    public String getToConcept() {
        return toConcept;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + ((this.linkName != null) ? this.linkName.hashCode() : 0);
        hash = 29 * hash + ((this.toConcept != null) ? this.toConcept.hashCode() : 0);
        hash = 29 * hash + ((this.linkValue != null) ? this.linkValue.hashCode() : 0);

        return hash;
    }
    
    public void setId(Long id) {
    	this.id = id;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public void setLinkValue(String linkValue) {
        this.linkValue = linkValue;
    }

    void setObservation(Observation observation) {
        this.observation = observation;
    }

    public void setToConcept(String toConcept) {
        this.toConcept = toConcept;
    }

    public String stringValue() {
        return LinkUtilities.formatAsString(this);
    }

    @Override
    public String toString() {
        return stringValue();
    }

    public void addPropertyChangeListener(String string, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(string, listener);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return propertyChangeSupport.getPropertyChangeListeners();
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String string) {
        return propertyChangeSupport.getPropertyChangeListeners(string);
    }

    public void removePropertyChangeListener(String string, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(string, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    
    public Object getPrimaryKey() {
    	return getId();
    }
}
