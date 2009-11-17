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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import org.mbari.swing.JFancyButton;
import org.mbari.swing.SwingUtils;
import org.mbari.util.Dispatcher;
import org.mbari.vars.annotation.ui.actions.NewVideoFrameAction;

import vars.annotation.ui.Lookup;


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

        final Dispatcher videoArchiveDispatcher = Lookup.getVideoArchiveDispatcher();
        hasVideoArchive = videoArchiveDispatcher.getValueObject()!= null;
        videoArchiveDispatcher.addPropertyChangeListener(new PropertyChangeListener() {
            
            public void propertyChange(PropertyChangeEvent evt) {
                hasVideoArchive = (evt.getNewValue() != null);
                checkEnable();
            }
        });
        
        final Dispatcher userAccountDispatcher = Lookup.getUserAccountDispatcher();
        hasPerson = userAccountDispatcher.getValueObject() != null;
        userAccountDispatcher.addPropertyChangeListener(new PropertyChangeListener() {
            
            public void propertyChange(PropertyChangeEvent evt) {
                hasPerson = (evt.getNewValue() != null);
                checkEnable();
            }
        });

        final Dispatcher videoServiceDispatcher = Lookup.getVideoServiceDispatcher();
        hasVcr = videoArchiveDispatcher.getValueObject() != null;
        videoArchiveDispatcher.addPropertyChangeListener(new PropertyChangeListener() {
            
            public void propertyChange(PropertyChangeEvent evt) {
                hasVcr = (evt.getNewValue() != null);
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
