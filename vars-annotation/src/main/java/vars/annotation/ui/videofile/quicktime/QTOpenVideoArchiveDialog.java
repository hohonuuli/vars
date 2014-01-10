package vars.annotation.ui.videofile.quicktime;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.*;

import org.mbari.awt.event.NonDigitConsumingKeyListener;
import org.mbari.swing.SpinningDialWaitIndicator;
import org.mbari.swing.WaitIndicator;
import org.mbari.text.IgnoreCaseToStringComparator;
import org.mbari.util.Tuple2;
import org.mbari.vcr.qt.TimeSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vars.CacheClearedEvent;
import vars.CacheClearedListener;
import vars.ToolBelt;
import vars.annotation.CameraDeployment;
import vars.annotation.VideoArchive;
import vars.annotation.ui.VARSProperties;
import vars.annotation.ui.videofile.VideoPlayerController;
import vars.annotation.ui.videofile.VideoPlayerDialogUI;
import vars.quicktime.QTVideoControlServiceImpl;
import vars.shared.ui.dialogs.StandardDialog;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class QTOpenVideoArchiveDialog extends StandardDialog implements VideoPlayerDialogUI {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private static final int RESPONSE_DELAY = 750;
    private static final Runnable DO_NOTHING_FUNCTION = () -> {  };
    private final ButtonGroup buttonGroup = new ButtonGroup();
    private final ItemListener rbItemListener = new SelectedRBItemListener();
    private final QTOpenVideoArchiveDialogController controller;
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
    public QTOpenVideoArchiveDialog(final Window parent, final ToolBelt toolBelt) {
        super(parent);
        this.toolBelt = toolBelt;
        controller = new QTOpenVideoArchiveDialogController(this);

        /*
         * We're adding a slight delay here so that db lookups don't try to
         * occur as a person types.
         */

        // This action occurs when the timer fires.
        ActionListener changeItemAction = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                updateVideoArchiveParameters();
            }
        };

        // The timer with a delay and bound to above action.
        delayTimer = new Timer(RESPONSE_DELAY, changeItemAction);
        delayTimer.setRepeats(false);

        // Checks when to update the ok button
        ActionListener updateOkayAction = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                updateOkayStatus();
            }
        };
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
        getOkayButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        getCancelButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        getContentPane().add(getPanel(), BorderLayout.CENTER);
        getOpenByLocationRB().setSelected(true);
    }
    
    private JPanel getPanel() {
        if (panel == null) {
            panel = new JPanel();
            GroupLayout gl_panel = new GroupLayout(panel);
            gl_panel.setHorizontalGroup(
                gl_panel.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_panel.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
                            .addComponent(getOpenByLocationRB())
                            .addGroup(gl_panel.createSequentialGroup()
                                .addGap(29)
                                .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
                                    .addComponent(getBrowseButton())
                                    .addGroup(gl_panel.createSequentialGroup()
                                        .addComponent(getLblMovie())
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(getUrlTextField(), GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE))
                                    .addGroup(gl_panel.createSequentialGroup()
                                        .addComponent(getLblCameraPlatform(), GroupLayout.PREFERRED_SIZE, 108, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                        .addComponent(getCameraPlatformComboBox(), 0, 314, Short.MAX_VALUE))
                                    .addGroup(gl_panel.createSequentialGroup()
                                        .addComponent(getLblSequenceNumber(), GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(getSequenceNumberTextField(), GroupLayout.PREFERRED_SIZE, 311, GroupLayout.PREFERRED_SIZE))))
                            .addComponent(getOpenExistingRB())
                            .addGroup(gl_panel.createSequentialGroup()
                                .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
                                    .addGroup(gl_panel.createSequentialGroup()
                                        .addGap(29)
                                        .addComponent(getLblSelectName(), GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE))
                                    .addComponent(getSelectTimeSourcelbl()))
                                .addGap(30)
                                .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
                                    .addComponent(getTimeSourceComboBox(), 0, 311, Short.MAX_VALUE)
                                    .addComponent(getExistingNamesComboBox(), 0, 311, Short.MAX_VALUE))))
                        .addContainerGap())
            );
            gl_panel.setVerticalGroup(
                gl_panel.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_panel.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(getOpenByLocationRB())
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                            .addComponent(getLblMovie())
                            .addComponent(getUrlTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(getBrowseButton())
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                            .addComponent(getLblCameraPlatform())
                            .addComponent(getCameraPlatformComboBox(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                            .addComponent(getLblSequenceNumber())
                            .addComponent(getSequenceNumberTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(18)
                        .addComponent(getOpenExistingRB())
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                            .addComponent(getLblSelectName())
                            .addComponent(getExistingNamesComboBox(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(18)
                        .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                            .addComponent(getSelectTimeSourcelbl())
                            .addComponent(getTimeSourceComboBox(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(53, Short.MAX_VALUE))
            );
            panel.setLayout(gl_panel);
        }
        return panel;
    }

    private String[] listCameraPlatforms() {
        final Collection<String> cameraPlatforms = VARSProperties.getCameraPlatforms();
        String[] cp = new String[cameraPlatforms.size()];
        cameraPlatforms.toArray(cp);
        Arrays.sort(cp, new IgnoreCaseToStringComparator());
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
                    int option = chooser.showOpenDialog(QTOpenVideoArchiveDialog.this);
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
                        List<String> names = toolBelt.getAnnotationPersistenceService().findAllVideoArchiveNames();
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
        VideoArchive videoArchive = controller.findByLocation(getUrlTextField().getText());
        if (videoArchive == null) {
            getSequenceNumberTextField().setEnabled(true);
            getSequenceNumberTextField().setText("");
            getCameraPlatformComboBox().setEnabled(true);
            updateOkayStatus();
        }
        else {
            getSequenceNumberTextField().setEnabled(false);
            CameraDeployment cameraDeployment = videoArchive.getVideoArchiveSet().getCameraDeployments().iterator().next();
            getSequenceNumberTextField().setText(cameraDeployment.getSequenceNumber() + "");
            getCameraPlatformComboBox().setEnabled(false);
            getCameraPlatformComboBox().setSelectedItem(videoArchive.getVideoArchiveSet().getPlatformName());
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

    public Tuple2<VideoArchive, VideoPlayerController> openVideoArchive() {
        return controller.openVideoArchive();
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
}
