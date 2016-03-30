/*
 * @(#)AddLinkRealizationDialog.java   2009.10.05 at 03:38:31 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.knowledgebase.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JDialog;
import javax.swing.JPanel;
import org.bushe.swing.event.EventBus;
import org.mbari.util.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.DAO;
import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.History;
import vars.knowledgebase.HistoryFactory;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.LinkRealization;
import vars.knowledgebase.ui.LinkEditorPanel;
import vars.knowledgebase.ui.StateLookup;
import vars.knowledgebase.ui.ToolBelt;
import vars.knowledgebase.ui.actions.ApproveHistoryTask;
import vars.shared.ui.OkCancelButtonPanel;

/**
 * @version
 */
public class AddLinkRealizationDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(AddLinkRealizationDialog.class);
    private OkCancelButtonPanel buttonPanel = null;
    private JPanel jContentPane = null;
    private LinkEditorPanel linkEditorPanel = null;
    private Concept concept;
    private final ToolBelt toolBelt;

    /**
     * Constructs ...
     *
     * @param toolBelt
     */
    public AddLinkRealizationDialog(ToolBelt toolBelt) {
        this(null, toolBelt);
    }

    /**
     * @param owner
     * @param toolBelt
     */
    public AddLinkRealizationDialog(Frame owner, ToolBelt toolBelt) {
        super(owner);
        setTitle("VARS - Add Property");
        this.toolBelt = toolBelt;
        initialize();
    }

    /**
     * This method initializes buttonPanel
     * @return  javax.swing.JPanel
     */
    private OkCancelButtonPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new OkCancelButtonPanel();

            buttonPanel.getOkayButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    onOkClick();
                }

            });

            buttonPanel.getCancelButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    onCancelClick();
                }

            });
        }

        return buttonPanel;
    }

    public Concept getConcept() {
        return concept;
    }

    /**
     * This method initializes jContentPane
     * @return  javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getButtonPanel(), BorderLayout.SOUTH);
            jContentPane.add(getLinkEditorPanel(), BorderLayout.CENTER);
        }

        return jContentPane;
    }

    /**
         * This method initializes linkEditorPanel
         * @return  javax.swing.JPanel
         */
    private LinkEditorPanel getLinkEditorPanel() {
        if (linkEditorPanel == null) {
            linkEditorPanel = new LinkEditorPanel(toolBelt);
            linkEditorPanel.getLinkNameField().setEditable(false);
            linkEditorPanel.getFromConceptComboBox().setEditable(false);
            linkEditorPanel.addPropertyChangeListener("link", new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    linkEditorPanel.getFromConceptComboBox().setEditable(false);
                    if (concept != null) {
                        linkEditorPanel.getFromConceptComboBox().setSelectedItem(concept.getPrimaryConceptName().getName());
                    }
                }
            });
        }

        return linkEditorPanel;
    }

    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize() {
        if (log.isDebugEnabled()) {
            log.debug("Initializing " + getClass().getName());
        }

        this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        this.setModal(true);
        this.setContentPane(getJContentPane());
        final Frame frame = StateLookup.getApplicationFrame();
        setLocationRelativeTo(frame);
        pack();
    }

    private void onCancelClick() {
        setVisible(false);
        setConcept(null);
    }

    private void onOkClick() {
        setVisible(false);

        UserAccount userAccount = StateLookup.getUserAccount();
        if ((userAccount != null) && !userAccount.isReadOnly()) {

            KnowledgebaseDAOFactory knowledgebaseDAOFactory = toolBelt.getKnowledgebaseDAOFactory();
            HistoryFactory historyFactory = toolBelt.getHistoryFactory();
            ApproveHistoryTask approveHistoryTask = toolBelt.getApproveHistoryTask();

            /*
             * Create the linkRealization
             */
            final LinkRealization linkRealization = toolBelt.getKnowledgebaseFactory().newLinkRealization();
            final LinkEditorPanel p = getLinkEditorPanel();
            linkRealization.setLinkName(p.getLinkName());
            linkRealization.setToConcept(p.getToConcept());
            linkRealization.setLinkValue(p.getLinkValue());
            Concept c = getConcept();

            try {

                /*
                 * Use the primary concept name of the 'toConcept'
                 */
                ConceptDAO conceptDAO = knowledgebaseDAOFactory.newConceptDAO();
                conceptDAO.startTransaction();
                Concept toConcept = conceptDAO.findByName(linkRealization.getToConcept());
                conceptDAO.endTransaction();
                conceptDAO.close();
                
                if (toConcept != null) {
                    linkRealization.setToConcept(toConcept.getPrimaryConceptName().getName());
                }

                DAO dao = knowledgebaseDAOFactory.newDAO();
                dao.startTransaction();
                c = dao.find(c);
                concept.getConceptMetadata().addLinkRealization(linkRealization);
                dao.persist(linkRealization);
                
                /*
                 * Create a History
                 */
                History history = historyFactory.add(userAccount, linkRealization);
                c.getConceptMetadata().addHistory(history);
                dao.persist(history);
                dao.endTransaction();
                dao.close();

                EventBus.publish(StateLookup.TOPIC_APPROVE_HISTORY, history);

            }
            catch (Exception e) {
                EventBus.publish(StateLookup.TOPIC_WARNING, e);
                EventBus.publish(StateLookup.TOPIC_REFRESH_KNOWLEGEBASE, c.getPrimaryConceptName().getName());
            }

            setConcept(null);
        }
    }

    public void setConcept(Concept concept) {
        getLinkEditorPanel().setConcept(concept);
        if (concept != null) {
            getLinkEditorPanel().getFromConceptComboBox().setSelectedItem(concept.getPrimaryConceptName().getName());
        }
        this.concept = concept;
    }
}
