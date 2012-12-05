/*
 * @(#)MeasurementCommentDialog.java   2012.11.26 at 08:48:29 PST
 *
 * Copyright 2011 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.annotation.ui.imagepanel;

import vars.annotation.ui.dialogs.AddCommentAssociationDialog;

import java.awt.Frame;

/**
 * @author Brian Schlining
 * @since 2011-09-13
 */
public class MeasurementCommentDialog extends AddCommentAssociationDialog {

    /**
     * Constructs ...
     *
     * @param parent
     */
    public MeasurementCommentDialog(Frame parent) {
        super(parent);
        setTitle("VARS - Enter a Comment About this Measurement");
    }
}
