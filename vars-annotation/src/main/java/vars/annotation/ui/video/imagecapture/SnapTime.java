/*
 * @(#)SnapTime.java   2013.02.15 at 09:56:33 PST
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



package vars.annotation.ui.video.imagecapture;

import org.mbari.vcr4j.time.Timecode;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Represents an instant of time related to a Video tape. This object combines 'real' time,
 * represented by a date object, with VCR time, represented by a tape time-code.
 *
 * @author Brian Schlining
 * @since 2013-02-15
 */
public class SnapTime {

    private final static NumberFormat format4i = new DecimalFormat("0000");
    private final static NumberFormat format3i = new DecimalFormat("000");
    private final static DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy");
    private final static DateFormat timezoneFormat = new SimpleDateFormat("ZZ");
    private final Calendar gmtCalendar;
    private final Date observationDate;
    private final Date recordedDate;
    private final Timecode timeCode;

    /**
     * Constructs ...
     *
     *
     * @param observationDate
     * @param timeCode
     */
    SnapTime(final Date observationDate, final Date recordedDate, final String timeCode) {
        this.observationDate = observationDate;
        this.recordedDate = recordedDate;
        this.timeCode = new Timecode(timeCode);
        gmtCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    }

    /**
     * @return  DDD
     */
    String getDayOfYear() {
        return format3i.format(gmtCalendar.get(Calendar.DAY_OF_YEAR));
    }

    /**
     * @return time formatted for the GMT timezone
     */
    String getFormattedGmtTime() {
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        return dateFormat.format(observationDate);
    }

    /**
     * @return time formatted for the local timezone
     */
    String getFormattedLocalTime() {
        dateFormat.setTimeZone(TimeZone.getDefault());

        return dateFormat.format(observationDate);
    }

    /**
     * @return  The timezone offset between local and GMT
     */
    String getGmtOffset() {
        return timezoneFormat.format(observationDate);
    }

    /**
     * @return  A date
     */
    Date getObservationDate() {
        return observationDate;
    }

    Date getRecordedDate() {
        return recordedDate;
    }

    /**
     * @return  Timecode formatted for names (':' is replaced with '_')
     */
    String getTimeCodeAsName() {
        return timeCode.toString().replace(':', '_');
    }

    /**
     * @return THe timecode as a string
     */
    String getTimeCodeAsString() {
        return timeCode.toString();
    }

    /**
     * @return  The current time in seconds
     */
    long getTimeInSecs() {
        return observationDate.getTime() / 1000L;
    }

    /**
     * @return  YYYYDDD
     */
    String getTrackingNumber() {
        return getYear() + getDayOfYear();
    }

    /**
     * @return  YYYY
     */
    String getYear() {
        return format4i.format(gmtCalendar.get(Calendar.YEAR));
    }
}
