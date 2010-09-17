/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.preferences;

import com.google.inject.Inject;
import com.google.inject.Injector;

import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.PreferencesFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import vars.MiscDAOFactory;
import vars.UserAccount;
import vars.annotation.ui.Lookup;
import vars.shared.preferences.PreferenceUpdater;

/**
 * Frame for allowing users to edit preferences.
 *
 * @author brian
 */
public class PreferencesFrame extends JFrame {

    private JButton okButton;
    private JButton cancelButton;
    private JPanel buttonPanel;
    private JTabbedPane tabbedPane;
    private ImagePreferencesPanel imagePreferencesPanel;
    private UserPreferencesPanel userPreferencesPanel;
    private VideoConnectionPreferencesPanel videoConnectionPreferencesPanel;
    private List<PreferenceUpdater> updaters = new ArrayList<PreferenceUpdater>();
    private final PreferencesFactory preferencesFactory;
    private UserAccount userAccount;


    /**
     * Create the frame
     */
    @Inject
    public PreferencesFrame(PreferencesFactory preferencesFactory) {
        super();
        this.preferencesFactory = preferencesFactory;
        setBounds(100, 100, 500, 375);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            initialize();
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void initialize() throws Exception {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(getTabbedPane(), BorderLayout.CENTER);
        getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);

        // Configure the updaters
        updaters.add(getImagePreferencesPanel());
        updaters.add(getUserPreferencesPanel());
        updaters.add(getVideoConnectionPreferencesPanel());
    }

    /**
     * @return
     */
    protected JTabbedPane getTabbedPane() {
        if (tabbedPane == null) {
            tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Image Settings", getImagePreferencesPanel());
            tabbedPane.addTab("Account Information", getUserPreferencesPanel());
            tabbedPane.addTab("Video Connection", getVideoConnectionPreferencesPanel());
        }
        return tabbedPane;
    }

    /**
     * @return
     */
    protected JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            buttonPanel.add(Box.createHorizontalGlue());
            buttonPanel.add(getCancelButton());
            buttonPanel.add(Box.createHorizontalStrut(10));
            buttonPanel.add(getOkButton());
        }
        return buttonPanel;
    }

    /**
     * @return
     */
    protected JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton();
            cancelButton.setText("Cancel");
            cancelButton.addActionListener(new CancelAction());
        }
        return cancelButton;
    }

    /**
     * @return
     */
    protected JButton getOkButton() {
        if (okButton == null) {
            okButton = new JButton();
            okButton.setText("OK");
            okButton.addActionListener(new OKAction());
        }
        return okButton;
    }

    public ImagePreferencesPanel getImagePreferencesPanel() {
        if (imagePreferencesPanel == null) {
            imagePreferencesPanel = new ImagePreferencesPanel(preferencesFactory);
        }
        return imagePreferencesPanel;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        getImagePreferencesPanel().setUserAccount(userAccount);
        this.userAccount = userAccount;
    }
    
    public UserPreferencesPanel getUserPreferencesPanel() {
        if (userPreferencesPanel == null) {
            // INJECTION HACK!!
            Injector injector = (Injector) Lookup.getGuiceInjectorDispatcher().getValueObject();
            MiscDAOFactory daoFactory = injector.getInstance(MiscDAOFactory.class);
            userPreferencesPanel = new UserPreferencesPanel(daoFactory);
        }
        return userPreferencesPanel;
    }

    public VideoConnectionPreferencesPanel getVideoConnectionPreferencesPanel() {
        if (videoConnectionPreferencesPanel == null) {
            videoConnectionPreferencesPanel = new VideoConnectionPreferencesPanel(preferencesFactory);
        }
        return videoConnectionPreferencesPanel;
    }




    private class OKAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            for (PreferenceUpdater preferenceUpdater : updaters) {
                preferenceUpdater.persistPreferences();
            }
            PreferencesFrame.this.setVisible(false);
        }

    }

    private class CancelAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            PreferencesFrame.this.setVisible(false);
        }

    }
}
