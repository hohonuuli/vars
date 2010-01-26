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

import org.mbari.sql.QueryableImpl;
import org.mbari.sql.QueryFunction;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import java.math.BigDecimal;
import java.sql.Connection;
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
public class EXPDPersistenceService extends QueryableImpl implements ExternalDataPersistenceService {

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
     * Filter for google-collections to remove {@link VideoMoment} with
     * null timecodes
     */
    private final Predicate<VideoMoment> nonNullTimecodePredicate = new Predicate<VideoMoment>() {
        public boolean apply(VideoMoment arg0) {
            return arg0.getTimecode() != null;
        }
    };

    /**
     * Filter for google-collections to remove {@link VideoMoment} with
     * null alternateTimecodes
     */
    private final Predicate<VideoMoment> nonNullAlternateTimecodePredicate = new Predicate<VideoMoment>() {
        public boolean apply(VideoMoment arg0) {
            return arg0.getAlternateTimecode() != null;
        }
    };
    
    private final ThreadLocal<Connection> connections = new ThreadLocal<Connection>();

    private static final ResourceBundle bundle = ResourceBundle.getBundle("annotation-jdbc");

    /**
     * Constructs ...
     */
    public EXPDPersistenceService() {
        super(bundle.getString("jdbc.url"), bundle.getString("jdbc.username"),
                bundle.getString("jdbc.password"), bundle.getString("jdbc.driver"));
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
    public VideoMoment findTimecodeNearDate(String platform, Date date, int millisecTolerance) {

        List<VideoMoment> tapeTimes = findTimecodesNearDate(platform, date, millisecTolerance);

        /*
         * Find the nearest time to our date
         */
        VideoMoment nearestDateTimecode = null;
        long dtMin = Long.MAX_VALUE;
        for (VideoMoment tapeTime : tapeTimes) {
            Date d = tapeTime.getRecordedDate();
            long dt = Math.abs(d.getTime() - date.getTime());
            if (dt < dtMin) {
                dtMin = dt;
                nearestDateTimecode = tapeTime;
            }
        }

        return nearestDateTimecode;
    }

    public List<VideoMoment> findTimecodesNearDate(String platform, Date date, int millisecTolerance) {

        /*
         * Retrive HDTimecode and GMT from the EXPD Database and store in a
         * map for us to work with later.
         */
        QueryFunction<List<VideoMoment>> queryFunction = new QueryFunction<List<VideoMoment>>() {
            public List<VideoMoment> apply(ResultSet resultSet) throws SQLException {
                List<VideoMoment> dateTimecodes = new ArrayList<VideoMoment>();
                while (resultSet.next()) {
                    Date rovDate = resultSet.getTimestamp(1, CALENDAR);
                    String timecode = resultSet.getString(2);
                    String alternateTimecode = resultSet.getString(3);
                    if (log.isDebugEnabled()) {
                        log.debug("Found record: " +
                                "\n\tUTC Date:           " + dateFormatUTC.format(rovDate) +
                                "\n\tTimecode:           " + timecode +
                                "\n\tAlternate Timecode: " + alternateTimecode);
                    }

                    dateTimecodes.add(new VideoMomentBean(rovDate, timecode, alternateTimecode));
                }
                return dateTimecodes;
            }
        };

        String table = platform + "CamlogData";
        Date startDate = new Date(date.getTime() - millisecTolerance);
        Date endDate = new Date(date.getTime() + millisecTolerance);

        String sql = "SELECT DateTimeGMT, betaTimecode, hdTimecode  " + "FROM " + table + " " +
                     "WHERE DateTimeGMT BETWEEN '" + dateFormatUTC.format(startDate) + "' AND '" +
                     dateFormatUTC.format(endDate) + "' ORDER BY DateTimeGMT";

        return executeQueryFunction(sql, queryFunction);

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
    public VideoMoment interpolateTimecodeByDate(String cameraIdentifier, Date date, int millisecTolerance,
            double frameRate) {

        /*
         * Retrieve the tapetimes and sort by date
         */
        List<VideoMoment> videoTimes = findTimecodesNearDate(cameraIdentifier, date, millisecTolerance);

        VideoMoment returnTapeTime = null;
        if (videoTimes.size() > 1) {

            /*
             * Extract the dates and timecodes (as frames) for interpolation
             */
            Timecode timecode = null;
            List<VideoMoment> nonNullTimecodes = new ArrayList<VideoMoment>(Collections2.filter(videoTimes,
                nonNullTimecodePredicate));
            Collections.sort(nonNullTimecodes, new VideoMomentByDateComparator());
            BigDecimal[] dates = new BigDecimal[nonNullTimecodes.size()];
            BigDecimal[] frames = new BigDecimal[nonNullTimecodes.size()];
            for (int i = 0; i < nonNullTimecodes.size(); i++) {
                VideoMoment tapeTime = nonNullTimecodes.get(i);
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
            List<VideoMoment> nonNullAltTimecodes = new ArrayList<VideoMoment>(Collections2.filter(videoTimes,
                nonNullAlternateTimecodePredicate));
            Collections.sort(nonNullAltTimecodes, new VideoMomentByDateComparator());
            dates = new BigDecimal[nonNullAltTimecodes.size()];
            frames = new BigDecimal[nonNullAltTimecodes.size()];

            for (int i = 0; i < nonNullAltTimecodes.size(); i++) {
                VideoMoment tapeTime = nonNullAltTimecodes.get(i);
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
            String tc = (timecode == null) ? VideoMoment.TIMECODE_INVALID : timecode.toString();
            String altTc = (alternateTimecode == null) ? VideoMoment.TIMECODE_INVALID : alternateTimecode.toString();
            returnTapeTime = new VideoMomentBean(date, tc, altTc);
        }

        return returnTapeTime;

    }
}
