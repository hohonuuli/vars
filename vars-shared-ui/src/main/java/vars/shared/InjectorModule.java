/*
 * @(#)InjectorModule.java   2010.05.03 at 01:40:46 PDT
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



package vars.shared;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import java.util.Locale;
import java.util.ResourceBundle;
import org.mbari.util.SystemUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.VARSException;
import vars.jpa.VarsJpaModule;
import vars.shared.ui.video.FakeImageCaptureServiceImpl;
import vars.shared.ui.video.ImageCaptureService;

/**
 *
 * @author brian
 */
public class InjectorModule implements Module {

    private final String annotationPersistenceUnit;
    private final String knowledgebasePersistenceUnit;
    private final String miscPersistenceUnit;

    /**
     * Constructs ...
     *
     * @param bundle
     */
    public InjectorModule(ResourceBundle bundle) {
        annotationPersistenceUnit = bundle.getString("annotation.persistence.unit");
        knowledgebasePersistenceUnit = bundle.getString("knowledgebase.persistence.unit");
        miscPersistenceUnit = bundle.getString("misc.persistence.unit");
    }

    /**
     * Constructs ...
     *
     * @param bundleName
     */
    public InjectorModule(String bundleName) {
        this(ResourceBundle.getBundle(bundleName, Locale.US));
    }

    /**
     *
     * @param binder
     */
    public void configure(Binder binder) {
        try {
            binder.install(new VarsJpaModule(annotationPersistenceUnit, knowledgebasePersistenceUnit,
                                             miscPersistenceUnit));
        }
        catch (Exception ex) {
            throw new VARSException("Failed to intialize dependency injection", ex);
        }

        binder.bind(ImageCaptureService.class).toProvider(ImageCaptureServiceProvider.class).in(Scopes.SINGLETON);
    }

    /**
     * This is where we configure the different image capture services for different
     * platforms. Currently the default is to try to use QuickTime for Java to do
     * the capture but we may change this in the future.
     */
    private static class ImageCaptureServiceProvider implements Provider<ImageCaptureService> {

        private final Logger log = LoggerFactory.getLogger(getClass());

        /**
         * @return
         */
        public ImageCaptureService get() {

            // ---- Initialize framecapture. Default is to use a FakeImageGrabber
            Class<? extends ImageCaptureService> grabberClazz = FakeImageCaptureServiceImpl.class;
            if (SystemUtilities.isMacOS()) {
                try {
                    grabberClazz = (Class<ImageCaptureService>) Class.forName("vars.quicktime.QTImageCaptureServiceImpl");
                }
                catch (ClassNotFoundException ex) {
                    log.info("QuickTime for Java could not be initialized", ex);
                }
            }
            else if (SystemUtilities.isWindowsOS()) {
                try {
                    grabberClazz = (Class<ImageCaptureService>) Class.forName("vars.quicktime.QTImageCaptureServiceImpl");
                }
                catch (ClassNotFoundException ex) {
                    log.info("QuickTime for Java could not be initialized");
                }
            }

            ImageCaptureService imageCaptureService = null;
            try {
                imageCaptureService = grabberClazz.newInstance();
            }
            catch (Throwable e) {
                imageCaptureService = new FakeImageCaptureServiceImpl();
                log.warn("Failed to frame capture using an instance of " + grabberClazz, e);
            }

            return imageCaptureService;
        }
    }
}
