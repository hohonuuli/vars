package vars.queryfx.ui.controllers;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import vars.queryfx.Lookup;
import vars.queryfx.QueryService;
import vars.queryfx.RXEventBus;
import vars.queryfx.beans.ConceptSelection;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Brian Schlining
 * @since 2015-07-22T09:28:00
 */
public class ConceptPaneController implements Initializable {

    private final QueryService queryService;
    private final RXEventBus eventBus;

    @FXML
    private VBox vbox;

    @FXML
    private ComboBox<String> conceptComboBox;

    @FXML
    private CheckBox parentCheckBox;

    @FXML
    private CheckBox siblingsCheckBox;

    @FXML
    private CheckBox childrenCheckBox;

    @FXML
    private CheckBox descendantsCheckBox;


    @Inject
    public ConceptPaneController(QueryService queryService, RXEventBus eventBus) {
        this.queryService = queryService;
        this.eventBus = eventBus;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        queryService.findAllConceptNamesAsStrings().thenAccept(names -> {
            Platform.runLater(() -> {
                conceptComboBox.setEditable(false);
                conceptComboBox.setPromptText("Loading ...");
                ObservableList<String> obs = FXCollections.observableArrayList(names);

                // Need to add wild card (i.e. Formerly NIL)
                obs.add(0, Lookup.WILD_CARD_CONCEPT.getPrimaryConceptName().getName());
                conceptComboBox.setItems(obs);
                conceptComboBox.setPromptText("");
                conceptComboBox.getSelectionModel().select(0);
                conceptComboBox.setEditable(true);
            });
        });
    }

    public ConceptSelection getConceptSelection() {
        return new ConceptSelection(conceptComboBox.getValue(),
                Lookup.WILD_CARD_LINK,
                parentCheckBox.isSelected(),
                siblingsCheckBox.isSelected(),
                childrenCheckBox.isSelected(),
                descendantsCheckBox.isSelected());
    }


}
