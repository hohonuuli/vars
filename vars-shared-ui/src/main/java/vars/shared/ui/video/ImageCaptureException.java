/*
 * @(#)ImageGrabberException.java   2010.04.30 at 01:44:35 PDT
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

package vars.shared.ui.video;

import vars.VARSException;

/**
 *
 * @author brian
 */
public class ImageCaptureException extends VARSException {

    /**
     * Constructs ...
     */
    public ImageCaptureException() {}

    /**
     * Constructs ...
     *
     * @param s
     */
    public ImageCaptureException(String s) {
        super(s);
    }

    /**
     * Constructs ...
     *
     * @param throwable
     */
    public ImageCaptureException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructs ...
     *
     * @param s
     * @param throwable
     */
    public ImageCaptureException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
