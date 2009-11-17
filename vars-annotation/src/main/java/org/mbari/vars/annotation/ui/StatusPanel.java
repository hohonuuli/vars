/*
 * @(#)StatusPanel.java   2009.11.17 at 09:37:01 PST
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

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.ui.Lookup;

/**
 */
public class StatusPanel extends JPanel {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private javax.swing.JLabel personLabel = null;
    private javax.swing.JLabel varsLabel = null;
    private javax.swing.JLabel vcrLabel = null;
    private javax.swing.JLabel videoArchiveLabel = null;

    /**
     * This is the default constructor
     */
    public StatusPanel() {
        super();
        initialize();
    }

    private javax.swing.JLabel getPersonLabel() {
        if (personLabel == null) {
            personLabel = new StatusLabelForPerson();
        }

        return personLabel;
    }

    private javax.swing.JLabel getVarsLabel() {
        if (varsLabel == null) {
            varsLabel = new StatusLabelForVarsDb();
        }

        return varsLabel;
    }

    private javax.swing.JLabel getVcrLabel() {
        if (vcrLabel == null) {
            vcrLabel = new StatusLabelForVcr();
        }

        return vcrLabel;
    }

    private javax.swing.JLabel getVideoArchiveLabel() {
        if (videoArchiveLabel == null) {
            videoArchiveLabel = new StatusLabelForVideoArchive();
        }

        return videoArchiveLabel;
    }

    private void initialize() {
        this.add(getVarsLabel(), null);
        this.add(getPersonLabel(), null);

        try {
            this.add(getVcrLabel(), null);
        }
        catch (Error e) {
            log.warn("RXTX, the software libraries used " +
                     "to support serial port I/O, are missing or are not properly installed." +
                     " VARS will not be able to communicate with your VCR.", e);
            EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
        }

        this.add(getVideoArchiveLabel(), null);
        this.setSize(400, 26);
        this.setMinimumSize(new Dimension(400, 26));
        this.setAlignmentX(Component.RIGHT_ALIGNMENT);
    }
}
