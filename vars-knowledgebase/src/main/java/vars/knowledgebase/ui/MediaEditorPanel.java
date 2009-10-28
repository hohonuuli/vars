/*
 * @(#)MediaEditorPanel.java   2009.10.09 at 05:29:08 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.knowledgebase.ui;

import java.awt.Component;
import vars.shared.ui.ILockableEditor;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.bushe.swing.event.EventBus;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.swing.ImageFrame;
import org.mbari.swing.ListListModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.History;
import vars.knowledgebase.Media;
import vars.knowledgebase.MediaDAO;
import vars.knowledgebase.MediaTypes;
import vars.knowledgebase.ui.dialogs.AddMediaDialog;

/**
 * Class description
 *
 *
 * @version        $date$, 2009.10.09 at 05:29:08 PDT
 * @author         Brian Schlining [brian@mbari.org]
 */
public class MediaEditorPanel extends EditorPanel implements ILockableEditor {

    private static final long serialVersionUID = 3980331775038029506L;
    private static final Logger log = LoggerFactory.getLogger(MediaEditorPanel.class);
    private EditorButtonPanel buttonPanel = null;
    private JList mediaList = null;
    private JPanel mediaPanel = null;
    private MediaViewPanel mediaViewPanel = null;
    private JScrollPane scrollPanel = null;
    private final ToolBelt toolBelt;

    /**
     * @param toolBelt
     */
    public MediaEditorPanel(ToolBelt toolBelt) {
        super();
        this.toolBelt = toolBelt;
        initialize();
        setLocked(isLocked());
    }

    private EditorButtonPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new EditorButtonPanel();

