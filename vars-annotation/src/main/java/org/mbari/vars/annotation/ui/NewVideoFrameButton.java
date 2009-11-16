/*
 * @(#)NewVideoFrameButton.java   2009.11.15 at 08:33:19 PST
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



package org.mbari.vars.annotation.ui;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import org.mbari.swing.JFancyButton;
import org.mbari.swing.SwingUtils;
import org.mbari.util.IObserver;
import org.mbari.vars.annotation.ui.actions.NewVideoFrameAction;
import org.mbari.vars.annotation.ui.dispatchers.PersonDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.VcrDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.VideoArchiveDispatcher;

/**
 * <p>A button that calls the <code>NewVideoFrameAction</code> </p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class NewVideoFrameButton extends JFancyButton {

    private final Action action = new NewVideoFrameAction();
    private boolean hasPerson;
    private boolean hasVcr;
    private boolean hasVideoArchive;

    /**
     * Constructor for the NewVideoFrameButton object
     */
    public NewVideoFrameButton() {
        super();
        setAction(action);
        setToolTipText("Create an Observation with a new timecode [" +
                       SwingUtils.getKeyString((KeyStroke) action.getValue(Action.ACCELERATOR_KEY)) + "]");
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/obs_new.png")));
        action.setEnabled(false);

        hasVideoArchive = VideoArchiveDispatcher.getInstance().getVideoArchive() != null;
        VideoArchiveDispatcher.getInstance().addObserver(new IObserver() {

            public void update(final Object obj, final Object changeCode) {
                hasVideoArchive = (obj != null);
                checkEnable();
            }
        });

        hasPerson = PersonDispatcher.DISPATCHER.getValueObject() != null;
        PersonDispatcher.getInstance().addObserver(new IObserver() {

            public void update(final Object obj, final Object changeCode) {
                hasPerson = (obj != null);
                checkEnable();
            }
        });

        hasVcr = VcrDispatcher.DISPATCHER.getValueObject() != null;
        VcrDispatcher.getInstance().addObserver(new IObserver() {

            public void update(final Object obj, final Object changeCode) {
                hasVcr = (obj != null);
                checkEnable();
            }
        });
        setText("");

    }

    /**
     * <p>Enable this button if someone is logged in AND a videoarchvie set is
     * open and the VCR exists.</p>
     */
    private void checkEnable() {
        action.setEnabled(hasVcr && hasPerson && hasVideoArchive);
    }
}
