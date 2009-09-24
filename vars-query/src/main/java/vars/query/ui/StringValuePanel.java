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

import java.awt.Dimension;
import java.util.List;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.mbari.swing.ListListModel;

//~--- classes ----------------------------------------------------------------

/**
 * @author Brian Schlining
 * @version $Id: StringValuePanel.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class StringValuePanel extends ValuePanel {

    private static final long serialVersionUID = -8550914763894420669L;
    /**
	 * @uml.property  name="scrollPane"
	 * @uml.associationEnd  
	 */
    private JScrollPane scrollPane = null;
    /**
	 * @uml.property  name="list"
	 * @uml.associationEnd  
	 */
    private JList list = null;
    /**
	 * @uml.property  name="values"
	 */
    private final List values;

    //~--- constructors -------------------------------------------------------

    /**
     * This is the default constructor
     *
     * @param name
     * @param values
     */
    public StringValuePanel(String name, List values) {
        super(name);
        this.values = values;
        initialize();
    }

    //~--- get methods --------------------------------------------------------

    /**
	 * <p><!-- Method description --></p>
	 * @return
	 * @uml.property  name="list"
	 */
    private JList getList() {
        if (list == null) {
            list = new JList();
            list.setVisibleRowCount(4);
            list.setModel(new ListListModel(values));
            list.setEnabled(false);
        }

        return list;
    }

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
        }

        return sb.toString();
    }

    /**
	 * <p><!-- Method description --></p>
	 * @return
	 * @uml.property  name="scrollPane"
	 */
    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane();
            scrollPane.setViewportView(getList());
        }

        return scrollPane;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * This method initializes this
     *
     */
    private void initialize() {
        Dimension d = new Dimension(120, 80);
        setPreferredSize(d);
        setMinimumSize(d);
        this.add(getScrollPane(), null);
        getConstrainCheckBox().addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                boolean enable = getConstrainCheckBox().isSelected();
                getList().setEnabled(enable);
            }
        });
    }
}
