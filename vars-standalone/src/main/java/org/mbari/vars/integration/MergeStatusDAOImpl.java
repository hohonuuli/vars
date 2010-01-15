/*
 * @(#)MergeStatusDAOImpl.java   2010.01.14 at 09:28:23 PST
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

import com.google.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TimeZone;
import org.mbari.expd.Dive;
import org.mbari.expd.DiveDAO;
import org.mbari.sql.DBException;
import org.mbari.sql.QueryFunction;
import org.mbari.sql.QueryableImpl;
import vars.DAO;
import vars.integration.MergeStatusDAO;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.VideoArchiveSet;
import vars.integration.MergeStatus;

/**
 *
 * @author brian
 */
public class MergeStatusDAOImpl extends QueryableImpl implements MergeStatusDAO {

    private static final ResourceBundle bundle = ResourceBundle.getBundle("annotation-jdbc");

    /**  */
    public static final DateFormat DATE_FORMAT_UTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") {{
            setTimeZone(TimeZone.getTimeZone("UTC"));
    }};

    private static final DateFormat DATE_FORMAT_TRACKINGNUMBER = new SimpleDateFormat("yyyyDDD") {{
        setTimeZone(TimeZone.getTimeZone("UTC"));
    }};

    public static final Calendar CALENDAR = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    private final AnnotationDAOFactory annotationDAOFactory;
    private final DiveDAO diveDAO;

    /**
     * Function used for queries that return a List of primary keys (Longs)
     */
    public final QueryFunction<List<Long>> ID_FUNCTION = new QueryFunction<List<Long>>() {
        public List<Long> apply(ResultSet resultSet) throws SQLException {
            List<Long> ids = new ArrayList<Long>();
            while (resultSet.next()) {
                ids.add(resultSet.getLong(1));
            }
            Collections.sort(ids);
            return ids;
        }
    };

    /**
     * Constructs ...
     */
    @Inject
    public MergeStatusDAOImpl(AnnotationDAOFactory annotationDAOFactory, DiveDAO diveDAO) {
        super(bundle.getString("jdbc.url"), bundle.getString("jdbc.username"), bundle.getString("jdbc.password"),
              bundle.getString("jdbc.driver"));

        try {
            Class.forName(bundle.getString("jdbc.driver"));
        }
        catch (ClassNotFoundException ex) {
            throw new DBException("Failed to initialize driver class:" + bundle.getString("jdbc.driver"), ex);
        }

        this.annotationDAOFactory = annotationDAOFactory;
        this.diveDAO = diveDAO;
    }

    /**
     *
     * @param id
     */
    public MergeStatus find(Long id) {
        MergeStatus mergeStatus = null;
        if (id != null) {
            String sql = "SELECT ms.MergeDate, ms.IsNavigationEdited, ms.StatusMessage, " +
                    "ms.VideoFrameCount, ms.IsMerged, ms.DateSource FROM " +
                    "EXPDMergeStatus as ms WHERE ms.VideoArchiveSetID_FK = " + id;

            QueryFunction<MergeStatus> queryFunction = new QueryFunction<MergeStatus>() {
                public MergeStatus apply(ResultSet resultSet) throws SQLException {
                    MergeStatus mergeStatus = null;
                    if (resultSet.next()) {
                        mergeStatus = new MergeStatus();
                        mergeStatus.setMergeDate(resultSet.getTimestamp(1, CALENDAR));
                        mergeStatus.setNavigationEdited(resultSet.getInt(2));
                        mergeStatus.setStatusMessage(resultSet.getString(3));
                        mergeStatus.setVideoFrameCount(Long.valueOf(resultSet.getInt(4)));
                        mergeStatus.setMerged(resultSet.getInt(5));
                        mergeStatus.setDateSource(resultSet.getString(6));
                    }
                    return mergeStatus;
                }
            };

            mergeStatus = executeQueryFunction(sql, queryFunction);


        }
        return mergeStatus;
    }

    /**
     *
     * @param mergeStatus
     */
    public void update(MergeStatus mergeStatus) {

        if (mergeStatus.getMerged() == null) {
            mergeStatus.setMerged(0);
        }

        MergeStatus storedMergeStatus = mergeStatus.getVideoArchiveSetID() == null ?
            null : find(mergeStatus.getVideoArchiveSetID());

        String sql = null;
        if (storedMergeStatus != null) {
            sql = "UPDATE EXPDMergeStatus SET " +
                    "MergeDate = CONVERT(DATETIME, '" + DATE_FORMAT_UTC.format(mergeStatus.getMergeDate()) + "', 120), " +
                    "IsNavigationEdited = " + mergeStatus.getNavigationEdited() + ", " +
                    "StatusMessage = '" + mergeStatus.getStatusMessage() + "', " +
                    "VideoFrameCount = " + mergeStatus.getVideoFrameCount() + ", " +
                    "IsMerged = " + mergeStatus.getMerged() + ", " +
                    "DateSource = '" + mergeStatus.getDateSource() + "' " +
                    "WHERE VideoArchiveSetID_FK = " + mergeStatus.getVideoArchiveSetID();
        }
        else {
            sql = "INSERT INTO EXPDMergeStatus (" +
                    "VideoArchiveSetID_FK, MergeDate, IsNavigationEdited, StatusMessage, " +
                    "VideoFrameCount, IsMerged, DateSource) " +
                    "VALUES (" +
                    mergeStatus.getVideoArchiveSetID() +  ", " +
                    "CONVERT(DATETIME, '" + DATE_FORMAT_UTC.format(mergeStatus.getMergeDate()) + "', 120), " +
                    mergeStatus.getNavigationEdited() + ", '" +
                    mergeStatus.getStatusMessage() + "', " +
                    mergeStatus.getVideoFrameCount() + ", " +
                    mergeStatus.getMerged() + ", '" +
                    mergeStatus.getDateSource() +
                    "')";
        }

        executeUpdate(sql);
    }

    /**
     * Find any VideoArchiveSets that have not been merged
     *
     * @return A list of Longs (primary keys for VideoArchiveSets)
     */
    public List<Long> findUnmergedSets() {
        return executeQueryFunction("SELECT id FROM VideoArchiveSet WHERE id NOT IN (" +
                "SELECT VideoArchiveSetID_FK FROM EXPDMergeStatus)", ID_FUNCTION);
    }

   /**
    * Find any merge status messages containing the given string.
    * Use % for wild cards; for example findByStatusMessage('%CONSERVATIVE%')
    *
    * @return A list of Longs (primary keys for VideoArchiveSets)
    */
    public List<MergeStatus> findByStatusMessage(String msg) {
        List<Long> ids = executeQueryFunction("SELECT VideoArchiveSetID_FK FROM EXPDMergeStatus " +
                "WHERE StatusMessage LIKE '" + msg + "' ORDER BY MergeDate", ID_FUNCTION);
        List<MergeStatus> mergeStatuses = new ArrayList<MergeStatus>(ids.size());
        for (Long id : ids) {
            MergeStatus mergeStatus = find(id);
            if (mergeStatus != null) {
                mergeStatuses.add(mergeStatus);
            }
        }
        return mergeStatuses;
    }

    /**
     * Find any VideoArchiveSets whose merge failed
     *
     * @return A list of Longs (primary keys for VideoArchiveSets)
     */
    public List<Long> findFailedSets() {
        return executeQueryFunction("SELECT VideoArchiveSetID_FK FROM EXPDMergeStatus WHERE IsMerged = 0", ID_FUNCTION);
    }

    public List<Long> findSetsWithEditedNav() {
        List<Long> mergedWithRawNav = executeQueryFunction("SELECT VideoArchiveSetID_FK FROM EXPDMergeStatus WHERE isNavigationEdited = 0", ID_FUNCTION);
        List<Long> good = new ArrayList<Long>();
        DAO dao = annotationDAOFactory.newVideoArchiveSetDAO();
        dao.startTransaction();
        for (Long id : mergedWithRawNav) {
            VideoArchiveSet videoArchiveSet = dao.findByPrimaryKey(VideoArchiveSet.class, id);
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
            if (dive == null || dive.getStartDate() == null || dive.getEndDate() == null) {
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

        return good;
    }

    public List<Long> findUpdatedSets() {

        List<Long> updatedSets = new ArrayList<Long>();

        QueryFunction<Integer> countFunction = new QueryFunction<Integer>() {
            public Integer apply(ResultSet resultSet) throws SQLException {
                return resultSet.next() ? resultSet.getInt(1) : 0;
            }
        };

        List<Long> allInMergeStatusTable = executeQueryFunction("SELECT VideoArchiveSetID_FK FROM EXPDMergeStatus", ID_FUNCTION);
        for (Long id : allInMergeStatusTable) {
            String sql = "SELECT COUNT(VideoArchiveSetID_FK) FROM Annotations " +
                    "WHERE VideoArchiveSetID_FK = " + id + " AND ObservationDate > (" +
                    "SELECT MergeDate FROM EXPDMergeStatus WHERE VideoArchiveSetID_FK = " + id + ")";
            Integer count = executeQueryFunction(sql, countFunction);

            if (count > 0) {
                updatedSets.add(id);
            }
        }
        return updatedSets;
    }

    public MergeStatus findByPlatformAndSequenceNumber(String platform, Number sequenceNumber) {
        String sql = "SELECT ms.VideoArchiveSetID_FK FROM " +
                "VideoArchiveSet as vas LEFT OUTER JOIN " +
                "EXPDMergeStatus as ms ON ms.VideoArchiveSetID_FK = vas.id LEFT OUTER JOIN " +
                "CameraPlatformDeployment as cpd ON cpd.VideoArchiveSetID_FK = vas.id " +
                "WHERE vas.PlatformName = '" + platform + "' AND cpd.SeqNumber = " + sequenceNumber;
        List<Long> ids = executeQueryFunction(sql, ID_FUNCTION);
        Long id = ids.size() > 0 ? ids.get(0) : null;
        MergeStatus mergeStatus = null;
        if (id != null) {
            mergeStatus = find(id);
        }
        return mergeStatus;
    }


}
