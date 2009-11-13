/*
 * @(#)Observation.java   2009.11.12 at 03:28:18 PST
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



package vars.annotation;

import java.util.Date;
import java.util.Set;

/**
 *
 * @author brian
 */
public interface Observation extends AnnotationObject {

    String PROP_ASSOCIATIONS = "associations";
    String PROP_CONCEPT_NAME = "conceptName";
    String PROP_NOTES = "notes";
    String PROP_OBSERVATION_DATE = "observationDate";
    String PROP_OBSERVER = "observer";
    String PROP_VIDEO_FRAME = "videoFrame";

    /**
     * Add to the <code>Association</code> collection.
     *
     * @param association
     * The <code>Association</code> object to add.
     * @return The passed <code>Association</code> object if added, the
     * matching <code>Association</code> object otherwise.
     * @see Association
     */
    void addAssociation(Association association);

    /**
     * WARNING! Do not add or remove directly from this collection.
     * @return  A synchronized collection
     */
    Set<Association> getAssociations();

    /**
     * @return  The fromConcept of this observation.
     */
    String getConceptName();

    /**
     * @return
     */
    String getNotes();

    /**
     * @return
     */
    Date getObservationDate();

    /**
     * @return
     */
    String getObserver();

    /**
     * @return
     */
    VideoFrame getVideoFrame();

    /**
     * 
     * @return The x location of the observation within the videoFrame in pixels
     */
    Double getX();

    /**
     * 
     * @return The y location of the observation within the videoFrame in pixels
     */
    Double getY();

    /**
     * Indicates if this observation has a sample associated with it. It does
     * this by looking at each association for the linkName starting with
     * sampled.
     *
     * @return true = a sample IS associated with this observation.
     */
    boolean hasSample();

    /**
     * Remove from the <code>Association</code> collection.
     *
     * @param association
     * The <code>Association</code> object to remove.
     */
    void removeAssociation(final Association association);

    /**
     * <p><!-- Method description --></p>
     * @param  conceptName
     */
    void setConceptName(String conceptName);

    /**
     * @param string  NOTE: String longer than 200 characters will be truncated
     */
    void setNotes(String string);

    /**
     * The date the annotator annotated this observation. Got it ;-)
     * @param  dtg
     */
    void setObservationDate(Date dtg);

    /**
     * @param observer  A name representing the person (or software?) who made the  observation
     */
    void setObserver(String observer);

    /**
     * 
     * @param x  The x location of the observation within the videoFrame in pixels
     */
    void setX(Double x);

    /**
     * 
     * @param y  The y location of the observation within the videoFrame in pixels
     */
    void setY(Double y);
}
