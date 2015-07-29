package vars.queryfx.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Brian Schlining
 * @since 2015-07-27T22:02:00
 */
public class StringValuePanel extends AbstractValuePanel {


    private ListView<String> listView;
    private TextField textField;
    private ToggleButton toggleButton;
    private BooleanProperty enableConstraint = new SimpleBooleanProperty(false);

    public StringValuePanel(String valueName) {
        super(valueName);
        getChildren().addAll(getTextField(), getToggleButton());
    }

    private ListView<String> getListView() {
        if (listView == null) {
            listView = new ListView<>();
            listView.getSelectionModel()
                    .setSelectionMode(SelectionMode.MULTIPLE);
            listView.getSelectionModel()
                    .getSelectedItems()
                    .addListener((ListChangeListener.Change<? extends String> c) -> {
                        if (listView.getSelectionModel().getSelectedItems().size() > 0) {
                            enableConstraint.set(true);
                        }
                    });
            listView.itemsProperty().addListener((obs, oldVal, newVal) -> {
                int visibleRows = 7;
                if (newVal.size() > 20) {
                    visibleRows = 14;
                }
                else if (newVal.size() == 0) {
                    visibleRows = 3;
                }
                listView.prefHeightProperty().set(getTextField().getHeight() * visibleRows);
            });
        }
        return listView;
    }

    private TextField getTextField() {
        if (textField == null) {
            textField = new TextField();
            textField.textProperty().addListener((obs, oldVal, newVal) -> {
                enableConstraint.set(newVal != null && !newVal.isEmpty());
            });
        }
        return textField;
    }

    private ToggleButton getToggleButton() {
        if (toggleButton == null) {
            toggleButton = new ToggleButton("Scan");
        }
        return toggleButton;
    }

    public void setOnScan(Runnable runnable) {
        getToggleButton().selectedProperty().addListener((obs, oldVal, newVal) -> {
            Node node;
            if (newVal) {
                runnable.run();
                node = getListView();
            }
            else {
                node = getTextField();
            }
            getChildren().remove(3);
            getChildren().add(3, node);
        });
    }

    public List<String> getSelectedValues() {
        List<String> list = new ArrayList<>();
        if (getToggleButton().isSelected()) {
             list.addAll(getListView().getItems().sorted());
        }
        else {
            String text = getTextField().getText();
            if (text != null && !text.isEmpty()) {
                list.add(text);
            }
        }
        return list;
    }

    public void setSelectedValues(Collection<String> values) {
        getListView().getItems().clear();
        getListView().getItems().addAll(values);
    }
}
