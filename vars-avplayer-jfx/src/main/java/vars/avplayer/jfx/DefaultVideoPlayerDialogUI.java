package vars.avplayer.jfx;

import org.mbari.awt.event.NonDigitConsumingKeyListener;
import org.mbari.swing.SpinningDialWaitIndicator;
import org.mbari.swing.WaitIndicator;
import org.mbari.text.IgnoreCaseToStringComparator;
import org.mbari.util.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.CacheClearedEvent;
import vars.CacheClearedListener;
import vars.ToolBelt;
import vars.VARSException;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.AnnotationPersistenceService;
import vars.annotation.CameraDeployment;
import vars.annotation.VideoArchive;
import vars.avplayer.VideoPlayer;
import vars.avplayer.VideoPlayerDialogUI;
import vars.shared.ui.dialogs.StandardDialog;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.MalformedURLException;
import java.util.*;
import java.util.List;

/**
 * Created by brian on 1/13/14.
 */
public class DefaultVideoPlayerDialogUI extends StandardDialog implements VideoPlayerDialogUI {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private static final int RESPONSE_DELAY = 750;
    private static final Runnable DO_NOTHING_FUNCTION = () -> {  };
    private final ButtonGroup buttonGroup = new ButtonGroup();
    private final ItemListener rbItemListener = new SelectedRBItemListener();
    private JPanel panel;
    private final ToolBelt toolBelt;
    private JLabel lblMovie;
    private JTextField urlTextField;
    private JButton browseButton;
    private JRadioButton openByLocationRB;
    private JLabel lblCameraPlatform;
    private JComboBox cameraPlatformComboBox;
    private JLabel lblSequenceNumber;
    private JTextField sequenceNumberTextField;
    private JRadioButton openExistingRB;
    private JLabel lblSelectName;
    private JComboBox existingNamesComboBox;
    private boolean loadExistingNames = true;
    private final Timer delayTimer;
    private final Timer updateOkayTimer;
    private JLabel selectTimeSourcelbl;
    private JComboBox timeSourceComboBox;
    private Runnable onOkayFunction = DO_NOTHING_FUNCTION;


//    /**
//     * Launch the application.
//     */
//    public static void main(String[] args) {
//        try {
//            QTOpenVideoArchiveDialog dialog = new QTOpenVideoArchiveDialog();
//            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//            dialog.setVisible(true);
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * Create the dialog.
     */
    public DefaultVideoPlayerDialogUI(final Window parent, final ToolBelt toolBelt) {
        super(parent);
        this.toolBelt = toolBelt;
        /*
         * We're adding a slight delay here so that db lookups don't try to
         * occur as a person types.
         */

        // This action occurs when the timer fires.
        ActionListener changeItemAction = e -> updateVideoArchiveParameters();

        // The timer with a delay and bound to above action.
        delayTimer = new Timer(RESPONSE_DELAY, changeItemAction);
        delayTimer.setRepeats(false);

        // Checks when to update the ok button
        ActionListener updateOkayAction = e -> updateOkayStatus();
        updateOkayTimer = new Timer(RESPONSE_DELAY, updateOkayAction);
        updateOkayTimer.setRepeats(false);

        initialize();
        getRootPane().setDefaultButton(getOkayButton());
        getOkayButton().addActionListener(actionEvent -> callOkayFunction()); // Execute onOkayFunction as ActionListener
        pack();
        toolBelt.getPersistenceCache().addCacheClearedListener(new CacheClearedListener() {
            public void afterClear(CacheClearedEvent evt) {
                loadExistingNames = true;
            }

            public void beforeClear(CacheClearedEvent evt) {
                DefaultComboBoxModel model = (DefaultComboBoxModel) getExistingNamesComboBox().getModel();
                model.removeAllElements();
            }
        });
        updateOkayStatus();
    }

    @Override
    public void onOkay(Runnable fn) {
        onOkayFunction = fn;
    }

    /**
     * We need this methods since lambdas require 'closed over' parameters to be effectively final. Our 'onOkayFunction'
     * is not final, so this indirection is required.
     */
    private void callOkayFunction() {
        if (onOkayFunction != null) {
            onOkayFunction.run();
        }
    }

    protected ToolBelt getToolBelt() {
        return toolBelt;
    }

    protected void initialize() {
        setPreferredSize(new Dimension(475, 400));
        buttonGroup.add(getOpenByLocationRB());
        buttonGroup.add(getOpenExistingRB());
        buttonGroup.setSelected(getOpenByLocationRB().getModel(), true);
        getOkayButton().addActionListener(e -> setVisible(false));
        getCancelButton().addActionListener(e -> setVisible(false));
        getContentPane().add(getPanel(), BorderLayout.CENTER);
        getOpenByLocationRB().setSelected(true);
    }

