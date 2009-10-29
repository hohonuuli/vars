/*
 * @(#)ApproveHistorySubscriber.java   2009.10.29 at 12:49:38 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.knowledgebase.ui.persistence;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import vars.UserAccount;
import vars.knowledgebase.History;
import vars.knowledgebase.ui.Lookup;
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

    public void onEvent(String topic, History history) {
        if (Lookup.TOPIC_INSERT_CONCEPT_NAME.equals(topic)) {
            final UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
            if ((userAccount != null) && userAccount.isAdministrator()) {
                try {
                    approveHistoryTask.approve(userAccount, history);
                }
                catch (Exception e) {
                    EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
                    EventBus.publish(Lookup.TOPIC_REFRESH_KNOWLEGEBASE,
                                     history.getConceptMetadata().getConcept().getPrimaryConceptName().getName());
                }
            }
        }
    }
}
