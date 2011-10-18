/*
 * @(#)PersistenceController.java   2011.09.21 at 12:05:21 PDT
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



package vars.annotation.ui;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.mbari.net.URLUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.DAO;
import vars.ILink;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.AnnotationFactory;
import vars.annotation.Association;
import vars.annotation.AssociationDAO;
import vars.annotation.CameraData;
import vars.annotation.Observation;
import vars.annotation.ObservationDAO;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.annotation.VideoArchiveSet;
import vars.annotation.VideoFrame;
import vars.annotation.ui.eventbus.VideoArchiveChangedEvent;
import vars.annotation.ui.eventbus.ObservationsSelectedEvent;
import vars.annotation.ui.eventbus.ObservationsChangedEvent;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

/**
 * PersistenceService manages database transactions for the user-interface. It will keep the
 * persistent objects AND the user interface in synch.
 *
 * @version        Enter version here..., 2009.11.16 at 10:49:40 PST
 * @author         Brian Schlining [brian@mbari.org]
 */
public class PersistenceController {

    private static final NumberFormat f0123 = new DecimalFormat("0000");
    private static final NumberFormat f01 = new DecimalFormat("00");
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final AnnotationDAOFactory annotationDAOFactory;
    private final AnnotationFactory annotationFactory;
    private final ToolBelt toolBelt;

    /**
     * Constructs ...
     *
     * @param toolBelt
     */
    public PersistenceController(ToolBelt toolBelt) {
        super();
        this.toolBelt = toolBelt;
        this.annotationDAOFactory = toolBelt.getAnnotationDAOFactory();
        this.annotationFactory = toolBelt.getAnnotationFactory();
    }


    /**
     * Look up the 'validate' name, that's the primary name for a given concept
     * 
     * @param conceptName The string name to validate
     * @return The validated name. If it's not found in the knowledgebase then
     *  then the original string is returned.
     */
    public String getValidatedConceptName(String conceptName) {
        final ConceptDAO conceptDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        conceptDAO.startTransaction();
        Concept concept = conceptDAO.findByName(conceptName);
        String validatedName;
        if (concept == null) {
            log.warn("Unable to find '" + conceptName + "' in the knowledgebase.");
            validatedName = conceptName;
        }
        else {
            validatedName = concept.getPrimaryConceptName().getName();
        }
        conceptDAO.endTransaction();
        return validatedName;
    }


