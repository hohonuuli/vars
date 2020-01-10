/*
 * @(#)ApproveHistoriesSubscriber.java   2010.05.05 at 10:53:45 PDT
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

import java.util.Collection;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.History;
import vars.knowledgebase.ui.actions.ApproveHistoryTask;

/**
 *
 * @author brian
 */
public class ApproveHistoriesSubscriber implements EventTopicSubscriber<Collection<? extends History>> {

    private final ApproveHistoryTask approveHistoryTask;

    /**
     * Constructs ...
     *
     * @param approveHistoryTask
     */
    public ApproveHistoriesSubscriber(ApproveHistoryTask approveHistoryTask) {
        super();
        this.approveHistoryTask = approveHistoryTask;
    }

    /**
     *
     * @param topic
     * @param histories
     */
    public void onEvent(String topic, Collection<? extends History> histories) {
        if (StateLookup.TOPIC_APPROVE_HISTORIES.equals(topic)) {
            final UserAccount userAccount = StateLookup.getUserAccount();

            try {
                if ((userAccount != null) && (userAccount.isAdministrator())) {
                    approveHistoryTask.doTask(userAccount, histories);
                }
            }
            catch (Exception e) {
                EventBus.publish(StateLookup.TOPIC_NONFATAL_ERROR, e);
            }
            finally {

                // Refresh KB to the first concept we find in the histories
                if (histories.size() > 0) {
                    Concept concept = histories.iterator().next().getConceptMetadata().getConcept();
                    if (concept != null) {
                        EventBus.publish(StateLookup.TOPIC_REFRESH_KNOWLEGEBASE, concept.getPrimaryConceptName().getName());
                    }
                }

            }
        }
    }
}
