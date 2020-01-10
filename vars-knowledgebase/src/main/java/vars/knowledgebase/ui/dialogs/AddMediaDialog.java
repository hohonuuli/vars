/*
 * @(#)AddMediaDialog.java   2009.12.02 at 04:00:15 PST
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



package vars.knowledgebase.ui.dialogs;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.bushe.swing.event.EventBus;
import org.mbari.swing.ProgressDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.DAO;
import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.History;
import vars.knowledgebase.HistoryFactory;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.Media;
import vars.knowledgebase.MediaTypes;
import vars.knowledgebase.ui.MediaViewPanel;
import vars.knowledgebase.ui.StateLookup;
import vars.knowledgebase.ui.ToolBelt;
import vars.shared.ui.OkCancelButtonPanel;

/**
 * Class description
 *
 *
 * @version        $date$, 2009.10.07 at 10:57:02 PDT
 * @author         Brian Schlining [brian@mbari.org]
 */
public class AddMediaDialog extends JDialog {

    private OkCancelButtonPanel buttonPanel = null;
    private JPanel jContentPane = null;
    private MediaViewPanel mediaViewPanel = null;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private Concept concept;
    private final HistoryFactory historyFactory;
    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;
    private final KnowledgebaseFactory knowledgebaseFactory;

    /**
     * @param owner
     * @param toolBelt
     */
    public AddMediaDialog(Frame owner, ToolBelt toolBelt) {
        super(owner);
        this.knowledgebaseDAOFactory = toolBelt.getKnowledgebaseDAOFactory();
        this.knowledgebaseFactory = toolBelt.getKnowledgebaseFactory();
        this.historyFactory = toolBelt.getHistoryFactory();
        initialize();
    }

