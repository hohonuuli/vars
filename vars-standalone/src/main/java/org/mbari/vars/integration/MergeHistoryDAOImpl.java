/*
 * @(#)MergeHistoryDAOImpl.java   2013.03.07 at 02:31:42 PST
 *
 * Copyright 2009 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package org.mbari.vars.integration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javax.inject.Inject;
import org.mbari.expd.Dive;
import org.mbari.expd.DiveDAO;
import org.mbari.sql.DBException;
import org.mbari.sql.QueryFunction;
import org.mbari.sql.QueryableImpl;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.VideoArchiveSet;
import vars.annotation.VideoArchiveSetDAO;
import vars.integration.MergeHistory;
import vars.integration.MergeHistoryDAO;

/**
 * @author Brian Schlining
 * @since 2013-03-06
 */
public class MergeHistoryDAOImpl extends QueryableImpl implements MergeHistoryDAO {

    private static final String SQL_SELECT_FROM = "SELECT ms.id, ms.VideoArchiveSetID_FK, ms.MergeDate, " +
            "ms.MergeType, ms.IsNavigationEdited, ms.StatusMessage, ms.VideoFrameCount, ms.DateSource, ms.IsHD " +
            "FROM EXPDMergeHistory as ms";
    private static final ResourceBundle bundle = ResourceBundle.getBundle("annotation-jdbc", Locale.US);
    private static final QueryFunction<List<MergeHistory>> QUERY_FUNCTION = new QueryFunction<List<MergeHistory>>() {

        public List<MergeHistory> apply(ResultSet resultSet) throws SQLException {

            final List<MergeHistory> mergeHistories = new ArrayList<>();

            while (resultSet.next()) {
                Long id = resultSet.getLong(1);
                Long videoArchiveSetId = resultSet.getLong(2);
                Date mergeDate = resultSet.getTimestamp(3, CALENDAR);
                String mergeType = resultSet.getString(4);
                Boolean navigationEdited = (resultSet.getInt(5) == 0) ? false : true;
                String statusMessage = resultSet.getString(6);
                Integer videoFrameCount = resultSet.getInt(7);
                String dateSource = resultSet.getString(8);
                Boolean hd = (resultSet.getInt(9) == 0) ? false : true;

                MergeHistory mergeHistory = new MergeHistory(id, videoArchiveSetId, mergeDate, mergeType,
                        navigationEdited, statusMessage, videoFrameCount, dateSource, hd);

                mergeHistories.add(mergeHistory);
            }

            return mergeHistories;    // Don't need to return this really as it closes over mergeHistories
        }
    };

    /**  */
    public static final DateFormat DATE_FORMAT_UTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") {

