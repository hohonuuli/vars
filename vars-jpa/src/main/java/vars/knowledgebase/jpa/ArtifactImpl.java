package vars.knowledgebase.jpa;

import vars.jpa.JPAEntity;
import vars.jpa.KeyNullifier;
import vars.jpa.TransactionLogger;
import vars.knowledgebase.Artifact;
import vars.knowledgebase.ConceptMetadata;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 
 */
@Entity(name = "Artifact")
@Table( name = "Artifact")
@EntityListeners({TransactionLogger.class, KeyNullifier.class})
@NamedQueries( {
    @NamedQuery(name = "Artifact.findById", query = "SELECT a FROM Artifact a WHERE a.id = :id") ,
    @NamedQuery(name = "Artifact.findByReference", query = "SELECT a FROM Artifact a WHERE a.reference = :reference"),
    @NamedQuery(name = "Artifact.findAllByGroup", query = "SELECT c FROM Artifact c WHERE c.groupId = :group") ,
    @NamedQuery(name = "Artifact.findAllByGroupAndKey", query = "SELECT c FROM Artifact c WHERE c.groupId = :group AND c.artifactId = :key") ,
    @NamedQuery(name = "Artifact.findAllByGroupKeyAndVersion", query = "SELECT c FROM Artifact c WHERE c.groupId = :group AND c.artifactId = :key AND c.version = :version") ,
    @NamedQuery(name = "Artifact.findByParameters", query = "SELECT c FROM Artifact c, IN (c.conceptMetadata.concept.conceptNames) AS n WHERE c.groupId = :group AND c.artifactId = :key AND c.version = :version AND c.classifier = :classifier AND n.name = :name") ,
    @NamedQuery(name = "Artifact.findByGroupKeyAndVersion", query = "SELECT c FROM Artifact c, IN (c.conceptMetadata.concept.conceptNames) AS n WHERE c.groupId = :group AND c.artifactId = :key AND c.version = :version AND n.name = :name") ,
    @NamedQuery(name = "Artifact.findByGroupAndKey", query = "SELECT c FROM Artifact c, IN (c.conceptMetadata.concept.conceptNames) AS n WHERE c.groupId = :group AND c.artifactId = :key AND n.name = :name") ,
    @NamedQuery(name = "Artifact.findByGroup", query = "SELECT c FROM Artifact c, IN (c.conceptMetadata.concept.conceptNames) AS n WHERE c.groupId = :group AND n.name = :name") ,
    @NamedQuery(name = "Artifact.findAll", query = "SELECT c FROM Artifact c"),
    @NamedQuery(name = "Artifact.findAllByConcept", query = "SELECT c FROM Artifact c, IN (c.conceptMetadata.concept.conceptNames) AS n WHERE n.name = :name")
})
public class ArtifactImpl implements Artifact, Serializable, JPAEntity {

    @Id
    @Column(
        name = "id",
        nullable = false,
        updatable = false
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Artifact_Gen")
    @TableGenerator(
        name = "Artifact_Gen",
        table = "UniqueID",
        pkColumnName = "TableName",
        valueColumnName = "NextID",
        pkColumnValue = "Artifact",
        allocationSize = 1
    )
    private Long id;

    @ManyToOne(optional = false, targetEntity = ConceptMetadataImpl.class, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "ConceptDelegateID_FK")
    private ConceptMetadata conceptMetadata;

    @Column(name = "GroupId", nullable = false, length = 64)
    private String groupId;

    @Column(name = "ArtifactId", nullable = false, length = 256)
    private String artifactId;

    @Column(name = "Version", nullable = false, length = 64)
    private String version;

    @Column(name = "Classifier", length = 64)
    private String classifier;

    @Column(name = "Reference", nullable = false, length = 1024)
    private String reference;

    @Column(name = "MimeType", length = 32)
    private String mimeType;

    @Column(name = "Description", length = 2048)
    private String description;

    @Column(name = "Caption", length = 1024)
    private String caption;

    @Column(name = "Credit", length = 1024)
    private String credit;

    @Column(name = "CreationDate")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date date;

    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime;


    public ConceptMetadata getConceptMetadata() {
        return conceptMetadata;
    }

    public void setConceptMetadata(ConceptMetadata conceptMetadata) {
        this.conceptMetadata = conceptMetadata;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Object getPrimaryKey() {
        return id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArtifactImpl artifact = (ArtifactImpl) o;

        if (classifier != null ? !classifier.equals(artifact.classifier) : artifact.classifier != null) return false;
        if (groupId != null ? !groupId.equals(artifact.groupId) : artifact.groupId != null) return false;
        if (artifactId != null ? !artifactId.equals(artifact.artifactId) : artifact.artifactId != null) return false;
        if (version != null ? !version.equals(artifact.version) : artifact.version != null) return false;
        if (reference != null ? !reference.equals(artifact.reference) : artifact.reference != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = groupId != null ? groupId.hashCode() : 0;
        result = 31 * result + (artifactId != null ? artifactId.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (classifier != null ? classifier.hashCode() : 0);
        result = 31 * result + (reference != null ? reference.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ArtifactImpl ([id = " + id +
                "] group='" + groupId + '\'' +
                ", key='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                ", classifier='" + classifier + '\'' +
                ')';
    }
}
