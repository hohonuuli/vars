/*
 * @(#)UpdateObservationsSubscriber.java   2009.10.29 at 01:23:37 PDT
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

import org.bushe.swing.event.EventTopicSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.KnowledgebaseDAO;
import vars.knowledgebase.ui.Lookup;

/**
 * Used to update observations in the knowledgebase when a primary conceptname
 * is changed.
 *
 * @author brian
 */
public class UpdateObservationsSubscriber implements EventTopicSubscriber<Concept> {

    private final ConceptDAO conceptDAO;
    private final KnowledgebaseDAO knowledgebaseDAO;
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Constructs ...
     *
     * @param conceptDAO
     * @param knowledgebaseDAO
     */
    public UpdateObservationsSubscriber(ConceptDAO conceptDAO, KnowledgebaseDAO knowledgebaseDAO) {
        this.conceptDAO = conceptDAO;
        this.knowledgebaseDAO = knowledgebaseDAO;
    }

    public void onEvent(String topic, final Concept concept) {
        if (Lookup.TOPIC_UPDATE_OBSERVATIONS.equals(topic)) {

            // Do the update in the background
            Thread thread = new Thread(new Runnable() {

                public void run() {
                    try {
                    Concept concept0 = conceptDAO.findInDatastore(concept);
                    knowledgebaseDAO.updateConceptNameUsedByAnnotations(concept0);
                    }
                    catch (Exception e) {
                        log.warn("Failed to update observations in the database", e);
                    }
                }

            });
            thread.run();
        }
    }
}