    private JPanel getPanel() {
        if (panel == null) {
            panel = new JPanel();
            GroupLayout gl_panel = new GroupLayout(panel);
            gl_panel.setHorizontalGroup(
                    gl_panel.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(gl_panel.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(gl_panel.createParallelGroup(GroupLayout.Alignment.LEADING)
                                            .addComponent(getOpenByLocationRB())
                                            .addGroup(gl_panel.createSequentialGroup()
                                                    .addGap(29)
                                                    .addGroup(gl_panel.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                            .addComponent(getBrowseButton())
                                                            .addGroup(gl_panel.createSequentialGroup()
                                                                    .addComponent(getLblMovie())
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                    .addComponent(getUrlTextField(), GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE))
                                                            .addGroup(gl_panel.createSequentialGroup()
                                                                    .addComponent(getLblCameraPlatform(), GroupLayout.PREFERRED_SIZE, 108, GroupLayout.PREFERRED_SIZE)
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                                    .addComponent(getCameraPlatformComboBox(), 0, 314, Short.MAX_VALUE))
                                                            .addGroup(gl_panel.createSequentialGroup()
                                                                    .addComponent(getLblSequenceNumber(), GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                    .addComponent(getSequenceNumberTextField(), GroupLayout.PREFERRED_SIZE, 311, GroupLayout.PREFERRED_SIZE))))
                                            .addComponent(getOpenExistingRB())
                                            .addGroup(gl_panel.createSequentialGroup()
                                                    .addGroup(gl_panel.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                            .addGroup(gl_panel.createSequentialGroup()
                                                                    .addGap(29)
                                                                    .addComponent(getLblSelectName(), GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE))
                                                            .addComponent(getSelectTimeSourcelbl()))
                                                    .addGap(30)
                                                    .addGroup(gl_panel.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                            .addComponent(getTimeSourceComboBox(), 0, 311, Short.MAX_VALUE)
                                                            .addComponent(getExistingNamesComboBox(), 0, 311, Short.MAX_VALUE))))
                                    .addContainerGap())
            );
            gl_panel.setVerticalGroup(
                    gl_panel.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(gl_panel.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(getOpenByLocationRB())
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(gl_panel.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(getLblMovie())
                                            .addComponent(getUrlTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(getBrowseButton())
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(gl_panel.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(getLblCameraPlatform())
                                            .addComponent(getCameraPlatformComboBox(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(gl_panel.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(getLblSequenceNumber())
                                            .addComponent(getSequenceNumberTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(18)
                                    .addComponent(getOpenExistingRB())
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(gl_panel.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(getLblSelectName())
                                            .addComponent(getExistingNamesComboBox(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(18)
                                    .addGroup(gl_panel.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(getSelectTimeSourcelbl())
                                            .addComponent(getTimeSourceComboBox(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addContainerGap(53, Short.MAX_VALUE))
            );
            panel.setLayout(gl_panel);
        }
        return panel;
    }

    private String[] listCameraPlatforms() {
        //final Collection<String> cameraPlatforms = VARSProperties.getCameraPlatforms();
        String[] cp = {""};
        try {
            AnnotationPersistenceService aps = toolBelt.getAnnotationPersistenceService();
            AnnotationDAOFactory daoFactory = toolBelt.getAnnotationDAOFactory();
            List<String> cameraPlatforms = aps.findAllCameraPlatforms();
            cp = new String[cameraPlatforms.size()];
            cameraPlatforms.toArray(cp);
            Arrays.sort(cp, new IgnoreCaseToStringComparator());
        }
        catch (Exception e) {
            // TODO throw error on eventbus
        }
        return cp;
    }

    private JLabel getLblMovie() {
        if (lblMovie == null) {
            lblMovie = new JLabel("Movie:");
        }
        return lblMovie;
    }

    protected JTextField getUrlTextField() {
        if (urlTextField == null) {
            urlTextField = new JTextField();
            urlTextField.setColumns(60);
            urlTextField.setToolTipText("Enter movie URL");

            // Editing the textfield will trigger a db lookup after a slight delay
            urlTextField.getDocument().addDocumentListener(new DocumentListener() {

                public void insertUpdate(DocumentEvent e) {
                    update();
                }

                public void removeUpdate(DocumentEvent e) {
                    update();
                }

                public void changedUpdate(DocumentEvent e) {
                    update();
                }

                public void update() {
                    delayTimer.restart();
                    updateOkayTimer.restart();
                }
            });

            // Do db lookup if textfield loses focus
            urlTextField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    updateVideoArchiveParameters();
                    updateOkayStatus();
                }
            });
        }
        return urlTextField;
    }

    private JButton getBrowseButton() {
        if (browseButton == null) {
            browseButton = new JButton("Browse");
            browseButton.setToolTipText("Browse for local file");
            browseButton.addActionListener(new ActionListener() {

                final JFileChooser chooser = new JFileChooser();

                public void actionPerformed(ActionEvent e) {
                    int option = chooser.showOpenDialog(DefaultVideoPlayerDialogUI.this);
                    if (option == JFileChooser.APPROVE_OPTION) {
                        File file = chooser.getSelectedFile();
                        try {
                            urlTextField.setText(file.toURI().toURL().toExternalForm());
                        }
                        catch (MalformedURLException e1) {
                            log.warn("Invalid File", e1);
                        }
                    }

                }

            });
        }
        return browseButton;
    }

    protected JRadioButton getOpenByLocationRB() {
        if (openByLocationRB == null) {
            openByLocationRB = new JRadioButton("Open by Location");
            openByLocationRB.addItemListener(rbItemListener);
        }
        return openByLocationRB;
    }

    private JLabel getLblCameraPlatform() {
        if (lblCameraPlatform == null) {
            lblCameraPlatform = new JLabel("Camera Platform:");
        }
        return lblCameraPlatform;
    }

    protected JComboBox getCameraPlatformComboBox() {
        if (cameraPlatformComboBox == null) {
            cameraPlatformComboBox = new JComboBox();
            cameraPlatformComboBox.setModel(new DefaultComboBoxModel(listCameraPlatforms()));
        }
        return cameraPlatformComboBox;
    }

    private JLabel getLblSequenceNumber() {
        if (lblSequenceNumber == null) {
            lblSequenceNumber = new JLabel("Sequence Number:");
        }
        return lblSequenceNumber;
    }

    protected JTextField getSequenceNumberTextField() {
        if (sequenceNumberTextField == null) {
            sequenceNumberTextField = new JTextField();
            sequenceNumberTextField.setColumns(10);
            sequenceNumberTextField.addKeyListener(new NonDigitConsumingKeyListener());
            sequenceNumberTextField.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) {
                    update();
                }

                public void removeUpdate(DocumentEvent e) {
                    update();
                }

                public void changedUpdate(DocumentEvent e) {
                    update();
                }

                private void update() {
                    updateOkayTimer.restart();
                }
            });
        }
        return sequenceNumberTextField;
    }

    protected JRadioButton getOpenExistingRB() {
        if (openExistingRB == null) {
            openExistingRB = new JRadioButton("Open Existing");
            openExistingRB.addItemListener(rbItemListener);
            openExistingRB.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (loadExistingNames) {
                        JComboBox comboBox = getExistingNamesComboBox();
                        WaitIndicator waitIndicator = new SpinningDialWaitIndicator(comboBox);
                        java.util.List<String> names = toolBelt.getAnnotationPersistenceService().findAllVideoArchiveNames();
                        String[] van = new String[names.size()];
                        names.toArray(van);
                        comboBox.setModel(new DefaultComboBoxModel(van));
                        waitIndicator.dispose();
                        loadExistingNames = false;
                    }
                }
            });
        }
        return openExistingRB;
    }

    private JLabel getLblSelectName() {
        if (lblSelectName == null) {
            lblSelectName = new JLabel("Select Name:");
        }
        return lblSelectName;
    }

    protected JComboBox getExistingNamesComboBox() {
        if (existingNamesComboBox == null) {
            existingNamesComboBox = new JComboBox();
            existingNamesComboBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        updateOkayStatus();
                    }
                }
            });
        }
        return existingNamesComboBox;
    }

    private void updateVideoArchiveParameters() {

        // TODO open using Toolbelt instead of controller
        Optional<VideoArchive> videoArchiveOpt = controller.findByLocation(getUrlTextField().getText(),
                toolBelt.getAnnotationDAOFactory());
        if (videoArchiveOpt.isPresent()) {
            VideoArchive videoArchive = videoArchiveOpt.get();
            getSequenceNumberTextField().setEnabled(false);
            CameraDeployment cameraDeployment = videoArchive.getVideoArchiveSet().getCameraDeployments().iterator().next();
            getSequenceNumberTextField().setText(cameraDeployment.getSequenceNumber() + "");
            getCameraPlatformComboBox().setEnabled(false);
            getCameraPlatformComboBox().setSelectedItem(videoArchive.getVideoArchiveSet().getPlatformName());
            updateOkayStatus();
        }
        else {
            getSequenceNumberTextField().setEnabled(true);
            getSequenceNumberTextField().setText("");
            getCameraPlatformComboBox().setEnabled(true);
            updateOkayStatus();
        }
    }

    private void updateOkayStatus() {
        boolean enable = false;
        if (getOpenExistingRB().isSelected()) {
            enable = getExistingNamesComboBox().getSelectedItem() != null;
        }
        else {
            String sequenceNumber = getSequenceNumberTextField().getText();
            String platform = (String) getCameraPlatformComboBox().getSelectedItem();
            enable = sequenceNumber != null && sequenceNumber.length() > 0 && platform != null;
        }
        getOkayButton().setEnabled(enable);
    }

    public Tuple2<VideoArchive, VideoPlayer> openVideoArchive() {
        String platformName = (String) getCameraPlatformComboBox().getSelectedItem();
        if (platformName != null && platformName.length() == 0) {
            platformName = null;
        }
        Optional<String> platformOpt = Optional.ofNullable(platformName);

        Optional<Integer> sequenceNumberOpt;
        try {
            Integer sn = Integer.parseInt(getSequenceNumberTextField().getText());
            sequenceNumberOpt = Optional.of(sn);
        }
        catch (Exception e) {
            sequenceNumberOpt = Optional.empty();
        }

        String movieLocation = getUrlTextField().getText();
        if (movieLocation == null || movieLocation.trim().length() == 0) {
            throw new VARSException("Unless you provide a movie location, VARS can't open the video file.");
        }

        Optional<TimeSource> timeSourceOpt = Optional.empty();
        if (getSupportTimeSource()) {
            timeSourceOpt = Optional.of((TimeSource) getTimeSourceComboBox().getSelectedItem());
        }

        VideoParams params = new VideoParams(getUrlTextField().getText(), platformOpt, sequenceNumberOpt,
                timeSourceOpt);
        return controller.openMoviePlayer(params, toolBelt.getAnnotationDAOFactory());
    }

    class SelectedRBItemListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                JRadioButton radioButton = (JRadioButton) e.getItemSelectable();
                if (radioButton.equals(getOpenByLocationRB())) {
                    getUrlTextField().setEnabled(true);
                    getBrowseButton().setEnabled(true);
                    getSequenceNumberTextField().setEnabled(false);
                    getCameraPlatformComboBox().setEnabled(false);
                    getExistingNamesComboBox().setEnabled(false);
                }
                else {
                    getUrlTextField().setEnabled(false);
                    getBrowseButton().setEnabled(false);
                    getSequenceNumberTextField().setEnabled(false);
                    getCameraPlatformComboBox().setEnabled(false);
                    getExistingNamesComboBox().setEnabled(true);
                }
            }
        }
    }

    private JLabel getSelectTimeSourcelbl() {
        if (selectTimeSourcelbl == null) {
            selectTimeSourcelbl = new JLabel("Select Time Source:");
        }
        return selectTimeSourcelbl;
    }

    protected JComboBox getTimeSourceComboBox() {
        if (timeSourceComboBox == null) {
            timeSourceComboBox = new JComboBox();
            timeSourceComboBox.setModel(new DefaultComboBoxModel(TimeSource.values()));
            timeSourceComboBox.setSelectedItem(TimeSource.AUTO);
        }
        return timeSourceComboBox;
    }

    @Override
    public VideoParams getVideoParams() {
        String movieLocation;
        String platformName = null;
        Integer sequenceNumber = null;
        TimeSource timeSource = null;
        if (getOpenExistingRB().isSelected()) {
            movieLocation = (String) getExistingNamesComboBox().getSelectedItem();
        }
        else {
            movieLocation = getUrlTextField().getText();
            // TODO if it's a file URL replace '%20' with a space
            platformName = (String) getCameraPlatformComboBox().getSelectedItem();
            sequenceNumber = Integer.parseInt(getSequenceNumberTextField().getText());
            if (getSupportTimeSource()) {
                timeSource = (TimeSource) getTimeSourceComboBox().getSelectedItem();
            }
        }
        return new VideoParams(movieLocation, platformName, sequenceNumber, timeSource);
    }

    @Override
    public void setSupportTimeSource(boolean s) {
        getTimeSourceComboBox().setEnabled(s);
    }

    @Override
    public boolean getSupportTimeSource() {
        return getTimeSourceComboBox().isEnabled();
    }
}
