package vars.query.results;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Optional;
import java.util.concurrent.Executor;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

/**
 * @author Brian Schlining
 * @since 2016-04-11T13:48:00
 */
public class SaveResultsAsJSONFn {

    private final File target;
    private final QueryResults queryResults;
    private final Executor executor;
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .create();

    public SaveResultsAsJSONFn(Executor executor, File target, QueryResults queryResults) {
        this.executor = executor;
        this.target = target;
        this.queryResults = queryResults;
    }

    public void apply() {
        executor.execute(() -> {
            String json = gson.toJson(queryResults);
            try {
                final BufferedWriter out = new BufferedWriter(new FileWriter(target));
                out.write(json);
                out.close();
            }
            catch (Exception e) {
                log.warn("Failed to save KML to " +target.getAbsolutePath());
            }
        });
    }
}
