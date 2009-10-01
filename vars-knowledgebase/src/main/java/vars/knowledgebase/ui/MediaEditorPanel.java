package vars.knowledgebase.ui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.qt.QT;
import org.mbari.swing.ImageFrame;
import org.mbari.swing.ListListModel;
import org.mbari.vars.dao.DAOException;
import org.mbari.vars.knowledgebase.model.Concept;
import org.mbari.vars.knowledgebase.model.HistoryFactory;
import org.mbari.vars.knowledgebase.model.Media;
import org.mbari.vars.knowledgebase.model.dao.ConceptDelegateDAO;
import org.mbari.vars.knowledgebase.ui.actions.ApproveHistoryTask;
import org.mbari.vars.knowledgebase.ui.dialogs.AddMediaDialog;
import org.mbari.vars.model.UserAccount;
import org.mbari.vars.util.AppFrameDispatcher;
import vars.knowledgebase.IConcept;
import vars.knowledgebase.IHistory;
import vars.knowledgebase.IMedia;

public class MediaEditorPanel extends EditorPanel implements ILockableEditor {
    
    private static final long serialVersionUID = 3980331775038029506L;
    /**
     * @uml.property  name="mediaViewPanel"
     * @uml.associationEnd
     */
    private MediaViewPanel mediaViewPanel = null;
    /**
     * @uml.property  name="buttonPanel"
     * @uml.associationEnd
     */
    private EditorButtonPanel buttonPanel = null;
    /**
     * @uml.property  name="mediaPanel"
     * @uml.associationEnd
     */
    private JPanel mediaPanel = null;
    /**
     * @uml.property  name="scrollPanel"
     * @uml.associationEnd
     */
    private JScrollPane scrollPanel = null;
    /**
     * @uml.property  name="mediaList"
     * @uml.associationEnd
     */
    private JList mediaList = null;
    
