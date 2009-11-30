/*
 * @(#)CameraDeploymentImpl.java   2009.11.10 at 01:09:01 PST
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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import vars.annotation.CameraDeployment;
import vars.annotation.VideoArchiveSet;
import vars.jpa.JPAEntity;
import vars.jpa.KeyNullifier;
import vars.jpa.TransactionLogger;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Nov 10, 2009
 * Time: 1:01:07 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "CameraDeployment")
@Table(name = "CameraPlatformDeployment")
@EntityListeners({ TransactionLogger.class, KeyNullifier.class })
@NamedQueries( {

    @NamedQuery(name = "CameraDeployment.findById", query = "SELECT v FROM CameraDeployment v WHERE v.id = :id") ,
    @NamedQuery(name = "CameraDeployment.findBySequenceNumber",
                query = "SELECT v FROM CameraDeployment v WHERE v.sequenceNumber = :sequenceNumber") ,
    @NamedQuery(name = "CameraDeployment.findByChiefScientistName",
                query = "SELECT v FROM CameraDeployment v WHERE v.chiefScientistName = :chiefScientistName") ,
    @NamedQuery(name = "CameraDeployment.findByStartDate",
                query = "SELECT v FROM CameraDeployment v WHERE v.startDate = :startDate") ,
    @NamedQuery(name = "CameraDeployment.findByEndDate",
                query = "SELECT v FROM CameraDeployment v WHERE v.endDate = :endDate")

})
public class CameraDeploymentImpl implements Serializable, CameraDeployment, JPAEntity {

    @Column(name = "ChiefScientist", length = 50)
    String chiefScientistName;

    @Column(name = "UsageEndDTG")
    @Temporal(value = TemporalType.TIMESTAMP)
    Date endDate;

    @Id
    @Column(
        name = "id",
        nullable = false,
        updatable = false
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "CameraPlatformDeployment_Gen")
    @TableGenerator(
        name = "CameraPlatformDeployment_Gen",
        table = "UniqueID",
        pkColumnName = "TableName",
        valueColumnName = "NextID",
        pkColumnValue = "CameraPlatformDeployment",
        allocationSize = 1
    )
    Long id;

    @Column(name = "SeqNumber")
    Integer sequenceNumber;

    @Column(name = "UsageStartDTG")
    @Temporal(value = TemporalType.TIMESTAMP)
    Date startDate;

    /** Optimistic lock to prevent concurrent overwrites */
    @SuppressWarnings("unused")
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime;
    
    @ManyToOne(optional = false, targetEntity = VideoArchiveSetImpl.class)
    @JoinColumn(name = "VideoArchiveSetID_FK")
    VideoArchiveSet videoArchiveSet;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final CameraDeploymentImpl other = (CameraDeploymentImpl) obj;
        if ((this.sequenceNumber != other.sequenceNumber) &&
                ((this.sequenceNumber == null) || !this.sequenceNumber.equals(other.sequenceNumber))) {
            return false;
        }

        if ((this.chiefScientistName == null)
                ? (other.chiefScientistName != null) : !this.chiefScientistName.equals(other.chiefScientistName)) {
            return false;
        }

        if ((this.startDate != other.startDate) &&
                ((this.startDate == null) || !this.startDate.equals(other.startDate))) {
            return false;
        }

        if ((this.endDate != other.endDate) && ((this.endDate == null) || !this.endDate.equals(other.endDate))) {
            return false;
        }

        return true;
    }

    public String getChiefScientistName() {
        return chiefScientistName;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Long getId() {
        return id;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public Date getStartDate() {
        return startDate;
    }

    public VideoArchiveSet getVideoArchiveSet() {
        return videoArchiveSet;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + ((this.sequenceNumber != null) ? this.sequenceNumber.hashCode() : 0);
        hash = 59 * hash + ((this.chiefScientistName != null) ? this.chiefScientistName.hashCode() : 0);
        hash = 59 * hash + ((this.startDate != null) ? this.startDate.hashCode() : 0);
        hash = 59 * hash + ((this.endDate != null) ? this.endDate.hashCode() : 0);

        return hash;
    }

    public void setChiefScientistName(String chiefScientistName) {
        this.chiefScientistName = chiefScientistName;
    }

    public void setEndDate(Date dtg) {
        this.endDate = dtg;
    }
    
    public void setId(Long id) {
    	this.id = id;
    }

    public void setSequenceNumber(Integer seqNumber) {
        this.sequenceNumber = seqNumber;
    }

    public void setStartDate(Date dtg) {
        this.startDate = dtg;
    }

    void setVideoArchiveSet(VideoArchiveSet videoArchiveSet) {
        this.videoArchiveSet = videoArchiveSet;
    }
}
