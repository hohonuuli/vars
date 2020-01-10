package vars.avplayer.noop;

import vars.avplayer.ImageCaptureException;
import vars.avplayer.ImageCaptureService;
import vars.shared.ui.GlobalStateLookup;


import javax.swing.JOptionPane;
import java.awt.Image;
import java.awt.Frame;
import java.io.File;
import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2016-03-24T13:53:00
 */
public class NoopImageCaptureService implements ImageCaptureService {


    /**
     *
     * @throws ImageCaptureException
     */
    @Override
    public Optional<Image> capture(File file) throws ImageCaptureException {
        return null;
    }

    /**
     */
    public void dispose() {

        // Nothing to do
    }


    /**
     */
    public void showSettingsDialog() {

        // Do nothing
        Frame frame = GlobalStateLookup.getSelectedFrame();
        JOptionPane.showMessageDialog(frame, "No settings are available", "VARS - Video Settings",
                JOptionPane.INFORMATION_MESSAGE);
    }
}