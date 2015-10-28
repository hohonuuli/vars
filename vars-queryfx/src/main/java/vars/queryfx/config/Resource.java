package vars.queryfx.config;

import com.google.common.base.Preconditions;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2015-07-19T12:50:00
 * @deprecated Use vars.shared.config.Resource instead
 */
public class Resource {

    private final Config config;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public Resource(String resource) {
        Preconditions.checkArgument(resource != null, "Resource argument can not be null");
        config = ConfigFactory.load(resource);
    }

    public Resource(Config config) {
        Preconditions.checkArgument(config != null, "Config argument can not be null");
        this.config = config;
    }

    public Config getConfig() {
        return config;
    }

    public Optional<String> findByKey(String key) {
        Optional<String> value = Optional.empty();
        try {
            value = Optional.ofNullable(config.getString(key));
        }
        catch (Exception e) {
            log.debug("Failed to lookup '" + key + "'", e);
        }
        return value;
    }
}