    private static final Logger log = LoggerFactory.getLogger(MediaEditorPanel.class);
    /**
     * This method initializes
     *
     */
    public MediaEditorPanel() {
        super();
        initialize();
        setLocked(isLocked());
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
    
    /**
     * This method initializes mediaViewPanel
     * @return  javax.swing.JPanel
     * @uml.property  name="mediaViewPanel"
     */
    private MediaViewPanel getMediaViewPanel() {
        if (mediaViewPanel == null) {
            mediaViewPanel = new MediaViewPanel();
            mediaViewPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        }
        return mediaViewPanel;
    }
    
    /**
     * This method initializes buttonPanel
     * @return  javax.swing.JPanel
     * @uml.property  name="buttonPanel"
     */
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
    
    /**
     * This method initializes mediaPanel
     * @return  javax.swing.JPanel
     * @uml.property  name="mediaPanel"
     */
    private JPanel getMediaPanel() {
        if (mediaPanel == null) {
            final BorderLayout borderLayout = new BorderLayout();
            mediaPanel = new JPanel();
            mediaPanel.setLayout(borderLayout);
            mediaPanel.setBorder(BorderFactory.createTitledBorder(null, "Media", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
            mediaPanel.add(getMediaViewPanel(), BorderLayout.SOUTH);
            mediaPanel.add(getScrollPanel(), BorderLayout.CENTER);
        }
        return mediaPanel;
    }
    
    /**
     * This method initializes scrollPanel
     * @return  javax.swing.JScrollPane
     * @uml.property  name="scrollPanel"
     */
    private JScrollPane getScrollPanel() {
        if (scrollPanel == null) {
            scrollPanel = new JScrollPane();
            scrollPanel.setViewportView(getMediaList());
        }
        return scrollPanel;
    }
    
    /**
     * This method initializes mediaList
     * @return  javax.swing.JList
     * @uml.property  name="mediaList"
     */
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
            
            /*
             * Show images when a URL is double clicked.
             */
            mediaList.addMouseListener(new MouseAdapter() {
                private ImageFrame imageFrame;
                
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                        final Media media = (Media) mediaList.getSelectedValue();
                        if (media != null) {
                            if (Media.TYPE_VIDEO.equals(media.getType())) {
                                // Display video
                                try {
                                    final java.awt.Frame frame = QT.playMovie(new URL(media.getUrl()));
                                    if (frame != null) {
                                        frame.addWindowListener(new WindowAdapter() {
                                            @Override
                                            public void windowClosing(WindowEvent e) { frame.dispose();}
                                        });
                                    }
                                }
                                catch (Exception e1) {
                                    AppFrameDispatcher.showErrorDialog("Unable to display the video at '" 
                                            + media.getUrl() + "'");
                                }
                            }
                            else {
                                try {
                                    final URL url = new URL(media.getUrl());
                                    getImageFrame().setImageUrl(url);
                                    getImageFrame().setVisible(true);
                                } catch (MalformedURLException e1) {
                                    AppFrameDispatcher.showErrorDialog("'" + media.getUrl() + "' is not a valid URL");
                                }
                                
                            }
                        }
                    }
                    
                }
                
                private ImageFrame getImageFrame() {
                    if (imageFrame == null) {
                        imageFrame = new ImageFrame();
                        imageFrame.setLocationRelativeTo(AppFrameDispatcher.getFrame());
                    }
                    return imageFrame;
                }
                
            });
        }
        return mediaList;
    }
    
    
    public void setLocked(final boolean locked) {
        getMediaViewPanel().setLocked(locked);
        getButtonPanel().getNewButton().setEnabled(!locked);
        getButtonPanel().getDeleteButton().setEnabled(!locked);
        getButtonPanel().getUpdateButton().setEnabled(!locked);
        super.setLocked(locked);
    }
    
    public void setConcept(final Concept concept) {
        final ListListModel model = (ListListModel) getMediaList().getModel();
        model.clear();
        if (concept != null) {
            model.addAll(concept.getMediaSet());
        }
        super.setConcept(concept);
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
         * @uml.property  name="dialog"
         */
        private AddMediaDialog getDialog() {
            if (dialog == null) {
                dialog = new AddMediaDialog(AppFrameDispatcher.getFrame());
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
                    AppFrameDispatcher.showWarningDialog("Unable to read from " + url.toExternalForm());
                } else {
                    media.setUrl(url.toExternalForm());
                }
            } catch (Exception e1) {
                final String s = "Failed to open URL, " + panel.getUrlField().getText() + ". The URL will not be updated.";
                AppFrameDispatcher.showWarningDialog(s);
            }
            
            // TODO 20070628 brian implement history
            // final History history = HistoryFactory.
            
            /**
             * Each concept can only have a single primary media for each media type
             *  i.e. only 1 primary image, 1 primary movie and 1 primary icon
             */
            IMedia oldPrimaryMedia = null;
            if (media.isPrimary()) {
                final IConcept concept = media.getConceptDelegate().getConcept();
                oldPrimaryMedia = concept.getPrimaryMedia(media.getType());
                if (oldPrimaryMedia != null && !oldPrimaryMedia.equals(media)) {
                    log.info("You are adding a primary media of '" + media.getUrl() +
                            "' to " + concept.getPrimaryConceptNameAsString() +
                            ". This concept contained a primary media of '" +
                            oldPrimaryMedia.getUrl() + "' which is now set to a secondary media");
                    oldPrimaryMedia.setPrimary(false);
                }
            }
            
            try {
                ConceptDelegateDAO.getInstance().update((Media) media.getConceptDelegate());
                // TODO 20070628 brian - verify that this redraws the media table
                getMediaList().paintImmediately(getMediaList().getBounds());
            } 
            catch (DAOException e) {
                log.warn("Failed to update " + media + " in the database", e);
                media.setCaption(oldCaption);
                media.setCredit(oldCredit);
                media.setType(oldType);
                media.setUrl(oldUrl);
                media.setPrimary(oldIsPrimary);
                oldPrimaryMedia.setPrimary(true);
                AppFrameDispatcher.showErrorDialog("Failed to write changes to the database. Rolling back to original values");
            }
        }
        
    }
    
    private class DeleteAction extends ActionAdapter {
        
        private static final long serialVersionUID = -2240738902892207112L;
        
        public void doAction() {
            final Media media = (Media) getMediaList().getSelectedValue();
            final UserAccount userAccount = (UserAccount) KnowledgebaseApp.DISPATCHER_USERACCOUNT.getValueObject();
            final IHistory history = HistoryFactory.delete(userAccount, media);
            final IConcept concept = media.getConceptDelegate().getConcept();
            concept.addHistory(history);
            if (userAccount.isAdmin()) {
                ApproveHistoryTask.approve(userAccount, history);
            }
            ((KnowledgebaseApp) KnowledgebaseApp.DISPATCHER.getValueObject()).getKnowledgebaseFrame().refreshTreeAndOpenNode(concept.getPrimaryConceptNameAsString());
        }
        
    }
    
}  //  @jve:decl-index=0:visual-constraint="73,44"
