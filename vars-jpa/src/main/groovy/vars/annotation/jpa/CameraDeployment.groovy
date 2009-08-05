package vars.annotation.jpa

import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.NamedQueries
import javax.persistence.NamedQuery
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.ManyToOne
import javax.persistence.Column
import javax.persistence.GenerationType
import javax.persistence.JoinColumn
import javax.persistence.Temporal
import javax.persistence.TemporalType
import javax.persistence.PrimaryKeyJoinColumn
import javax.persistence.Version
import java.sql.Timestamp
import javax.persistence.TableGenerator
import vars.annotation.ICameraDeployment
import vars.annotation.IVideoArchiveSet

@Entity(name = "CameraDeployment")
@Table(name = "CameraPlatformDeployment")
@NamedQueries( value = [
    @NamedQuery(name = "CameraDeployment.findById",
                query = "SELECT v FROM CameraDeployment v WHERE v.id = :id"),
    @NamedQuery(name = "CameraDeployment.findBySequenceNumber",
                query = "SELECT v FROM CameraDeployment v WHERE v.sequenceNumber = :sequenceNumber"),
    @NamedQuery(name = "CameraDeployment.findByChiefScientist",
                query = "SELECT v FROM CameraDeployment v WHERE v.chiefScientist = :chiefScientist"),
    @NamedQuery(name = "CameraDeployment.findByStartDate",
                query = "SELECT v FROM CameraDeployment v WHERE v.startDate = :startDate"),
    @NamedQuery(name = "CameraDeployment.findByEndDate",
                query = "SELECT v FROM CameraDeployment v WHERE v.endDate = :endDate")
])
class CameraDeployment implements Serializable {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "CameraPlatformDeployment_Gen")
    @TableGenerator(name = "CameraPlatformDeployment_Gen", table = "UniqueID",
            pkColumnName = "TableName", valueColumnName = "NextID",
            pkColumnValue = "CameraPlatformDeployment", allocationSize = 1)
    Long id

    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime

    @ManyToOne(optional = false)
    @JoinColumn(name = "VideoArchiveSetID_FK")
    private VideoArchiveSet videoArchiveSet

    @Column(name = "SeqNumber")
    Integer sequenceNumber

    @Column(name = "ChiefScientist", length = 50)
    String chiefScientistName

    @Column(name = "UsageStartDTG")
    @Temporal(value = TemporalType.TIMESTAMP)
    Date startDate

    @Column(name = "UsageEndDTG")
    @Temporal(value = TemporalType.TIMESTAMP)
    Date endDate

    void setVideoArchiveSet(IVideoArchiveSet videoArchiveSet) {
        this.videoArchiveSet = videoArchiveSet
    }

    public IVideoArchiveSet getVideoArchiveSet() {
        return videoArchiveSet
    }
}