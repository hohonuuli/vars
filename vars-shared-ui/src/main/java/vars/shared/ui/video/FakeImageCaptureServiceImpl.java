/*
 * @(#)FakeImageGrabber.java   2010.04.30 at 01:48:38 PDT
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

import java.awt.Frame;
import java.awt.Image;
import javax.swing.JOptionPane;
import vars.shared.ui.GlobalLookup;

/**
 * Empty implementation of ImageGrabber, calls to grab() return null.
 * 
 * @author brian
 */
public class FakeImageCaptureServiceImpl implements ImageCaptureService {

    /**
     */
    public void dispose() {
        // Nothing to do
    }

    /**
     * @return
     *
     * @throws vars.shared.ui.video.ImageCaptureException
     */
    public Image capture(String timecode) throws ImageCaptureException {
        return null;
    }

    public void showSettingsDialog() {
        // Do nothing
        Frame frame = (Frame) GlobalLookup.getSelectedFrameDispatcher().getValueObject();
        JOptionPane.showMessageDialog(frame, "No settings are available", "VARS - Video Settings",
                JOptionPane.INFORMATION_MESSAGE);
    }


}
