/*
 * @(#)CacheClearedEvent.java   2009.10.26 at 02:43:40 PDT
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

/**
 * <p>Event that occurs when a {@link PersistenceCache} is cleared.</p>
 */
public class CacheClearedEvent {

    private final PersistenceCache cache;

    /**
     * Constructs ...
     *
     *
     * @param cache The cache that was cleared.
     */
    public CacheClearedEvent(PersistenceCache cache) {
        this.cache = cache;
    }

    /**
     * <p>A reference to the cache object that was cleared.</p>
     * @return
     */
    public PersistenceCache getCache() {
        return cache;
    }
}