    /**
     * Creates a URL of [image.archive.url]/[platform]/images/[dive]/filename from
     * a file of [image.archive.dir]/[platform]/images/[dive]/filename
     *
     * @param  targetFile The File where the image that an image was copied to.
     * @return  The URL that corresponds to the File targetFile.
     * @exception  IllegalArgumentException Description of the Exception
     * @throws  MalformedURLException
     */
    URL fileToUrl(final File targetFile, final File imageTarget, final URL imageTargetMapping)
            throws IllegalArgumentException, MalformedURLException {

        // ---- Ensure that the file provided is located under the image archive directory
        String targetPath = targetFile.getAbsolutePath();
        final String rootPath = imageTarget.getAbsolutePath();
        if (!targetPath.startsWith(rootPath)) {
            throw new IllegalArgumentException("The file, " + targetPath +
                    ", is not located in the expected location, " + rootPath);
        }

        // Chop off the part of the path that matches the image target
        // e.g. /Target/MyDir with an image of /Target/MyDir/images/Tiburon/foo.png
        // would yield a postfix of /images/Tiburon/foo.png
        String postfix = targetPath.substring(rootPath.length(), targetPath.length());
        final String[] parts = postfix.split("[\\\\\\\\,/]");
        StringBuffer dstUrl = new StringBuffer(imageTargetMapping.toExternalForm());

        // Make sure the URL ends with "/"
        if (!dstUrl.toString().endsWith("/")) {
            dstUrl.append("/");
        }

        // Chain the parts into a new URL
        boolean b = false;    // Don't add "/" on the first loop iteration. It's already in the URL
        for (int i = 0; i < parts.length; i++) {
            if (!"".equals(parts[i])) {
                if (b) {
                    dstUrl.append("/");
                }

                dstUrl.append(parts[i]);
                b = true;
            }
        }

        String dstUrlString = dstUrl.toString().replaceAll(" ", "%20");    // Space break things

        URL out = null;
        try {
            out = new URL(dstUrlString);
        }
        catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Strings in Java suck!!!", e);
            }
        }

        return out;
    }








    /**
     * Convenience method very specific to MBARI internal usage and naming
     * conventions. MBARI likes to name video archives so that a tape #3 from
     * dive# 302 (seqNumber)using the ROV Tiburon (platform) would be named
     * T0302-03.
     *
     * @param platform The platform name. THe first character of the name
     *          is used. This is stored in the VideoArchiveSet
     * @param seqNumber In MBARI's case we use dive number. seqNumber is
     *          stored in the CameraPlatformDeployment. Numbers with more than
     *          4 digits are not supported.
     * @param tapeNumber This is an MBARI specific value. It is not stored in
     *          the VARS database. Numbers of more than 2 digits are not supported.
     * @param postfix This is any text to be appended to the end of the {@link VideoArchive}'s name. At MBARI,
     *          we use this to indicate HD tapes by appending 'HD'. If it's null nothing will be
     *          appended
     * @return A string name that is generated from the supplied arguments
     */
    public static String makeVideoArchiveName(final String platform, final int seqNumber, final int tapeNumber,
            final String postfix) {
        StringBuffer sb = new StringBuffer();
        sb.append(platform.charAt(0));
        sb.append(f0123.format((long) seqNumber));
        sb.append("-");
        sb.append(f01.format((long) tapeNumber));

        if (postfix != null) {
            sb.append(postfix);
        }

        return sb.toString();
    }



    /**
     * Thread-safe. Updates changes made to the observations in the database. Validates the
     * concept names used by the {@link Observation}s and their child {@link Association}
     * @param observations
     * @return
     */
    public Collection<Observation> updateAndValidate(Collection<Observation> observations) {
        Collection<Observation> updatedObservations = new Vector<Observation>(observations.size());
        ObservationDAO dao = toolBelt.getAnnotationDAOFactory().newObservationDAO();
        AssociationDAO aDao = toolBelt.getAnnotationDAOFactory().newAssociationDAO(dao.getEntityManager());
        final ConceptDAO conceptDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        conceptDAO.startTransaction();
        dao.startTransaction();

        for (Observation observation : observations) {
            observation = dao.updateFields(observation);
            if (observation != null) {
                updatedObservations.add(observation);
                dao.validateName(observation, conceptDAO);
                for (Association association : new ArrayList<Association>(observation.getAssociations())) {
                    aDao.validateName(association, conceptDAO);
                }
            }
        }

        dao.endTransaction();
        dao.close();
        conceptDAO.endTransaction();
        conceptDAO.close();

        return updatedObservations;
    }


    /**
     * Changes the CameraData URL's that match the currently set local directory to
     * use ther URL (image mapping target) defined in the uses preferences.
     *
     * @param videoArchive
     * @param imageTarget The  directory where the frames are saved into
     * @param imageTargetMapping The URL that maps imageTarget onto a web server
     * @return A Collection of CameraData objects whose URL's have been updated
     *  in the database. (Returns the udpated instance)
     *
     * @throws MalformedURLException
     */
    public Collection<CameraData> updateCameraDataUrls(VideoArchive videoArchive, File imageTarget,
            URL imageTargetMapping)
            throws MalformedURLException {
        Collection<CameraData> cameraDatas = new ArrayList<CameraData>();
        DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
        dao.startTransaction();
        videoArchive = dao.find(videoArchive);
        Collection<VideoFrame> videoFrames = videoArchive.getVideoFrames();
        URL imageTargetUrl = imageTarget.toURI().toURL();
        String imageTargetExternalForm = imageTargetUrl.toExternalForm();
        for (VideoFrame videoFrame : videoFrames) {
            CameraData cameraData = videoFrame.getCameraData();
            cameraData = dao.find(cameraData);
            String imageReference = cameraData.getImageReference();
            if ((imageReference != null) && imageReference.startsWith(imageTargetExternalForm)) {
                URL imageReferenceURL = new URL(imageReference);
                File imageReferenceFile = URLUtilities.toFile(imageReferenceURL);
                URL newUrl = fileToUrl(imageReferenceFile, imageTarget, imageTargetMapping);
                cameraData.setImageReference(newUrl.toExternalForm());
                cameraDatas.add(cameraData);
            }
        }
        dao.endTransaction();
        dao.close();

        return cameraDatas;
    }


}