            // TODO 20060606 brian: ECLIPSE VE Chokes when the following three lines are uncommented
            buttonPanel.getNewButton().addActionListener(new NewAction());
            buttonPanel.getUpdateButton().addActionListener(new UpdateAction());
            buttonPanel.getDeleteButton().addActionListener(new DeleteAction());
        }

        return buttonPanel;
    }


    private JList getMediaList() {
        if (mediaList == null) {
            mediaList = new JList();
            mediaList.setModel(new ListListModel(Collections.synchronizedList(new ArrayList())));
            mediaList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            mediaList.addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(final ListSelectionEvent e) {
                    final Media media = (Media) mediaList.getSelectedValue();

                    // Set the view to the selected media
                    getMediaViewPanel().setMedia(media);

                    boolean enable = !isLocked() && (media != null);
                    getButtonPanel().getDeleteButton().setEnabled(enable);
                    getButtonPanel().getUpdateButton().setEnabled(enable);
                }


            });
            mediaList.setCellRenderer(new MediaListCellRenderer());

            /*
             * Show images when a URL is double clicked.
             */
            mediaList.addMouseListener(new MouseAdapter() {

                private ImageFrame imageFrame;

                @Override
                public void mouseClicked(MouseEvent e) {
                    if ((e.getButton() == MouseEvent.BUTTON1) && (e.getClickCount() == 2)) {
                        final Media media = (Media) mediaList.getSelectedValue();
                        if (media != null) {
                            if (Media.TYPE_VIDEO.equals(media.getType())) {

                                // Display video
                                try {

                                    // TODO Open movie with native movie player
                                    log.warn("Displaying movies is not currently implemented");

//                                    final java.awt.Frame frame = QT.playMovie(new URL(media.getUrl()));
//                                    if (frame != null) {
//                                        frame.addWindowListener(new WindowAdapter() {
//                                            @Override
//                                            public void windowClosing(WindowEvent e) { frame.dispose();}
//                                        });
//                                    }
                                }
                                catch (Exception e1) {
                                    EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR,
                                                     "Unable to display the video at '" + media.getUrl() + "'");
                                }
                            }
                            else {
                                try {
                                    final URL url = new URL(media.getUrl());
                                    getImageFrame().setImageUrl(url);
                                    getImageFrame().setVisible(true);
                                }
                                catch (MalformedURLException e1) {
                                    EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR,
                                                     "'" + media.getUrl() + "' is not a valid URL");
                                }

                            }
                        }
                    }

                }

                private ImageFrame getImageFrame() {
                    if (imageFrame == null) {
                        imageFrame = new ImageFrame();
                        final Frame frame = (Frame) Lookup.getApplicationFrameDispatcher().getValueObject();
                        imageFrame.setLocationRelativeTo(frame);
                    }

                    return imageFrame;
                }


            });
        }

        return mediaList;
    }

    private JPanel getMediaPanel() {
        if (mediaPanel == null) {
            final BorderLayout borderLayout = new BorderLayout();
            mediaPanel = new JPanel();
            mediaPanel.setLayout(borderLayout);
            mediaPanel.setBorder(BorderFactory.createTitledBorder(null, "Media", TitledBorder.DEFAULT_JUSTIFICATION,
                    TitledBorder.DEFAULT_POSITION, null, null));
            mediaPanel.add(getMediaViewPanel(), BorderLayout.SOUTH);
            mediaPanel.add(getScrollPanel(), BorderLayout.CENTER);
        }

        return mediaPanel;
    }

    private MediaViewPanel getMediaViewPanel() {
        if (mediaViewPanel == null) {
            mediaViewPanel = new MediaViewPanel();
            mediaViewPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        }

        return mediaViewPanel;
    }

    private JScrollPane getScrollPanel() {
        if (scrollPanel == null) {
            scrollPanel = new JScrollPane();
            scrollPanel.setViewportView(getMediaList());
        }

        return scrollPanel;
    }

    /**
     * This method initializes this
     *
     */
    private void initialize() {
        if (log.isDebugEnabled()) {
            log.debug("Initializing " + getClass().getName());
        }

        this.setLayout(new BorderLayout());
        setSize(300, 300);
        this.add(getButtonPanel(), BorderLayout.SOUTH);
        this.add(getMediaPanel(), BorderLayout.CENTER);
    }

    @Override
    public void setConcept(final Concept concept) {
        final ListListModel model = (ListListModel) getMediaList().getModel();
        model.clear();

        if (concept != null) {
            model.addAll(concept.getConceptMetadata().getMedias());
        }

        super.setConcept(concept);
    }

    @Override
    public void setLocked(final boolean locked) {
        getMediaViewPanel().setLocked(locked);
        getButtonPanel().getNewButton().setEnabled(!locked);
        getButtonPanel().getDeleteButton().setEnabled(!locked);
        getButtonPanel().getUpdateButton().setEnabled(!locked);
        super.setLocked(locked);
    }

    private class DeleteAction extends ActionAdapter {

        private static final long serialVersionUID = -2240738902892207112L;

        public void doAction() {
            final Media media = (Media) getMediaList().getSelectedValue();
            final UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
            History history = toolBelt.getHistoryFactory().delete(userAccount, media);
            final Concept concept = media.getConceptMetadata().getConcept();
            concept.getConceptMetadata().addHistory(history);
            history = toolBelt.getKnowledgebaseDAOFactory().newHistoryDAO().makePersistent(history);

            if (userAccount.isAdministrator()) {
                toolBelt.getApproveHistoryTask().approve(userAccount, history);
            }

            ((KnowledgebaseApp) Lookup.getApplicationFrameDispatcher().getValueObject()).getKnowledgebaseFrame()
                .refreshTreeAndOpenNode(concept.getPrimaryConceptName().getName());
        }
    }


    /**
     * @author  brian
     */
    private class NewAction extends ActionAdapter {

        private static final long serialVersionUID = -2681737638703696425L;
        private AddMediaDialog dialog;

        public void doAction() {
            getDialog().setConcept(getConcept());
            getDialog().setVisible(true);

        }

        /**
         * @return  the dialog
         */
        private AddMediaDialog getDialog() {
            if (dialog == null) {
                final Frame frame = (Frame) Lookup.getApplicationFrameDispatcher().getValueObject();
                dialog = new AddMediaDialog(frame, toolBelt);
            }

            return dialog;
        }
    }


    private class UpdateAction extends ActionAdapter {

        private static final long serialVersionUID = -89011999566883148L;

        public void doAction() {
            final Media media = (Media) getMediaList().getSelectedValue();

            // Store old values in case we need to reset the UI on DB Exception
            final String oldCaption = media.getCaption();
            final String oldCredit = media.getCredit();
            final String oldType = media.getType();
            final String oldUrl = media.getUrl();
            final boolean oldIsPrimary = media.isPrimary();

            // Set the new values
            final MediaViewPanel panel = getMediaViewPanel();
            media.setCaption(panel.getCaptionArea().getText());
            media.setCredit(panel.getCreditArea().getText());
            media.setType((String) panel.getTypeComboBox().getSelectedItem());
            media.setPrimary(panel.getPrimaryCheckBox().isSelected());

            // Check that the URL is valid
            URL url = null;
            try {
                url = new URL(panel.getUrlField().getText());
                final InputStream in = url.openStream();
                final int b = in.read();
                if (b == -1) {
                    EventBus.publish(Lookup.TOPIC_WARNING, "Unable to read from " + url.toExternalForm());
                }
                else {
                    media.setUrl(url.toExternalForm());
                }
            }
            catch (Exception e1) {
                final String s = "Failed to open URL, " + panel.getUrlField().getText() +
                                 ". The URL will not be updated.";
                EventBus.publish(Lookup.TOPIC_WARNING, s);
            }

            // TODO 20070628 brian implement history
            // final History history = HistoryFactory.

            /**
             * Each concept can only have a single primary media for each media type
             *  i.e. only 1 primary image, 1 primary movie and 1 primary icon
             */
            Media oldPrimaryMedia = null;
            if (media.isPrimary()) {
                final Concept concept = media.getConceptMetadata().getConcept();
                oldPrimaryMedia = concept.getConceptMetadata().getPrimaryMedia(MediaTypes.getType(media.getType()));

                if ((oldPrimaryMedia != null) && !oldPrimaryMedia.equals(media)) {
                    log.info("You are adding a primary media of '" + media.getUrl() + "' to " +
                             concept.getPrimaryConceptName().getName() +
                             ". This concept contained a primary media of '" + oldPrimaryMedia.getUrl() +
                             "' which is now set to a secondary media");
                    oldPrimaryMedia.setPrimary(false);
                }
            }

            try {
                MediaDAO mediaDAO = toolBelt.getKnowledgebaseDAOFactory().newMediaDAO();
                mediaDAO.update(media);
                mediaDAO.update(oldPrimaryMedia);

                // TODO 20070628 brian - verify that this redraws the media table
                getMediaList().paintImmediately(getMediaList().getBounds());
            }
            catch (Exception e) {
                log.warn("Failed to update " + media + " in the database", e);
                media.setCaption(oldCaption);
                media.setCredit(oldCredit);
                media.setType(oldType);
                media.setUrl(oldUrl);
                media.setPrimary(oldIsPrimary);
                oldPrimaryMedia.setPrimary(true);
                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR,
                                 "Failed to write changes to the database. Rolling back to original values");
            }
        }
    }

    private class MediaListCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel c =  (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            final Media media = (Media) value;
            final String text = (media == null) ? "" : media.getUrl();
            c.setText(text);
            return c;
        }

    }

}  

