/*
 * @(#)StatusLabel.java   2009.11.16 at 04:21:30 PST
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

import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>A widget that live at the bottom of the annotation app. Indicates that
 * status of a particular connection or setting. Green = good, red = bad</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public abstract class StatusLabel extends JLabel implements PropertyChangeListener {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    private ImageIcon offIcon;
    private boolean ok;
    private ImageIcon onIcon;

    /**
     * Constructor for the StatusLabel object
     */
    public StatusLabel() {
        offIcon = getImageIcon("/images/vars/annotation/error.png");
        onIcon = getImageIcon("/images/vars/annotation/check.png");
        setOk(false);
        setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
    }

    private ImageIcon getImageIcon(final String relativePath) {
        return new ImageIcon(getClass().getResource(relativePath));
    }

    /**
     * @return returns the state of the Label.
     */
    public boolean isOk() {
        return ok;
    }

    /**
     *
     * @param b
     */
    public void setOk(final boolean b) {
        ok = b;

        if (ok) {
            setIcon(onIcon);
        }
        else {
            setIcon(offIcon);
        }
    }
}
