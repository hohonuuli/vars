package vars.knowledgebase.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.swing.ProgressDialog;
import org.mbari.vars.dao.DAOException;
import org.mbari.vars.knowledgebase.model.Concept;
import org.mbari.vars.knowledgebase.model.HistoryFactory;
import org.mbari.vars.knowledgebase.model.Media;
import org.mbari.vars.knowledgebase.model.dao.ConceptDAO;
import org.mbari.vars.knowledgebase.ui.KnowledgebaseApp;
import org.mbari.vars.knowledgebase.ui.MediaViewPanel;
import org.mbari.vars.knowledgebase.ui.actions.ApproveHistoryTask;
import org.mbari.vars.model.UserAccount;
import org.mbari.vars.ui.OkCancelButtonPanel;
import org.mbari.vars.util.AppFrameDispatcher;
import vars.knowledgebase.IHistory;

public class AddMediaDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    /**
	 * @uml.property  name="jContentPane"
	 * @uml.associationEnd  
	 */
    private JPanel jContentPane = null;

    /**
	 * @uml.property  name="mediaViewPanel"
	 * @uml.associationEnd  
	 */
    private MediaViewPanel mediaViewPanel = null;

    /**
	 * @uml.property  name="buttonPanel"
	 * @uml.associationEnd  
	 */
    private OkCancelButtonPanel buttonPanel = null;
    
    /**
	 * @uml.property  name="concept"
	 * @uml.associationEnd  
	 */
    private Concept concept;
    
    private static final Logger log = LoggerFactory.getLogger(AddMediaDialog.class);

    /**
     * @param owner
     */
    public AddMediaDialog(Frame owner) {
        super(owner);
        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setSize(300, 200);
        this.setContentPane(getJContentPane());
    }

    /**
	 * This method initializes jContentPane
	 * @return  javax.swing.JPanel
	 * @uml.property  name="jContentPane"
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
	 * @uml.property  name="mediaViewPanel"
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
                    getButtonPanel().getOkButton().setEnabled(getConcept() != null && text != null && text.length() > 5);
                }
                
            });
        }
        return mediaViewPanel;
    }

    /**
	 * This method initializes buttonPanel	
	 * @return  javax.swing.JPanel
	 * @uml.property  name="buttonPanel"
	 */
    private OkCancelButtonPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new OkCancelButtonPanel();
            buttonPanel.getOkButton().addActionListener(new OkActionListener());
            buttonPanel.getCancelButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    setConcept(null);
                    setVisible(false);
                }
                
            });
        }
        return buttonPanel;
    }
    
    private class OkActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            setVisible(false);
            
            final Concept concept = getConcept();
            
            final ProgressDialog progressDialog = AppFrameDispatcher.getProgressDialog();
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
                    AppFrameDispatcher.showWarningDialog("Unable to read from " + url.toExternalForm());
                }
            }
            catch (Exception e1) {
                final String s = "Failed to open URL, " + p.getUrlField().getText();
                AppFrameDispatcher.showWarningDialog(s);
                log.warn(s, e1);
            }
            
            progressBar.setIndeterminate(false);
            if (isUrlValid) {
                
                // Make sure that this concept does not already contain a media with the same URL
                Set mediaSet = concept.getMediaSet();
                for (Iterator i = mediaSet.iterator(); i.hasNext();) {
                    Media m = (Media) i.next();
                    if (m.getUrl().equalsIgnoreCase(url.toExternalForm())) {
                        setConcept(null);
                        progressDialog.setVisible(false);
                        AppFrameDispatcher.showErrorDialog("A media with the URL, '" + url.toExternalForm() +
                                "', was already found in '" + concept.getPrimaryConceptNameAsString() + "'.");
                        return;
                    }
                }
                
                progressBar.setString("Building data");
                progressBar.setValue(1);
                
                // Build the Media
                final Media media = new Media();
                media.setUrl(url.toExternalForm());
                media.setCaption(p.getCaptionArea().getText());
                media.setCredit(p.getCreditArea().getText());
                final String type = (String) p.getTypeComboBox().getSelectedItem();
                media.setType(type);

                /**
                 * Each concept can only have a single primary media for each media type
                 *  i.e. only 1 primary image, 1 primary movie and 1 primary icon
                 */
                boolean isPrimary = p.getPrimaryCheckBox().isSelected();
                media.setPrimary(isPrimary);
                if (isPrimary) {
                    final Media primaryMedia = concept.getPrimaryMedia(type);
                    if (primaryMedia != null) {
                        log.info("You are adding a primary media of '" + media.getUrl() +
                                "' to " + concept.getPrimaryConceptNameAsString() +
                                ". This concept contained a primary media of '" +
                                primaryMedia.getUrl() + "' which is now set to a secondary media"); 
                        primaryMedia.setPrimary(false);
                    }
                }
                
                concept.addMedia(media);
                
                // Build the History
                IHistory history = HistoryFactory.add((UserAccount) KnowledgebaseApp.DISPATCHER_USERACCOUNT.getValueObject(), media);
                concept.addHistory(history);
                
                progressBar.setString("Updating database");
                progressBar.setValue(2);
                
                // Update the database
                try {
                    ConceptDAO.getInstance().update(concept);
                    final UserAccount userAccount = (UserAccount) KnowledgebaseApp.DISPATCHER_USERACCOUNT.getValueObject();
                    if (userAccount.isAdmin()) {
                        ApproveHistoryTask.approve(userAccount, history);
                    }
                }
                catch (DAOException e1) {
                    concept.removeMedia(media);
                    concept.removeHistory(history);
                    final String s = "Failed to upate '" + concept.getPrimaryConceptNameAsString() + 
                            "' in the database. Removing the media reference to '" + url.toExternalForm() + "'.";
                    log.error(s, e1);
                    AppFrameDispatcher.showErrorDialog(s);
                }
                
                progressBar.setString("Refreshing");
                progressBar.setValue(3);
                
            }
            setConcept(null);
            progressDialog.setVisible(false);
            
            
            ((KnowledgebaseApp) KnowledgebaseApp.DISPATCHER.getValueObject()).getKnowledgebaseFrame().refreshTreeAndOpenNode(concept.getPrimaryConceptNameAsString());
        }
        
    }

    /**
	 * @return  the concept
	 * @uml.property  name="concept"
	 */
    public Concept getConcept() {
        return concept;
    }

    /**
	 * @param concept  the concept to set
	 * @uml.property  name="concept"
	 */
    public void setConcept(Concept concept) {
        final MediaViewPanel p = getMediaViewPanel();
        p.getCaptionArea().setText(null);
        p.getCreditArea().setText(null);
        p.getTypeComboBox().setSelectedIndex(0);
        p.getUrlField().setText(null);
        final boolean locked = (concept == null);
        p.setLocked(locked);
        getButtonPanel().getOkButton().setEnabled(concept != null);
        this.concept = concept;
    }

}
