/*
 * Copyright 2005 MBARI
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


/*
Created on Oct 8, 2004
 */
package org.mbari.vars.annotation.ui;

import java.util.Date;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.swing.PropertyPanel;
import org.mbari.vars.annotation.model.Observation;
import org.mbari.vars.annotation.model.VideoFrame;
import org.mbari.vars.annotation.ui.actions.ChangeTimeCodeActionWithDialog;
import org.mbari.vars.knowledgebase.model.Concept;
import org.mbari.vars.knowledgebase.model.ConceptName;
import vars.knowledgebase.IConceptName;
import org.mbari.vars.knowledgebase.model.dao.CacheClearedEvent;
import org.mbari.vars.knowledgebase.model.dao.CacheClearedListener;
import org.mbari.vars.knowledgebase.model.dao.KnowledgeBaseCache;
import vars.annotation.IObservation;
import vars.annotation.IVideoFrame;
import vars.knowledgebase.IConcept;

/**
 * <p>Displays the properties of an observation</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 * @version $Id: PObservationPanel.java 376 2006-10-26 18:21:43Z hohonuuli $
 */
public class PObservationPanel extends PropertiesPanel {

    /**
     *
     */
    private static final long serialVersionUID = -2722676487288783983L;

    /**
     *     @uml.property  name="annotation"
     *     @uml.associationEnd  multiplicity="(1 1)" inverse="this$0:org.mbari.vars.annotation.ui.PObservationPanel$Annotation"
     */
    private final Annotation annotation;

    /**
     *     Allows the annotator to change the timecode
     *     @uml.property  name="timeCodeAction"
     *     @uml.associationEnd
     */
    private ActionAdapter timeCodeAction;

    /**
     * Constructs ...
     *
     */
    public PObservationPanel() {
        super();
        annotation = new Annotation();
        setPropertyNames(new String[] { "TimeCode", "Concept", "RecordedDate", "ObservationDate", "Observer" });
        addListeners();
        addToolTip("Concept");

        /*
         * If the KnowledgebaseCache is cleared we need to update the
         * displayed concept name.
         */
        KnowledgeBaseCache.getInstance().addCacheClearedListener(new CacheClearedListener() {

            public void afterClear(CacheClearedEvent evt) {
                update(annotation.getObservation(), null);
            }

            public void beforeClear(CacheClearedEvent evt) {

                // TODO Verify this default implementation is correct
            }
        });
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    private void addListeners() {

        // Allow the TimeCode to be edited
        final PropertyPanel p = getPropertyPanel("TimeCode");
        p.getEditButton();
        p.setEditAction(getTimeCodeAction());
    }

    /**
     *     @return  The ChangeTimeCodeAction. We override the doAction method in order to  redraw the panel properly.
     *     @uml.property  name="timeCodeAction"
     */
    private ActionAdapter getTimeCodeAction() {
        if (timeCodeAction == null) {
            timeCodeAction = new ChangeTimeCodeActionWithDialog() {

                /**
                 *
                 */
                private static final long serialVersionUID = -8801974108216091602L;

                public void doAction() {
                    super.doAction();

                    // This forces a redraw
                    update(annotation.getObservation(), "");
                }
            };
        }

        return timeCodeAction;
    }

    /**
     *  Subscribes to the observationDispatcher.
     *
     * @param  obj Description of the Parameter
     * @param  changeCode Description of the Parameter
     * @see org.mbari.util.IObserver#update(java.lang.Object, java.lang.Object)
     */
    public void update(final Object obj, final Object changeCode) {
        final Observation obs = (Observation) obj;
        if (obs == null) {
            clearValues();

            return;
        }

        annotation.setObservation(obs);
        setProperties(annotation);
    }

    /**
     *     <p>This class represents a hybid of Observation-VideoFrame properties.</p>
     */
    private class Annotation {

        private final IObservation nullObs;
        private IObservation observation;

        /**
         * Constructs ...
         *
         */
        Annotation() {
            final IVideoFrame nullVf = new VideoFrame();
            nullObs = new Observation();
            nullVf.addObservation(nullObs);
            setObservation(nullObs);
        }

        /**
         * <p><!-- Method description --></p>
         *
         *
         * @return
         */
        public IConcept getConcept() {
            final String conceptName = observation.getConceptName();
            IConcept out = null;
            try {
                out = KnowledgeBaseCache.getInstance().findConceptByName(conceptName);
            }
            catch (final Exception e) {
                out = new Concept(new ConceptName(conceptName, IConceptName.NAMETYPE_PRIMARY), null);
            }

            return out;
        }

        /**
         *         <p><!-- Method description --></p>
         *         @return
         *         @uml.property  name="observation"
         */
        IObservation getObservation() {
            return observation;
        }

        /**
         * <p><!-- Method description --></p>
         *
         *
         * @return
         */
        public Date getObservationDate() {
            return observation.getObservationDate();
        }

        /**
         * <p><!-- Method description --></p>
         *
         *
         * @return
         */
        public String getObserver() {
            return observation.getObserver();
        }

        /**
         * <p><!-- Method description --></p>
         *
         *
         * @return
         */
        public Date getRecordedDate() {
            return observation.getVideoFrame().getRecordedDate();
        }

        /**
         * <p><!-- Method description --></p>
         *
         *
         * @return
         */
        public String getTimeCode() {
            return observation.getVideoFrame().getTimeCode();
        }

        /**
         *         <p><!-- Method description --></p>
         *         @param  obs
         *         @uml.property  name="observation"
         */
        void setObservation(final IObservation obs) {
            if (obs == null) {
                observation = nullObs;
            }
            else {
                observation = obs;
            }
        }
    }
}
