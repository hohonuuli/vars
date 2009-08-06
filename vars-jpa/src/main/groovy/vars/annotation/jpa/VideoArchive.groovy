package vars.annotation.jpa

import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.NamedQueries
import javax.persistence.NamedQuery
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.ManyToOne
import javax.persistence.Column
import javax.persistence.OneToMany
import javax.persistence.FetchType
import javax.persistence.GenerationType
import javax.persistence.JoinColumn
import javax.persistence.CascadeType
import javax.persistence.PrimaryKeyJoinColumn
import javax.persistence.Version
import javax.persistence.Temporal
import javax.persistence.TemporalType
import java.sql.Timestamp
import javax.persistence.OrderBy
import javax.persistence.TableGenerator
import vars.annotation.IVideoArchiveSet

@Entity(name = "VideoArchive")
@Table(name = "VideoArchive")
@NamedQueries( value = [
    @NamedQuery(name = "VideoArchive.findById",
                query = "SELECT v FROM VideoArchive v WHERE v.id = :id"),
    @NamedQuery(name = "VideoArchive.findByName",
                query = "SELECT v FROM VideoArchive v WHERE v.name = :name")
])
class VideoArchive implements Serializable {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "VideoArchive_Gen")
    @TableGenerator(name = "VideoArchive_Gen", table = "UniqueID",
            pkColumnName = "TableName", valueColumnName = "NextID",
            pkColumnValue = "VideoArchive", allocationSize = 1)
    Long id

    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime

    @ManyToOne(optional = false, targetEntity = VideoArchiveSet.class)
    @JoinColumn(name = "VideoArchiveSetID_FK")
    IVideoArchiveSet videoArchiveSet

    @Column(name = "VideoArchiveName", nullable = false, unique = true, length = 512)
    String name

    @Column(name = "TapeNumber")
    @Deprecated
    Short tapeNumber

    @Column(name = "StartTimeCode", length = 11)
    @Deprecated
    String startTimecode

    @OneToMany(fetch = FetchType.LAZY,
            targetEntity = VideoFrame.class,
            mappedBy = "videoArchive",
            cascade = CascadeType.ALL)
    @OrderBy(value = "recordedDate")
    List<VideoFrame> videoFrames

    List<VideoFrame> getVideoFrames() {
        if (videoFrames == null) {
            videoFrames = new ArrayList<VideoFrame>()
        }
        return videoFrames
    }

    void addVideoFrame(VideoFrame videoFrame) {
        if (getVideoFrames().find {VideoFrame vf -> vf.timecode.equals(videoFrame.timecode)}) {
            throw new IllegalArgumentException("A VideoFrame with a timecode of ${videoFrame.timecode} already exists in ${this}.")
        }
        videoFrames << videoFrame
        videoFrame.videoArchive = this
    }

    void removeVideoFrame(VideoFrame videoFrame) {
        if (videoFrames?.remove(videoFrame)) {
            videoFrame.videoArchive = null
        }
    }
    
}
