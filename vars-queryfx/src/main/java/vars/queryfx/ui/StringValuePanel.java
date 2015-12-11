package vars.queryfx.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import vars.queryfx.ui.db.IConstraint;
import vars.queryfx.ui.db.InConstraint;
import vars.queryfx.ui.db.LikeConstraint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2015-07-27T22:02:00
 */
public class StringValuePanel extends AbstractValuePanel {


    private ListView<String> listView;
    private TextField textField;
    private ToggleButton toggleButton;

    public StringValuePanel(String valueName) {
        super(valueName);
        Region btnSpacer = new Region();
        btnSpacer.setMinWidth(10);
        HBox.setHgrow(btnSpacer, Priority.NEVER);
        getChildren().addAll(getTextField(), btnSpacer, getToggleButton());
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
                            getConstrainCheckBox().setSelected(true);
                        }
                    });
            listView.itemsProperty().addListener((obs, oldVal, newVal) -> {
                int visibleRows = 6;
                if (newVal.size() > 20) {
                    visibleRows = 10;
                }
                else if (newVal.size() == 0) {
                    visibleRows = 3;
                }
                listView.prefHeightProperty().set(getTextField().getHeight() * visibleRows);
            });
            HBox.setHgrow(listView, Priority.ALWAYS);
        }
        return listView;
    }

    private TextField getTextField() {
        if (textField == null) {
            textField = new TextField();
            textField.textProperty().addListener((obs, oldVal, newVal) -> {
                getConstrainCheckBox().setSelected(newVal != null && !newVal.isEmpty());
            });
            HBox.setHgrow(textField, Priority.ALWAYS);
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
            getChildren().remove(2);
            getChildren().add(2, node);
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

    public void setValues(Collection<String> values) {
        getListView().getItems().clear();
        getListView().getItems().addAll(values);
    }

    public Optional<IConstraint> getConstraint() {
        List<String> selectedValues = getSelectedValues();
        Optional<IConstraint> constraint = Optional.empty();
        if (isConstrained() && !selectedValues.isEmpty()) {
            if (getToggleButton().isSelected()) {
                constraint = Optional.of(new InConstraint(getValueName(), selectedValues));
            }
            else {
                constraint = Optional.of(new LikeConstraint(getValueName(), selectedValues.get(0)));
            }
        }
        return constraint;
    }
}
