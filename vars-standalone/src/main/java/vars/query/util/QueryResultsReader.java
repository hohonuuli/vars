package vars.query.util;

import com.google.common.collect.Maps;

import java.io.*;
import java.util.Map;

/**
 * Created by brian on 12/30/13.
 */
public class QueryResultsReader {

    public static Dataset read(File file) throws IOException {
        final InputStream in = new FileInputStream(file);
        final Dataset dataset = read(in);
        in.close();
        return dataset;
    }

    public static Dataset read(InputStream in) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        MutableSimpleDataset dataset = null;
        String[] headers = null;
        while((line = reader.readLine()) != null) {
            if (line.trim().startsWith("#")) {
                // skip comment
            }
            else if (dataset != null) {
                String[] parts = line.split("\t");
                Map<String, Object> map = Maps.newHashMap();
                for (int i = 0; i < headers.length; i++) {
                    map.put(headers[i], parts[i]);
                }
                dataset.addRow(map);
            }
            else {
                // read header
                headers = line.split("\t");
                dataset = new MutableSimpleDataset(headers);
            }
        }
        reader.close();

        return dataset;
    }

}
