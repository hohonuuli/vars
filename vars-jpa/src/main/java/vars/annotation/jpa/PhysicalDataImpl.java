/*
 * @(#)PhysicalDataImpl.java   2011.03.18 at 09:08:44 PDT
 *
 * Copyright 2011 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.annotation.jpa;

import vars.annotation.PhysicalData;
import vars.annotation.VideoFrame;
import vars.jpa.JPAEntity;
import vars.jpa.KeyNullifier;
import vars.jpa.TransactionLogger;

import javax.persistence.Cacheable;
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
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author brian
 */


@Entity(name = "PhysicalData")
@Table(name = "PhysicalData")
// @Cacheable(true)
@EntityListeners({ TransactionLogger.class, KeyNullifier.class })
@NamedQueries({ @NamedQuery(name = "PhysicalData.findById", query = "SELECT v FROM PhysicalData v WHERE v.id = :id") })
public class PhysicalDataImpl implements Serializable, PhysicalData, JPAEntity {

    Float altitude;

    Float depth;

    @Id
    @Column(
        name = "id",
        nullable = false,
        updatable = false
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "PhysicalData_Gen")
    @TableGenerator(
        name = "PhysicalData_Gen",
        table = "UniqueID",
        pkColumnName = "TableName",
        valueColumnName = "NextID",
        pkColumnValue = "PhysicalData",
        allocationSize = 1
    )
    Long id;

    Double latitude;

    Float light;

    @Column(name = "LogDTG")
    @Temporal(value = TemporalType.TIMESTAMP)
    Date logDate;

    Double longitude;

    Float oxygen;

    Float salinity;

    Float temperature;

    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime;
    
    @OneToOne(targetEntity = VideoFrameImpl.class, optional = false)
    @JoinColumn(name = "VideoFrameID_FK")
    VideoFrame videoFrame;

    /**
     * @return
     */
    public boolean containsData() {
        return ((depth != null) || (temperature != null) || (salinity != null) || (oxygen != null) ||
                (light != null) || (latitude != null) || (longitude != null));
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final PhysicalDataImpl other = (PhysicalDataImpl) obj;
        if ((this.logDate != other.logDate) && ((this.logDate == null) || !this.logDate.equals(other.logDate))) {
            return false;
        }

        if ((this.depth != other.depth) && ((this.depth == null) || !this.depth.equals(other.depth))) {
            return false;
        }

        if ((this.temperature != other.temperature) &&
                ((this.temperature == null) || !this.temperature.equals(other.temperature))) {
            return false;
        }

        if ((this.salinity != other.salinity) && ((this.salinity == null) || !this.salinity.equals(other.salinity))) {
            return false;
        }

        if ((this.oxygen != other.oxygen) && ((this.oxygen == null) || !this.oxygen.equals(other.oxygen))) {
            return false;
        }

        if ((this.light != other.light) && ((this.light == null) || !this.light.equals(other.light))) {
            return false;
        }

        if ((this.latitude != other.latitude) && ((this.latitude == null) || !this.latitude.equals(other.latitude))) {
            return false;
        }

        if ((this.longitude != other.longitude) &&
                ((this.longitude == null) || !this.longitude.equals(other.longitude))) {
            return false;
        }

        return true;
    }

    /**
     * @return
     */
    public Float getAltitude() {
        return altitude;
    }

    /**
     * @return
     */
    public Float getDepth() {
        return depth;
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
    public Double getLatitude() {
        return latitude;
    }

    /**
     * @return
     */
    public Float getLight() {
        return light;
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
    public Double getLongitude() {
        return longitude;
    }

    /**
     * @return
     */
    public Float getOxygen() {
        return oxygen;
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
    public Float getSalinity() {
        return salinity;
    }

    /**
     * @return
     */
    public Float getTemperature() {
        return temperature;
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
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + ((this.logDate != null) ? this.logDate.hashCode() : 0);
        hash = 53 * hash + ((this.depth != null) ? this.depth.hashCode() : 0);
        hash = 53 * hash + ((this.temperature != null) ? this.temperature.hashCode() : 0);
        hash = 53 * hash + ((this.salinity != null) ? this.salinity.hashCode() : 0);
        hash = 53 * hash + ((this.oxygen != null) ? this.oxygen.hashCode() : 0);
        hash = 53 * hash + ((this.light != null) ? this.light.hashCode() : 0);
        hash = 53 * hash + ((this.latitude != null) ? this.latitude.hashCode() : 0);
        hash = 53 * hash + ((this.longitude != null) ? this.longitude.hashCode() : 0);

        return hash;
    }

    /**
     *
     * @param altitude
     */
    public void setAltitude(Float altitude) {
        this.altitude = altitude;
    }

    /**
     *
     * @param depth
     */
    public void setDepth(Float depth) {
        this.depth = depth;
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
     * @param latitude
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     *
     * @param light
     */
    public void setLight(Float light) {
        this.light = light;
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
     * @param longitude
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     *
     * @param oxygen
     */
    public void setOxygen(Float oxygen) {
        this.oxygen = oxygen;
    }

    /**
     *
     * @param salinity
     */
    public void setSalinity(Float salinity) {
        this.salinity = salinity;
    }

    /**
     *
     * @param temperature
     */
    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    void setVideoFrame(VideoFrame videoFrame) {
        this.videoFrame = videoFrame;
    }

    /**
     * @return
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append(" ([id=").append(getId()).append("] latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        sb.append(", depth=").append(depth).append(")");

        return sb.toString();
    }
}
