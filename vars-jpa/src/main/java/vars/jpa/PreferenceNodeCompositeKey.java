/*
 * @(#)PreferenceNodeCompositeKey.java   2009.12.07 at 02:11:07 PST
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



package vars.jpa;

import java.io.Serializable;
import javax.persistence.Embeddable;

/**
 * A {@link PreferenceNode} has a unique nodename, prefKey combination. We'll use
 * that as a composite key. This class is a construct needed by JPA to work
 * with composite keys.
 * 
 * @author brian
 */
@Embeddable
public class PreferenceNodeCompositeKey implements Serializable {

    String prefKey;
    String nodeName;

    /**
     * Constructs ...
     */
    public PreferenceNodeCompositeKey() {}

    /**
     * Constructs ...
     *
     * @param node
     * @param key
     */
    public PreferenceNodeCompositeKey(String nodeName, String prefKey) {
        this.nodeName = nodeName;
        this.prefKey = prefKey;
    }

    /**
     * @return
     */
    public String getPrefKey() {
        return prefKey;
    }

    /**
     * @return
     */
    public String getNodeName() {
        return nodeName;
    }

    /**
     *
     * @param key
     */
    public void setPrefKey(String key) {
        this.prefKey = key;
    }

    /**
     *
     * @param node
     */
    public void setNodeName(String node) {
        this.nodeName = node;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PreferenceNodeCompositeKey other = (PreferenceNodeCompositeKey) obj;
        if ((this.prefKey == null) ? (other.prefKey != null) : !this.prefKey.equals(other.prefKey)) {
            return false;
        }
        if ((this.nodeName == null) ? (other.nodeName != null) : !this.nodeName.equals(other.nodeName)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.prefKey != null ? this.prefKey.hashCode() : 0);
        hash = 97 * hash + (this.nodeName != null ? this.nodeName.hashCode() : 0);
        return hash;
    }


}
