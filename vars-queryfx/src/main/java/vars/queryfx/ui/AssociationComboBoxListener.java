package vars.queryfx.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import vars.ILink;

import java.util.stream.Collectors;


/**
 * @author Brian Schlining
 * @since 2015-07-22T16:42:00
 */
public class AssociationComboBoxListener implements EventHandler<KeyEvent> {

    private final ComboBox<ILink> comboBox;
    private ObservableList<ILink> data;
    private boolean moveCaretToPos = false;
    private int caretPos;

    public AssociationComboBoxListener(ComboBox<ILink> comboBox) {
        this.comboBox = comboBox;

        data = comboBox.getItems();

        this.comboBox.setEditable(true);
        this.comboBox.setOnKeyPressed(e -> comboBox.hide());
        this.comboBox.setOnKeyReleased(this);
    }

    @Override
    public void handle(KeyEvent event) {

        TextField editor = comboBox.getEditor();
        String text = editor.getText();

        if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT
                || event.isControlDown() || event.getCode() == KeyCode.HOME
                || event.getCode() == KeyCode.END || event.getCode() == KeyCode.TAB) {
            return;
        }
        else if(event.getCode() == KeyCode.UP) {
            caretPos = -1;
            moveCaret(text.length());
            return;
        }
        else if(event.getCode() == KeyCode.DOWN) {
            if(!comboBox.isShowing()) {
                comboBox.show();
            }
            caretPos = -1;
            moveCaret(text.length());
            return;
        }
        else if(event.getCode() == KeyCode.BACK_SPACE) {
            moveCaretToPos = true;
            caretPos = editor.getCaretPosition();
        }
        else if(event.getCode() == KeyCode.DELETE) {
            moveCaretToPos = true;
            caretPos = editor.getCaretPosition();
        }

        String upperCaseText = text.toUpperCase();
        ObservableList<ILink> list = FXCollections.observableArrayList(data.stream()
                .filter(s -> s.getLinkName().toUpperCase().contains(upperCaseText) ||
                            s.getLinkValue().toUpperCase().contains(upperCaseText))
                .collect(Collectors.toList()));

        comboBox.setItems(list);
        comboBox.getEditor().setText(text);
        if(!moveCaretToPos) {
            caretPos = -1;
        }
        moveCaret(text.length());
        if(!list.isEmpty()) {
            comboBox.show();
        }
    }

    private void moveCaret(int textLength) {
        if(caretPos == -1) {
            comboBox.getEditor().positionCaret(textLength);
        } else {
            comboBox.getEditor().positionCaret(caretPos);
        }
        moveCaretToPos = false;
    }
}
