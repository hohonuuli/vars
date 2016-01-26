package vars.avplayer.novideo;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.mbari.util.Tuple2;
import vars.ToolBelt;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.annotation.VideoFrame;
import vars.avplayer.EmptyVideoPlayerController;
import vars.avplayer.VideoPlayerController;

import javax.swing.*;
import java.util.Collection;
import java.util.List;

/**
 * @author Brian Schlining
 * @since 2016-01-25T12:07:00
 */
public class NVOpenVideoArchiveDialogController {

    private static final Runnable DO_NOTHING_FUNCTION = () -> {};
    private Runnable onOKFunction = DO_NOTHING_FUNCTION;
    private JDialog dialog;
    private volatile boolean isSelectNameViewInitialized = false;
    private ToolBelt toolBelt;

    @FXML
    private ComboBox<String> cameraPlatformView;

    @FXML
    private TextField sequenceNumberView;

    @FXML
    private TextField clipIdView;

    @FXML
    private RadioButton rbOpenByLocation;

    @FXML
    private RadioButton rbOpenExisting;

    @FXML
    private ComboBox<String> selectNameView;

    @FXML
    private Button cancelButton;

    @FXML
    private Button okButton;


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
            KeyCode k = e.getCode();
            if (!k.isDigitKey() || k != KeyCode.BACK_SPACE || k != KeyCode.DELETE) {
                e.consume();
            }
            updateView();
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
        // TODO FIXME imlement this ASAP
        //final List<String> cameraPlatforms = new ArrayList<>(VARSProperties.getCameraPlatforms());
        //cameraPlatforms.sort(new IgnoreCaseToStringComparator());
        //return FXCollections.observableList(cameraPlatforms);
        return  FXCollections.emptyObservableList();
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
                selectNameView.getSelectionModel().select(0);
            });
        });
        lookupThread.run();
    }


    private VideoArchive findByLocation(String location) {
        VideoArchiveDAO dao = toolBelt.getAnnotationDAOFactory().newVideoArchiveDAO();
        dao.startTransaction();
        VideoArchive videoArchive = dao.findByName(location);
        dao.endTransaction();
        return videoArchive;
    }

    private void toggleOpenSelection(boolean useExisting) {
        cameraPlatformView.setDisable(useExisting);
        sequenceNumberView.setDisable(useExisting);
        selectNameView.setDisable(!useExisting);
    }

    protected VideoArchive createVideoArchive() {
        int sequenceNumber = Integer.parseInt(sequenceNumberView.getText());
        String platform = cameraPlatformView.getSelectionModel().getSelectedItem();
        String clipId = clipIdView.getText();
        VideoArchiveDAO dao = toolBelt.getAnnotationDAOFactory().newVideoArchiveDAO();
        dao.startTransaction();
        VideoArchive videoArchive = dao.findOrCreateByParameters(platform, sequenceNumber, location);
        dao.endTransaction();
        dao.close();
        return videoArchive;
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
