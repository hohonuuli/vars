/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.jpa;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.persistence.*;

import com.google.common.collect.Iterables;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveSet;
import vars.annotation.VideoFrame;
import vars.annotation.VideoFrameTCBean;
import vars.jpa.JPAEntity;
import vars.jpa.KeyNullifier;
import vars.jpa.TransactionLogger;

/**
 * @author brian
 */

@Entity(name = "VideoArchive")
@Table(name = "VideoArchive", uniqueConstraints = {@UniqueConstraint(columnNames = {"VideoArchiveName"})})
@Cacheable(false)
@EntityListeners( {TransactionLogger.class, KeyNullifier.class} )
@NamedQueries( {
        @NamedQuery(name = "VideoArchive.findById",
                query = "SELECT v FROM VideoArchive v WHERE v.id = :id"),
        @NamedQuery(name = "VideoArchive.findByName",
                query = "SELECT v FROM VideoArchive v WHERE v.name = :name")
})
public class VideoArchiveImpl implements Serializable, VideoArchive, JPAEntity {

    @Transient
    private final Predicate<VideoFrame> emptyVideoFramePredicate = new EmptyVideoFramePredicate();

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "VideoArchive_Gen")
    @TableGenerator(name = "VideoArchive_Gen", table = "UniqueID",
            pkColumnName = "TableName", valueColumnName = "NextID",
            pkColumnValue = "VideoArchive", allocationSize = 1)
    Long id;

    /** Optimistic lock to prevent concurrent overwrites */
    @SuppressWarnings("unused")
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime;

    @ManyToOne(optional = false, targetEntity = VideoArchiveSetImpl.class)
    @JoinColumn(name = "VideoArchiveSetID_FK")
    VideoArchiveSet videoArchiveSet;

    @Column(name = "VideoArchiveName", nullable = false, unique = true, length = 512)
    String name;

    @Column(name = "StartTimeCode", length = 11)
    @Deprecated
    String startTimecode;

    /**
     * Should be sorted by timecode
     */
    @OneToMany(fetch = FetchType.LAZY,
            targetEntity = VideoFrameImpl.class,
            mappedBy = "videoArchive",
            cascade = CascadeType.ALL)
    @OrderBy(value = "timecode")
    List<VideoFrame> videoFrames;

    public List<VideoFrame> getVideoFrames() {
        if (videoFrames == null) {
            videoFrames = new ArrayList<VideoFrame>();
        }
        return videoFrames;
    }

    private int indexByTimecode(final String timecode) {
        return Collections.binarySearch(getVideoFrames(),
                new VideoFrameTCBean(timecode),
                new Comparator<VideoFrame>() {
            @Override
            public int compare(VideoFrame o1, VideoFrame o2) {
                return o1.getTimecode().compareTo(o2.getTimecode());
            }
        });
    }

    public void addVideoFrame(final VideoFrame videoFrame) {

        int idx = indexByTimecode(videoFrame.getTimecode());

        if (idx >= 0) {
            throw new IllegalArgumentException("A VideoFrame with a timecode of " + videoFrame.getTimecode() +
                    " already exists in " + this + ".");
        }
        else {
            int insertIdx = -idx - 1;
            getVideoFrames().add(insertIdx, videoFrame);
            ((VideoFrameImpl) videoFrame).setVideoArchive(this);
        }
    }

    public VideoFrame findVideoFrameByTimeCode(final String timecode) {
        int idx = indexByTimecode(timecode);
        return (idx >= 0) ? getVideoFrames().get(idx) : null;
    }

    public Collection<VideoFrame> getEmptyVideoFrames() {
        return Collections2.filter(getVideoFrames(), emptyVideoFramePredicate);
    }

    public void removeVideoFrame(VideoFrame videoFrame) {
        if (getVideoFrames().remove(videoFrame)) {
            ((VideoFrameImpl) videoFrame).setVideoArchive(null);
        }
    }

    public void loadLazyRelations() {
        for (VideoFrame videoFrame : videoFrames) {
            videoFrame.getTimecode(); // Touch each one to ensure it's read from db
        }
    }

    public String getStartTimecode() {
        return startTimecode;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public VideoArchiveSet getVideoArchiveSet() {
        return videoArchiveSet;
    }

    public void setStartTimecode(String timecode) {
        this.startTimecode = timecode;
    }

    public void setName(String videoArchiveName) {
        this.name = videoArchiveName;
    }

    public Long getId() {
        return id;
    }

    private class TimecodePredicate implements Predicate<VideoFrame> {

        private final String timecode;

        public TimecodePredicate(String timecode) {
            this.timecode = timecode;
        }

        public boolean apply(VideoFrame input) {
            return  input.getTimecode().equals(timecode);
        }

    }

    private class EmptyVideoFramePredicate implements Predicate<VideoFrame> {

        public boolean apply(VideoFrame input) {
            return  input.getObservations().size() == 0;
        }
    }

    void setVideoArchiveSet(VideoArchiveSet videoArchiveSet) {
        this.videoArchiveSet = videoArchiveSet;
    }

    public Object getPrimaryKey() {
        return getId();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "([id=" + id + "] name=" + name + ")";
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        VideoArchiveImpl that = (VideoArchiveImpl) o;
//
//        if (name != null ? !name.equals(that.name) : that.name != null) return false;
//
//        return true;
//    }
//
//    @Override
//    public int hashCode() {
//        return name != null ? name.hashCode() : 0;
//    }
}
