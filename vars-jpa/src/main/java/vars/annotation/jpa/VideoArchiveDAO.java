package vars.annotation.jpa;

import vars.jpa.DAO;
import vars.annotation.IVideoArchiveDAO;
import vars.annotation.IVideoArchive;
import vars.knowledgebase.IConcept;
import org.mbari.jpax.EAO;

import java.util.Set;

import com.google.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:41:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class VideoArchiveDAO extends DAO implements IVideoArchiveDAO{

    @Inject
    public VideoArchiveDAO(EAO eao) {
        super(eao);
    }

    public Set<String> findAllReferenceNumbers(IVideoArchive videoArchive) {
        return null;  // TODO implement this method.
    }

    public Set<String> findAllReferenceNumbers(IVideoArchive videoArchive, IConcept concept) {
        return null;  // TODO implement this method.
    }

    public IVideoArchive findOrCreateByParameters(String platform, int sequenceNumber, String videoArchiveName) {
        return null;  // TODO implement this method.
    }

    public IVideoArchive findByName(String name) {
        return null;  // TODO implement this method.
    }
}
