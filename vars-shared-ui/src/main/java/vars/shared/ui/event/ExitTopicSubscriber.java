/*
 * @(#)ExitTopicSubscriber.java   2009.12.03 at 08:58:37 PST
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

/**
 *
 * @author brian
 */
@SuppressWarnings("unchecked")
public class ExitTopicSubscriber implements EventTopicSubscriber {

    /**  */

    /**
     *
     * @param topic
     * @param data
     */
    public void onEvent(String topic, Object data) {
        System.exit(0);
    }
}


;
