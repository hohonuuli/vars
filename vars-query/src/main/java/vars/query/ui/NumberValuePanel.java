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

import com.google.inject.Inject;
import org.mbari.sql.QueryResults;
import org.mbari.swing.JSimpleButton;
import org.mbari.swing.SpinningDial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.swingworker.SwingWorker;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import vars.query.QueryDAO;

//~--- classes ----------------------------------------------------------------

/**
 * @author Brian Schlining
 * @version $Id: NumberValuePanel.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class NumberValuePanel extends ValuePanel {

    private static final long serialVersionUID = -6246141989512191267L;
    /**
	 * @uml.property  name="minLabel"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private JLabel minLabel = null;
    /**
	 * @uml.property  name="minTextField"
	 * @uml.associationEnd  
	 */
    private JTextField minTextField = null;
    /**
	 * @uml.property  name="maxTextField"
	 * @uml.associationEnd  
	 */
    private JTextField maxTextField = null;
    /**
	 * @uml.property  name="maxLabel"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private JLabel maxLabel = null;

    private JButton scanButton = null;

    private static final Logger log = LoggerFactory.getLogger(NumberValuePanel.class);

    private final QueryDAO queryDAO;

    
    //~--- constructors -------------------------------------------------------

    /**
     * This is the default constructor
     *
     * @param name
     */
    @Inject
    public NumberValuePanel(String name, QueryDAO queryDAO) {
        super(name);
        this.queryDAO = queryDAO;
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
            if (!getMinTextField().getText().equals("")) {
                sb.append(" ").append(getValueName()).append(" >= ").append(getMinTextField().getText());
            }

            if (!getMinTextField().getText().equals("") && !getMaxTextField().getText().equals("")) {
                sb.append(" AND ");
            }

            if (!getMaxTextField().getText().equals("")) {
                sb.append("  ").append(getValueName()).append(" <= ").append(getMaxTextField().getText());
            }
        }

        return sb.toString();
    }

    /**
	 * This method initializes jTextField1
	 * @return  javax.swing.JTextField
	 * @uml.property  name="maxTextField"
	 */
    private JTextField getMaxTextField() {
        if (maxTextField == null) {
            maxTextField = new JTextField();
            maxTextField.setPreferredSize(new java.awt.Dimension(120, 20));
            maxTextField.setText("");
            maxTextField.getDocument().addDocumentListener(new DocumentListener() {

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
                    boolean enable = (getMaxTextField().getText().length() > 0) ||
                        (getMinTextField().getText().length() > 0);
                    getConstrainCheckBox().setSelected(enable);
                }

            });

            /*
             * Consume those pesky letters
             */
            maxTextField.addKeyListener(new KeyAdapter() {

                @Override
                public void keyTyped(KeyEvent e) {
                    char key = e.getKeyChar();
                    if (Character.isLetter(key)) {
                        e.consume();
                    }
                }

            });
        }

        return maxTextField;
    }

    /**
	 * This method initializes jTextField
	 * @return  javax.swing.JTextField
	 * @uml.property  name="minTextField"
	 */
    private JTextField getMinTextField() {
        if (minTextField == null) {
            minTextField = new JTextField();
            minTextField.setPreferredSize(new java.awt.Dimension(120, 20));
            minTextField.setText("");
            minTextField.getDocument().addDocumentListener(new DocumentListener() {

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
                    boolean enable = (getMaxTextField().getText().length() > 0) ||
                        (getMinTextField().getText().length() > 0);
                    getConstrainCheckBox().setSelected(enable);
                }

            });

            /*
             * Consume those pesky letters
             */
            minTextField.addKeyListener(new KeyAdapter() {

                @Override
                public void keyTyped(KeyEvent e) {
                    char key = e.getKeyChar();
                    if (Character.isLetter(key)) {
                        e.consume();
                    }
                }

            });
        }

        return minTextField;
    }

    /**
     * The scan button scans for min and max values and sets them in the text fields.
     * @return The scanButton
     */
    private JButton getScanButton() {
        if (scanButton == null) {
            final ImageIcon icon = new ImageIcon(getClass().getResource("/images/vars/query/16px/refresh.png"));
            scanButton = new JSimpleButton(icon);
            scanButton.setToolTipText("Retrieve minimum and maximum " + getValueName() + " values from database");

            /*
             * When the button is pressed it queries the database for the max and min values. When they are
             * returned it sets them in the UI.
             */
            scanButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    final String sql = "SELECT MIN(" + getValueName() + ") AS minValue, MAX(" + getValueName() +
                            ") AS maxValue FROM Annotations WHERE " + getValueName() + " IS NOT NULL";

                    /*
                     * Execute the queries asynchronously so that the UI doesn't block. WHile executing display a
                     * wait indicator over the valuepanel so that no further actions can be taken on it.
                     */
                    scanButton.setIcon(new SpinningDial(16, 16));
                    getMinTextField().setEnabled(false);
                    getMaxTextField().setEnabled(false);
                    scanButton.setEnabled(false);
                    SwingWorker swingWorker = new SwingWorker() {

                        QueryResults queryResults = null;

                        @Override
                        protected Object doInBackground() throws Exception {
                            try {
                                queryResults = queryDAO.executeQuery(sql);
                            } catch (Exception e1) {
                                // TODO Log exception to EventBus
                                log.error("An error occurred while executing the SQL statement: '" + sql + "'", e1);
                            }
                            return queryResults;
                        }

                        @Override
                        protected void done() {

                            String min = "";
                            String max = "";
                            if (queryResults != null && queryResults.rowCount() == 1) {
                                List values = queryResults.getResults("minValue");
                                min = values.get(0).toString();
                                values = queryResults.getResults("maxValue");
                                max = values.get(0).toString();
                            }

                            getMinTextField().setText(min);
                            getMaxTextField().setText(max);
                            scanButton.setIcon(icon);
                            getMinTextField().setEnabled(true);
                            getMaxTextField().setEnabled(true);
                            scanButton.setEnabled(true);
                        }
                    };

                    swingWorker.execute();



                }
            });
        }
        return scanButton;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * This method initializes this
     *
     */
    private void initialize() {
        minLabel = new JLabel();
        maxLabel = new JLabel();
        minLabel.setText(" Min ");
        maxLabel.setText(" Max ");
        this.add(minLabel, null);
        this.add(getMinTextField(), null);
        this.add(maxLabel, null);
        this.add(getMaxTextField(), null);
        this.add(getScanButton());
    }
}
