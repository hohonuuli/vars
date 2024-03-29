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
package vars.annotation.ui.ppanel;

import java.util.Date;

import mbarix4j.awt.event.ActionAdapter;
import mbarix4j.swing.PropertyPanel;

import vars.CacheClearedEvent;
import vars.CacheClearedListener;
import vars.annotation.AnnotationFactory;
import vars.annotation.Observation;
import vars.annotation.VideoFrame;
import vars.annotation.ui.actions.ChangeTimeCodeActionWithDialog;
import vars.annotation.ui.ToolBelt;

/**
 * <p>Displays the properties of an observation</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 */
public class PObservationPanel extends PropertiesPanel {


    private final Annotation annotation;

    private ActionAdapter timeCodeAction;
    private final ToolBelt toolBelt;

    /**
     * Constructs ...
     *
     */
    public PObservationPanel(ToolBelt toolBelt) {
        super();
        this.toolBelt = toolBelt;
        annotation = new Annotation();
        setPropertyNames(new String[] { "TimeCode", "Concept", "RecordedDate", "ObservationDate", "Observer", "X", "Y" });
        addListeners();
        addToolTip("Concept");

        /*
         * If the KnowledgebaseCache is cleared we need to update the
         * displayed concept name.
         */
        toolBelt.getPersistenceCache().addCacheClearedListener(new CacheClearedListener() {

            public void afterClear(CacheClearedEvent evt) {
                update(annotation.getObservation(), null);
            }

            public void beforeClear(CacheClearedEvent evt) {

                // Do nada
            }
        });
    }


    private void addListeners() {

        // Allow the TimeCode to be edited
        final PropertyPanel p = getPropertyPanel("TimeCode");
        p.getEditButton();
        p.setEditAction(getTimeCodeAction());
    }


    private ActionAdapter getTimeCodeAction() {
        if (timeCodeAction == null) {
            timeCodeAction = new ChangeTimeCodeActionWithDialog(toolBelt) {

                @Override
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
     * @see mbarix4j.util.IObserver#update(java.lang.Object, java.lang.Object)
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

        private final Observation nullObs;
        private Observation observation;

        /**
         * Constructs ...
         *
         */
        Annotation() {
            AnnotationFactory factory = toolBelt.getAnnotationFactory();
            final VideoFrame nullVf = factory.newVideoFrame();
            nullObs = factory.newObservation();
            nullVf.addObservation(nullObs);
            setObservation(nullObs);
        }

        public String getConcept() {
            return observation.getConceptName();
        }


        Observation getObservation() {
            return observation;
        }


        public Date getObservationDate() {
            return observation.getObservationDate();
        }

        public String getObserver() {
            return observation.getObserver();
        }


        public Date getRecordedDate() {
            return observation.getVideoFrame().getRecordedDate();
        }

 
        public String getTimeCode() {
            return observation.getVideoFrame().getTimecode();
        }

        void setObservation(final Observation obs) {
            if (obs == null) {
                observation = nullObs;
            }
            else {
                observation = obs;
            }
        }

        public double getX() {
            return observation.getX() == null ? Double.NaN : observation.getX();
        }

        public double getY() {
            return observation.getY() == null ? Double.NaN : observation.getY();
        }
    }
}