    /**
         * This method initializes buttonPanel
         * @return  javax.swing.JPanel
         */
    private OkCancelButtonPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new OkCancelButtonPanel();
            buttonPanel.getOkayButton().addActionListener(new OkActionListener());
            buttonPanel.getCancelButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    setConcept(null);
                    setVisible(false);
                }

            });
        }

        return buttonPanel;
    }

    /**
         * @return  the concept
         */
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
            jContentPane.add(getMediaViewPanel(), BorderLayout.CENTER);
            jContentPane.add(getButtonPanel(), BorderLayout.SOUTH);
        }

        return jContentPane;
    }

    /**
         * This method initializes mediaViewPanel
         * @return  javax.swing.JPanel
         */
    private MediaViewPanel getMediaViewPanel() {
        if (mediaViewPanel == null) {
            mediaViewPanel = new MediaViewPanel();

            /*
             *OK Button should be disabled when concept == null or a non valid url is entered.
             */
            mediaViewPanel.getUrlField().getDocument().addDocumentListener(new DocumentListener() {

                public void changedUpdate(DocumentEvent e) {
                    update();
                }

                public void insertUpdate(DocumentEvent e) {
                    update();
                }

                public void removeUpdate(DocumentEvent e) {
                    update();
                }

                void update() {
                    String text = mediaViewPanel.getUrlField().getText();

                    getButtonPanel().getOkayButton().setEnabled((getConcept() != null) && (text != null) &&
                            (text.length() > 5));
                }

            });

            mediaViewPanel.setPreferredSize(new Dimension(500, 300));
        }

        return mediaViewPanel;
    }

    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize() {
        //this.setSize(300, 200);
        this.setContentPane(getJContentPane());
        pack();
    }

    /**
     * @param concept  the concept to set
     */
    public void setConcept(Concept concept) {
        final MediaViewPanel p = getMediaViewPanel();

        p.getCaptionArea().setText(null);
        p.getCreditArea().setText(null);
        p.setMedia(null);
        p.getUrlField().setText(null);

        final boolean locked = (concept == null);

        p.setLocked(locked);
        getButtonPanel().getOkayButton().setEnabled(concept != null);
        this.concept = concept;
    }

    private class OkActionListener implements ActionListener {

        /**
         *
         * @param e
         */
        public void actionPerformed(ActionEvent e) {
            setVisible(false);

            // DAOTX
            DAO dao = knowledgebaseDAOFactory.newDAO();

            dao.startTransaction();

            final Concept concept = dao.find(getConcept());


            final ProgressDialog progressDialog = StateLookup.getProgressDialog();

            progressDialog.setTitle("VARS - Adding Media");

            final JProgressBar progressBar = progressDialog.getProgressBar();

            progressBar.setMinimum(0);
            progressBar.setMaximum(3);
            progressBar.setIndeterminate(true);
            progressBar.setString("Searching for media");
            progressDialog.setVisible(true);

            final MediaViewPanel p = getMediaViewPanel();

            boolean isUrlValid = false;

            URL url = null;

            try {
                url = new URL(p.getUrlField().getText());

                final InputStream in = url.openStream();
                final int b = in.read();

                isUrlValid = (b > -1);

                if (!isUrlValid) {
                    EventBus.publish(StateLookup.TOPIC_WARNING, "Unable to read from " + url.toExternalForm());
                }
            }
            catch (Exception e1) {
                final String s = "Failed to open URL, " + p.getUrlField().getText();

                EventBus.publish(StateLookup.TOPIC_WARNING, s);
                log.warn(s, e1);
            }

            progressBar.setIndeterminate(false);

            if (isUrlValid) {

                // Make sure that this concept does not already contain a media with the same URL
                final URL fUrl = url;
                Collection<Media> mediaSet = new ArrayList<Media>(concept.getConceptMetadata().getMedias());
                Collection<Media> matchingMedia = Collections2.filter(mediaSet, new Predicate<Media>() {

                    public boolean apply(Media input) {
                        return input.getUrl().equals(fUrl.toExternalForm());
                    }

                });

                if (matchingMedia.size() > 0) {
                    setConcept(null);
                    progressDialog.setVisible(false);
                    EventBus.publish(StateLookup.TOPIC_WARNING,
                                     "A media with the URL, '" + url.toExternalForm() + "', was already found in '" +
                                     concept.getPrimaryConceptName().getName() + "'.");
                }


                progressBar.setString("Building data");
                progressBar.setValue(1);

                // Build the Media
                Media media = knowledgebaseFactory.newMedia();

                media.setUrl(url.toExternalForm());
                media.setCaption(p.getCaptionArea().getText());
                media.setCredit(p.getCreditArea().getText());

                final String type = (String) p.getTypeComboBox().getSelectedItem();

                media.setType(type);

                /*
                 * Each concept can only have a single primary media for each media type
                 *  i.e. only 1 primary image, 1 primary movie and 1 primary icon
                 */
                boolean isPrimary = p.getPrimaryCheckBox().isSelected();

                media.setPrimary(isPrimary);

                if (isPrimary) {
                    final Media primaryMedia = concept.getConceptMetadata().getPrimaryMedia(MediaTypes.getType(type));

                    if (primaryMedia != null) {
                        log.info("You are adding a primary media of '" + media.getUrl() + "' to " +
                                 concept.getPrimaryConceptName().getName() +
                                 ". This concept contained a primary media of '" + primaryMedia.getUrl() +
                                 "' which is now set to a secondary media");
                        primaryMedia.setPrimary(false);
                    }
                }


                progressBar.setString("Updating database");
                progressBar.setValue(2);

                // Update the database
                History history = null;

                try {
                    concept.getConceptMetadata().addMedia(media);
                    dao.persist(media);

                    // Build the History
                    final UserAccount userAccount = StateLookup.getUserAccount();
                    history = historyFactory.add(userAccount, media);
                    concept.getConceptMetadata().addHistory(history);
                    dao.persist(history);

                }
                catch (Exception e1) {
                    final String s = "Failed to upate '" + concept.getPrimaryConceptName().getName() +
                                     "' in the database. Removing the media reference to '" + url.toExternalForm() +
                                     "'.";

                    EventBus.publish(StateLookup.TOPIC_NONFATAL_ERROR, s);
                }
                dao.endTransaction();
                dao.close();

                progressBar.setString("Refreshing");
                progressBar.setValue(3);

                EventBus.publish(StateLookup.TOPIC_APPROVE_HISTORY, history);

            }

            setConcept(null);
            progressDialog.setVisible(false);
            EventBus.publish(StateLookup.TOPIC_REFRESH_KNOWLEGEBASE, concept.getPrimaryConceptName().getName());
        }
    }
}
