/*
 * @(#)LoggingSubscriber.java   2009.12.03 at 08:58:37 PST
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



package vars.shared.ui.event;

import org.bushe.swing.event.EventTopicSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @version        Enter version here..., 2009.12.03 at 08:57:51 PST
 * @author         Brian Schlining [brian@mbari.org]
 */
@SuppressWarnings("unchecked")
public class LoggingTopicSubscriber implements EventTopicSubscriber {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     *
     * @param topic
     * @param data
     */
    public void onEvent(String topic, Object data) {
        if (log.isDebugEnabled()) {
            if (data instanceof Throwable) {
                log.debug("Event Published:\n\tTOPIC: " + topic + " (Exception Stacktrace below) ", data);
            }
            else {
                log.debug("Event Published:\n\tTOPIC: " + topic + "\n\tDATA: " + data);
            }
        }
    }
}
