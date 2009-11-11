/*
 * @(#)VideoFrameImpl.java   2009.11.10 at 02:39:55 PST
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

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import vars.annotation.CameraData;
import vars.annotation.Observation;
import vars.annotation.PhysicalData;
import vars.annotation.VideoArchive;
import vars.annotation.VideoFrame;
import vars.jpa.JPAEntity;
import vars.jpa.KeyNullifier;
import vars.jpa.TransactionLogger;

/**
 *
 * @author brian
 */

@Entity(name = "VideoFrame")
@Table(name = "VideoFrame")
@EntityListeners({ TransactionLogger.class, KeyNullifier.class })
@NamedQueries({ @NamedQuery(name = "VideoFrame.findById", query = "SELECT v FROM VideoFrame v WHERE v.id = :id") ,
                @NamedQuery(name = "VideoFrame.findByName",
                            query = "SELECT v FROM VideoFrame v WHERE v.recordedDate = :recordedDate") ,
                @NamedQuery(name = "VideoFrame.findByVideoArchivePrimaryKey",
                            query = "SELECT v FROM VideoFrame v WHERE v.videoArchive.id = :primaryKey") })
public class VideoFrameImpl implements Serializable, VideoFrame, JPAEntity {

    @Column(
        name = "HDTimeCode",
        nullable = false,
        length = 11
    )
    String alternateTimecode;

    @OneToOne(
        mappedBy = "videoFrame",
        fetch = FetchType.EAGER,
        cascade = CascadeType.ALL,
        targetEntity = CameraDataImpl.class
    )
    private CameraData cameraData;

    @Id
    @Column(
        name = "id",
        nullable = false,
        updatable = false
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "VideoFrame_Gen")
    @TableGenerator(
        name = "VideoFrame_Gen",
        table = "UniqueID",
        pkColumnName = "TableName",
        valueColumnName = "NextID",
        pkColumnValue = "VideoFrame",
        allocationSize = 1
    )
    Long id;

    @Column(name = "inSequence")
    Short inSequence;

    @OneToMany(
        targetEntity = ObservationImpl.class,
        mappedBy = "videoFrame",
        fetch = FetchType.EAGER,
        cascade = CascadeType.ALL
    )
    Set<Observation> observations;

    @OneToOne(
        mappedBy = "videoFrame",
        fetch = FetchType.EAGER,
        cascade = CascadeType.ALL,
        targetEntity = PhysicalDataImpl.class
    )
    private PhysicalData physicalData;

    @Column(name = "RecordedDTG")
    @Temporal(value = TemporalType.TIMESTAMP)
    Date recordedDate;
    
    @Column(
        name = "TapeTimeCode",
        nullable = false,
        length = 11
    )
    String timecode;

    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime;
    @ManyToOne(optional = false, targetEntity = VideoArchiveImpl.class)
    @JoinColumn(name = "VideoArchiveID_FK")
    VideoArchive videoArchive;

    public void addObservation(Observation observation) {
        if (getObservations().add(observation)) {
            ((ObservationImpl) observation).setVideoFrame(this);
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

        final VideoFrameImpl other = (VideoFrameImpl) obj;
        if ((this.recordedDate != other.recordedDate) &&
                ((this.recordedDate == null) || !this.recordedDate.equals(other.recordedDate))) {
            return false;
        }

        if ((this.timecode == null) ? (other.timecode != null) : !this.timecode.equals(other.timecode)) {
            return false;
        }

        return true;
    }

    public String getAlternateTimecode() {
        return alternateTimecode;
    }

    public CameraData getCameraData() {
        if (cameraData == null) {
            cameraData = new CameraDataImpl();
            ((CameraDataImpl) cameraData).setVideoFrame(this);
        }

        return cameraData;
    }

    public Long getId() {
        return id;
    }

    public Set<Observation> getObservations() {
        if (observations == null) {
            observations = new HashSet<Observation>();
        }

        return observations;
    }

    public PhysicalData getPhysicalData() {
        if (physicalData == null) {
            physicalData = new PhysicalDataImpl();
            ((PhysicalDataImpl) physicalData).setVideoFrame(this);
        }

        return physicalData;
    }

    public Date getRecordedDate() {
        return recordedDate;
    }

    public String getTimecode() {
        return timecode;
    }

    public VideoArchive getVideoArchive() {
        return videoArchive;
    }

    public boolean hasImageReference() {
        return getCameraData().getImageReference() != null;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + ((this.recordedDate != null) ? this.recordedDate.hashCode() : 0);
        hash = 71 * hash + ((this.timecode != null) ? this.timecode.hashCode() : 0);

        return hash;
    }

    public boolean isInSequence() {
        return inSequence != 0;
    }

    public void removeObservation(Observation observation) {
        if (getObservations().remove(observation)) {
            ((ObservationImpl) observation).setVideoFrame(null);
        }
    }

    public void setAlternateTimecode(String altTimecode) {
        this.alternateTimecode = altTimecode;
    }
    
    public void setId(Long id) {
    	this.id = id;
    }

    public void setInSequence(boolean state) {
        this.inSequence = state ? Short.valueOf((short) 1) : Short.valueOf((short) 0);
    }

    public void setRecordedDate(Date dtg) {
        this.recordedDate = dtg;
    }

    public void setTimecode(String timecode) {
        this.timecode = timecode;
    }

    void setVideoArchive(VideoArchive videoArchive) {
        this.videoArchive = videoArchive;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append(" ([id=").append(getId()).append("] timecode=").append(timecode);
        sb.append(", recordedDate=").append(recordedDate).append(")");

        return sb.toString();
    }
}
