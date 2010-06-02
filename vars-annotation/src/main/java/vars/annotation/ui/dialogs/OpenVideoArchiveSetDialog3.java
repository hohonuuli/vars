/*
 * @(#)OpenVideoArchiveSetDialog3.java   2010.01.19 at 02:04:29 PST
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


package vars.annotation.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.dialogs.VideoSourceSelectionPanel;
import vars.shared.ui.dialogs.StandardDialog;

/**
 * Dialog for opening a VideoArchive.
 * @author brian
 */
public class OpenVideoArchiveSetDialog3 extends StandardDialog {

    private final AnnotationDAOFactory annotationDAOFactory;
    private VideoSourceSelectionPanel selectionPanel;

    /**
     * Creates a new instance of OpenVideoArchiveSetDialog3
     *
     * @param annotationDAOFactory
     */
    public OpenVideoArchiveSetDialog3(AnnotationDAOFactory annotationDAOFactory) {
        super((Frame) Lookup.getApplicationFrameDispatcher().getValueObject());
        this.annotationDAOFactory = annotationDAOFactory;
        initialize();
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    okButtonAction();
                }
            }

        });
    }

    private VideoSourceSelectionPanel getSelectionPanel() {
        if (selectionPanel == null) {
            selectionPanel = new VideoSourceSelectionPanel(annotationDAOFactory);

            // Resize the frame when the videosource changes.
            selectionPanel.addContainerListener(new ContainerListener() {

                public void componentAdded(ContainerEvent e) {
                    pack();
                }
                public void componentRemoved(ContainerEvent e) {

                    // Do nothing
                }

            });
        }

        return selectionPanel;
    }

    void initialize() {
        getContentPane().add(getSelectionPanel(), BorderLayout.CENTER);
        getOkayButton().addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                okButtonAction();
                dispose();
            }

        });
        getCancelButton().addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                dispose();

            }
        });
        pack();
    }

    private void okButtonAction() {
        getSelectionPanel().open();
    }
}
