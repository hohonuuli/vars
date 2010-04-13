package vars.knowledgebase;

import vars.DAO;

/**
 * @author brian
 */
public interface ArtifactDAO  extends DAO {

    Artifact find(String concept, String group, String key, String version);

    Artifact find(String concept, String group, String key, String version, String classifier);

}
