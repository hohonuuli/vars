package vars.queryfx.ui;

import com.guigarage.sdk.util.MaterialDesignButton;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import vars.queryfx.ui.db.IConstraint;
import vars.queryfx.ui.db.MaxConstraint;
import vars.queryfx.ui.db.MinConstraint;
import vars.queryfx.ui.db.MinMaxConstraint;

import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2015-07-27T21:20:00
 */
public class NumberValuePanel extends AbstractValuePanel {

    private TextField minTextField;
    private TextField maxTextField;
    private Button scanButton;

    public NumberValuePanel(String valueName) {
        super(valueName);
        getChildren().addAll(new Label(" Min "),
                getMinTextField(),
                new Label(" Max "),
                getMaxTextField(),
                getScanButton());
    }

    private TextField getMinTextField() {
        if (minTextField == null) {
            minTextField = new TextField();
            minTextField.setPromptText("Enter minimum value");
            minTextField.textProperty().addListener((obs, oldVal, newVal) -> {
                boolean selected = newVal != null && !newVal.isEmpty();
                getConstrainCheckBox().setSelected(selected);
            });

            minTextField.setOnKeyPressed(e -> {
                KeyCode keyCode = e.getCode();
                if (keyCode.isLetterKey()) {
                    e.consume();
                }
            });
            HBox.setHgrow(minTextField, Priority.ALWAYS);
        }
        return minTextField;
    }

    private TextField getMaxTextField() {
        if (maxTextField == null) {
            maxTextField = new TextField();
            maxTextField.setPromptText("Enter maximum value");
            maxTextField.textProperty().addListener((obs, oldVal, newVal) -> {
                boolean selected = newVal != null && !newVal.isEmpty();
                getConstrainCheckBox().setSelected(selected);
            });

            maxTextField.setOnKeyPressed(e -> {
                KeyCode keyCode = e.getCode();
                if (keyCode.isLetterKey()) {
                    e.consume();
                }
            });
            HBox.setHgrow(maxTextField, Priority.ALWAYS);
        }
        return maxTextField;
    }

    protected Button getScanButton() {
        if (scanButton == null) {
            scanButton = new MaterialDesignButton("Scan");
//            scanButton.setTooltip(new Tooltip("Retrieve minimum and maximum " +
//                    getValueName() + " values"));
        }
        return scanButton;
    }

    public void setOnScan(Runnable runnable) {
        scanButton.setOnAction(eventHandler -> runnable.run());
    }

    public String getMinValue() {
        return getMinTextField().getText();
    }

    public String getMaxValue() {
        return getMaxTextField().getText();
    }

    public void setMinValue(Number min) {
        getMinTextField().setText(min.toString());
    }

    public void setMaxValue(Number max) {
        getMaxTextField().setText(max.toString());
    }

    @Override
    public Optional<IConstraint> getConstraint() {
        Double min = null;
        Double max = null;
        try {
            min = Double.parseDouble(getMinValue());
        }
        catch (Exception e) {}
        try {
            max = Double.parseDouble(getMaxValue());
        }
        catch (Exception e) {}

        IConstraint constraint = null;
        if (min != null && max != null) {
            constraint = new MinMaxConstraint(getValueName(), min, max);
        }
        else if (min == null && max != null) {
            constraint = new MaxConstraint(getValueName(), max);
        }
        else if (min != null) {
            constraint = new MinConstraint(getValueName(), min);
        }
        return Optional.ofNullable(constraint);
    }
}
