/*
 * @(#)SamplePropButton.java   2009.11.20 at 06:06:19 PST
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

import javax.swing.ImageIcon;
import org.mbari.vars.annotation.ui.actions.AddSamplePropWithDialogAction;
import vars.annotation.ui.ToolBelt;

/**
 * <p>
 * Adds a 'sampled-by' association to the currently selected observations
 * </p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 */
public class SamplePropButton extends PropButton {


    /**
     *      Consructor
     *
     * @param toolBelt
     */
    public SamplePropButton(ToolBelt toolBelt) {
        super();
        setAction(new AddSamplePropWithDialogAction(toolBelt));
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/sbutton.png")));
        setToolTipText("sample");
        setEnabled(false);
    }
}
