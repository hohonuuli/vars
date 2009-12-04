/*
 * @(#)WarningSubscriber.java   2009.12.03 at 08:58:37 PST
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



package vars.shared.ui.event;

import java.awt.Frame;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.bushe.swing.event.EventTopicSubscriber;

/**
 *
 *
 * @version        Enter version here..., 2009.12.03 at 08:58:37 PST
 * @author         Brian Schlining [brian@mbari.org]    
 */
public class WarningSubscriber implements EventTopicSubscriber<String> {

    private final Frame parentComponent;
    private final Icon icon;

    /**
     * Constructs ...
     *
     * @param parentComponent
     */
    public WarningSubscriber(Frame parentComponent) {
        super();
        this.parentComponent = parentComponent;
        this.icon = new ImageIcon(getClass().getResource("/vars/images/128/gear_warning.png"));
    }

    /**
     *
     * @param topic
     * @param data
     */
    public void onEvent(String topic, String data) {

    	Object[] options = { "OK"};
    	JOptionPane.showOptionDialog(parentComponent, data, "VARS - Warning", JOptionPane.DEFAULT_OPTION, 
    			JOptionPane.WARNING_MESSAGE, icon, options, options[0]);

    }
}
