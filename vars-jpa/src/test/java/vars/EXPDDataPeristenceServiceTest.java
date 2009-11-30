/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mbari.movie.Timecode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.jpa.VarsJpaTestModule;

/**
 *
 * @author brian
 */
public class EXPDDataPeristenceServiceTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    
    DateFormat dateFormat;
    ExternalDataPersistenceService dao;

    @Before
    public void setup() {
        Injector injector = Guice.createInjector(new VarsJpaTestModule());
        dao = injector.getInstance(ExternalDataPersistenceService.class);
        dateFormat = injector.getInstance(DateFormat.class);
    }

    @Test
    @Ignore // Uncomment this to test in MBARI environment.
    public void findTimecodeByDateTest01() {

        int millisecTolerance = 1000 * 7;
        double framerate = 29.97;

        List<TimecodeBean> beans = new ArrayList<TimecodeBean>();
        try {
            beans.add(new TimecodeBean(new Timecode("04:28:08:03", framerate),
                    dateFormat.parse("2008-05-19T21:03:31Z"), "Ventana"));
            beans.add(new TimecodeBean(new Timecode("04:31:49:13", framerate),
                    dateFormat.parse("2007-12-20T20:34:08Z"), "Tiburon"));
            beans.add(new TimecodeBean(new Timecode("01:05:48:24", framerate),
                    dateFormat.parse("2008-04-09T17:16:35Z"), "Ventana"));

        }
        catch (ParseException e) {
            log.error("Failed to generate test data", e);
            Assert.fail("Failed to generate test data");
        }

        for (TimecodeBean b : beans) {
            VideoMoment videoMoment = dao.findTimecodeNearDate(b.platform, b.date, millisecTolerance);
            Timecode tc = new Timecode(videoMoment.getAlternateTimecode(), framerate);
            double dt = Math.abs(tc.diffFrames(b.timecode));
            log.debug("EXPECTED: " + b.timecode + " FOUND: " + tc);

            /* This doesn't always work as expected because of the lag between the
             * VXWorks clock used to write the utc time to the tape and all the
             * other computers which are NTP'd
             */
            Assert.assertTrue("Timecode is wrong. EXPECTED: " + b.timecode +
                    " FOUND: " + tc, dt / framerate * 1000 <= millisecTolerance);
        }

    }

    private class TimecodeBean {

        private final Timecode timecode;
        private final Date date;
        private final String platform;

        public TimecodeBean(Timecode timecode, Date date, String platform) {
            this.timecode = timecode;
            this.date = date;
            this.platform = platform;
        }

    }

}
