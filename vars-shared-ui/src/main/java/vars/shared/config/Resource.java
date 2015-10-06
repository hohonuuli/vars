package vars.shared.config;

import com.google.common.base.Preconditions;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2015-07-19T12:50:00
 */
public class Resource {

    private final Config config;
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Typesafe's config uses a slightly weird resource path. Don't use "/"
     * instead for a resource don't use the absolute ref: e.g. Do not use
     * "/vars/queryfx/app" use "vars/queryfx/app
     *
     * @param resource Path to config file
     */
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
