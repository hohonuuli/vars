package vars.avplayer;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.typesafe.config.Config;
import org.mbari.util.SystemUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.shared.config.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Brian Schlining
 * @since 2015-10-01T14:22:00
 */
public class InjectorModule implements Module {

//    private static Resource resource;
//
//    public InjectorModule(Config config) {
//        resource = new Resource(config);
//    }

    /**
     *
     * @param binder
     */
    public void configure(Binder binder) {
        // TODO we can use SPI instead
        //binder.bind(ImageCaptureService.class).toProvider(ImageCaptureServiceProvider.class).in(Scopes.SINGLETON);
    }



    /**
     * This is where we configure the different image capture services for different
     * platforms. Currently the default is to try to use QuickTime for Java to do
     * the capture but we may change this in the future.
     *
     * MUST BE STATIC for Guice
     */
//    private static class ImageCaptureServiceProvider implements Provider<ImageCaptureService> {
//
//        private final Logger log = LoggerFactory.getLogger(getClass());
//
//        /**
//         * @return
//         */
//        public ImageCaptureService get() {
//
//
//            // --- Extract service classes from properties file
//            String platform = "";
//            if (SystemUtilities.isMacOS()) {
//                platform = "mac";
//            }
//            else if (SystemUtilities.isWindowsOS()) {
//                platform = "windows";
//            }
//
//            String rsrc = "image.capture.services." + platform;
//            List<String> imageCaptureServices = new ArrayList<String>();
//            try {
//                // Get service resource
//                String ics = bundle.getString(rsrc);
//                String[] parts = ics.split(" ");
//                imageCaptureServices.addAll(Arrays.asList(parts));
//
//            }
//            catch (Exception e) {
//                // Do nothing
//            }
//
//            // --- Instantiate the first working class
//            Class<? extends ImageCaptureService> grabberClazz = FakeImageCaptureServiceImpl.class;
//            for (int i = 0; i < imageCaptureServices.size(); i++) {
//                String clazzName = imageCaptureServices.get(i);
//                try {
//                    log.info("Attempting to initialize " + clazzName);
//                    grabberClazz = (Class<ImageCaptureService>) Class.forName(clazzName);
//                    break;
//                }
//                catch (Exception e) {
//                    log.info("Failed to initialize " + clazzName);
//                }
//            }
//
//            ImageCaptureService imageCaptureService = null;
//            try {
//                imageCaptureService = grabberClazz.newInstance();
//            }
//            catch (Throwable e) {
//                imageCaptureService = new FakeImageCaptureServiceImpl();
//                log.warn("Failed to frame capture using an instance of " + grabberClazz, e);
//            }
//
//            return imageCaptureService;
//
//        }
//    }
}
