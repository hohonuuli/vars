package vars.query.util;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Created by brian on 12/30/13.
 */
public class QueryResultsReaderTest {

    @Test
    public void testRead() {
        URL url = getClass().getResource("/vars/query/util/SES6061.txt");
        Dataset dataset = null;
        try {
            InputStream in = url.openStream();
            dataset = QueryResultsReader.read(in);
            in.close();
        }
        catch (IOException e) {
            Assert.fail(e.getMessage());
        }

        // Check dataset
        Assert.assertNotNull("Dataset that was read is null", dataset);
        Assert.assertEquals("Number of columns was wrong", 11, dataset.getColumnCount());
        Assert.assertEquals("Number of rows was wrong", 11164, dataset.getRowCount());


        // Check some values
        Object[] urls = dataset.getData("Image");
        Object[] timecodes = dataset.getData("TapeTimeCode");
        Assert.assertEquals("First value in 'Image' column is wrong",
                "http://tripod.shore.mbari.org/ImageArchive/SES/Pulse%2060/AdjustedImages/StaM_Pulse60_SES_120611_12_11_29_autolevels.jpg",
                urls[0]);
        Assert.assertEquals("First value in 'TapeTimeCode' column is wrong",
                "00:00:00:00", timecodes[0]);

        Assert.assertEquals("Last value in 'Image' column is wrong",
                "http://tripod.shore.mbari.org/ImageArchive/SES/SES%20pulse%2061/fluoro_rng_pulse61_130416-232019.jpg",
                urls[urls.length - 1]);
        Assert.assertEquals("Last value in 'TapeTimeCode' column is wrong",
                "00:00:26:21", timecodes[timecodes.length - 1]);

    }
}
