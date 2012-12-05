/*
 * @(#)IAFRepaintEvent.java   2012.11.26 at 08:48:35 PST
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

import vars.annotation.ui.eventbus.UIChangeEvent;

/**
 * ImageAnnotationFrame Repaint Event. This event is issued via EventBus
 *
 * @author Brian Schlining
 * @since 2012-08-07
 */
public class IAFRepaintEvent extends UIChangeEvent<UIDataCoordinator> {

    /**
     * Constructs ...
     *
     * @param changeSource
     * @param refs
     */
    public IAFRepaintEvent(Object changeSource, UIDataCoordinator refs) {
        super(changeSource, refs);
    }
}
