/*
 * @(#)StringValuePanel.java   2009.11.21 at 08:16:56 PST
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

import java.awt.Dimension;
import java.util.List;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import mbarix4j.swing.ListListModel;

/**
 * @author Brian Schlining
 */
public class StringValuePanel extends ValuePanel {

    private JList list = null;
    private JScrollPane scrollPane = null;
    private final List values;

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

    public JList getList() {
        if (list == null) {
            list = new JList();
            list.setVisibleRowCount(4);
            list.setModel(new ListListModel(values));
            list.setEnabled(false);
        }

        return list;
    }

    /**
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

                    if ((obj.length > 0) && (i < obj.length - 1)) {
                        sb.append(", ");
                    }
                }

                sb.append(")");
            }
        }

        return sb.toString();
    }

    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane();
            scrollPane.setViewportView(getList());
        }

        return scrollPane;
    }

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
