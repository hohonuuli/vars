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
import javax.swing.event.ListSelectionEvent;
import org.mbari.swing.ListListModel;
import org.mbari.swing.SpinningDial;
import org.mbari.text.IgnoreCaseToStringComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.event.ListSelectionListener;
import org.mbari.swingworker.SwingWorker;
import vars.VARSException;
import vars.query.QueryDAO;

/**
 * <p><!-- Insert Description --></p>
 *
 * @author Brian Schlining
 * @version $Id: StringLikeValuePanel.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class AdvancedStringValuePanel extends ValuePanel {

    private final QueryDAO queryDAO;

    private JTextField textField;
    private JList list;
    private ListListModel listModel;
    private JToggleButton toggleButton;
    private final Icon textFieldIcon;
    private final Icon listIcon;
    private JScrollPane scrollPane;
    private static final Logger log = LoggerFactory.getLogger(AdvancedStringValuePanel.class);
    /** Flag is true if fetching data from database. False otherwise */
    private volatile transient boolean scanFlag = false;
    private static final int VISIBLE_ROW_COUNT = 14;
    //~--- constructors -------------------------------------------------------

    /**
     * @param name
     */
    @Inject
    public AdvancedStringValuePanel(String name, QueryDAO queryDAO) {
        super(name);
        this.queryDAO = queryDAO;
        listIcon = new ImageIcon(getClass().getResource("/images/vars/query/16px/table_view.png"));
        textFieldIcon = new ImageIcon(getClass().getResource("/images/vars/query/16px/pencil2.png"));
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
        // TODO implement list selection here.
        StringBuffer sb = new StringBuffer();
        if (getToggleButton().isSelected()) {
            Object[] obj = getList().getSelectedValues();
            if (obj.length > 0) {
                sb.append(" ").append(getValueName()).append(" IN (");
                for (int i = 0; i < obj.length; i++) {
                    sb.append("'").append(obj[i].toString()).append("'");
                    if (obj.length > 0 && i < obj.length - 1) {
                        sb.append(", ");
                    }
                }
                sb.append(")");
            }
            
        } else {
            if (getConstrainCheckBox().isSelected()) {
                String text = getTextField().getText();
                if (text.length() > 0) {
                    sb.append(" ").append(getValueName()).append(" LIKE '%").append(getTextField().getText());
                    sb.append("%' ");
                }
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
            textField.setToolTipText("Enter a value. The query will return items that contain this value.");

            // Enable constrain check box if ANY text is in the text field.
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
                    boolean enable = textField.getText().length() > 0;
                    getConstrainCheckBox().setSelected(enable);
                }
            });
        }

        return textField;
    }

    /**
     * <p><!-- Method description --></p>
     * @return
     * @uml.property  name="list"
     */
    private JList getList() {
        if (list == null) {
            list = new JList();
            list.setVisibleRowCount(3);
            list.setModel(getListModel());
            list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            
            
            // Select the constrain checkbox if any items are selected in the list
            list.addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    int i = list.getSelectedIndex();
                    getConstrainCheckBox().setSelected(i > -1);
                }
            });
            
        }

        return list;
    }

    /**
     * @return A scrollpane to hold the list
     */
    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane(getList());
            scrollPane.setMinimumSize(new Dimension(200, 100));
        }
        return scrollPane;
    }
    
    private ListListModel getListModel(JList list) {
        if (listModel == null) {
            
        }
        return listModel;
    }

    /**
     * A list model used internally to store the data list
     */
    @SuppressWarnings(value = "unchecked")
    private ListListModel getListModel() {
        if (listModel == null) {
            scanFlag = true;
            listModel = new ListListModel(Collections.synchronizedList(new ArrayList()));
            // Get DISTINCT Values from the databse

            /*
             * Fetch from the database off of the EDT. During the fetch disable
             * only the current ValuePanel.
             */
            SwingWorker worker = new SwingWorker() {

                final List results = new ArrayList();

                protected Object doInBackground() throws Exception {
                    try {
                        // TODO this needs to be spun off in SwingWorker
                        results.addAll(queryDAO.getUniqueValuesByColumn(getValueName()));
                    } catch (VARSException e1) {
                        log.error("An error occurred while finding unique values for " + getValueName(), e1);
                    }
                    Collections.sort(results, new IgnoreCaseToStringComparator());
                    return results;
                }

                @Override
                protected void done() {
                    super.done();
                    
                    /*
                     * If the contents of a JList are too long, it won't calculate
                     * the cell size. This causes the JList to appear as a single
                     * row in the UI; pretty annoying when there's 1000's of items 
                     * in the list!! As a workaround we'll create a prototype
                     * cell value for the list to work with. It'll use this 
                     * prototype to caculate cell height and width.
                     */
                    if (results.size() > VISIBLE_ROW_COUNT) {
                        String prototype = " ";
                        for (Iterator it = results.iterator(); it.hasNext();) {
                            int maxLength = 0;
                            Object object = it.next();
                            if (object != null) {
                                String s = object.toString();
                                int length = s.length();
                                if (length > maxLength) {
                                    maxLength = length;
                                    prototype = s;
                                }
                            }
                        }
                        getList().setPrototypeCellValue(prototype);
                    }

                    
                    listModel.addAll(results);
                    Icon currentIcon = getToggleButton().isSelected() ? textFieldIcon : listIcon;
                    getToggleButton().setIcon(currentIcon);
                    scanFlag = false;
                    update();
                }
            };

            worker.execute();
        }

        return listModel;
    }

    /**
     * Toggle which 'editor' to use. If selected, use the list editor otherwise use the textField
     * @return
     */
    private JToggleButton getToggleButton() {
        if (toggleButton == null) {
            toggleButton = new JToggleButton(listIcon);
            toggleButton.setSelected(false);

            /**
             * ON click swap the editor. Selected means use the list, unselected
             * means use the textfield
             */
            toggleButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    update();
                }
            });
        }
        return toggleButton;
    }

    private void update() {
        Component[] components = getComponents();
        int i = 0;
        Component tf = getTextField();
        Component sp = getScrollPane();
        for (Component c : components) {
            if (c == sp || c == tf) {
                break;
            }
            i++;
        }
        remove(i);

        if (scanFlag) {
            setEnabled(false);
        }

        Icon icon = null;
        JToggleButton button = getToggleButton();
        if (scanFlag) {
            icon = new SpinningDial(16, 16);
        } else {
            icon = (button.isSelected()) ? textFieldIcon : listIcon;
        }
        button.setIcon(icon);

        if (toggleButton.isSelected()) {
            add(getScrollPane(), i);
            int size = getListModel().getSize();
            int visibleRowCount = 7;
            if (size > 20) {
                visibleRowCount = 14;
            }
            else if (size > 0) {
                visibleRowCount = size;
            }
            else {
                visibleRowCount = 3;
            }
            log.debug("Setting visible row count in '" + getValueName() + "' list to " + 
                    visibleRowCount + " (" + size + " items in list )");
            getList().setVisibleRowCount(visibleRowCount);
            toggleButton.setToolTipText("Press to open text editor");
            // Set contrain check box if any items are selected
            int idx = getList().getSelectedIndex();
            getConstrainCheckBox().setSelected(idx > -1);
            
        } else {
            add(getTextField(), i);
            toggleButton.setToolTipText("Press to open a selectable list of values");
            // Set constrain checkbox if text field has anything it it.
            String text = getTextField().getText();
            getConstrainCheckBox().setSelected(text.length() > 0);
        }

        if (!scanFlag) {
            setEnabled(true);
        }
        revalidate();
    }
    //~--- methods ------------------------------------------------------------

    /**
     * <p><!-- Method description --></p>
     *
     */
    private void initialize() {
        add(getTextField());
        add(getToggleButton());
    }
}