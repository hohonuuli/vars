/*
 * @(#)ActionPanel.java   2009.12.17 at 10:14:36 PST
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



package vars.annotation.ui;

import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.google.inject.Injector;
import mbarix4j.awt.layout.WrappingFlowLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.ui.buttons.*;

/**
 * <p> This panel contains buttons for actions that can modify the contents of the
 *  <code>ObservatonTable</code></p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @created  February 17, 2004
 */
public class ActionPanel extends JPanel {

    private final Logger log = LoggerFactory.getLogger(getClass());
    FlowLayout flowLayout = new WrappingFlowLayout();
    JButton btnDeepCopy;
    JButton btnDelete;
    JButton btnFramegrab;
    JButton btnRemoveFramegrab;
    JButton btnNew;
    JButton btnShallowCopy;
    private final ToolBelt toolBelt;

    /**
     *  Constructor for the ActionPanel object
     *
     * @param toolBelt
     */
    public ActionPanel(ToolBelt toolBelt) {
        super();
        this.toolBelt = toolBelt;
        /*
         * HACK! Make toolbelt available to PropButtons. PropButtons need no arg
         * constructors to be intialized BUT they all need access to the
         * toolbelt.
         *
         * This must be set BEFORE intializing any PropButtons
         */
        PropButton.setToolBelt(toolBelt);
        btnDeepCopy = new DeepCopyObservationsButton(toolBelt);
        btnDelete = new DeleteSelectedObservationsButton(toolBelt);
        btnNew = new NewObservationButton(toolBelt);
        btnShallowCopy = new DuplicateObservationButton(toolBelt);
        btnFramegrab = new FrameCaptureButton(toolBelt);
        btnRemoveFramegrab = new DeleteImageReferenceButton(toolBelt);
        initialize();
        registerHotKeys();
    }

    void initialize() {
        setLayout(flowLayout);
        flowLayout.setAlignment(FlowLayout.LEFT);
        add(btnNew, null);
        add(btnShallowCopy, null);
        add(btnDeepCopy, null);
        add(btnFramegrab, null);
        add(btnRemoveFramegrab, null);

        /*
         * The majority of buttons are read from the annotation-app property
         * file and instantiated at runtime. This allows location specific
         * customization with requiring the agency to rebuild VARS from scratch.
         */
        ResourceBundle bundle = ResourceBundle.getBundle(StateLookup.RESOURCE_BUNDLE, Locale.US);
        Enumeration<String> enumeration = bundle.getKeys();
        Map<String, JButton> map = new TreeMap<String, JButton>();
        Injector injector = StateLookup.GUICE_INJECTOR;
        while (enumeration.hasMoreElements()) {
            String element = enumeration.nextElement();
            if (element.startsWith("actionpanel.button")) {
                try {
                    Class clazz = Class.forName(bundle.getString(element));
                    JButton button = (JButton) injector.getInstance(clazz);
                    map.put(element, button);
                    //map.put(element, (JButton) Class.forName(bundle.getString(element)).newInstance());
                }
                catch (Exception ex) {
                    log.warn("Unable to add button class specified by '" + element + "' in vars-annotation.properties",
                             ex);
                }
            }
        }

        for (String key : map.keySet()) {
            add(map.get(key));
        }

        add(btnDelete, null);
    }

    /**
     *  Register
     */
    public void registerHotKeys() {
        final Action[] as = { btnFramegrab.getAction() , btnDelete.getAction(), btnNew.getAction(), btnDeepCopy.getAction(),
                              btnShallowCopy.getAction()};
        ActionMap actionMap = getActionMap();
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        for (Action a: as) {
            actionMap.put(a.getValue(Action.ACTION_COMMAND_KEY), a);
            inputMap.put((KeyStroke) a.getValue(Action.ACCELERATOR_KEY), a.getValue(Action.ACTION_COMMAND_KEY));
        }

        // Map delete action to delete key
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        Action a = btnDelete.getAction();
        inputMap.put(stroke, a.getValue(Action.ACTION_COMMAND_KEY));

        // Map delete action to [meta] + backspace
        stroke = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        inputMap.put(stroke, a.getValue(Action.ACTION_COMMAND_KEY));

    }
}
