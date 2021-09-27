/*
 * @(#)PointAdditionLayerDemo.java   2012.11.26 at 08:48:26 PST
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

import org.jdesktop.jxlayer.JXLayer;
import mbarix4j.swing.JImageUrlCanvas;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.net.URL;

/**
 *
 * @author brian
 */
public class PointAdditionLayerDemo {

    /**
     *
     * @param args
     */
    public static void main(String[] args) {

        // Layout components
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Load image
        URL url = frame.getClass().getResource("/images/BrianSchlining.jpg");
        final JImageUrlCanvas label = new JImageUrlCanvas(url);

        // Create JXLayer
        PointAdditionLayerUI<JImageUrlCanvas> layerUI = new PointAdditionLayerUI<JImageUrlCanvas>();
        JXLayer<JImageUrlCanvas> layer = new JXLayer<JImageUrlCanvas>(label);
        layer.setUI(layerUI);

//        frame.addComponentListener(new ComponentAdapter() {
//
//            public void componentResized(ComponentEvent e) {
//                label.setSize(frame.getWidth(), frame.getHeight());
//                frame.repaint();
//            }
//
//        });

        frame.add(layer, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
}
