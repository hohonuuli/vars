/*
 * @(#)FakeImageCaptureServiceImpl.java   2013.02.15 at 10:56:20 PST
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



package vars.avplayer;

import java.awt.Frame;
import java.awt.Image;
import java.io.File;
import javax.swing.JOptionPane;
import vars.shared.ui.GlobalLookup;

/**
 * Empty implementation of ImageGrabber, calls to grab() return null.
 *
 * @author brian
 */
public class FakeImageCaptureServiceImpl implements ImageCaptureService {

    /**
     *
     * @param timecode
     * @return
     *
     *
     * @throws ImageCaptureException
     */
    public Image capture(String timecode) throws ImageCaptureException {
        return null;
    }

    /**
     *
     * @param file
     * @return
     *
     * @throws ImageCaptureException
     */
    @Override
    public Image capture(File file) throws ImageCaptureException {
        return null;
    }

    /**
     */
    public void dispose() {

        // Nothing to do
    }

    /**
     * @return
     */
    @Override
    public boolean isPngAutosaved() {
        return false;
    }

    /**
     */
    public void showSettingsDialog() {

        // Do nothing
        Frame frame = (Frame) GlobalLookup.getSelectedFrameDispatcher().getValueObject();
        JOptionPane.showMessageDialog(frame, "No settings are available", "VARS - Video Settings",
                                      JOptionPane.INFORMATION_MESSAGE);
    }
}
