/*
 * @(#)CameraDataImpl.java   2009.11.10 at 12:58:38 PST
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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import vars.annotation.CameraData;
import vars.annotation.VideoFrame;
import vars.jpa.JPAEntity;
import vars.jpa.KeyNullifier;
import vars.jpa.TransactionLogger;

/**
 *
 * @author brian
 */

@Entity(name = "CameraData")
@Table(name = "CameraData")
@EntityListeners({ TransactionLogger.class, KeyNullifier.class })
@NamedQueries( {

    @NamedQuery(name = "CameraData.findById", query = "SELECT v FROM CameraData v WHERE v.id = :id") ,
    @NamedQuery(name = "CameraData.findByName", query = "SELECT v FROM CameraData v WHERE v.name = :name") ,
    @NamedQuery(name = "CameraData.findByDirection",
                query = "SELECT c FROM CameraData c WHERE c.direction = :direction") ,
    @NamedQuery(name = "CameraData.findByImageReference",
                query = "SELECT c FROM CameraData c WHERE c.imageReference = :imageReference") ,
    @NamedQuery(name = "CameraData.findByImageReferenceLike",
                query = "SELECT c FROM CameraData c WHERE c.imageReference LIKE :imageReference")

})
public class CameraDataImpl implements Serializable, CameraData, JPAEntity {

    @Column(name = "Direction", length = 50)
    String direction;

    Double fieldWidth;

    Integer focus;

    @Id
    @Column(
        name = "id",
        nullable = false,
        updatable = false
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "CameraData_Gen")
    @TableGenerator(
        name = "CameraData_Gen",
        table = "UniqueID",
        pkColumnName = "TableName",
        valueColumnName = "NextID",
        pkColumnValue = "CameraData",
        allocationSize = 1
    )
    Long id;

    @Column(name = "StillImageUrl", length = 1024)
    String imageReference;

    Integer iris;

    @Column(name = "LogDTG")
    @Temporal(value = TemporalType.TIMESTAMP)
    Date logDate;
    
    @Column(name = "Name", length = 50)
    String name;

    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime;

    @OneToOne(targetEntity = VideoFrameImpl.class, optional = false)
    @JoinColumn(name = "VideoFrameID_FK", nullable = false)
    VideoFrame videoFrame;
    
    Integer zoom;

    public boolean containsData() {
        return ((name != null) || (direction != null) || (zoom != null) || (focus != null) || (iris != null) ||
                (fieldWidth != null) || (imageReference != null));
    }

    public String getDirection() {
        return direction;
    }

    public Double getFieldWidth() {
        return fieldWidth;
    }

    public Integer getFocus() {
        return focus;
    }

    public Long getId() {
        return id;
    }

    public String getImageReference() {
        return imageReference;
    }

    public Integer getIris() {
        return iris;
    }

    public Date getLogDate() {
        return logDate;
    }

    public String getName() {
        return name;
    }

    public VideoFrame getVideoFrame() {
        return videoFrame;
    }

    public Integer getZoom() {
        return zoom;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setFieldWidth(Double fieldWidth) {
        this.fieldWidth = fieldWidth;
    }

    public void setFocus(Integer focus) {
        this.focus = focus;
    }

    public void setImageReference(String stillImage) {
        this.imageReference = stillImage;
    }
    
    public void setId(Long id) {
    	this.id = id;
    }

    public void setIris(Integer iris) {
        this.iris = iris;
    }

    public void setLogDate(Date logDate) {
        this.logDate = logDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    void setVideoFrame(VideoFrame videoFrame) {
        this.videoFrame = videoFrame;
    }

    public void setZoom(Integer zoom) {
        this.zoom = zoom;
    }
}
