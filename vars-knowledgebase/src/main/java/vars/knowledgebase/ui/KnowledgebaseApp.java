
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



package vars.knowledgebase.ui;

import foxtrot.Task;
import foxtrot.Worker;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Date;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.awt.WaitCursorEventQueue;
import org.mbari.swing.SplashFrame;
import org.mbari.util.Dispatcher;
import org.mbari.util.SystemUtilities;
import org.mbari.vars.dao.DAOException;
import org.mbari.vars.knowledgebase.model.Concept;
import org.mbari.vars.knowledgebase.model.dao.KnowledgeBaseCache;
import org.mbari.vars.knowledgebase.ui.actions.PopulateDatabaseAction;
import org.mbari.vars.model.UserAccount;
import org.mbari.vars.ui.SearchableConceptTreePanel;
import org.mbari.vars.util.AppFrameDispatcher;

/**
 * <p><!-- Class description --></p>
 *
 * @version    $Id: KnowledgebaseApp.java 295 2006-07-06 23:47:31Z hohonuuli $
 * @author     <a href="http://www.mbari.org">Monterey Bay Aquarium Research Institute</a>
 */
public class KnowledgebaseApp {

    /**
     * Key used to retrieve the current QueryApp instance from a Dispatcher object.
     * Use as:
     * <pre>
     * KnowledgebaseApp app = (KnowledgebaseApp) Dispatcher.getDispatcher(DISPATCHER_KEY).getValueObject();
     * </pre>
     */
    public static final Object KEY_DISPATCHER = KnowledgebaseApp.class;

    /**
     *
     */
    public static final Dispatcher DISPATCHER_USERACCOUNT = Dispatcher.getDispatcher(UserAccount.class);

    /**
     *
     */
    public static final Dispatcher DISPATCHER_TREE = Dispatcher.getDispatcher(SearchableConceptTreePanel.class);

    /**
     *
     */
    public static final Dispatcher DISPATCHER_SELECTED_CONCEPT = Dispatcher.getDispatcher(Concept.class);

    /**
     *
     */
    public static final Dispatcher DISPATCHER = Dispatcher.getDispatcher(KnowledgebaseApp.class);

    /**
     * This is the key to the dispatcher that holds the currently logged in
     * UserAccount.
     * <pre>
     * UserAccount c = (UserAccount) Dispatcher.getDispatcher(DISPATCHER_KEY_USERACOUNT).getValueObject();
     * </pre>
     */
    public static final Object KEY_DISPATCHER_USERACCOUNT = UserAccount.class;

    /**
     * This is the key to the dispatcerh that holds the
     * SearchableConceptTreePanel that is being used by the Knowledgebase
     * application
     * <pre>
     * SearchableConceptTreePanel c = (SearchableConceptTreePanel) Dispatcher.getDispatcher(DISPATCHER_KEY_TREE).getValueObject();
     * </pre>
     */
    public static final Object KEY_DISPATCHER_TREE = SearchableConceptTreePanel.class;

    /**
     * This is the key to the dispatcher that holds the Concept that is
     * currently selected in the SearchableConceptTreePanel
     * <pre>
     * Concept c = (Concept) Dispatcher.getDispatcher(DISPATCHER_KEY_SELECTED_CONCEPT).getValueObject();
     * </pre>
     */
    public static final Object KEY_DISPATCHER_SELECTED_CONCEPT = Concept.class;
    private static Logger log;
    /**
	 * @uml.property  name="knowledgebaseFrame"
	 * @uml.associationEnd  
	 */
    private KnowledgebaseFrame knowledgebaseFrame;

