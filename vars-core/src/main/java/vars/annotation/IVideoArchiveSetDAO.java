package vars.annotation;

import vars.IDAO;
import vars.knowledgebase.IConcept;

import java.util.Set;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 2:52:32 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IVideoArchiveSetDAO extends IDAO {

    Set<String> findAllReferenceNumbers(IVideoArchiveSet videoArchiveSet);

    Set<String> findAllReferenceNumbers(IVideoArchiveSet videoArchiveSet, IConcept concept);

    Set<IVideoArchiveSet> findAllBetweenDates(Date startDate, Date endDate);

    Set<IVideoArchiveSet> findAll();

    Set<IVideoArchiveSet> findAllByPlatformAndSequenceNumber(String platform, int sequenceNumber);

    Set<IVideoArchiveSet> findAllByPlatformAndTrackingNumber(String platform, String trackingNumber);

    Set<IVideoArchiveSet> findAllByTrackingNumber(String trackingNumber);

    /**
     * Find all sequence numbers (e.g. dive numbers) available for a particular platform
     * @param platform The platform of interest
     * @return A set of all dive numbers in the database for a given platform. An empty set is returned
     *      if no numbers are found.
     */
    Set<Integer> findAllDiveNumbersByPlatform(String platform);

    /**
     * Supposed to be a fast lookup of videoframe count in the database.
     * @param primaryKey THe VideoArchiveSets primary key
     * @return A count of videoframes contained by this videoarchiveset
     */
    Integer findVideoFrameCountByPrimaryKey(Object primaryKey);

    /**
     * Looks up all VideoArchiveSets that do not havea cameradeployment associated with them
     * @return
     */
    Set<IVideoArchiveSet> findAllWithoutCameraDeployment();

    Set<IVideoArchiveSet> findAllWithoutTrackingNumber();

    /**
     * Find all videoarchivesets that duplicate platform and sequence numbers
     * @return
     */
    Set<IVideoArchiveSet> findAllThatDuplicatePlatformAndSequenceNumber();

    Set<IVideoArchiveSet> findAllWithMultipleCameraDeployments();

    /**
     * Look up VideoArchiveSets without startDate or endDate values
     * @return
     */
    Set<IVideoArchiveSet> findAllWithoutDates();

}
