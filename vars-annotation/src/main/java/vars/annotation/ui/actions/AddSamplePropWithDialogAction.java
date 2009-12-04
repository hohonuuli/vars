/*
 * @(#)AddSamplePropWithDialogAction.java   2009.11.19 at 01:46:13 PST
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



package vars.annotation.ui.actions;

import javax.swing.JDialog;
import org.mbari.awt.event.ActionAdapter;
import vars.annotation.ui.dialogs.AddSamplePropDialog;
import vars.annotation.ui.ToolBelt;

/**
 * <p>Displays a dialog that prompts the user to input parameters needed to
 * set the samples properties.</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 */
public class AddSamplePropWithDialogAction extends ActionAdapter {

    private final JDialog dialog;

    /**
     *
     *
     * @param toolBelt
     */
    public AddSamplePropWithDialogAction(ToolBelt toolBelt) {
        dialog = new AddSamplePropDialog(toolBelt);
    }

    /**
     *
     */
    public void doAction() {
        dialog.setVisible(true);
    }
}
