/*
 * Copyright 2005 MBARI
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1 
 * (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package vars.query.ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

//~--- classes ----------------------------------------------------------------

/**
 * <p><!-- Insert Description --></p>
 *
 * @author Brian Schlining
 * @version $Id: StringLikeValuePanel.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class StringLikeValuePanel extends ValuePanel {

    private static final long serialVersionUID = -6475458493306891895L;
    /**
	 * @uml.property  name="textField"
	 * @uml.associationEnd  
	 */
    private JTextField textField;

    //~--- constructors -------------------------------------------------------

    /**
     * @param name
     */
    public StringLikeValuePanel(String name) {
        super(name);
        initialize();
    }

    //~--- get methods --------------------------------------------------------

    /*
     *  (non-Javadoc)
     * @see query.ui.ValuePanel#getSQL()
     */

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @return
     */
    public String getSQL() {
        StringBuffer sb = new StringBuffer();
        if (getConstrainCheckBox().isSelected()) {
            String text = getTextField().getText();
            if (text.length() > 0) {
                sb.append(
                        " ").append(
                        getValueName()).append(
                        " LIKE '%").append(getTextField().getText());
                sb.append("%' ");
            }
        }

        return sb.toString();
    }

    /**
	 * <p><!-- Method description --></p>
	 * @return
	 * @uml.property  name="textField"
	 */
    private JTextField getTextField() {
        if (textField == null) {
            textField = new JTextField();
            textField.setToolTipText(
                    "Enter a value. The query will return items that contain this value.");
            textField.getDocument().addDocumentListener(
                    new DocumentListener() {

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

            /*
             * textField.addKeyListener(new KeyAdapter() {
             *
             *   public void keyTyped(KeyEvent e) {
             *       char key = e.getKeyChar();
             *
             *       if (key == KeyEvent.VK_ENTER) {
             *           // DO nothing
             *       } else if ((key == KeyEvent.VK_DELETE) &&
             *                  (textField.getText().length() < 1)) {
             *           getConstrainCheckBox().setSelected(false);
             *       } else if ((key == KeyEvent.VK_BACK_SPACE) &&
             *                  (textField.getText().length() == 1)) {
             *           getConstrainCheckBox().setSelected(false);
             *       } else {
             *           getConstrainCheckBox().setSelected(true);
             *       }
             *   }
             *
             * });
             */
        }

        return textField;
    }



    //~--- methods ------------------------------------------------------------

    /**
     * <p><!-- Method description --></p>
     *
     */
    private void initialize() {
        add(getTextField());
    }
}
