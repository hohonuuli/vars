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
import javax.persistence.Version
import java.sql.Timestamp
import javax.persistence.OrderBy
import javax.persistence.TableGenerator
import vars.annotation.VideoArchiveSet
import vars.annotation.VideoArchive
import vars.annotation.VideoFrame
import vars.jpa.JPAEntity
import vars.EntitySupportCategory
import javax.persistence.EntityListeners
import vars.jpa.TransactionLogger
import vars.jpa.KeyNullifier
import vars.EntitySupportCategory
import javax.persistence.Transient
import javax.persistence.UniqueConstraint

@Entity(name = "VideoArchive")
@Table(name = "VideoArchive", uniqueConstraints = [@UniqueConstraint(columnNames = ["VideoArchiveName"])])
@EntityListeners( value = [TransactionLogger.class, KeyNullifier.class] )
@NamedQueries( value = [
    @NamedQuery(name = "VideoArchive.findById",
                query = "SELECT v FROM VideoArchive v WHERE v.id = :id"),
    @NamedQuery(name = "VideoArchive.findByName",
                query = "SELECT v FROM VideoArchive v WHERE v.name = :name")
])
class GVideoArchive implements Serializable, VideoArchive, JPAEntity {

    @Transient
    private static final PROPS = Collections.unmodifiableList([VideoArchive.PROP_NAME])

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

    @ManyToOne(optional = false, targetEntity = GVideoArchiveSet.class)
    @JoinColumn(name = "VideoArchiveSetID_FK")
    VideoArchiveSet videoArchiveSet

    @Column(name = "VideoArchiveName", nullable = false, unique = true, length = 512)
    String name

    @Column(name = "StartTimeCode", length = 11)
    @Deprecated
    String startTimecode

    @OneToMany(fetch = FetchType.LAZY,
            targetEntity = GVideoFrame.class,
            mappedBy = "videoArchive",
            cascade = CascadeType.ALL)
    @OrderBy(value = "recordedDate")
    List<VideoFrame> videoFrames

    List<VideoFrame> getVideoFrames() {
        if (videoFrames == null) {
            videoFrames = new ArrayList<VideoFrame>();
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

    VideoFrame findVideoFrameByTimeCode(String timecode) {
        return videoFrames.find { it.timecode == timecode }
    }

    Collection<? extends VideoFrame> getEmptyVideoFrames() {
        return getVideoFrames().findAll { VideoFrame vf ->
            def observations = vf.observations
            return (observations?.size() > 0)
        }
    }

    void removeVideoFrame(VideoFrame videoFrame) {
        if (getVideoFrames().remove(videoFrame)) {
            videoFrame.videoArchive = null
        }
    }

    void loadLazyRelations() {
        videoFrames.each { it.id } // Touch each one to ensure it's read from db
    }

    @Override
    String toString() {
        return EntitySupportCategory.basicToString(this, [PROP_NAME, PROP_START_TIME_CODE])
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
