/*
 * @(#)AddCommentAssociationDialog.java   2009.12.23 at 09:02:11 PST
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



package vars.annotation.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import vars.annotation.ui.Lookup;
import vars.shared.ui.dialogs.StandardDialog;

/**
 *
 *
 * @version        Enter version here..., 2009.12.23 at 09:02:11 PST
 * @author         Brian Schlining [brian@mbari.org]
 */
public class AddCommentAssociationDialog extends StandardDialog {

    private JScrollPane scrollPane;
    private Status status;
    private JTextField textField;

    /**
     *
     */
    public enum Status { OK, Cancel }

    /**
     * Create the dialog
     */
    public AddCommentAssociationDialog() {
        this((Frame) Lookup.getApplicationFrameDispatcher().getValueObject());
    }

    public AddCommentAssociationDialog(Frame parent) {
        super(parent);

        try {
            initialize();
        }
        catch (Throwable e) {
            e.printStackTrace();
        }

        status = Status.Cancel;
    }

    /**
     * @return
     */
    public String getComment() {
        return getTextField().getText();
    }

    /**
     * @return
     */
    protected JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane();
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
            scrollPane.setViewportView(getTextField());
        }

        return scrollPane;
    }

    /**
     * @return
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @return
     */
    public JTextField getTextField() {
        if (textField == null) {
            textField = new JTextField();
            Dimension size = textField.getPreferredSize();
            textField.setPreferredSize(new Dimension(350, size.height));
        }

        return textField;
    }

    private void initialize() throws Exception {
        setModal(true);
        setTitle("VARS - Enter Comment");
        getCancelButton().addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                AddCommentAssociationDialog.this.setVisible(false);
                status = Status.Cancel;
            }

        });
        getRootPane().setDefaultButton(getOkayButton());
        addHierarchyListener(new HierarchyListener() {

            public void hierarchyChanged(final HierarchyEvent e) {
                if (HierarchyEvent.SHOWING_CHANGED == (HierarchyEvent.SHOWING_CHANGED & e.getChangeFlags())) {
                    AddCommentAssociationDialog.this.getRootPane().setDefaultButton(getOkayButton());
                }
            }

        });

        getContentPane().add(getScrollPane(), BorderLayout.CENTER);

        pack();
    }

    /**
     * Launch the application
     * @param args
     */
    public static void main(String args[]) {
        try {
            AddCommentAssociationDialog dialog = new AddCommentAssociationDialog();
            dialog.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @param comment
     */
    public void setComment(String comment) {
        getTextField().setText(comment);
    }

    /**
     *
     * @param b
     */
    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);

        if (b) {
            getTextField().requestFocusInWindow();
        }
    }
}
