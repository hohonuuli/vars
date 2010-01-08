package vars.annotation

import java.text.SimpleDateFormat
import org.slf4j.LoggerFactory
import org.mbari.sql.QueryFunction


class EXPDMergeStatusDAO {

    private static final dateFormat = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss')
    private static final tnFormat = new SimpleDateFormat('yyyyDDD')
    private final log = LoggerFactory.getLogger(EXPDMergeStatusDAO.getClass());
    final AnnotationPersistenceService annotationPersistenceService
    final AnnotationDAOFactory annotationDAOFactory

    def EXPDMergeStatusDAO(AnnotationDAOFactory annotationDAOFactory1, AnnotationPersistenceService annotationPersistenceService1) {
        this.annotationDAOFactory = annotationDAOFactory1
        this.annotationPersistenceService = annotationPersistenceService1
    }

    private final handler = { resultSet ->
        def list = []
        while (resultSet.next()) {
            list << resultSet.getLong(1)
        }
        return list.sort()
    } as QueryFunction

    void update(EXPDMergeStatus expdMergeStatus) {

        def id = annotationPersistenceService.executeQueryFunction("SELECT videoArchiveSetID_FK FROM EXPDMergeStatus " +
                "WHERE videoArchiveSetID_FK = ${expdMergeStatus.videoArchiveSetID_FK}",
                { resultSet -> resultSet.next() ? resultSet.getLong(1) : null } as QueryFunction );

        def sql = null
        if (id) {
            sql = "UPDATE EXPDMergeStatus SET " +
                    "MergeDate = CONVERT(DATETIME, '${dateFormat.format(expdMergeStatus.mergeDate)}', 120), " +
                    "IsNavigationEdited = ${expdMergeStatus.navigationEdited}, " +
                    "StatusMessage = '${expdMergeStatus.statusMessage}', " +
                    "VideoFrameCount = ${expdMergeStatus.videoFrameCount}, " +
                    "IsMerged = ${expdMergeStatus.merged}, " +
                    "DateSource = '${expdMergeStatus.dateSource}' " +
                    "WHERE VideoArchiveSetID_FK = ${expdMergeStatus.videoArchiveSetID_FK}"
        }
        else {
            sql = "INSERT INTO EXPDMergeStatus (" +
                    "VideoArchiveSetID_FK, MergeDate, IsNavigationEdited, StatusMessage, " +
                    "VideoFrameCount, IsMerged, DateSource) " +
                    "VALUES (${expdMergeStatus.videoArchiveSetID_FK}, " +
                    "CONVERT(DATETIME, '${dateFormat.format(expdMergeStatus.mergeDate)}', 120), " +
                    "${expdMergeStatus.navigationEdited}, '${expdMergeStatus.statusMessage}', " +
                    "${expdMergeStatus.videoFrameCount}, ${expdMergeStatus.merged}, " +
                    "'${expdMergeStatus.dateSource}')"
        }
        log.info("Executing the following SQL:\n${sql}")
        annotationPersistenceService.executeUpdate(sql);
    }

    /**
     * Find any VideoArchiveSets that have not been merged
     *
     * @return A list of Longs (primary keys for VideoArchiveSets)
     */
    List findUnmergedSets() {
        def sql = "SELECT id FROM VideoArchiveSet WHERE id NOT IN (" +
                "SELECT VideoArchiveSetID_FK FROM EXPDMergeStatus)"
        return annotationPersistenceService.executeQueryFunction(sql, handler)
    }

    /*
    * Find any merge status messages containing the given string.
    * Use % for wild cards; for example findByStatusMessage('%CONSERVATIVE%')
    *
    * @return A list of Longs (primary keys for VideoArchiveSets)
    */
    static List findByStatusMessage(String msg) {
        def sql = "SELECT VideoArchiveSetID_FK FROM EXPDMergeStatus " +
                "WHERE StatusMessage LIKE '${msg}' ORDER BY MergeDate"
        return annotationPersistenceService.executeQueryFunction(sql, handler)
    }

    /**
     * Find any VideoArchiveSets whose merge failed
     *
     * @return A list of Longs (primary keys for VideoArchiveSets)
     */
    static List findFailedSets() {
        def sql = "SELECT VideoArchiveSetID_FK FROM EXPDMergeStatus WHERE IsMerged = 0"
        return annotationPersistenceService.executeQueryFunction(sql, handler)
    }

