package vars.queryfx.ui;

import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Brian Schlining
 * @since 2015-07-27T20:44:00
 */
public abstract class ValuePanel extends HBox {

    private static final Pattern PATTERN = Pattern.compile("\\B[A-Z]+");
    private CheckBox constrainCheckBox;
    private CheckBox returnCheckBox;
    private final String valueName;
    private String title;

    public ValuePanel(String valueName) {
        this.valueName = valueName;
        constrainCheckBox = new CheckBox();
        returnCheckBox = new CheckBox();
        getChildren().addAll(returnCheckBox, constrainCheckBox);
    }

    public boolean isReturned() {
        return returnCheckBox.isSelected();
    }

    public boolean isConstrained() {
        return constrainCheckBox.isSelected();
    }

    public String getTitle() {
        if (title == null) {
            title = valueName;
            if (!valueName.toUpperCase().equals(valueName)) {
                Matcher matcher = PATTERN.matcher(valueName);
                title = matcher.replaceAll(" $0");
            }
        }
        return title;
    }

    public String getValueName() {
        return valueName;
    }

    protected CheckBox getConstrainCheckBox() {
        return constrainCheckBox;
    }

    protected CheckBox getReturnCheckBox() {
        return returnCheckBox;
    }
}
