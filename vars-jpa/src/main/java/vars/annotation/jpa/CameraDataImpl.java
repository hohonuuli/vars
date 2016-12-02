/*
 * @(#)CameraDataImpl.java   2010.01.20 at 04:15:29 PST
 *
 * Copyright 2009 MBARI
 *
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
import javax.persistence.*;

import vars.annotation.CameraData;
import vars.annotation.VideoFrame;
import vars.jpa.JPAEntity;
import vars.jpa.KeyNullifier;
import vars.jpa.TransactionLogger;

/**
 * @author brian
 */

@Entity(name = "CameraData")
@Table(name = "CameraData",
        indexes = {@Index(name = "idx_CameraData_FK1", columnList = "ObservationID_FK"),
                @Index(name = "idx_CameraData_LUT", columnList = "LAST_UPDATED_TIME")})
@Cacheable(false)
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

    Float heading;

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

    Float pitch;

    Float roll;

    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime;

    @OneToOne(targetEntity = VideoFrameImpl.class, optional = false)
    @JoinColumn(name = "VideoFrameID_FK", nullable = false)
    VideoFrame videoFrame;

    Float viewHeight;

    String viewUnits;

    Float viewWidth;

    Float x;

    String xyUnits;

    Float y;

    Float z;

    String zUnits;
    
    Integer zoom;

    /**
     * @return
     */
    public boolean containsData() {
        return ((name != null) || (direction != null) || (zoom != null) || (focus != null) || (iris != null) ||
                (fieldWidth != null) || (imageReference != null));
    }

    /**
     * @return
     */
    public String getDirection() {
        return direction;
    }

    /**
     * @return
     */
    public Double getFieldWidth() {
        return fieldWidth;
    }

    /**
     * @return
     */
    public Integer getFocus() {
        return focus;
    }

    /**
     * @return
     */
    public Float getHeading() {
        return heading;
    }

    /**
     * @return
     */
    public Long getId() {
        return id;
    }

    /**
     * @return
     */
    public String getImageReference() {
        return imageReference;
    }

    /**
     * @return
     */
    public Integer getIris() {
        return iris;
    }

    /**
     * @return
     */
    public Date getLogDate() {
        return logDate;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @return
     */
    public Float getPitch() {
        return pitch;
    }

    /**
     * @return
     */
    public Object getPrimaryKey() {
        return getId();
    }

    /**
     * @return
     */
    public Float getRoll() {
        return roll;
    }

    /**
     * @return
     */
    public VideoFrame getVideoFrame() {
        return videoFrame;
    }

    /**
     * @return
     */
    public Float getViewHeight() {
        return viewHeight;
    }

    /**
     * @return
     */
    public String getViewUnits() {
        return viewUnits;
    }

    /**
     * @return
     */
    public Float getViewWidth() {
        return viewWidth;
    }

    /**
     * @return
     */
    public Float getX() {
        return x;
    }

    /**
     * @return
     */
    public String getXYUnits() {
        return xyUnits;
    }

    /**
     * @return
     */
    public Float getY() {
        return y;
    }

    /**
     * @return
     */
    public Float getZ() {
        return z;
    }

    /**
     * @return
     */
    public String getZUnits() {
        return zUnits;
    }

    /**
     * @return
     */
    public Integer getZoom() {
        return zoom;
    }

    /**
     *
     * @param direction
     */
    public void setDirection(String direction) {
        this.direction = direction;
    }

    /**
     *
     * @param fieldWidth
     */
    public void setFieldWidth(Double fieldWidth) {
        this.fieldWidth = fieldWidth;
    }

    /**
     *
     * @param focus
     */
    public void setFocus(Integer focus) {
        this.focus = focus;
    }

    /**
     *
     * @param heading
     */
    public void setHeading(Float heading) {
        this.heading = heading;
    }

    /**
     *
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     *
     * @param stillImage
     */
    public void setImageReference(String stillImage) {
        this.imageReference = stillImage;
    }

    /**
     *
     * @param iris
     */
    public void setIris(Integer iris) {
        this.iris = iris;
    }

    /**
     *
     * @param logDate
     */
    public void setLogDate(Date logDate) {
        this.logDate = logDate;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @param pitch
     */
    public void setPitch(Float pitch) {
        this.pitch = pitch;
    }

    /**
     *
     * @param roll
     */
    public void setRoll(Float roll) {
        this.roll = roll;
    }

    void setVideoFrame(VideoFrame videoFrame) {
        this.videoFrame = videoFrame;
    }

    /**
     *
     * @param height
     */
    public void setViewHeight(Float height) {
        this.viewHeight = height;
    }

    /**
     *
     * @param units
     */
    public void setViewUnits(String units) {
        this.viewUnits = units;
    }

    /**
     *
     * @param width
     */
    public void setViewWidth(Float width) {
        this.viewWidth = width;
    }

    /**
     *
     * @param x
     */
    public void setX(Float x) {
        this.x = x;
    }

    /**
     *
     * @param units
     */
    public void setXYUnits(String units) {
        this.xyUnits = units;
    }

    /**
     *
     * @param y
     */
    public void setY(Float y) {
        this.y = y;
    }

    /**
     *
     * @param z
     */
    public void setZ(Float z) {
        this.z = z;
    }

    /**
     *
     * @param units
     */
    public void setZUnits(String units) {
        this.zUnits = units;
    }

    /**
     *
     * @param zoom
     */
    public void setZoom(Integer zoom) {
        this.zoom = zoom;
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + " ([id=" + id + "] imageReference=" + imageReference + ")";
    }
}
