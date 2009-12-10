/*
 * @(#)StandardDialog.java   2009.12.09 at 09:50:30 PST
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



package vars.shared.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import org.mbari.swing.JFancyButton;
import javax.swing.border.EmptyBorder;

/**
 *
 *
 * @version        Enter version here..., 2009.12.09 at 09:46:46 PST
 * @author         Brian Schlining [brian@mbari.org]
 */
public class StandardDialog extends JDialog {

    private JButton cancelButton;
    private JButton okayButton;
    private JPanel panel;

    /**
     * Constructs ...
     */
    public StandardDialog() {
        super();
        initialize();
        pack();
    }

    /**
     * Constructs ...
     *
     * @param owner
     */
    public StandardDialog(Dialog owner) {
        super(owner);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param owner
     */
    public StandardDialog(Frame owner) {
        super(owner);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param owner
     */
    public StandardDialog(Window owner) {
        super(owner);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param owner
     * @param modal
     */
    public StandardDialog(Dialog owner, boolean modal) {
        super(owner, modal);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param owner
     * @param title
     */
    public StandardDialog(Dialog owner, String title) {
        super(owner, title);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param owner
     * @param modal
     */
    public StandardDialog(Frame owner, boolean modal) {
        super(owner, modal);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param owner
     * @param title
     */
    public StandardDialog(Frame owner, String title) {
        super(owner, title);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param owner
     * @param modalityType
     */
    public StandardDialog(Window owner, ModalityType modalityType) {
        super(owner, modalityType);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param owner
     * @param title
     */
    public StandardDialog(Window owner, String title) {
        super(owner, title);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param owner
     * @param title
     * @param modal
     */
    public StandardDialog(Dialog owner, String title, boolean modal) {
        super(owner, title, modal);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param owner
     * @param title
     * @param modal
     */
    public StandardDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param owner
     * @param title
     * @param modalityType
     */
    public StandardDialog(Window owner, String title, ModalityType modalityType) {
        super(owner, title, modalityType);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param owner
     * @param title
     * @param modal
     * @param gc
     */
    public StandardDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param owner
     * @param title
     * @param modal
     * @param gc
     */
    public StandardDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param owner
     * @param title
     * @param modalityType
     * @param gc
     */
    public StandardDialog(Window owner, String title, ModalityType modalityType, GraphicsConfiguration gc) {
        super(owner, title, modalityType, gc);
        initialize();
    }

    /**
     * @return
     */
    public JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JFancyButton("Cancel");
            cancelButton.setIcon(new ImageIcon(getClass().getResource("/vars/images/24/delete2.png")));
            cancelButton.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        cancelButton.doClick();
                    }
                }
            });
        }

        return cancelButton;
    }

    /**
     * @return
     */
    public JButton getOkayButton() {
        if (okayButton == null) {
            okayButton = new JFancyButton("OK");
            okayButton.setIcon(new ImageIcon(getClass().getResource("/vars/images/24/check2.png")));
            okayButton.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        okayButton.doClick();
                    }
                }
            });
        }

        return okayButton;
    }



    private JPanel getPanel() {
        if (panel == null) {
            panel = new JPanel();
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            panel.add(Box.createHorizontalGlue());
            panel.add(getCancelButton());
            panel.add(Box.createHorizontalStrut(10));
            panel.add(getOkayButton());
        }

        return panel;
    }

    private void initialize() {
        getContentPane().add(getPanel(), BorderLayout.SOUTH);
        setLocationRelativeTo(getOwner());
    }

}
