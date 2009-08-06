package vars.annotation.jpa

import java.sql.Timestamp
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.NamedQueries
import javax.persistence.NamedQuery
import javax.persistence.OneToOne
import javax.persistence.Table
import javax.persistence.TableGenerator
import javax.persistence.Version
import vars.annotation.jpa.VideoFrame
import vars.annotation.IPhysicalData
import vars.annotation.IVideoFrame

@Entity(name = "PhysicalData")
@Table(name = "PhysicalData")
@NamedQueries( value = [
    @NamedQuery(name = "PhysicalData.findById",
                query = "SELECT v FROM PhysicalData v WHERE v.id = :id")
])
class PhysicalData implements Serializable, IPhysicalData {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "PhysicalData_Gen")
    @TableGenerator(name = "PhysicalData_Gen", table = "UniqueID",
            pkColumnName = "TableName", valueColumnName = "NextID",
            pkColumnValue = "PhysicalData", allocationSize = 1)
    Long id

    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime

    @OneToOne(targetEntity = VideoFrame.class)
    @JoinColumn(name = "VideoFrameID_FK")
    IVideoFrame videoFrame
    
    Float depth
    Float temperature
    Float salinity
    Float oxygen
    Float light
    Float latitude
    Float longitude

    boolean containsData() {
        return (depth || temperature || salinity || oxygen || light || latitude || longitude);
    }

    
    
}