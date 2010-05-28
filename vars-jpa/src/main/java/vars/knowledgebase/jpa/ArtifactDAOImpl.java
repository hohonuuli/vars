package vars.knowledgebase.jpa;

import com.google.inject.Inject;
import vars.jpa.DAO;
import vars.knowledgebase.Artifact;
import vars.knowledgebase.ArtifactDAO;

import javax.persistence.EntityManager;
import java.util.Collection;

/**
 * @author brian
 */
public class ArtifactDAOImpl extends DAO implements ArtifactDAO {

    @Inject
    public ArtifactDAOImpl(EntityManager entityManager) {
        super(entityManager);
    }

    public Collection<Artifact> find(String concept, String group, String key) {
        return findByNamedQuery("Artifact.findByGroupAndKey", toParameterMap("group", group, "key", key, "name", concept));
    }

    public Collection<Artifact> find(String concept, String group, String key, String version) {
        return findByNamedQuery("Artifact.findByGroupKeyAndVersion",
                toParameterMap("group", group, "key", key, "version", version, "name", concept));
    }

    public Collection<Artifact> find(String concept, String group, String key, String version,
            String classifier) {
        return findByNamedQuery("Artifact.findByParameters",
                toParameterMap("group", group, "key", key, "version", version, "classifier", classifier,
                "name", concept));
    }

    /**
     * Find all artifacts that use the same reference (i.e. URL)
     * 
     * @param reference The reference we'll use to find matching artifacts
     * @return A collection of all {@link vars.knowledgebase.Artifact} objects that
     *      use the 'reference'.
     */
    public Collection<Artifact> findByReference(String reference) {
        return findByNamedQuery("Artifact.findByReference", toParameterMap("reference", reference));
    }
}
