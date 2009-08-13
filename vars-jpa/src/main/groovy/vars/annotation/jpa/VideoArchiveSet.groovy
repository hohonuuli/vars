package vars.annotation.jpa;

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.NamedQueries
import javax.persistence.NamedQuery
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.persistence.Temporal
import javax.persistence.TemporalType
import javax.persistence.CascadeType
import javax.persistence.Version
import java.sql.Timestamp
import javax.persistence.OrderBy
import javax.persistence.TableGenerator
import vars.annotation.IVideoArchiveSet
import vars.annotation.ICameraDeployment
import vars.annotation.IVideoArchive
import vars.annotation.IVideoFrame
import vars.jpa.JPAEntity
import vars.EntityToStringCategory

@Entity(name = "VideoArchiveSet")
@Table(name = "VideoArchiveSet")
@NamedQueries( value = [
    @NamedQuery(name = "VideoArchiveSet.findById", 
                query = "SELECT v FROM VideoArchiveSet v WHERE v.id = :id"),
    @NamedQuery(name = "VideoArchiveSet.findAll",
                query = "SELECT v FROM VideoArchiveSet v"),
    @NamedQuery(name = "VideoArchiveSet.findByTrackingNumber",
                query = "SELECT v FROM VideoArchiveSet v WHERE v.trackingNumber = :trackingNumber"),
    @NamedQuery(name = "VideoArchiveSet.findByPlatformName",
                query = "SELECT v FROM VideoArchiveSet v WHERE v.platformName = :platformName"),
    @NamedQuery(name = "VideoArchiveSet.findByFormatCode",
                query = "SELECT v FROM VideoArchiveSet v WHERE v.formatCode = :formatCode"),
    @NamedQuery(name = "VideoArchiveSet.findByStartDate",
                query = "SELECT v FROM VideoArchiveSet v WHERE v.startDate = :startDate"),
    @NamedQuery(name = "VideoArchiveSet.findByEndDate",
                query = "SELECT v FROM VideoArchiveSet v WHERE v.endDate = :endDate"),
    @NamedQuery(name = "VideoArchiveSet.findBetweenDates",
                query = "SELECT v FROM VideoArchiveSet v WHERE v.startDate BETWEEN :date0 AND :date1"),
    @NamedQuery(name = "VideoArchiveSet.findByPlatformAndTrackingNumber",
                query = "SELECT v FROM VideoArchiveSet v WHERE v.platformName = :platformName AND v.trackingNumber = :trackingNumber"),
    @NamedQuery(name = "VideoArchiveSet.findByPlatformAndSequenceNumber",
                query = "SELECT v FROM VideoArchiveSet v WHERE v.platformName = :platformName AND v.cameraDeployment.sequenceNumber = :sequenceNumber")
])
class VideoArchiveSet implements Serializable, IVideoArchiveSet, JPAEntity {

    @Id 
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "VideoArchiveSet_Gen")
    @TableGenerator(name = "VideoArchiveSet_Gen", table = "UniqueID",
            pkColumnName = "TableName", valueColumnName = "NextID",
            pkColumnValue = "VideoArchiveSet", allocationSize = 1)
    Long id

    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime
    
    @Column(name = "TrackingNumber", length = 7)
    String trackingNumber

    @Column(name = "ShipName", length = 32)
    @Deprecated
    String shipName
    
    @Column(name = "PlatformName", nullable = false, length = 32)
    String platformName
    
    @Column(name = "FormatCode", length = 2)
    char formatCode
    
    @Column(name = "StartDTG")
    @Temporal(value = TemporalType.TIMESTAMP)
    Date startDate
    
    @Column(name = "EndDTG")
    @Temporal(value = TemporalType.TIMESTAMP)
    Date endDate
    
    @OneToMany(targetEntity = VideoArchive.class,
            mappedBy = "videoArchiveSet",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    @OrderBy(value = "name")
    Set<IVideoArchive> videoArchives
    
    @OneToMany(targetEntity = CameraDeployment.class,
            mappedBy = "videoArchiveSet",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    Set<ICameraDeployment> cameraDeployments

    Set<ICameraDeployment> getCameraDeployments() {
        if (cameraDeployments == null) {
            cameraDeployments = new HashSet()
        }
        return cameraDeployments
    }

    Set<IVideoArchive> getVideoArchives() {
        if (videoArchives == null) {
            videoArchives = new HashSet<VideoArchive>()
        }
        return videoArchives
    }

    void addVideoArchive(IVideoArchive videoArchive) {
        if (getVideoArchives().find { VideoArchive va -> va.name.equals(videoArchive.name) }) {
            throw new IllegalArgumentException("A VideoArchive named '${va.name} already exists in ${this}")
        }
        videoArchives << videoArchive
        videoArchive.videoArchiveSet = this
    }

    void removeVideoArchive(IVideoArchive videoArchive) {
        videoArchives.remove(videoArchive)
        videoArchive.videoArchiveSet = null
    }

    void addCameraDeployment(ICameraDeployment cameraDeployment) {
        getCameraDeployments().add(cameraDeployment)
        cameraDeployment.videoArchiveSet = this
    }

    void removeCameraDeployment(ICameraDeployment cameraDeployment) {
        getCameraDeployments().remove(cameraDeployment)
        cameraDeployment.videoArchiveSet = null
    }

    IVideoArchive getVideoArchiveByName(String videoArchiveName) {
        (IVideoArchive) videoArchives.find { it.name.equals(videoArchiveName) }
    }

    List<IVideoFrame> getVideoFrames() {
        def videoFrames = new ArrayList<? extends IVideoFrame>()
        videoArchives.each { va ->
            videoFrames.addAll(va.videoFrames)
        }
        return videoFrames.sort { it.timecode }
    }

    boolean hasSequenceNumber(int seqNumber) {
        return (cameraDeployments.find {it.sequenceNumber == seqNumber} != null)
    }

    boolean hasVideoArchiveName(String videoArchiveName) {
        return (videoArchives.find { it.name.equals(videoArchiveName) } != null)
    }

    @Override
    String toString() {
        return EntityToStringCategory.basicToString(this, [PROP_PLATFORM_NAME, PROP_TRACKING_NUMBER, PROP_START_DATE])
    }

}
