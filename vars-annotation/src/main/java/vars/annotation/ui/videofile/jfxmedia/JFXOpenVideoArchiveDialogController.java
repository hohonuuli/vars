package vars.annotation.ui.videofile.jfxmedia;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import org.bushe.swing.event.EventBus;
import org.mbari.text.IgnoreCaseToStringComparator;
import vars.annotation.CameraDeployment;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.VARSProperties;

import javax.swing.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by brian on 12/19/13.
 */
public class JFXOpenVideoArchiveDialogController {


    private static final Runnable DO_NOTHING_FUNCTION = () -> {};
    private Runnable onOKFunction = DO_NOTHING_FUNCTION;
    private JDialog dialog;
    private JFileChooser chooser;
    private volatile boolean isSelectNameViewInitialized = false;
    private ToolBelt toolBelt;

    @FXML
    private Button browseButton;

    @FXML
    private ComboBox<String> cameraPlatformView;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField movieLocationView;

    @FXML
    private Button okButton;

    @FXML
    private RadioButton rbOpenByLocation;

    @FXML
    private RadioButton rbOpenExisting;

    @FXML
    private ComboBox<String> selectNameView;

    @FXML
    private TextField sequenceNumberView;


    @FXML
    void onBrowse(ActionEvent event) {
        SwingUtilities.invokeLater(() -> {
            if (chooser == null) {
                chooser = new JFileChooser();
            }

            int option = chooser.showOpenDialog(dialog);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try {
                    String url = file.toURI().toURL().toExternalForm();
                    Platform.runLater(() -> movieLocationView.setText(url));
                }
                catch (MalformedURLException e) {
                    EventBus.publish(Lookup.TOPIC_WARNING, e);
                }
            }
        });
    }

    @FXML
    void onCancel(ActionEvent event) {
        dialog.setVisible(false);
    }

    @FXML
    void onOK(ActionEvent event) {
        if (getOnOKFunction() != null) {
            getOnOKFunction().run();
        }
    }

    @FXML
    void onOpenByLocation(ActionEvent event) {
        rbOpenExisting.setSelected(false);
        toggleOpenSelection(false);
    }

    @FXML
    void onOpenExisting(ActionEvent event) {
        rbOpenByLocation.setSelected(false);
        toggleOpenSelection(true);
        if (!isSelectNameViewInitialized) {
            initializeSelectNameView();
        }
    }


    @FXML
    void initialize() {
        rbOpenByLocation.setSelected(true);
        cameraPlatformView.setItems(listCameraPlatforms());
        // only allow digits to be typed
        sequenceNumberView.setOnKeyPressed(e -> {
            if (!e.getCode().isDigitKey()) {
                e.consume();
            }
            updateView();
        });
        movieLocationView.focusedProperty().addListener(cl -> {
            // TODO implement this
        });
    }

    private void updateView() {
        boolean enable = false;
        if (rbOpenExisting.isSelected()) {
            enable = selectNameView.getSelectionModel().getSelectedItem() != null;
        }
        else {
            String sequenceNumber = sequenceNumberView.getText();
            String platform  = cameraPlatformView.getValue();
            enable = sequenceNumber != null && sequenceNumber.length() > 0 && platform != null;
        }
        okButton.setDisable(!enable);

    }

    private ObservableList<String> listCameraPlatforms() {
        final List<String> cameraPlatforms = new ArrayList<>(VARSProperties.getCameraPlatforms());
        cameraPlatforms.sort(new IgnoreCaseToStringComparator());
        return FXCollections.observableList(cameraPlatforms);
    }

    private void initializeSelectNameView() {
        selectNameView.getItems().clear();
        Thread lookupThread = new Thread(() -> {
            // TODO need to add some sort of wait indicator
            List<String> names = toolBelt.getAnnotationPersistenceService().findAllVideoArchiveNames();
            ObservableList<String> obsNames = FXCollections.observableList(names);
            Platform.runLater(() -> {
                selectNameView.setItems(obsNames);
                isSelectNameViewInitialized = true;
            });
        });
        lookupThread.run();
    }

    private void updateVideoArchiveParameters() {
        VideoArchive videoArchive = findByLocation(movieLocationView.getText());
        if (videoArchive == null) {
            sequenceNumberView.setDisable(false);
            sequenceNumberView.setText("");
            cameraPlatformView.setDisable(false);
            updateView();
        }
        else {
            sequenceNumberView.setDisable(true);
            CameraDeployment cameraDeployment = videoArchive.getVideoArchiveSet().getCameraDeployments().iterator().next();
            sequenceNumberView.setText(cameraDeployment.getSequenceNumber().toString());
            cameraPlatformView.setDisable(true);
            cameraPlatformView.getSelectionModel().select(videoArchive.getVideoArchiveSet().getPlatformName());
            updateView();
        }
    }

    private VideoArchive findByLocation(String location) {
        VideoArchiveDAO dao = toolBelt.getAnnotationDAOFactory().newVideoArchiveDAO();
        dao.startTransaction();
        VideoArchive videoArchive = dao.findByName(location);
        dao.endTransaction();
        return videoArchive;
    }

    private void toggleOpenSelection(boolean useExisting) {
        movieLocationView.setDisable(!useExisting);
        cameraPlatformView.setDisable(!useExisting);
        browseButton.setDisable(!useExisting);
        sequenceNumberView.setDisable(!useExisting);
        selectNameView.setDisable(useExisting);
    }

    public Runnable getOnOKFunction() {
        return onOKFunction;
    }

    public void setOnOKFunction(Runnable onOKFunction) {
        this.onOKFunction = onOKFunction;
    }

    public JDialog getDialog() {
        return dialog;
    }

    public void setDialog(JDialog dialog) {
        this.dialog = dialog;
    }

    public ToolBelt getToolBelt() {
        return toolBelt;
    }

    public void setToolBelt(ToolBelt toolBelt) {
        this.toolBelt = toolBelt;
    }
}
