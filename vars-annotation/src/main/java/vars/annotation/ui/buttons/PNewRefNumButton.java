/*
 * @(#)PNewRefNumButton.java   2012.06.21 at 03:14:16 PDT
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



package vars.annotation.ui.buttons;

import org.mbari.swing.IPopup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.VideoArchive;
import vars.annotation.ui.StateLookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.actions.AddNewRefNumPropAction;
import vars.annotation.ui.eventbus.VideoArchiveSelectedEvent;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * <p>
 * Adds a 'new reference number' association to the currently selected observations.
 * </p>
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class PNewRefNumButton extends PropButton implements IPopup {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private JPopupMenu jPopupMenu;
    private final ToolBelt toolBelt;
    private final PNewRefNumButtonController controller;

    /**
     *  Constructor
     */
    public PNewRefNumButton() {
        super();
        this.toolBelt = getToolBelt();
        controller = new PNewRefNumButtonController(toolBelt.getAnnotationDAOFactory());
        setAction(new AddNewRefNumPropAction(toolBelt));
        initialize();
    }

    private JPopupMenu getJPopupMenu() {
        if (jPopupMenu == null) {

            // Add popup
            jPopupMenu = new JPopupMenu();

            // Set to the last known value + 1
            final JMenuItem auto = new JMenuItem("Set to maximum reference number + 1");
            auto.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    VideoArchive videoArchive = StateLookup.getVideoArchive();
                    controller.respondTo(new VideoArchiveSelectedEvent(PNewRefNumButton.this, videoArchive));
                }
            });
            jPopupMenu.add(auto);

            // Add reset
            final JMenuItem reset = new JMenuItem("Reset to one");
            reset.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent ae) {
                    AddNewRefNumPropAction.setRefNumber(1);
                }
            });
            jPopupMenu.add(reset);

            // Add set to refNumber
            final JMenuItem set = new JMenuItem("Set value");
            set.addActionListener(new ActionListener() {

                public void actionPerformed(final ActionEvent ae) {
                    VideoArchive videoArchive = StateLookup.getVideoArchive();
                    Integer max = controller.findMaxReferenceNumber(videoArchive);
                    String maxString = max == null ? "1" : max.toString();

                    // TO help the user out let's put the max used number in the dialog
                    Frame frame = StateLookup.getAnnotationFrame();
                    final String s = (String) JOptionPane.showInputDialog(frame, "Enter a reference number",
                        "VARS - Input", JOptionPane.PLAIN_MESSAGE, null, null, maxString);
                    if (s == null) {
                        // do nothing
                    }
                    else {
                        try {
                            AddNewRefNumPropAction.setRefNumber(Integer.parseInt(s));
                        }
                        catch (final Exception e) {
                            log.info("We have a failure to communicate. Unable to parse " + s +
                                    " as an integer");
                            actionPerformed(ae); // Try, try again.
                        }
                    }
                }

            });
            jPopupMenu.add(set);
        }

        return jPopupMenu;
    }

    /**
     *  Gets the popupMenu used by this button
     *
     * @return  The popupMenu value
     */
    public JPopupMenu getPopupMenu() {
        return getJPopupMenu();
    }

    private void initialize() {

        // Set some properties
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/newnum.png")));
        setToolTipText("reference #");
        setEnabled(false);
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(final MouseEvent e) {
                maybeShowPopup(e);
            }
            @Override
            public void mouseReleased(final MouseEvent e) {
                maybeShowPopup(e);
            }
            private void maybeShowPopup(final MouseEvent e) {
                if (e.isPopupTrigger()) {
                    getJPopupMenu().show(e.getComponent(), e.getX(), e.getY());
                }
            }

        });
    }


}
