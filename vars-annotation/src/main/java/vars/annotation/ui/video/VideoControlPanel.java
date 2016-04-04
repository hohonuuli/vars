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



package vars.annotation.ui.video;

import org.mbari.vcr4j.ui.swing.VCRPanel;
import vars.annotation.ui.StateLookup;

/**
 * <p>A VCR panel that monitors for changes of VCRs</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class VideoControlPanel extends VCRPanel {

    /**
     * Constructor
     */
    public VideoControlPanel() {
        super();

        videoControllerProperty().set(StateLookup.getVideoController());


    }

}
