/*
 * @(#)MarkerStyle.java   2012.11.26 at 08:48:30 PST
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

/**
 * Enumeration of default MarkerStyles
 * @author Brian Schlining
 * @since 2011-08-30
 */
public enum MarkerStyle {

    FAINT(new Color(126, 126, 126, 180), new Font("Sans Serif", Font.PLAIN, 10), 6,
            new BasicStroke(2)),
    NOTSELECTED(new Color(255, 0, 0, 180), new Font("Sans Serif", Font.PLAIN, 10), 7,
            new BasicStroke(3)),
    NOTSELECTED_FAINT(new Color(126, 0, 0, 180), new Font("Sans Serif", Font.PLAIN, 10), 6,
            new BasicStroke(3)),
    SELECTED(new Color(0, 255, 0, 180), new Font("Sans Serif", Font.PLAIN, 14), 14,
            new BasicStroke(3)),
    SELECTED_FAINT(new Color(0, 126, 0, 180), new Font("Sans Serif", Font.PLAIN, 10), 7,
            new BasicStroke(3));

    final int armLength;
    final Color color;
    final Font font;
    final Stroke stroke;

    private MarkerStyle(Color color, Font font, int armLength, Stroke stroke) {
        this.color = color;
        this.font = font;
        this.armLength = armLength;
        this.stroke = stroke;
    }
}
