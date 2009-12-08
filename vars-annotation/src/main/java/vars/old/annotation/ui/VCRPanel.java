/*
 * @(#)VCRPanel.java   2009.11.17 at 10:20:54 PST
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



package vars.old.annotation.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.mbari.util.Dispatcher;
import org.mbari.vcr.IVCR;

import vars.old.annotation.ui.VideoService;
import vars.annotation.ui.Lookup;

/**
 * <p>A VCR panel that monitors for changes of VCRs</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class VCRPanel extends org.mbari.vcr.ui.VCRPanel implements PropertyChangeListener {

    /**
     * Constructor
     */
    public VCRPanel() {
        super();
        final Dispatcher dispatcher = Lookup.getVideoServiceDispatcher();
        dispatcher.addPropertyChangeListener(this);
        final VideoService videoService = (VideoService) dispatcher.getValueObject();
        final IVCR vcr = (videoService == null) ? null : videoService.getVCR();
        setVcr(vcr);
    }

    /**
     *
     * @param evt
     */
    public void propertyChange(PropertyChangeEvent evt) {
        final VideoService videoService = (VideoService) evt.getNewValue();
        final IVCR vcr = (videoService == null) ? null : videoService.getVCR();
        setVcr(vcr);
    }
}
