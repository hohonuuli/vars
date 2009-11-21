/*
 * @(#)OpenVideoArchiveSetDialog3.java   2009.11.20 at 03:33:03 PST
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

package org.mbari.vars.annotation.locale;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import javax.swing.JButton;
import org.mbari.vars.annotation.ui.dialogs.OkayCancelDialog;
import org.mbari.vars.annotation.ui.dialogs.VideoSourceSelectionPanel;
import vars.annotation.ui.Lookup;

/**
 * Dialog for opening a VideoArchive.
 * @author brian
 */
public class OpenVideoArchiveSetDialog3 extends OkayCancelDialog {

    private VideoSourceSelectionPanel selectionPanel;

    /** Creates a new instance of OpenVideoArchiveSetDialog3 */
    public OpenVideoArchiveSetDialog3() {
        super((Frame) Lookup.getApplicationFrameDispatcher().getValueObject());
        initialize();
    }

    private VideoSourceSelectionPanel getSelectionPanel() {
        if (selectionPanel == null) {
            selectionPanel = new VideoSourceSelectionPanel();

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
        JButton okButton = getOkayButton();
        okButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                okButtonAction();
            }

        });
        pack();
    }

    private void okButtonAction() {
        getSelectionPanel().open();
    }
}
