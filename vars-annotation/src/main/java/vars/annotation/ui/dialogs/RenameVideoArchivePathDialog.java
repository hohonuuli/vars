package vars.annotation.ui.dialogs;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.annotation.ui.StateLookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.eventbus.VideoArchiveChangedEvent;
import vars.annotation.ui.eventbus.VideoArchiveSelectedEvent;
import vars.shared.ui.dialogs.StandardDialog;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Brian Schlining
 * @since 2012-05-08
 */
public class RenameVideoArchivePathDialog extends StandardDialog {

    private final ToolBelt toolBelt;
    private JScrollPane scrollPane;
    private JTextField textField;
    private JLabel errorLabel;
    private JPanel centerPanel;
    private VideoArchive videoArchive;


    public RenameVideoArchivePathDialog(final Window parent, final ToolBelt toolBelt) {
        super(parent);
        this.toolBelt = toolBelt;
        initialize();
        getRootPane().setDefaultButton(getOkayButton());
        pack();
        VideoArchive va = StateLookup.getVideoArchive();
        setVideoArchive(va);
        AnnotationProcessor.process(this);
    }

    public String getNewVideoArchiveName() {
        return getTextField().getText();
    }


    private void initialize() {

        getOkayButton().setEnabled(false);
        getOkayButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = getTextField().getText();

                // Check to see if the video archive name already exists
                VideoArchiveDAO dao = toolBelt.getAnnotationDAOFactory().newVideoArchiveDAO();
                dao.startTransaction();
                VideoArchive existingVideoArchive = dao.findByName(name);
                dao.endTransaction();

                if (existingVideoArchive != null) {
                    if (existingVideoArchive.equals(videoArchive)) {
                        getErrorLabel().setText("You haven't changed the name.");
                    }
                    else {
                        getErrorLabel().setText("That name is already used in the database. " +
                                "Pick a different name.");
                    }
                }
                else {
                    getErrorLabel().setText(null);
                    setVisible(false);
                }



            }
        });

        getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getErrorLabel().setText(null);
                getTextField().setText(videoArchive.getName());
                setVisible(false);
            }
        });
        getContentPane().add(getCenterPanel(), BorderLayout.CENTER);
    }

    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane(getTextField());
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        }
        return scrollPane;
    }

    private JTextField getTextField() {
        if (textField == null) {
            textField = new JTextField(50);
        }
        return textField;
    }

    private JLabel getErrorLabel() {
        if (errorLabel == null) {
            errorLabel = new JLabel();
            errorLabel.setForeground(Color.RED);
        }
        return errorLabel;
    }

    private JPanel getCenterPanel() {
        if (centerPanel == null) {
            centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.add(getScrollPane());
            centerPanel.add(getErrorLabel());
        }
        return centerPanel;
    }

    @EventSubscriber(eventClass = VideoArchiveChangedEvent.class)
    public void respondTo(VideoArchiveChangedEvent event) {
        setVideoArchive(event.get());
    }

    @EventSubscriber(eventClass = VideoArchiveSelectedEvent.class)
    public void respondTo(VideoArchiveSelectedEvent event) {
        setVideoArchive(event.get());
    }

    private void setVideoArchive(VideoArchive videoArchive) {

        this.videoArchive = videoArchive;
        getErrorLabel().setText(null);
        if (videoArchive == null) {
            getTextField().setText(null);
            getOkayButton().setEnabled(false);
        }
        else {
            getTextField().setText(videoArchive.getName());
            getOkayButton().setEnabled(true);
        }
    }

}
