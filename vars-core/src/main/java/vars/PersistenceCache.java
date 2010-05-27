/*
 * @(#)PersistenceCache.java   2009.10.26 at 03:25:37 PDT
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

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.AnnotationObject;
import vars.knowledgebase.KnowledgebaseObject;

/**
 * It's expected that there will be an underlying level 2 cache used for most
 * VARS applications. It may be nescessary to evict all objects in the cache.
 * When that happens, components may need to be notified. A developer will need
 * to implement a {@link PersistenceCacheProvider} for your particular
 * persistence implementation.
 * @author brian
 */
public class PersistenceCache {

    private final List<CacheClearedListener> clearCacheListeners = Collections.synchronizedList(
        new ArrayList<CacheClearedListener>());
    private final PersistenceCacheProvider provider;
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Constructs ...
     *
     * @param provider
     */
    @Inject
    public PersistenceCache(PersistenceCacheProvider provider) {
        this.provider = provider;
    }

    public void addCacheClearedListener(CacheClearedListener listener) {
        clearCacheListeners.add(listener);
    }

    public void clear() {
        log.debug("Clearing persistence cache");
        notifyCacheClearedListenersBeforeClear();
        provider.clear();
        notifyCacheClearedListenersAfterClear();
    }

    public void evict(AnnotationObject object) {
        provider.evict(object);
    }

    public void evict(KnowledgebaseObject object) {
        provider.evict(object);
    }

    private void notifyCacheClearedListenersAfterClear() {
        final CacheClearedEvent event = new CacheClearedEvent(this);
        final List<CacheClearedListener> listeners = new ArrayList<CacheClearedListener>(clearCacheListeners);
        for (CacheClearedListener listener : listeners) {
            listener.afterClear(event);
        }
    }

    private void notifyCacheClearedListenersBeforeClear() {
        final CacheClearedEvent event = new CacheClearedEvent(this);
        final List<CacheClearedListener> listeners = new ArrayList<CacheClearedListener>(clearCacheListeners);
        for (CacheClearedListener listener : listeners) {
            listener.beforeClear(event);
        }
    }

    /**
     *
     * @param listener
     */
    public void removeCacheClearedListener(CacheClearedListener listener) {
        clearCacheListeners.remove(listener);
    }
}
