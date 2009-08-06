/*
 * @(#)IObservation.java   2008.12.30 at 01:50:54 PST
 *
 * Copyright 2007 MBARI
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.annotation;

import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

/**
 *
 * @author brian
 */
public interface IObservation extends IAnnotationObject {
    
    String PROP_CONCEPT_NAME = "conceptName";
    String PROP_OBSERVATION_DATE = "observationDate";
    String PROP_NOTES = "notes";
    String PROP_OBSERVER = "observer";
    String PROP_VIDEO_FRAME = "videoFrame";
    String PROP_ASSOCIATIONS = "associations";
        

    /**
     * Add to the <code>Association</code> collection.
     *
     * @param association
     * The <code>Association</code> object to add.
     * @return The passed <code>Association</code> object if added, the
     * matching <code>Association</code> object otherwise.
     * @see IAssociation
     */
    void addAssociation(IAssociation association);


    /**
     * WARNING! Do not add or remove directly from this collection.
     * @return  A synchronized collection
     */
    Set<? extends IAssociation> getAssociations();

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
    IVideoFrame getVideoFrame();

    /**
     * Indecates if this observation has a sample associated with it. It does
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
    void removeAssociation(final IAssociation association);


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

}
