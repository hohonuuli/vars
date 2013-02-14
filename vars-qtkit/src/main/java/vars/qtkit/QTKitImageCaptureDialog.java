package vars.qtkit;

import vars.shared.ui.dialogs.StandardDialog;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

/**
 * @author Brian Schlining
 * @since 2013-02-13
 */
public class QTKitImageCaptureDialog extends StandardDialog {

    private JComboBox videoSourcesComboBox;
    private JPanel panel;

    public QTKitImageCaptureDialog(final Window parent, String[] videoSources) {
        super(parent);
        videoSourcesComboBox = new JComboBox(videoSources);
        initialize();
    }

    protected void initialize() {
        getContentPane().add(getPanel());
        getOkayButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Preferences preferences = Preferences.userRoot();
                preferences.put(QTKitImageCaptureServiceImpl.LIBRARY_NAME, (String) videoSourcesComboBox.getSelectedItem());
                setVisible(false);
            }
        });

        getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        pack();
    }

    public JPanel getPanel() {
        if (panel == null) {
            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            panel.add(new JLabel("Select the Video Source: "));
            panel.add(videoSourcesComboBox);
        }
        return panel;
    }
}
