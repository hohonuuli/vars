/*
 * @(#)LinkListCellRenderer.java   2009.12.02 at 01:55:30 PST
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

package vars.shared.ui;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import vars.ILink;
import vars.LinkUtilities;

/**
 *
 *
 * @version        Enter version here..., 2009.12.02 at 01:55:30 PST
 * @author         Brian Schlining [brian@mbari.org]    
 */
public class LinkListCellRenderer extends DefaultListCellRenderer {

    /**
     *
     * @param list
     * @param value
     * @param index
     * @param isSelected
     * @param cellHasFocus
     * @return
     */
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {

        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        setText(LinkUtilities.formatAsString((ILink) value));

        return this;
    }
}
