/*
 * @(#)StatusLabelForVarsDb.java   2009.11.17 at 09:23:46 PST
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



package vars.old.annotation.ui;

import java.beans.PropertyChangeEvent;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;

import vars.annotation.ui.Lookup;

/**
 * <p>Indicates connection state of the database.</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 */
public class StatusLabelForVarsDb extends StatusLabel {

    /**
     * Constructor
     */
    public StatusLabelForVarsDb() {
        super();
        setToolTipText("Status of the VARS database connection");
        setText(" VARS ");
        setOk(true);
        EventBus.subscribe(Lookup.TOPIC_DATABASE_STATUS, new EventTopicSubscriber<Boolean>() {

            public void onEvent(String topic, Boolean ok) {
                setOk(ok);

                if (ok) {
                    setToolTipText("Connected to database");
                }
                else {
                    setToolTipText("There is a problem with the database connection. Restart VARS.");
                }

            }

        });

    }

    /**
     *
     * @param evt
     */
    public void propertyChange(PropertyChangeEvent evt) {

        // Do nothing. We're listening via eventbus

    }
}
