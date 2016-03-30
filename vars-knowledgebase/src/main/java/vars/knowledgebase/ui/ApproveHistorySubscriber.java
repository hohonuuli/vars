/*
 * @(#)ApproveHistorySubscriber.java   2009.12.02 at 10:00:44 PST
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



package vars.knowledgebase.ui;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import vars.UserAccount;
import vars.knowledgebase.History;
import vars.knowledgebase.ui.actions.ApproveHistoryTask;

/**
 *
 * @version        $date$, 2009.10.29 at 12:49:38 PDT
 * @author         Brian Schlining [brian@mbari.org]
 */
public class ApproveHistorySubscriber implements EventTopicSubscriber<History> {

    private final ApproveHistoryTask approveHistoryTask;

    /**
     * Constructs ...
     *
     * @param approveHistoryTask
     */
    public ApproveHistorySubscriber(ApproveHistoryTask approveHistoryTask) {
        super();
        this.approveHistoryTask = approveHistoryTask;
    }

    /**
     *
     * @param topic
     * @param history
     */
    public void onEvent(String topic, History history) {
        if (StateLookup.TOPIC_APPROVE_HISTORY.equals(topic)) {
            final UserAccount userAccount = StateLookup.getUserAccount();

            try {
                if ((userAccount != null) && (userAccount.isAdministrator()) && (!history.isProcessed())) {
                    approveHistoryTask.doTask(userAccount, history);
                }
            }
            catch (Exception e) {
                EventBus.publish(StateLookup.TOPIC_NONFATAL_ERROR, e);
            }
            finally {
                EventBus.publish(StateLookup.TOPIC_REFRESH_KNOWLEGEBASE,
                                 history.getConceptMetadata().getConcept().getPrimaryConceptName().getName());
            }
        }
    }
}
