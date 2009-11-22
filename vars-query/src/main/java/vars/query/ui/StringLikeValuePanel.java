/*
 * @(#)StringLikeValuePanel.java   2009.11.21 at 08:16:04 PST
 *
 * Copyright 2009 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.query.ui;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Brian Schlining
 */
public class StringLikeValuePanel extends ValuePanel {

    private JTextField textField;

    /**
     * @param name
     */
    public StringLikeValuePanel(String name) {
        super(name);
        initialize();
    }


    /**
     *
     * @return
     */
    public String getSQL() {
        StringBuffer sb = new StringBuffer();
        if (getConstrainCheckBox().isSelected()) {
            String text = getTextField().getText();
            if (text.length() > 0) {
                sb.append(" ").append(getValueName()).append(" LIKE '%").append(getTextField().getText());
                sb.append("%' ");
            }
        }

        return sb.toString();
    }

    private JTextField getTextField() {
        if (textField == null) {
            textField = new JTextField();
            textField.setToolTipText("Enter a value. The query will return items that contain this value.");
            textField.getDocument().addDocumentListener(new DocumentListener() {

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
                    boolean enable = (textField.getText().length() > 0);
                    getConstrainCheckBox().setSelected(enable);
                }

            });

        }

        return textField;
    }

 
    private void initialize() {
        add(getTextField());
    }
}
