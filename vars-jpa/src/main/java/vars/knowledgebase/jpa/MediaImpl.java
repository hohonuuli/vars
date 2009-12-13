/*
 * @(#)MediaImpl.java   2009.11.10 at 11:42:08 PST
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.knowledgebase.jpa;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.CascadeType;
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
import javax.persistence.Version;
import vars.jpa.JPAEntity;
import vars.jpa.KeyNullifier;
import vars.jpa.TransactionLogger;
import vars.knowledgebase.ConceptMetadata;
import vars.knowledgebase.Media;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Jun 19, 2009
 * Time: 10:02:15 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "Media")
@Table(name = "Media")
@EntityListeners({ TransactionLogger.class, KeyNullifier.class })
@NamedQueries( {
    @NamedQuery(name = "Media.findById", query = "SELECT v FROM Media v WHERE v.id = :id") ,
    @NamedQuery(name = "Media.findByUrl", query = "SELECT m FROM Media m WHERE m.url = :url") ,
    @NamedQuery(name = "Media.findByType", query = "SELECT m FROM Media m WHERE m.type = :type") ,
    @NamedQuery(name = "Media.findByPrimaryMedia",
                query = "SELECT m FROM Media m WHERE m.primaryMedia = :primaryMedia") ,
    @NamedQuery(name = "Media.findByCredit", query = "SELECT m FROM Media m WHERE m.credit = :credit") ,
    @NamedQuery(name = "Media.findByCaption", query = "SELECT m FROM Media m WHERE m.caption = :caption")
})
public class MediaImpl implements Serializable, Media, JPAEntity {

    @Column(name = "Caption", length = 1000)
    String caption;

    @ManyToOne(optional = false, targetEntity = ConceptMetadataImpl.class, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "ConceptDelegateID_FK")
    ConceptMetadata conceptMetadata;

    @Column(name = "Credit", length = 255)
    String credit;

    @Id
    @Column(
        name = "id",
        nullable = false,
        updatable = false
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Media_Gen")
    @TableGenerator(
        name = "Media_Gen",
        table = "UniqueID",
        pkColumnName = "TableName",
        valueColumnName = "NextID",
        pkColumnValue = "Media",
        allocationSize = 1
    )
    Long id;

    @Column(name = "PrimaryMedia")
    Boolean primaryMedia;

    @Column(name = "MediaType", length = 5)
    String type;

    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime;
    @Column(name = "Url", length = 1024)
    String url;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final MediaImpl other = (MediaImpl) obj;
        if ((this.url == null) ? (other.url != null) : !this.url.equals(other.url)) {
            return false;
        }

        return true;
    }

    public String getCaption() {
        return caption;
    }

    public ConceptMetadata getConceptMetadata() {
        return conceptMetadata;
    }

    public String getCredit() {
        return credit;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }
    
    public Object getPrimaryKey() {
    	return getId();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + ((this.url != null) ? this.url.hashCode() : 0);

        return hash;
    }

    public Boolean isPrimary() {
        return primaryMedia;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    void setConceptMetadata(ConceptMetadata conceptMetadata) {
        this.conceptMetadata = conceptMetadata;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }
    
    public void setId(Long id) {
    	this.id = id;
    }

    public void setPrimary(Boolean primary) {
        this.primaryMedia = primary;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String stringValue() {
        return url; 
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append(" ([id=").append(getId()).append("] ");
        sb.append("url=").append(url).append(")");

        return sb.toString();
    }
}
