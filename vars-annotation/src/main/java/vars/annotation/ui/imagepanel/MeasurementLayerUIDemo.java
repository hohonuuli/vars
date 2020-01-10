/*
 * @(#)MeasurementLayerUIDemo.java   2012.11.26 at 08:48:27 PST
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

import com.google.inject.Injector;
import org.jdesktop.jxlayer.JXLayer;
import org.mbari.swing.JImageUrlCanvas;
import vars.annotation.ui.StateLookup;
import vars.annotation.ui.ToolBelt;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.net.URL;

/**
 * @author Brian Schlining
 * @since 2011-08-30
 */
public class MeasurementLayerUIDemo {

    /**
     *
     * @param args
     */
    public static void main(String[] args) {

        final Injector injector = StateLookup.GUICE_INJECTOR;
        ToolBelt toolBelt = injector.getInstance(ToolBelt.class);

        // Layout components
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Load image
        URL url = frame.getClass().getResource("/images/BrianSchlining.jpg");
        final JImageUrlCanvas label = new JImageUrlCanvas(url);

        // Create JXLayer
        MeasurementLayerUI<JImageUrlCanvas> layerUI = new MeasurementLayerUI<JImageUrlCanvas>(toolBelt,
                new CommonPainters<JImageUrlCanvas>(new JXHorizontalLinePainter<JImageUrlCanvas>(label),
                new JXCrossHairPainter<JImageUrlCanvas>()));
        JXLayer<JImageUrlCanvas> layer = new JXLayer<JImageUrlCanvas>(label);
        layer.setUI(layerUI);

        frame.add(layer, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
}