        {
            setTimeZone(TimeZone.getTimeZone("UTC"));
        }
    };
    private static final DateFormat DATE_FORMAT_TRACKINGNUMBER = new SimpleDateFormat("yyyyDDD") {

        {
            setTimeZone(TimeZone.getTimeZone("UTC"));
        }
    };

    /**  */
    public static final Calendar CALENDAR = new GregorianCalendar(TimeZone.getTimeZone("UTC"));

    /**
     * Function used for queries that return a List of primary keys (Longs)
     */
    public final QueryFunction<List<Long>> ID_FUNCTION = new QueryFunction<List<Long>>() {

        public List<Long> apply(ResultSet resultSet) throws SQLException {
            List<Long> ids = new ArrayList<>();
            while (resultSet.next()) {
                ids.add(resultSet.getLong(1));
            }
            Collections.sort(ids);

            return ids;
        }
    };
    private final AnnotationDAOFactory annotationDAOFactory;
    private final DiveDAO diveDAO;

    /**
     * Constructs ...
     *
     * @param annotationDAOFactory
     * @param diveDAO
     */
    @Inject
    public MergeHistoryDAOImpl(AnnotationDAOFactory annotationDAOFactory, DiveDAO diveDAO) {
        super(bundle.getString("jdbc.url"), bundle.getString("jdbc.username"), bundle.getString("jdbc.password"),
                bundle.getString("jdbc.driver"));

        this.annotationDAOFactory = annotationDAOFactory;
        this.diveDAO = diveDAO;

        try {
            Class.forName(bundle.getString("jdbc.driver"));
        }
        catch (ClassNotFoundException ex) {
            throw new DBException("Failed to initialize driver class:" + bundle.getString("jdbc.driver"), ex);
        }
    }

    /**
     * Find MergeHistory objects for the given VideoArchiveSet
     * @param videoArchiveSetId
     * @return A List of all MergeHistory objects for the given VideoArchiveSet. Sorted by most
     *      recent date first.
     */
    @Override
    public List<MergeHistory> find(final Long videoArchiveSetId, final boolean isHD) {
        int hd = isHD ? 1 : 0;
        if (videoArchiveSetId != null) {
            String sql = SQL_SELECT_FROM + " WHERE ms.VideoArchiveSetID_FK = " + videoArchiveSetId +
                    "AND IsHD = " + hd + " ORDER BY MergeDate DESC";

            return executeQueryFunction(sql, QUERY_FUNCTION);
        }
        else {
            return new ArrayList<>();
        }
    }

    /**
     * Find the most recent MergeHistory for every merge in the database.
     *
     * See http://stackoverflow.com/questions/1049702/create-a-sql-query-to-retrieve-most-recent-records
     * @return A list of MergeHistory objects containing the most recent one for each VideoArchiveSet
     *  that was merged.
     */
    public List<MergeHistory> findAllMostRecent(final boolean isHD) {
        int hd = isHD ? 1 : 0;
        String sql = "SELECT m.id, m.VideoArchiveSetID_FK, m.MergeDate FROM EXPDMergeHistory m INNER JOIN " +
                "(SELECT max(mergeDate) AS LatestDate, VideoArchiveSetID_FK " +
                " FROM EXPDMergeHistory GROUP BY VideoArchiveSetID_FK) d " + "ON m.MergeDate = d.LatestDate " +
                "AND m.VideoArchiveSetID_FK = d.VideoArchiveSetID_FK " +
                "WHERE IsHD = " + hd + " " +
                "ORDER BY m.MergeDate DESC";

        List<Long> ids = executeQueryFunction(sql, ID_FUNCTION);

        List<MergeHistory> histories = new ArrayList<>(ids.size());
        for (Long id : ids) {
            sql = SQL_SELECT_FROM + " WHERE ms.id = " + id;
            histories.addAll(executeQueryFunction(sql, QUERY_FUNCTION));
        }

        return histories;
    }

    /**
     * Finds all MergeHistory objects for the given platform and sequenceNumber.
     *
     * @param platform
     * @param sequenceNumber
     * @return A list of all histories (most recent first) or an empty list if no matches are found.
     */
    @Override
    public List<MergeHistory> findByPlatformAndSequenceNumber(String platform, Number sequenceNumber, boolean isHD) {
        int hd = isHD ? 1 : 0;
        String sql = "SELECT ms.VideoArchiveSetID_FK FROM " + "VideoArchiveSet as vas LEFT OUTER JOIN " +
                "EXPDMergeHistory as ms ON ms.VideoArchiveSetID_FK = vas.id LEFT OUTER JOIN " +
                "CameraPlatformDeployment as cpd ON cpd.VideoArchiveSetID_FK = vas.id " +
                "WHERE vas.PlatformName = '" + platform + "' AND cpd.SeqNumber = " + sequenceNumber +
                " AND IsHD = " + hd;
        List<Long> ids = executeQueryFunction(sql, ID_FUNCTION);
        Long id = (ids.size() > 0) ? ids.get(0) : null;
        if (id == null) {
            return new ArrayList<>();
        }
        else {
            return find(id, isHD);
        }
    }

    /**
     *
     * @param videoArchiveSetId
     * @return
     */
    public MergeHistory findMostRecent(final Long videoArchiveSetId, boolean isHD) {
        List<MergeHistory> mergeHistories = find(videoArchiveSetId, isHD);
        if (mergeHistories.isEmpty()) {
            return null;
        }
        else {
            return mergeHistories.get(0);
        }
    }

    /**
     * @return
     */
    @Override
    public List<Long> findSetsWithEditedNav() {
        // Fetch both HD and beta
        List<MergeHistory> allHistories = findAllMostRecent(true);
        allHistories.addAll(findAllMostRecent(false));

        List<Long> mergedWithRawNav = new ArrayList<>();
        for (MergeHistory history : allHistories) {
            if (!history.isNavigationEdited()) {
                mergedWithRawNav.add(history.getVideoArchiveSetID());
            }
        }
        log.debug("Found " + mergedWithRawNav.size() + " MergeHistories that used raw navigation");

        List<Long> good = new ArrayList<Long>();
        VideoArchiveSetDAO dao = annotationDAOFactory.newVideoArchiveSetDAO();
        dao.startTransaction();
        for (Long id : mergedWithRawNav) {
            VideoArchiveSet videoArchiveSet = dao.findByPrimaryKey(id);
            if (videoArchiveSet == null) {
                log.info("Unable to find VideoArchiveSet with id = " + id + " in the database");
                continue;
            }

            String platform = videoArchiveSet.getPlatformName();

            /*
             * A VideoArchiveSet must have exactly 1 cameraPlatformDeployment in
             * order to merge it
             */
            Integer sequenceNumber = null;
            if (videoArchiveSet.getCameraDeployments().size() == 1) {
                sequenceNumber = videoArchiveSet.getCameraDeployments().iterator().next().getSequenceNumber();
            }
            else {
                log.info(videoArchiveSet + " represents more than one CameraDeployment. Unable to merge it");
                continue;
            }

            /*
             * Verify that there is dive info in EXPD for the dive
             */
            Dive dive = diveDAO.findByPlatformAndDiveNumber(platform, sequenceNumber);
            if ((dive == null) || (dive.getStartDate() == null) || (dive.getEndDate() == null)) {
                log.info("Dive info is not available in EXPD for " + platform + " #" + sequenceNumber);
                continue;
            }

            /*
             * Query CleanRovNavLoad in EXPD (use YYYYDDD in filename column)
             */
            String sql = "SELECT id FROM CleanRovNavLoad WHERE fileName LIKE '%" +
                    DATE_FORMAT_TRACKINGNUMBER.format(dive.getStartDate()) + platform.substring(0, 1) +
                    "%' AND isLoaded > 0";
            List<Long> r = ((QueryableImpl) diveDAO).executeQueryFunction(sql, ID_FUNCTION);
            if (r.size() > 0) {
                good.add(id);
            }

        }
        dao.endTransaction();
        dao.close();

        return good;
    }

    /**
     * @return
     */
    @Override
    public List<Long> findUnmergedSets() {
        return executeQueryFunction("SELECT id FROM VideoArchiveSet WHERE id NOT IN (" +
                "SELECT VideoArchiveSetID_FK FROM EXPDMergeHistory)", ID_FUNCTION);
    }

    /**
     * @return
     */
    @Override
    public List<Long> findUpdatedSets() {
        // Fetch both HD and Beta
        List<MergeHistory> recentHistories = findAllMostRecent(true);
        recentHistories.addAll(findAllMostRecent(false));

        QueryFunction<Integer> countFunction = new QueryFunction<Integer>() {
            public Integer apply(ResultSet resultSet) throws SQLException {
                return resultSet.next() ? resultSet.getInt(1) : 0;
            }
        };
        List<Long> updatedSets = new ArrayList<Long>();
        for (MergeHistory history : recentHistories) {
            Long id = history.getVideoArchiveSetID();
            String sql = "SELECT COUNT(VideoArchiveSetID_FK) FROM Annotations WHERE VideoArchiveSetID_FK = " + id +
                    " AND ObservationDate > (" +
                    "SELECT MAX(MergeDate) FROM EXPDMergeHistory WHERE VideoArchiveSetID_FK = " + id + ")";
            Integer count = executeQueryFunction(sql, countFunction);

            if (count > 0) {
                updatedSets.add(id);
            }
        }

        return updatedSets;
    }

    /**
     *
     * @param mergeHistory
     */
    @Override
    public void update(MergeHistory mergeHistory) {

        int navEdited = mergeHistory.isNavigationEdited() ? 1 : 0;
        int isHD = mergeHistory.isHd() ? 1 : 0;

        String sql = "INSERT INTO EXPDMergeHistory (" +
                "VideoArchiveSetID_FK, MergeDate, MergeType, IsNavigationEdited, StatusMessage, " +
                "VideoFrameCount, DateSource, IsHD) " + "VALUES (" +
                mergeHistory.getVideoArchiveSetID() +
                ", CONVERT(DATETIME, '" + DATE_FORMAT_UTC.format(mergeHistory.getMergeDate()) + "', 120)" +
                ", '" +  mergeHistory.getMergeType() + "', " +
                navEdited +
                ", '" + mergeHistory.getStatusMessage() + "', " +
                mergeHistory.getVideoFrameCount() +
                ", '" + mergeHistory.getDateSource() + "', " +
                isHD + ")";

        executeUpdate(sql);
    }
}
