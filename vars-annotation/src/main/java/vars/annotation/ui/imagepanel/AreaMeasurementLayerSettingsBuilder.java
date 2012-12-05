/*
 * @(#)AreaMeasurementLayerSettingsBuilder.java   2012.11.26 at 08:48:37 PST
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

import org.mbari.swing.JImageUrlCanvas;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Builds a JPanel for allowing user adjustment of the settings for an AreaMeasurementLayerUI
 *
 * @author Brian Schlining
 * @since 2012-08-13
 *
 * @param <T>
 */
public class AreaMeasurementLayerSettingsBuilder<T extends JImageUrlCanvas> implements UISettingsBuilder {

    private final MultiLayerUI<T> layerUI;
    private final JPanel panel;

    /**
     * Constructs ...
     *
     * @param layerUI
     */
    public AreaMeasurementLayerSettingsBuilder(MultiLayerUI<T> layerUI) {
        this.layerUI = layerUI;

        // --- Configure Panel
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(new JLabel("No Settings Available"));
    }

    /**
     */
    @Override
    public void clearPainters() {

        // Nothing to do
    }

    /**
     * @return
     */
    public JPanel getPanel() {
        return panel;
    }
}
