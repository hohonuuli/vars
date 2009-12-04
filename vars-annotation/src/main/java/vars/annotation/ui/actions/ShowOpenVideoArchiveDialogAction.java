/*
 * @(#)ShowOpenVideoArchiveDialogAction.java   2009.11.21 at 09:05:49 PST
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
import org.mbari.vars.annotation.locale.OpenVideoArchiveSetDialog3;


import vars.annotation.AnnotationDAOFactory;

/**
 * <p>Action that opens a <code>OpenVideoArchiveSetUsingParamsDialog</code></p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 */
public class ShowOpenVideoArchiveDialogAction extends ActionAdapter {

    private final JDialog dialog;
    
    

    /**
     * Constructor
     *
     * @param annotationDAOFactory
     */
    public ShowOpenVideoArchiveDialogAction(AnnotationDAOFactory annotationDAOFactory) {
        super("Open Archive");
        dialog = new OpenVideoArchiveSetDialog3(annotationDAOFactory);

    }

    /**
     *  Initiates the action
     */
    public void doAction() {
        dialog.setVisible(true);
    }

    /**
     *  Gets the dialog.
     *
     * @return  An <code>OpenVideoArchiveSetUsingParamsDialog</code>
     */
    public JDialog getDialog() {
        return dialog;
    }
}