    /**
     * Finds sets that were merged with raw nav but that now have edited nav
     * available
     *
     * @return A list of Longs (primary keys for VideoArchiveSets)
     */
    static List findSetsWithEditedNav() {
        // Find rows in EXPDMergeStatus where the IsNavigationEdited == 0
        def sql = "SELECT VideoArchiveSetID_FK FROM EXPDMergeStatus WHERE isNavigationEdited = 0"
        def ids = annotationPersistenceService.executeQueryFunction(sql, handler)

        // Figure out the tracking number (YYYYDDD) for each
        def good = []
        for (id in ids) {
            def videoArchiveSet = annotationDAOFactory.newDAO().findByPrimaryKey(id);
            if (videoArchiveSet == null) {
                log.warn("Unable to load VideoArchiveSet with id = ${id}")
                continue
            }

            def platform = videoArchiveSet.platformName

            /*
            * A VideoArchiveSet must have exactly 1 cameraPlatformDeployment in
            * order to merge it
            */
            def seqNumber = null
            def cds = videoArchiveSet.cameraDeployments
            if (cds.size() == 1) {
                seqNumber = cds.iterator().next().sequenceNumber
            }
            else {
                continue
            }

            // Lookup starting date by platform and seqNumber
            def dateBounds = EXPDDAO.findDateBounds(platform, seqNumber)
            if (dateBounds?.size() == 0) {
                continue
            }
            def yyyyddd = tnFormat.format(dateBounds[0])

            // Query CleanRovNavLoad in EXPD (use YYYYDDD in filename column)
            sql = "SELECT id FROM CleanRovNavLoad WHERE fileName LIKE '%${yyyyddd}${platform[0]}%' AND isLoaded > 0"
            def r = EXPDDAO.query(sql, handler)
            if (r?.size() > 0) {
                good << id
            }
        }
        return good
    }

    /**
     * Finds VideoArchiveSets that have observations that were created since the
     * last merge
     *
     * @return A list of Longs (primary keys for VideoArchiveSets)
     */
    static List findUpdatedSets() {
        def resultHandler = { resultSet ->
            def n = 0
            if (resultSet.next()) {
                n = resultSet.getInt(1)
            }
            return n
        }

        def updatedSets = []

        /*
        * Loop through all sets that have been merged before. If it contains
        * annotations that were created after the last merge for that set that
        * set is added to updateSets
        */
        def sql = "SELECT VideoArchiveSetID_FK FROM EXPDMergeStatus"
        def ids = DAO.query(sql, handler)
        for (id in ids) {
            sql = "SELECT COUNT(VideoArchiveSetID_FK) FROM Annotations " +
                    "WHERE VideoArchiveSetID_FK = ${id} AND ObservationDate > (" +
                    "SELECT MergeDate FROM EXPDMergeStatus WHERE VideoArchiveSetID_FK = ${id})"

            def count = DAO.query(sql, resultHandler)
            if (count != null && count > 0) {
                updatedSets << id
            }
        }
        return updatedSets
    }

    /**
     * @return a Long (primary key for VideoArchiveSets). <b>null</b> if no match is found
     */
    static findByPlatformAndSeqNumber(String platform, seqNumber) {
        def platformString = "'${platform}'" // Don't embed single-quote gstrings in SQL

        def sql = """\
SELECT
   ms.VideoArchiveSetID_FK
FROM
    VideoArchiveSet as vas LEFT OUTER JOIN
    EXPDMergeStatus as ms ON ms.VideoArchiveSetID_FK = vas.id LEFT OUTER JOIN
    CameraPlatformDeployment as cpd ON cpd.VideoArchiveSetID_FK = vas.id
WHERE
    vas.PlatformName = $platformString AND
    cpd.SeqNumber = $seqNumber
        """
        def ids = DAO.query(sql, handler)
        return ids.size() ? ids[0] : null
    }

    /**
     * Retrive a EXPDMergeStatus Object from the database
     * @param id The videoArchiveSetID_FK to use for lookup
     */
    static find(def id) {
        def sql = """\
SELECT
   ms.MergeDate,
   ms.IsNavigationEdited,
   ms.StatusMessage,
   ms.VideoFrameCount,
   ms.IsMerged,
   ms.DateSource
FROM
    EXPDMergeStatus as ms
WHERE
    ms.VideoArchiveSetID_FK = ${id}
        """

        def mergeStatus = new EXPDMergeStatus(videoArchiveSetID_FK: id as Long)
        def resultHandler = {resultSet ->
            if (resultSet.next()) {
                mergeStatus.mergeDate = resultSet.getTimestamp(1)
                mergeStatus.navigationEdited = resultSet.getInt(2)
                mergeStatus.statusMessage = resultSet.getString(3)
                mergeStatus.videoFrameCount = resultSet.getInt(4)
                mergeStatus.merged = resultSet.getInt(5)
                mergeStatus.dateSource = resultSet.getString(6)
            }
        }
        DAO.query(sql, resultHandler)
        return mergeStatus
    }



}