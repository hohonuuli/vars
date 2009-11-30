/*
 * @(#)DeleteConceptNameTask.java   2009.09.30 at 09:41:12 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.knowledgebase.ui.actions;

import java.awt.Frame;

import javax.swing.JProgressBar;

import org.bushe.swing.event.EventBus;
import org.mbari.swing.ProgressDialog;
import org.mbari.util.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vars.DAO;
import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameDAO;
import vars.knowledgebase.History;
import vars.knowledgebase.HistoryFactory;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.KnowledgebasePersistenceService;
import vars.knowledgebase.ui.KnowledgebaseFrame;
import vars.knowledgebase.ui.Lookup;
import foxtrot.Job;
import foxtrot.Worker;

/**
 * Class description
 *
 *
 * @version        $date$, 2009.09.30 at 09:41:12 PDT
 * @author         Brian Schlining [brian@mbari.org]
 */
public class DeleteConceptNameTask {

    private static final Logger log = LoggerFactory.getLogger(DeleteConceptNameTask.class);

    private final HistoryFactory historyFactory;
    private final KnowledgebasePersistenceService knowledgebaseDAO;
    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;

    public DeleteConceptNameTask(KnowledgebaseDAOFactory knowledgebaseDAOFactory, 
            KnowledgebaseFactory knowledgebaseFactory, KnowledgebasePersistenceService knowledgebaseDAO) {
        this.knowledgebaseDAOFactory = knowledgebaseDAOFactory;
        this.historyFactory = new HistoryFactory(knowledgebaseFactory);
        this.knowledgebaseDAO = knowledgebaseDAO;
    }

    public void delete(final ConceptName conceptName) {
        boolean okToProceed = conceptName != null;


        if (okToProceed) {

            /*
             * Add a History object to track changes
             */
            UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
            History history = historyFactory.delete(userAccount, conceptName);
            // DAOTX
            DAO dao = knowledgebaseDAOFactory.newConceptDAO();
            dao.startTransaction();
            final Concept concept = dao.findInDatastore(conceptName.getConcept());
            concept.getConceptMetadata().addHistory(history);
            dao.persist(concept);
            dao.endTransaction();

            ProgressDialog progressDialog = Lookup.getProgressDialog();
            progressDialog.setLabel("Deleting '" + conceptName.getName() + "'");
            JProgressBar progressBar = progressDialog.getProgressBar();
            progressBar.setMinimum(0);
            progressBar.setMaximum(4);
            progressBar.setString("Deleting '" + conceptName.getName() + "'");
            progressBar.setStringPainted(true);
            progressDialog.pack();
            progressDialog.setVisible(true);

            /*
             * Make sure that no annotations are using the concept-name that is
             * being deleted.
             */
            progressBar.setString("Removing usage from annotations");
            progressBar.setValue(1);
            Boolean ok = (Boolean) Worker.post(new Job() {

                public Object run() {
                    Boolean ok = Boolean.FALSE;
                    try {
                        knowledgebaseDAO.updateConceptNameUsedByAnnotations(concept);
                        ok = Boolean.TRUE;
                    }
                    catch (Exception e) {
                        String msg = "Failed to remove all references to " + "'" + conceptName.getName() +
                                     "' from annotations stored " + "in the database";
                        log.error(msg);
                        EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, msg);
                    }

                    return ok;
                }

            });
            boolean success = ok.booleanValue();

            if (success) {

                progressBar.setString("Removing usage from knowledgebase");
                progressBar.setValue(2);
                Worker.post(new Job() {

                    public Object run() {

                        /*
                         * Delete the offending conceptName
                         */
                        try {
                            // DAOTX
                            ConceptNameDAO conceptNameDAO = knowledgebaseDAOFactory.newConceptNameDAO();
                            conceptNameDAO.startTransaction();
                            ConceptName myConceptName = conceptNameDAO.findInDatastore(conceptName);
                            myConceptName.getConcept().removeConceptName(myConceptName);
                            conceptNameDAO.remove(conceptName);
                            conceptNameDAO.endTransaction();
                        }
                        catch (Exception e) {
                            if (log.isErrorEnabled()) {
                                log.error("Failed to delete " + conceptName, e);
                            }
                            EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, "Failed to delete '" + conceptName.getName() + "'");
                        }

                        return null;
                    }

                });
            }

            progressBar.setString("Reloading knowledgebase");
            progressBar.setValue(3);
            Dispatcher dispatcher = Lookup.getApplicationFrameDispatcher();
            Frame frame = (Frame) dispatcher.getValueObject();
            if ((frame != null) && (frame instanceof KnowledgebaseFrame)) {
                ((KnowledgebaseFrame) frame).refreshTreeAndOpenNode(concept.getPrimaryConceptName().getName());
            }

            progressBar.setString("Finished");
            progressBar.setValue(4);
            progressDialog.setVisible(false);
        }
    }
}
