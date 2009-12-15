/*
 * @(#)AssociationEditorPanelLiteController.java   2009.12.15 at 02:17:42 PST
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



package vars.annotation.ui.roweditor;

import vars.ILink;
import vars.annotation.Association;
import vars.annotation.Observation;
import vars.annotation.ui.ToolBelt;

/**
 *
 *
 * @version        Enter version here..., 2009.12.15 at 02:17:42 PST
 * @author         Brian Schlining [brian@mbari.org]
 */
public class AssociationEditorPanelLiteController {

    private Association association;
    private Observation observation;
    private final AssociationEditorPanelLite panel;
    private final ToolBelt toolBelt;

    /**
     * Constructs ...
     *
     * @param toolBelt
     * @param panel
     */
    public AssociationEditorPanelLiteController(ToolBelt toolBelt, AssociationEditorPanelLite panel) {
        super();
        this.toolBelt = toolBelt;
        this.panel = panel;
    }

    /**
     *
     * @param observation
     * @param association
     */
    public void setTarget(Observation observation, Association association) {
        this.observation = observation;
        this.association = association;
    }

    public Observation getObservation() {
        return observation;
    }

    public Association getAssociation() {
        return association;
    }

    public ToolBelt getToolBelt() {
        return toolBelt;
    }

    public void doCancel() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void doOkay(ILink link) {
        throw new UnsupportedOperationException("Not Implemented yet");
    }
}
