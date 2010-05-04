/*
 * @(#)FancyButton.java   2010.05.04 at 10:55:15 PDT
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



package vars.shared.ui;

import java.awt.Color;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.border.Border;
import org.mbari.swing.JFancyButton;

/**
 *
 * @author brian
 */
public class FancyButton extends JFancyButton {

    /**
     * Constructs ...
     */
    public FancyButton() {
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param a
     */
    public FancyButton(Action a) {
        super(a);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param icon
     */
    public FancyButton(Icon icon) {
        super(icon);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param text
     */
    public FancyButton(String text) {
        super(text);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param text
     * @param icon
     */
    public FancyButton(String text, Icon icon) {
        super(text, icon);
        initialize();
    }

    private void initialize() {
        final Border border = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(Color.ORANGE,
            Color.GRAY), BorderFactory.createEmptyBorder(2, 2, 2, 2));
        setBorder(border);
    }
}
