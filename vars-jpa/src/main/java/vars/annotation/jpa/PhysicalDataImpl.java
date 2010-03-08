/*
 * @(#)PhysicalDataImpl.java   2009.11.10 at 01:33:37 PST
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
import vars.annotation.PhysicalData;
import vars.annotation.VideoFrame;
import vars.jpa.JPAEntity;
import vars.jpa.KeyNullifier;
import vars.jpa.TransactionLogger;

/**
 * @author brian
 */


@Entity(name = "PhysicalData")
@Table(name = "PhysicalData")
@EntityListeners({ TransactionLogger.class, KeyNullifier.class })
@NamedQueries({ @NamedQuery(name = "PhysicalData.findById", query = "SELECT v FROM PhysicalData v WHERE v.id = :id") })
public class PhysicalDataImpl implements Serializable, PhysicalData, JPAEntity {

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

    public boolean containsData() {
        return ((depth != null) || (temperature != null) || (salinity != null) || (oxygen != null) ||
                (light != null) || (latitude != null) || (longitude != null));
    }

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

    public Float getDepth() {
        return depth;
    }

    public Long getId() {
        return id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Float getLight() {
        return light;
    }

    public Date getLogDate() {
        return logDate;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Float getOxygen() {
        return oxygen;
    }

    public Float getSalinity() {
        return salinity;
    }

    public Float getTemperature() {
        return temperature;
    }

    public VideoFrame getVideoFrame() {
        return videoFrame;
    }

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

    public void setDepth(Float depth) {
        this.depth = depth;
    }
    
    public void setId(Long id) {
    	this.id = id;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLight(Float light) {
        this.light = light;
    }

    public void setLogDate(Date logDate) {
        this.logDate = logDate;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setOxygen(Float oxygen) {
        this.oxygen = oxygen;
    }

    public void setSalinity(Float salinity) {
        this.salinity = salinity;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append(" ([id=").append(getId()).append("] latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        sb.append(", depth=").append(depth).append(")");

        return sb.toString();
    }

    void setVideoFrame(VideoFrame videoFrame) {
        this.videoFrame = videoFrame;
    }
    
    public Object getPrimaryKey() {
    	return getId();
    }
}
