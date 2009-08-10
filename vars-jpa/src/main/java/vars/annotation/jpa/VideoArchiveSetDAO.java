package vars.annotation.jpa;

import vars.jpa.DAO;
import vars.annotation.IVideoArchiveSetDAO;
import vars.annotation.IVideoArchiveSet;
import vars.knowledgebase.IConcept;
import org.mbari.jpax.EAO;

import java.util.Set;
import java.util.Date;

import com.google.inject.Inject;

public class VideoArchiveSetDAO extends DAO implements IVideoArchiveSetDAO {

    @Inject
    public VideoArchiveSetDAO(EAO eao) {
        super(eao);
    }

    public Set<String> findAllReferenceNumbers(IVideoArchiveSet videoArchiveSet) {
        return null;  // TODO implement this method.
    }

    public Set<String> findAllReferenceNumbers(IVideoArchiveSet videoArchiveSet, IConcept concept) {
        return null;  // TODO implement this method.
    }

    public Set<IVideoArchiveSet> findAllBetweenDates(Date startDate, Date endDate) {
        return null;  // TODO implement this method.
    }

    public Set<IVideoArchiveSet> findAll() {
        return null;  // TODO implement this method.
    }

    public Set<IVideoArchiveSet> findAllByPlatformAndSequenceNumber(String platform, int sequenceNumber) {
        return null;  // TODO implement this method.
    }

    public Set<IVideoArchiveSet> findAllByPlatformAndTrackingNumber(String platform, String trackingNumber) {
        return null;  // TODO implement this method.
    }

    public Set<IVideoArchiveSet> findAllByTrackingNumber(String trackingNumber) {
        return null;  // TODO implement this method.
    }

    public Set<Integer> findAllDiveNumbersByPlatform(String platform) {
        return null;  // TODO implement this method.
    }

    public Integer findVideoFrameCountByPrimaryKey(Object primaryKey) {
        return null;  // TODO implement this method.
    }

    public Set<IVideoArchiveSet> findAllWithoutCameraDeployment() {
        return null;  // TODO implement this method.
    }

    public Set<IVideoArchiveSet> findAllWithoutTrackingNumber() {
        return null;  // TODO implement this method.
    }

    public Set<IVideoArchiveSet> findAllThatDuplicatePlatformAndSequenceNumber() {
        return null;  // TODO implement this method.
    }

    public Set<IVideoArchiveSet> findAllWithMultipleCameraDeployments() {
        return null;  // TODO implement this method.
    }

    public Set<IVideoArchiveSet> findAllWithoutDates() {
        return null;  // TODO implement this method.
    }
}
