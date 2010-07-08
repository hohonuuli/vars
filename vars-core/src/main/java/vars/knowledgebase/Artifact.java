package vars.knowledgebase;

import java.util.Date;

/**
 * Reference to some artifact, such as plot, data-file, or other object that's useful
 * for the deep-sea guide. The artifact is meant to be used as follows:
 *  A unique identifier is [GroupID]-[ArtifactID]-[Version]-[Classfier]. Maybe something like:
 * [Graph]-[Depth Distribution Histogram]-[normalize_distibution]-[50m bins]
 */
public interface Artifact extends KnowledgebaseObject {

    ConceptMetadata getConceptMetadata();

    /**
     * The highlevel grouping of artifacts. (REQUIRED)
     * @return
     */
    String getGroupId();

    void setGroupId(String group);

    /**
     * 2nd level grouping of artifacts (REQUIRED)
     * @return
     */
    String getArtifactId();

    void setArtifactId(String key);

    /**
     * The version of the artifact (REQUIRED)
     * @return
     */
    String getVersion();

    void setVersion(String version);

    /**
     * Optional, may be used to differentiate between two similar artifacts
     * @return
     */
    String getClassifier();

    void setClassifier(String classifier);

    /**
     * A String representation of a URL that points to the object this artifact
     * references. (REQUIRED)
     * @return
     */
    String getReference();

    void setReference(String reference);

    /**
     * The mime type of the artifact
     * @return
     */
    String getMimeType();

    void setMimeType(String mimeType);

    /**
     * Textual description of the artifact
     * @return
     */
    String getDescription();

    void setDescription(String description);

    /**
     * Caption to be displayed under the artifact in a web representation
     * @return
     */
    String getCaption();

    void setCaption(String caption);

    /**
     * Credit for the authorship of the artifact
     * @return
     */
    String getCredit();

    void setCredit(String credit);

    /**
     * Date the artifact was created
     * @return
     */
    Date getDate();

    void setDate(Date date);

}
