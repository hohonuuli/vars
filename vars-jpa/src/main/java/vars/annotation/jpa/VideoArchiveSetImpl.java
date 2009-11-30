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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import vars.VideoMomentByTimecodeComparator;
import vars.annotation.CameraDeployment;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveSet;
import vars.annotation.VideoFrame;
import vars.jpa.JPAEntity;
import vars.jpa.KeyNullifier;
import vars.jpa.TransactionLogger;

/**
 *
 * @author brian
 */

@Entity(name = "VideoArchiveSet")
@Table(name = "VideoArchiveSet")
@EntityListeners( {TransactionLogger.class, KeyNullifier.class} )
@NamedQueries( {
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
                query = "SELECT v FROM VideoArchiveSet v, IN (v.cameraDeployments) c WHERE v.platformName = :platformName AND c.sequenceNumber = :sequenceNumber")
})
public class VideoArchiveSetImpl implements Serializable, VideoArchiveSet, JPAEntity {

    @Transient
    private final VideoMomentByTimecodeComparator comparator = new VideoMomentByTimecodeComparator();


    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "VideoArchiveSet_Gen")
    @TableGenerator(name = "VideoArchiveSet_Gen", table = "UniqueID",
            pkColumnName = "TableName", valueColumnName = "NextID",
            pkColumnValue = "VideoArchiveSet", allocationSize = 1)
    Long id;

    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime;

    @Column(name = "TrackingNumber", length = 7)
    String trackingNumber;

    @Column(name = "ShipName", length = 32)
    @Deprecated
    String shipName;

    @Column(name = "PlatformName", nullable = false, length = 32)
    String platformName;

    @Column(name = "FormatCode", length = 2)
    char formatCode;

    @Column(name = "StartDTG")
    @Temporal(value = TemporalType.TIMESTAMP)
    Date startDate;

    @Column(name = "EndDTG")
    @Temporal(value = TemporalType.TIMESTAMP)
    Date endDate;

    @OneToMany(targetEntity = VideoArchiveImpl.class,
            mappedBy = "videoArchiveSet",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    @OrderBy(value = "name")
    Set<VideoArchive> videoArchives;

    @OneToMany(targetEntity = CameraDeploymentImpl.class,
            mappedBy = "videoArchiveSet",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    Set<CameraDeployment> cameraDeployments;

    public Set<CameraDeployment> getCameraDeployments() {
        if (cameraDeployments == null) {
            cameraDeployments = new HashSet<CameraDeployment>();
        }
        return cameraDeployments;
    }

    public Set<VideoArchive> getVideoArchives() {
        if (videoArchives == null) {
            videoArchives = new HashSet<VideoArchive>();
        }
        return videoArchives;
    }

    public void addVideoArchive(VideoArchive videoArchive) {
        Collection<VideoArchive> archives = Collections2.filter(getVideoArchives(), new NamePredicate(videoArchive.getName()));
        if (archives.size() > 0) {
            throw new IllegalArgumentException("A VideoArchive named '" + videoArchive.getName() + "' already exists in " + this);
        }

        if(getVideoArchives().add(videoArchive)) {
            ((VideoArchiveImpl) videoArchive).setVideoArchiveSet(this);
        }
 
    }

    public void removeVideoArchive(VideoArchive videoArchive) {
        if(getVideoArchives().remove(videoArchive)) {
            ((VideoArchiveImpl) videoArchive).setVideoArchiveSet(null);
        }
    }

    public void addCameraDeployment(CameraDeployment cameraDeployment) {
        if(getCameraDeployments().add(cameraDeployment) ){
            ((CameraDeploymentImpl) cameraDeployment).setVideoArchiveSet(this);
        }
    }

    public void removeCameraDeployment(CameraDeployment cameraDeployment) {
        if(getCameraDeployments().remove(cameraDeployment)) {
            ((CameraDeploymentImpl) cameraDeployment).setVideoArchiveSet(null);
        }
    }

    public VideoArchive getVideoArchiveByName(String videoArchiveName) {
        Collection<VideoArchive> archives = Collections2.filter(getVideoArchives(), new NamePredicate(videoArchiveName));
        VideoArchive videoArchive = null;
        if (archives.size() > 0) {
            videoArchive = archives.iterator().next();
        }
        return videoArchive;
    }

    public List<VideoFrame> getVideoFrames() {
        List<VideoFrame> videoFrames = new ArrayList<VideoFrame>();
        for (VideoArchive videoArchive : getVideoArchives()) {
            videoFrames.addAll(videoArchive.getVideoFrames());
        }
        Collections.sort(videoFrames, comparator);

        return videoFrames;
    }

    public boolean hasSequenceNumber(int seqNumber) {
        Collection<CameraDeployment> deployments = Collections2.filter(getCameraDeployments(), new SequenceNumberPredicate(seqNumber));
        return deployments.size() > 0;
    }

    public boolean hasVideoArchiveName(String videoArchiveName) {
        return getVideoArchiveByName(videoArchiveName) != null;
    }

    public Date getEndDate() {
        return endDate;
    }

    public char getFormatCode() {
        return formatCode;
    }

    public String getPlatformName() {
        return platformName;
    }

    public String getShipName() {
        return shipName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setEndDate(Date endDTG) {
        this.endDate = endDTG;
    }

    public void setFormatCode(char formatCode) {
        this.formatCode = formatCode;
    }
    
    public void setId(Long id) {
    	this.id = id;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public void setStartDate(Date startDTG) {
        this.startDate = startDTG;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Long getId() {
        return id;
    }

    private class NamePredicate implements Predicate<VideoArchive> {

        private final String name;

        public NamePredicate(String name) {
            this.name = name;
        }

        public boolean apply(VideoArchive input) {
            return  input.getName().equals(name);
        }

    }

    private class SequenceNumberPredicate implements Predicate<CameraDeployment> {

        private final Integer sequenceNumber;

        public SequenceNumberPredicate(Integer sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
        }

        public boolean apply(CameraDeployment input) {
            return  input.getSequenceNumber().equals(sequenceNumber);
        }

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VideoArchiveSetImpl other = (VideoArchiveSetImpl) obj;
        if ((this.trackingNumber == null) ? (other.trackingNumber != null) : !this.trackingNumber.equals(other.trackingNumber)) {
            return false;
        }
        if ((this.platformName == null) ? (other.platformName != null) : !this.platformName.equals(other.platformName)) {
            return false;
        }
        if (this.formatCode != other.formatCode) {
            return false;
        }
        if (this.startDate != other.startDate && (this.startDate == null || !this.startDate.equals(other.startDate))) {
            return false;
        }
        if (this.endDate != other.endDate && (this.endDate == null || !this.endDate.equals(other.endDate))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.trackingNumber != null ? this.trackingNumber.hashCode() : 0);
        hash = 97 * hash + (this.platformName != null ? this.platformName.hashCode() : 0);
        hash = 97 * hash + this.formatCode;
        hash = 97 * hash + (this.startDate != null ? this.startDate.hashCode() : 0);
        hash = 97 * hash + (this.endDate != null ? this.endDate.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append(" ([id=").append(getId()).append("] platformName=");
        sb.append(platformName).append(", trackingNumber=").append(trackingNumber);
        sb.append(", formatCode=").append(formatCode).append(")");
        return sb.toString();
    }

}
