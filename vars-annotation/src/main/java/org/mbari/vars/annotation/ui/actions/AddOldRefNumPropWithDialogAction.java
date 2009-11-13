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
The Monterey Bay Aquarium Research Institute (MBARI) provides this
documentation and code 'as is', with no warranty, express or
implied, of its quality or consistency. It is provided without support and
without obligation on the part of MBARI to assist in its use, correction,
modification, or enhancement. This information should not be published or
distributed to third parties without specific written permission from MBARI
 */
package org.mbari.vars.annotation.ui.actions;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import javax.swing.JOptionPane;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.awt.event.IAction;
import org.mbari.text.ReverseSortComparator;
import org.mbari.vars.annotation.model.dao.VideoArchiveSetDAO;
import org.mbari.vars.annotation.ui.dispatchers.ObservationDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.VideoArchiveDispatcher;
import org.mbari.vars.dao.DAOException;
import org.mbari.vars.knowledgebase.model.dao.KnowledgeBaseCache;
import org.mbari.vars.util.AppFrameDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.IVideoArchiveSet;
import vars.annotation.IObservation;
import vars.knowledgebase.IConcept;

/**
 * <p>Adds 'identity-reference | self | [some integer]' property to the Observation set in
 * the ObservationDispatcher</p>
 *
 * @author <a href="http://www.mbari.org">MBARI </a>
 * @version $Id: AddOldRefNumPropWithDialogAction.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class AddOldRefNumPropWithDialogAction extends ActionAdapter {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(AddOldRefNumPropWithDialogAction.class);
    private static final Comparator COMPARATOR = new ReverseSortComparator();

    /**
     *     @uml.property  name="action"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    private final IAction action;

    /**
     * Constructs ...
     *
     */
    public AddOldRefNumPropWithDialogAction() {
        super();
        action = new AddOldRefNumPropAction();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mbari.awt.event.IAction#doAction()
     */

    /**
     * <p><!-- Method description --></p>
     *
     */
    @SuppressWarnings("unchecked")
    public void doAction() {

        /*
         * Get all the existing reference numbers in a VideoArchive
         */
        final IVideoArchiveSet vas = VideoArchiveDispatcher.getInstance().getVideoArchive().getVideoArchiveSet();
        final IObservation obs = ObservationDispatcher.getInstance().getObservation();
        final String conceptName = obs.getConceptName();
        IConcept concept = null;
        try {
            concept = KnowledgeBaseCache.getInstance().findConceptByName(conceptName);
        }
        catch (final DAOException e) {
            log.error("Failed to lookup a concept in the knowledebase", e);
        }

        final Collection refNums = VideoArchiveSetDAO.getInstance().findAllReferenceNumbers((IVideoArchiveSet) vas, concept);

        /*
         * If non were found let the user know. Otherwise show a dialog
         * allowing the user to select.
         */
        if (refNums.size() == 0) {
            AppFrameDispatcher.showWarningDialog(
                "<html><body>This Video Archive does not have any reference numbers assigned. Use 'New #' instead.</body></html>");
        }
        else {
            
            final Object[] choices = refNums.toArray(new String[refNums.size()]);
            Arrays.sort(choices, COMPARATOR);
            final String i = (String) JOptionPane.showInputDialog(AppFrameDispatcher.getFrame(),
                                 "Select a reference number", "VARS - Select Reference Number",
                                 JOptionPane.PLAIN_MESSAGE, null, choices, choices[0]);

            // If a string was returned, say so.
            if (i != null) {
                AddOldRefNumPropAction.setRefNumber(i);
                action.doAction();
            }
        }
    }
}
