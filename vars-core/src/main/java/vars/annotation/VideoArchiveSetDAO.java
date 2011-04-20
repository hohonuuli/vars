package vars.annotation;

import vars.DAO;
import vars.knowledgebase.Concept;

import java.util.Set;
import java.util.Date;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 2:52:32 PM
 * To change this template use File | Settings | File Templates.
 */
public interface VideoArchiveSetDAO extends DAO {

    Set<String> findAllLinkValues(VideoArchiveSet videoArchiveSet, String linkName);

    Set<String> findAllLinkValues(VideoArchiveSet videoArchiveSet, String linkName, Concept concept);

    Collection<VideoArchiveSet> findAllBetweenDates(Date startDate, Date endDate);

    Collection<VideoArchiveSet> findAll();

    Collection<VideoArchiveSet> findAllByPlatformAndSequenceNumber(String platform, int sequenceNumber);

    Collection<VideoArchiveSet> findAllByPlatformAndTrackingNumber(String platform, String trackingNumber);

    Collection<VideoArchiveSet> findAllByTrackingNumber(String trackingNumber);

    Collection<VideoArchiveSet> findAllByPlatform(String platform);

    /**
     * Find all sequence numbers (e.g. dive numbers) available for a particular platform
     * @param platformName The platform of interest
     * @return A set of all dive numbers in the database for a given platform. An empty set is returned
     *      if no numbers are found.
     */
    Set<Integer> findAllSequenceNumbersByPlatformName(String platformName);

    /**
     * Supposed to be a fast lookup of videoframe count in the database.
     * @param primaryKey The VideoArchiveSets primary key
     * @return A count of videoframes contained by this videoarchiveset
     */
    Integer findVideoFrameCountByPrimaryKey(Object primaryKey);

    /**
     * Looks up all VideoArchiveSets that do not havea cameradeployment associated with them
     * @return
     */
    Collection<VideoArchiveSet> findAllWithoutCameraDeployment();

    Collection<VideoArchiveSet> findAllWithoutTrackingNumber();

    /**
     * Find all videoarchivesets that duplicate platform and sequence numbers
     * @return
     */
    Collection<VideoArchiveSet> findAllThatDuplicatePlatformAndSequenceNumber();

    Collection<VideoArchiveSet> findAllWithMultipleCameraDeployments();

    /**
     * Look up VideoArchiveSets without startDate or endDate values
     * @return
     */
    Collection<VideoArchiveSet> findAllWithoutDates();

    VideoArchiveSet findByPrimaryKey(Object primaryKey);

}
