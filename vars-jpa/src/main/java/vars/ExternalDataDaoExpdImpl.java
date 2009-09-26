/*
 * @(#)ExternalDataDAOMBARIImpl.java   2009.09.21 at 09:01:57 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vars;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TimeZone;
import org.mbari.movie.Timecode;
import org.mbari.util.MathUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fetches data external to VARS from the MBARI EXPD database. THis is a read-only
 * class.
 * 
 * @author brian
 */
public class ExternalDataDaoExpdImpl implements ExternalDataDAO {

    public static final int SAMPLERATE_MILLSEC = 15 * 1000;
    private static final Calendar CALENDAR = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Standard format for all Dates. No timezone is displayed.
     * THe date will be formatted for the UTC timezone
     */
    private final DateFormat dateFormatUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") {{
            setTimeZone(TimeZone.getTimeZone("UTC"));
    }};

    /**
     * Filter for google-collections to remove {@link IVideoMoment} with
     * null timecodes
     */
    private final Predicate<IVideoMoment> nonNullTimecodePredicate = new Predicate<IVideoMoment>() {
        @Override
        public boolean apply(IVideoMoment arg0) {
            return arg0.getTimecode() != null;
        }
    };

    /**
     * Filter for google-collections to remove {@link IVideoMoment} with
     * null alternateTimecodes
     */
    private final Predicate<IVideoMoment> nonNullAlternateTimecodePredicate = new Predicate<IVideoMoment>() {
        @Override
        public boolean apply(IVideoMoment arg0) {
            return arg0.getAlternateTimecode() != null;
        }
    };
    
    private final ThreadLocal<Connection> connections = new ThreadLocal<Connection>();
    private final String jdbcPassword;
    private final String jdbcUrl;
    private final String jdbcUsername;

    /**
     * Constructs ...
     */
    public ExternalDataDaoExpdImpl() {
        ResourceBundle bundle = ResourceBundle.getBundle("external-dao");
        jdbcUrl = bundle.getString("jdbc.url");
        jdbcUsername = bundle.getString("jdbc.username");
        jdbcPassword = bundle.getString("jdbc.password");
        try {
            Class.forName(bundle.getString("jdbc.driver"));
        } catch (ClassNotFoundException ex) {
            throw new VARSException("Failed to initialize driver class:" + bundle.getString("jdbc.driver"), ex);
        }
    }

    /**
     * Retrive the HD timecode nearest to the given date for a platform
     * @param platform
     * @param date
     * @param millisecTolerance
     * @return The HD Timcode closest to the Date. <b>null</b> is returned if no
     *      timcode is found within the tolerance bounds
     */
    public IVideoMoment findTimecodeNearDate(String platform, Date date, int millisecTolerance) {

        List<IVideoMoment> tapeTimes = findTimecodesNearDate(platform, date, millisecTolerance);

        /*
         * Find the nearest time to our date
         */
        IVideoMoment nearestDateTimecode = null;
        long dtMin = Long.MAX_VALUE;
        for (IVideoMoment tapeTime : tapeTimes) {
            Date d = tapeTime.getRecordedDate();
            long dt = Math.abs(d.getTime() - date.getTime());
            if (dt < dtMin) {
                dtMin = dt;
                nearestDateTimecode = tapeTime;
            }
        }

        return nearestDateTimecode;
    }

    public List<IVideoMoment> findTimecodesNearDate(String platform, Date date, int millisecTolerance) {

        /*
         * Retrive HDTimecode and GMT from the EXPD Database and store in a
         * map for us to work with later.
         */
        List<IVideoMoment> dateTimecodes = new ArrayList<IVideoMoment>();
        String table = platform + "CamlogData";    // TODO map platforms to table names
        Date startDate = new Date(date.getTime() - millisecTolerance);
        Date endDate = new Date(date.getTime() + millisecTolerance);

        String sql = "SELECT DateTimeGMT, betaTimecode, hdTimecode  " + "FROM " + table + " " +
                     "WHERE DateTimeGMT BETWEEN '" + dateFormatUTC.format(startDate) + "' AND '" +
                     dateFormatUTC.format(endDate) + "' ORDER BY DateTimeGMT";
        Connection connection = null;
        try {
            connection = getConnection();
            Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            if (log.isDebugEnabled()) {
                log.debug("Executing query: " + sql);
            }

            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Date rovDate = resultSet.getTimestamp(1, CALENDAR);
                String timecode = resultSet.getString(2);
                String alternateTimecode = resultSet.getString(3);
                if (log.isDebugEnabled()) {
                    log.debug("Found record: " + "\n\tUTC Date:           " + dateFormatUTC.format(rovDate) +
                              "\n\tTimecode:           " + timecode + "\n\tAlternate Timecode: " + alternateTimecode);
                }

                dateTimecodes.add(new VideoMoment(rovDate, timecode, alternateTimecode));
            }

            resultSet.close();
            statement.close();
        }
        catch (SQLException e) {
            if (connection != null) {
                log.error("Failed to execute the following SQL on EXPD:\n" + sql, e);

                try {
                    connection.close();
                }
                catch (SQLException ex) {
                    log.error("Failed to close database connection", ex);
                }
            }

            throw new VARSException("Failed to execute the following SQL on EXPD: " + sql, e);
        }

        return dateTimecodes;

    }

    /**
     * @return A {@link Connection} to the EXPD database. The connection should
     *      be closed when you're done with it.
     */
    private Connection getConnection() throws SQLException {
        Connection connection = connections.get();
        if ((connection == null) || connection.isClosed()) {
            if (log.isDebugEnabled()) {
                log.debug("Opening JDBC connection:" + jdbcUsername + " @ " + jdbcUrl);
            }
            connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
            connections.set(connection);
        }

        return connection;
    }

    /**
     * Interpolates a given date to a timecode from data stored in the
     * datastore.
     *
     * @param cameraIdentifier The cameraIdentifier (e.g. Ventana or Tiburon)
     * @param date The date of that we're interested in
     * @param millisecTolerance Specifies the widthInMeters of the time window to pull samples
     *  from the database
     * @param frameRate The Frame rate to use. At MBARI, we use NTSC (29.97 fps)
     * @return The interpolated tape time value.
     */
    public IVideoMoment interpolateTimecodeByDate(String cameraIdentifier, Date date, int millisecTolerance,
            double frameRate) {

        /*
         * Retrieve the tapetimes and sort by date
         */
        List<IVideoMoment> videoTimes = findTimecodesNearDate(cameraIdentifier, date, millisecTolerance);

        IVideoMoment returnTapeTime = null;
        if (videoTimes.size() > 1) {

            /*
             * Extract the dates and timecodes (as frames) for interpolation
             */
            Timecode timecode = null;
            List<IVideoMoment> nonNullTimecodes = new ArrayList<IVideoMoment>(Collections2.filter(videoTimes,
                nonNullTimecodePredicate));
            Collections.sort(nonNullTimecodes, new VideoMomentByDateComparator());
            BigDecimal[] dates = new BigDecimal[nonNullTimecodes.size()];
            BigDecimal[] frames = new BigDecimal[nonNullTimecodes.size()];
            for (int i = 0; i < nonNullTimecodes.size(); i++) {
                IVideoMoment tapeTime = nonNullTimecodes.get(i);
                dates[i] = new BigDecimal(tapeTime.getRecordedDate().getTime());
                Timecode tc = new Timecode(tapeTime.getTimecode(), frameRate);
                frames[i] = new BigDecimal(tc.getFrames());
            }

            BigDecimal[] iFrame = MathUtilities.interpLinear(dates, frames,
                new BigDecimal[] { new BigDecimal(date.getTime()) });
            if ((iFrame != null) && (iFrame.length > 0) && (iFrame[0] != null)) {
                timecode = new Timecode(iFrame[0].doubleValue(), frameRate);
            }

            /*
             * Extract the dates and alternateTimecodes as frames for interpolation
             */
            Timecode alternateTimecode = null;
            List<IVideoMoment> nonNullAltTimecodes = new ArrayList<IVideoMoment>(Collections2.filter(videoTimes,
                nonNullAlternateTimecodePredicate));
            Collections.sort(nonNullAltTimecodes, new VideoMomentByDateComparator());
            dates = new BigDecimal[nonNullAltTimecodes.size()];
            frames = new BigDecimal[nonNullAltTimecodes.size()];

            for (int i = 0; i < nonNullAltTimecodes.size(); i++) {
                IVideoMoment tapeTime = nonNullAltTimecodes.get(i);
                dates[i] = new BigDecimal(tapeTime.getRecordedDate().getTime());
                Timecode tc = new Timecode(tapeTime.getTimecode(), frameRate);
                frames[i] = new BigDecimal(tc.getFrames());
            }

            iFrame = MathUtilities.interpLinear(dates, frames, new BigDecimal[] { new BigDecimal(date.getTime()) });

            if ((iFrame != null) && (iFrame.length > 0) && (iFrame[0] != null)) {
                alternateTimecode = new Timecode(iFrame[0].doubleValue(), frameRate);
            }

            /*
             * Interpolate to the new frame
             */
            String tc = (timecode == null) ? IVideoMoment.TIMECODE_INVALID : timecode.toString();
            String altTc = (alternateTimecode == null) ? IVideoMoment.TIMECODE_INVALID : alternateTimecode.toString();
            returnTapeTime = new VideoMoment(date, tc, altTc);
        }

        return returnTapeTime;

    }
}
