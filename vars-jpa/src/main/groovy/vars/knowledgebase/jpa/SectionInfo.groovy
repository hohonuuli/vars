package vars.knowledgebase.jpa

import javax.persistence.Version
import javax.persistence.Column
import java.sql.Timestamp
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.TableGenerator
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.NamedQueries
import javax.persistence.NamedQuery
import javax.persistence.GenerationType
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import vars.knowledgebase.IConceptDelegate
import vars.knowledgebase.ISectionInfo;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Jun 19, 2009
 * Time: 10:03:40 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "SectionInfo")
@Table(name = "SectionInfo")
@NamedQueries( value = [
    @NamedQuery(name = "SectionInfo.findById",
                query = "SELECT v FROM SectionInfo v WHERE v.id = :id"),
    @NamedQuery(name = "SectionInfo.findByHeader", query = "SELECT s FROM SectionInfo s WHERE s.header = :header") ,
    @NamedQuery(name = "SectionInfo.findByLabel", query = "SELECT s FROM SectionInfo s WHERE s.label = :label") ,
    @NamedQuery(name = "SectionInfo.findByInformation",
                query = "SELECT s FROM SectionInfo s WHERE s.information = :information")
])
class SectionInfo implements Serializable, ISectionInfo {

    @Id
    @Column(name = "id", nullable = false, updatable=false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "SectionInfo_Gen")
    @TableGenerator(name = "SectionInfo_Gen", table = "UniqueID",
            pkColumnName = "TableName", valueColumnName = "NextID",
            pkColumnValue = "SectionInfo", allocationSize = 1)
    Long id

    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime

    @Column(name = "Header", length = 30)
    String header

    @Column(name = "Label", length = 50)
    String label

    @Column(name = "Information", length = 5000)
    String information

    @ManyToOne(optional = false, targetEntity = ConceptDelegate.class)
    @JoinColumn(name = "ConceptDelegateID_FK")
    IConceptDelegate conceptDelegate

    public String stringValue() {
        return null;  // TODO implement this method.
    }
}
