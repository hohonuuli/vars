/*
 * @(#)ImageFrameLayerUI.java   2012.11.26 at 08:48:33 PST
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

import javax.swing.JComponent;


/**
 * Base class for LayerUI's that sit on top of the ImageAnnotationFrame
 *
 * @author Brian Schlining
 * @since 2012-08-13
 *
 * @param <T>
 */
public abstract class ImageFrameLayerUI<T extends JComponent> extends MultiLayerUI<T> {

    private String displayName;
    private UISettingsBuilder settingsBuilder;
    private final CommonPainters<T> commonPainters;

    public ImageFrameLayerUI(CommonPainters<T> commonPainters) {
        this.commonPainters = commonPainters;
        for (JXPainter<T> p : commonPainters.getPainters()) {
            addPainter(p);
        }
    }

    /**
     */
    @Override
    public void clearPainters() {
        super.clearPainters();
        settingsBuilder.clearPainters();
        for (JXPainter<T> p : commonPainters.getPainters()) {
            addPainter(p);
        }
    }

    /**
     * @return
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return
     */
    public UISettingsBuilder getSettingsBuilder() {
        return settingsBuilder;
    }


    /**
     * Subclasses can override if they need to reset the state of the UI.
     */
    public void resetUI() {

        // Empty implementation
    }

    /**
     *
     * @param displayName
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     *
     * @param settingsBuilder
     */
    public void setSettingsBuilder(UISettingsBuilder settingsBuilder) {
        this.settingsBuilder = settingsBuilder;
    }
}
