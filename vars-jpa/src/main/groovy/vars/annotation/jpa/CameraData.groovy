package vars.annotation.jpa

import javax.persistence.Id
import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.NamedQueries
import javax.persistence.NamedQuery
import javax.persistence.Version
import javax.persistence.OneToOne
import javax.persistence.JoinColumn
import javax.persistence.Temporal
import javax.persistence.TemporalType
import java.sql.Timestamp
import javax.persistence.TableGenerator
import vars.annotation.ICameraData
import vars.annotation.IVideoFrame
import vars.jpa.JPAEntity
import vars.EntityToStringCategory
import javax.persistence.EntityListeners
import org.mbari.jpax.TransactionLogger
import vars.jpa.KeyNullifier

@Entity(name = "CameraData")
@Table(name = "CameraData")
@EntityListeners( value = [TransactionLogger.class, KeyNullifier.class] )
@NamedQueries( value = [
    @NamedQuery(name = "CameraData.findById",
                query = "SELECT v FROM CameraData v WHERE v.id = :id"),
    @NamedQuery(name = "CameraData.findByName",
                query = "SELECT v FROM CameraData v WHERE v.name = :name"),
    @NamedQuery(name = "CameraData.findByDirection",
                query = "SELECT c FROM CameraData c WHERE c.direction = :direction"),  
    @NamedQuery(name = "CameraData.findByFrameGrabURL",
                query = "SELECT c FROM CameraData c WHERE c.frameGrabURL = :frameGrabURL"),
    @NamedQuery(name = "CameraData.findByFrameGrabURLLike",
                query = "SELECT c FROM CameraData c WHERE c.frameGrabURL LIKE :frameGrabURL")
])
class CameraData implements Serializable, ICameraData, JPAEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "CameraData_Gen")
    @TableGenerator(name = "CameraData_Gen", table = "UniqueID",
            pkColumnName = "TableName", valueColumnName = "NextID",
            pkColumnValue = "CameraData", allocationSize = 1)
    Long id

    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime

    @OneToOne(targetEntity = VideoFrame.class)
    @JoinColumn(name = "VideoFrameID_FK")
    IVideoFrame videoFrame

    @Column(name = "Name", length = 50)
    String name

    @Column(name = "Direction", length = 50)
    String direction

    Integer zoom
    Integer focus
    Integer iris
    Double fieldWidth

    @Column(name = "StillImageUrl", length = 1024)
    String frameGrabURL

    @Column(name = "LogDTG")
    @Temporal(value = TemporalType.TIMESTAMP)
    Date logDate

    boolean containsData() {
        return (name || direction || zoon || focus || iris || fieldWidth || stillImage);
    }

    @Override
    String toString() {
        return EntityToStringCategory.basicToString(this, [PROP_DIRECTION, PROP_NAME, PROP_FRAME_GRAB_URL])
    }


}