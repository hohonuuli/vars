/*
 * @(#)UISettingsBuilder.java   2012.11.26 at 08:48:25 PST
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

import javax.swing.JPanel;

/**
 * Builder used to fetch a custom settings JPanel for an instance of MultiLayerUI components such as
 * AnnotationLayerUI. The JPanel is then used by the ImageAnnotationFrame. The JPanel contains
 * controls that allow users to tweak settings for a particular LayerUI tool.
 *
 * @author Brian Schlining
 *
 * @since 2012-08-13
 */
public interface UISettingsBuilder {

    /**
     * Removes all JXPainters from a layer and resets the LayerUI to a default state
     */
    public void clearPainters();

    /**
     * @return The JPanel used for adjusting settings for a particular LayerUI tool
     */
    public JPanel getPanel();
}
