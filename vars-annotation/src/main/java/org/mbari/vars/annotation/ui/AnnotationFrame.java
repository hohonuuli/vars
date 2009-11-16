/*
 * @(#)AnnotationFrame.java   2009.11.13 at 11:01:28 PST
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



/**
 * @created  December 16, 2003
 */
package org.mbari.vars.annotation.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import org.mbari.awt.WaitCursorEventQueue;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.vars.annotation.ui.actions.ClearDatabaseCacheAction;
import org.mbari.vars.annotation.ui.actions.ExitAction;
import org.mbari.vars.annotation.ui.actions.FrameCaptureAction2;
import org.mbari.vars.annotation.ui.actions.OpenConnectionsAction;
import org.mbari.vars.annotation.ui.actions.ShowOpenVideoArchiveDialogAction;
import org.mbari.vars.annotation.ui.dialogs.AboutDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.ui.ToolBelt;

/**
 * <p>This frame is the parent frame of all UI components in the annotation
 * application.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class AnnotationFrame extends JFrame {

    private static final Logger log = LoggerFactory.getLogger(AnnotationFrame.class);
    private VideoSetViewer viewer = null;
    private JPanel bottomPanel;
    private JPanel contentPanel;
    private EventQueue eventQueue;
    private JMenuBar myMenuBar;
    private JPanel quickControlsPanel;
    private JPanel statusPanel;
    private final ToolBelt toolbelt;

    /**
     * Creates new form JFrame
     */
    public AnnotationFrame(ToolBelt toolbelt) {
        super();
        this.toolbelt = toolbelt;
        initialize();
        pack();
    }

    /**
     * Exit the Application
     *
     * @param  evt             Description of the Parameter
     */
    private void exitForm(final WindowEvent evt) {

        (new ExitAction()).doAction();
    }

    private JPanel getBottomPanel() {
        if (bottomPanel == null) {
            bottomPanel = new JPanel();
            bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
            bottomPanel.add(getQuickControlsPanel());
            bottomPanel.add(Box.createHorizontalGlue());
            bottomPanel.add(getStatusPanel());

            // bottomPanel.add(getClockPanel(), BorderLayout.EAST);
        }

        return bottomPanel;
    }

    private JPanel getContentPanel() {
        if (contentPanel == null) {
            contentPanel = new AnnotationPanel(toolbelt);
            contentPanel.setPreferredSize(new Dimension(300, 200));
        }

        return contentPanel;
    }

    EventQueue getEventQueue() {
        if (eventQueue == null) {
            eventQueue = new WaitCursorEventQueue(500);
        }

        return eventQueue;
    }

    private JMenuBar getMyMenuBar() {
        if (myMenuBar == null) {
            myMenuBar = new JMenuBar();

            /*
             *  File Menu
             */
            final JMenu menuFile = new JMenu("File");

            menuFile.setMnemonic('F');

            // create Connect menu item
            final JMenuItem connect = new JMenuItem(new OpenConnectionsAction());

            menuFile.add(connect);
            menuFile.setMnemonic('C');


            // create Open menu item
            final JMenuItem openVideoArchive = new JMenuItem(new ShowOpenVideoArchiveDialogAction());

            menuFile.add(openVideoArchive);
            menuFile.setMnemonic('O');

            // Clears the kb cache and resets the kb tree
            JMenuItem clearCache = new JMenuItem(new ClearDatabaseCacheAction());

            menuFile.add(clearCache);

            // create Exit menu item
            final JMenuItem fileExit = new JMenuItem(new ExitAction());

            fileExit.setMnemonic('X');
            menuFile.add(fileExit);


            /*
             *  View Menu
             */
            final JMenu menuView = new JMenu("View");

            menuView.setMnemonic('V');

            // Opens a Window that views all Observations in a set
            final JMenuItem viewVideoSet = new JMenuItem("Video Set");

            viewVideoSet.setMnemonic('V');
            viewVideoSet.setToolTipText("View the video set which the currently opened video archive belongs to");
            viewVideoSet.addActionListener(new ActionListener() {

                public void actionPerformed(final ActionEvent ae) {
                    if (viewer == null) {
                        viewer = new VideoSetViewer();
                    }

                    viewer.setVisible(true);
                    viewer.toFront();
                }

            });
            menuView.add(viewVideoSet);

            // Create Quicktime settings menu item
            final JMenuItem quicktime = new JMenuItem(new ActionAdapter() {

                /**
                 *
                 */
                private static final long serialVersionUID = -5027677027442672087L;

                public void doAction() {
                    FrameCaptureAction2.showSettingsDialog();
                }
            });

            quicktime.setText("QuickTime Settings");
            menuView.add(quicktime);

            /*
             *  About Menu
             */
            final JMenu menuHelp = new JMenu("Help");

            menuHelp.setMnemonic('H');

            // create About menu item
            final JMenuItem helpAbout = new JMenuItem("About");

            helpAbout.setMnemonic('A');
            helpAbout.addActionListener(new ActionListener() {

                public void actionPerformed(final ActionEvent e) {
                    final AboutDialog aboutDialog = new AboutDialog(AnnotationFrame.this, true);
                    final Dimension frameSize = getSize();
                    final Dimension aboutSize = aboutDialog.getPreferredSize();
                    int x = getLocation().x + (frameSize.width - aboutSize.width) / 2;
                    int y = getLocation().y + (frameSize.height - aboutSize.height) / 2;

                    if (x < 0) {
                        x = 0;
                    }

                    if (y < 0) {
                        y = 0;
                    }

                    aboutDialog.setLocation(x, y);
                    aboutDialog.setVisible(true);
                }

            });
            menuHelp.add(helpAbout);
            myMenuBar.add(menuFile);
            myMenuBar.add(menuView);
            myMenuBar.add(menuHelp);
        }

        return myMenuBar;
    }

    private JPanel getQuickControlsPanel() {
        if (quickControlsPanel == null) {
            quickControlsPanel = new QuickControlsPanel();
        }

        return quickControlsPanel;
    }

    private JPanel getStatusPanel() {
        if (statusPanel == null) {
            statusPanel = new StatusPanel();
        }

        return statusPanel;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    private void initialize() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(getContentPanel(), BorderLayout.CENTER);

        // set title
        ResourceBundle bundle = ResourceBundle.getBundle("vars-annotation");
        final String title = bundle.getString("frame.title");

        setTitle(title);

        // add status bar
        getContentPane().add(getBottomPanel(), BorderLayout.SOUTH);

        // add menu bar
        setJMenuBar(getMyMenuBar());
        addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosing(final java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });
        Toolkit.getDefaultToolkit().getSystemEventQueue().push(getEventQueue());
    }
}