    /**
     * Constructs ...
     *
     */
    public KnowledgebaseApp() {
        super();
        initialize();

        /*
         * We put this application into a dispatcher so that other components
         * like dialogs, can grab it.
         */
        DISPATCHER.setValueObject(this);

        /*
         * Ensure that the concept is registered with all listeners when a user logs in.
         */
        DISPATCHER_USERACCOUNT.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                Concept selectedConcept = (Concept) DISPATCHER_SELECTED_CONCEPT.getValueObject();
                DISPATCHER_SELECTED_CONCEPT.setValueObject(null);
                DISPATCHER_SELECTED_CONCEPT.setValueObject(selectedConcept);

            }
        });
    }

    /**
     * <p>UI initialization</p>
     *
     */
    private void initialize() {
        ImageIcon mbariLogo =
            new ImageIcon(getClass().getResource("/images/vars/knowledgebase/knowledgebase-splash.png"));
        SplashFrame splashFrame = new SplashFrame(mbariLogo);

        // TODO 20050415 brian: Need to load a configuration
        splashFrame.setMessage(" Loading configuration...");
        splashFrame.setVisible(true);
        splashFrame.repaint();

        /*
         * Load knowledgebase
         */
        splashFrame.setMessage(" Loading knowledgebase...");
        splashFrame.repaint();

        /*
         * Load the KB in a seperate thread so the UI remains responsive.
         */
        try {
            Worker.post(new Task() {

                public Object run() throws Exception {

                    KnowledgeBaseCache.getInstance().findAllConceptNames();

                    // KnowledgeBaseCache.getInstance().findRootConcept().getHierarchicalLinkTemplates();
                    return null;
                }
            });
        }
        catch (Exception e) {
            splashFrame.setMessage(" Error: Failed to load the knowledgebase");
            getLog().error("Failed to load the knowledgebase", e);
            splashFrame.repaint();
            JOptionPane.showMessageDialog(splashFrame, "Unable to load knowledgebase", "VARS - Error",
                                          JOptionPane.ERROR_MESSAGE, null);
            System.exit(-1);
        }

        /*
         * Initialize GUI
         *
         * TODO 20050415 brian: Need to use the loaded configuration to
         * initiaize the GUI settings.
         */
        splashFrame.setMessage(" Initializing the GUI...");
        splashFrame.repaint();
        getKnowledgebaseFrame().pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getKnowledgebaseFrame().getSize();
        double newHeight = (screenSize.getHeight() - 150);
        frameSize.setSize(frameSize.getWidth(), (int) newHeight);
        

        /*
         * Make sure that the knowledgebase exists. If it's empty then give the
         * user the oppurtunity to create a root object
         */
        try {
            (new PopulateDatabaseAction()).doAction();
        }
        catch (Exception e) {
            splashFrame.setMessage(" Error: Failed to load the knowledgebase");
            getLog().error("Failed to load the knowledgebase", e);
            splashFrame.repaint();
            JOptionPane.showMessageDialog(splashFrame, "Unable to load knowledgebase", "VARS - Error",
                                          JOptionPane.ERROR_MESSAGE, null);
            System.exit(-1);
        }

        getKnowledgebaseFrame().setSize(frameSize);
        getKnowledgebaseFrame().setIconImage(mbariLogo.getImage());
        try {
            DISPATCHER_SELECTED_CONCEPT.setValueObject(KnowledgeBaseCache.getInstance().findRootConcept());
        } 
        catch (DAOException e) {
            log.error("Unable to locate root concept --- this means trouble!!");
        }
        getKnowledgebaseFrame().setVisible(true);
        splashFrame.dispose();
        
        /*
         * Add a special eventQueue that toggles the cursor if the application is busy
         */
        Toolkit.getDefaultToolkit().getSystemEventQueue().push(new WaitCursorEventQueue(500));


        
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param args
     */
    public static void main(String[] args) {

        /*
         * Castor.properties has a setting that tells castor to save Dates
         * as GMT. IN order to minimize confusion in displays we set
         * the timezone of the application to GMT too. This way database
         * values and displayed values are always the same
         */
        System.setProperty("user.timezone", "UTC");

        /*
         * Create an application settings directory if needed and create the log directory
         */
        String home = System.getProperty("user.home");

        File settingsDirectory = new File(home, ".vars");
        if (!settingsDirectory.exists()) {
            settingsDirectory.mkdir();
        }
        File logDirectory = new File(settingsDirectory, "logs");
        if (!logDirectory.exists()) {
            logDirectory.mkdir();
        }

        final Logger log = getLog();
        if (log.isInfoEnabled()) {
            final Date date = new Date();
            log.info("This application was launched at " + date.toString());
        }

        /*
         * Make it pretty on Macs
         */
        if (SystemUtilities.isMacOS()) {
            SystemUtilities.configureMacOSApplication("VARS Knowledgebase");
        }


        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            log.info("Unable to set look and feel", e);
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new KnowledgebaseApp();
            }
        });
    }

    /**
	 * <p><!-- Method description --></p>
	 * @return
	 * @uml.property  name="knowledgebaseFrame"
	 */
    public KnowledgebaseFrame getKnowledgebaseFrame() {
        if (knowledgebaseFrame == null) {
            knowledgebaseFrame = new KnowledgebaseFrame();

            /*
             * We store the frame here so that other components can easily
             * access it.
             */
            AppFrameDispatcher.setFrame(knowledgebaseFrame);
        }

        return knowledgebaseFrame;
    }

    /**
     * Do NOT initialize a log until the 'user.timezone' property has been
     * set or you will not be able to stroe dates in the UTC timezone! This
     */
    private static Logger getLog() {
        if (log == null) {
            log = LoggerFactory.getLogger(KnowledgebaseApp.class);
        }

        return log;
    }

}
