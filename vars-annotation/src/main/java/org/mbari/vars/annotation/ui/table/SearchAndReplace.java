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


/**
 * @created  August 18, 2004
 */
package org.mbari.vars.annotation.ui.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import vars.annotation.ISimpleConcept;
import vars.annotation.IObservation;
import vars.annotation.IAssociation;
import org.mbari.vars.annotation.ui.dispatchers.PersonDispatcher;
import org.mbari.vars.dao.DAOEventQueue;
import org.mbari.vars.dao.DAOException;
import org.mbari.vars.dao.DAOExceptionHandler;
import org.mbari.vars.dao.IDataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Search and replace functions for ObservationTables.</p>
 * <p>TODO achase 20040507 All the methods in this class, could conceivably go into the
 * ObservationTable class since they only operate on the ObservationTable. I've
 * put them in a separate class because I'm worried about the added
 * feature-bloat that would result if they were in ObservationTable. Maybe
 * ObservationTable should declare all the methods that are in this class, and
 * delegate the work to this class.</p>
 *
 * <p>Also to note, all the methods are static, which should make them really easy
 * to move to the ObservationTable class, or to call from that class.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: SearchAndReplace.java 314 2006-07-10 02:38:46Z hohonuuli $
 */
public class SearchAndReplace {

    private static final Logger log = LoggerFactory.getLogger(SearchAndReplace.class);

    /**
     *  Gets the associations from the observations at the specifed rows.
     *
     * @param  table The observation table of interest
     * @param  rows The rows in the table
     * @return  An array of associations for observations at the selected
     *          rows. This include all child, grandchild, etc. associations.
     */
    public static IAssociation[] getAssociationsAtRows(final ObservationTable table, final int[] rows) {
        if ((table == null) || (rows == null)) {
            throw new NullPointerException("both arguments must be non-null");
        }

        final ArrayList associations = new ArrayList();
        for (int i = 0; i < rows.length; i++) {
            final IObservation observation = table.getObservationAt(rows[i]);
            associations.addAll(observation.getAssociationList());
        }

        final IAssociation[] associationsArray = (IAssociation[]) associations.toArray(new IAssociation[0]);

        return associationsArray;
    }

    /**
     *  Returns all associations in the table
     *
     * @param  table THe ObservationTable of interest
     * @return  An array of all associations in the table.
     */
    public static IAssociation[] getAssociationsInTable(final ObservationTable table) {
        if (table == null) {
            throw new NullPointerException("ObservationTable argument must be non-null");
        }

        final SortedSet associations = new TreeSet();
        IObservation observation;
        for (int i = 0; i < table.getRowCount(); i++) {
            observation = table.getObservationAt(i);
            associations.addAll(observation.getAssociations());
        }

        final IAssociation[] associationArray = (IAssociation[]) associations.toArray(new IAssociation[0]);

        return associationArray;
    }

    /**
     *  Returns all concept names (as Strings) in an ObservationTable
     *
     * @param  table The table of interest
     * @return  A string array of all conceptNames in the table.
     */
    public static String[] getConceptNamesInTable(final ObservationTable table) {
        if (table == null) {
            throw new NullPointerException("ObservationTable argument must be non-null");
        }

        try {
            Thread.sleep(1000);
        }
        catch (final InterruptedException e) {
            log.error("The Thread " + Thread.currentThread().getName() + " was interrupted", e);
        }

        final Set conceptNamesInTable = new HashSet();
        for (int i = 0; i < table.getRowCount(); i++) {
            conceptNamesInTable.add(table.getObservationAt(i).getConceptName());
        }

        final String[] conceptNames = (String[]) conceptNamesInTable.toArray(new String[0]);

        return conceptNames;
    }

    /**
     *  Finds rows in an ObservationTable that match the specified criteria.
     *
     * @param  table The table of interest
     * @param  conceptNameToSearchFor A string name to serach for. This is compared
     *          to the conceptName of Observations only. Associations are not compared.
     * @param  associationToSearchFor If the conceptname matches. the associations
     *          are then compared to this. If this is <b>null</> then associations
     *          are not checked.
     * @return  indices of the matching rows.
     */
    public static int[] getMatchingRows(final ObservationTable table, final String conceptNameToSearchFor,
            final IAssociation associationToSearchFor) {
        if (table == null) {
            throw new NullPointerException("ObservationTable argument must be non-null");
        }

        if ((conceptNameToSearchFor == null) && (associationToSearchFor == null)) {
            throw new IllegalArgumentException("The concept name or association or both must be non-null");
        }

        final ArrayList selectedRows = new ArrayList();
        final int rowCount = table.getRowCount();
        IObservation currentObservation;
        for (int i = 0; i < rowCount; i++) {
            currentObservation = table.getObservationAt(i);
            boolean selectThisRow = true;
            if (conceptNameToSearchFor != null) {
                if (currentObservation.getConceptName().equals(conceptNameToSearchFor)) {
                    selectThisRow = true;
                }
                else {
                    selectThisRow = false;
                }
            }

            if (associationToSearchFor != null) {

                // need to check if selectThisRow is already false, if so, then
                // it will stay false because that means the conceptName didn't
                // match
                if (selectThisRow && currentObservation.getAssociationList().contains(associationToSearchFor)) {
                    selectThisRow = true;
                }
                else {
                    selectThisRow = false;
                }
            }

            if (selectThisRow) {
                selectedRows.add(Integer.valueOf(i));
            }
        }

        final int[] selectedRowsArray = new int[selectedRows.size()];
        int selectedRowsArrayIndex = 0;
        for (final Iterator rowIterator = selectedRows.iterator(); rowIterator.hasNext(); selectedRowsArrayIndex++) {
            selectedRowsArray[selectedRowsArrayIndex] = ((Integer) rowIterator.next()).intValue();
        }

        return selectedRowsArray;
    }

    /**
     *  Gets the selectedObservations attribute of the SearchAndReplace class
     *
     * @param  table Description of the Parameter
     * @return  The selectedObservations value
     */
    public static IObservation[] getSelectedObservations(final ObservationTable table) {
        if (table == null) {
            throw new NullPointerException("ObservationTable argument must be non-null");
        }

        final int[] selectedRows = table.getSelectedRows();
        final List observationList = new ArrayList();
        for (int i = 0; i < selectedRows.length; i++) {
            final IObservation observation = table.getObservationAt(selectedRows[i]);
            observationList.add(observation);
        }

        return (IObservation[]) observationList.toArray(new IObservation[0]);
    }

    /**
     *  Description of the Method
     *
     * @param  table Description of the Parameter
     * @throws  DAOException
     */
    public static void removeAllAssociationsOnSelectedObservations(final ObservationTable table) throws DAOException {
        if (table == null) {
            throw new NullPointerException("ObservationTable argument must be non-null");
        }

        final int[] selectedRows = table.getSelectedRows();
        for (int i = 0; i < selectedRows.length; i++) {
            final IObservation observation = table.getObservationAt(selectedRows[i]);
            final Collection associations = observation.getAssociations();
            synchronized (associations) {
                for (final Iterator iter = associations.iterator(); iter.hasNext(); ) {
                    final IAssociation association = (IAssociation) iter.next();
                    observation.removeAssociation(association);
                    DAOEventQueue.updateVideoArchiveSet((IDataObject) observation);
                    DAOEventQueue.delete((IDataObject) association);
                }
            }
        }
    }

    /**
     *  Description of the Method
     *
     * @param  table Description of the Parameter
     * @param  association Description of the Parameter
     * @exception  DAOException Description of the Exception
     */
    public static void removeAssociationOnSelectedObservations(final ObservationTable table,
            final IAssociation association)
            throws DAOException {
        if (table == null) {
            throw new NullPointerException("ObservationTable argument must be non-null");
        }

        final int[] selectedRows = table.getSelectedRows();
        for (int i = 0; i < selectedRows.length; i++) {
            final IObservation obs = table.getObservationAt(selectedRows[i]);

            /*
             *  We don't use an associationList directly because it's contents
             *  dynamically change as associations are added or removed. This
             *  dynamic update would hose the iterator.
             */
            final Collection ass = new ArrayList(obs.getAssociationList());
            for (final Iterator it = ass.iterator(); it.hasNext(); ) {
                final IAssociation a = (IAssociation) it.next();
                if (a.equals(association)) {
                    final ISimpleConcept parent = a.getParent();
                    parent.removeAssociation(a);
                    DAOEventQueue.updateVideoArchiveSet((IDataObject) parent);
                    DAOEventQueue.delete((IDataObject) a);
                }
            }
        }
    }

    /**
     * Select all Observations in the the ObservationTable that have the matching
     * concept name and association. Null arguments will not be searched for, however
     * at least one of the two search items must be non-null.
     *
     * @param  table The table to search through
     * @param  conceptNameToSearchFor The ConceptName to search for
     * @param  associationToSearchFor The Association to search for
     */
    public static void selectMatchingObservations(final ObservationTable table, final String conceptNameToSearchFor,
            final IAssociation associationToSearchFor) {
        if (table == null) {
            throw new NullPointerException("ObservationTable argument must be non-null");
        }

        if ((conceptNameToSearchFor == null) && (associationToSearchFor == null)) {
            throw new IllegalArgumentException("The concept name or association or both must be non-null");
        }

        final int[] selectedRows = getMatchingRows(table, conceptNameToSearchFor, associationToSearchFor);
        table.clearSelection();

        for (int i = 0; i < selectedRows.length; i++) {
            table.getSelectionModel().addSelectionInterval(selectedRows[i], selectedRows[i]);
        }
    }

    /**
     *  Sets the conceptNameForSelectedObservations attribute of the SearchAndReplace class
     *
     * @param  table The new conceptNameForSelectedObservations value
     * @param  conceptName The new conceptNameForSelectedObservations value
     */
    public static void setConceptNameForSelectedObservations(final ObservationTable table, final String conceptName) {
        if (table == null) {
            throw new NullPointerException("ObservationTable argument must be non-null");
        }

        if (conceptName == null) {
            throw new NullPointerException("conceptName argument must be non-null");
        }

        final int[] selectedRows = table.getSelectedRows();
        final String observer = PersonDispatcher.getInstance().getPerson();
        final Date obsDate = new Date();
        for (int i = 0; i < selectedRows.length; i++) {
            final IObservation obs = table.getObservationAt(selectedRows[i]);
            final String originalName = obs.getConceptName();
            obs.setConceptName(conceptName);
            obs.setObserver(observer);
            obs.setObservationDate(obsDate);

            /*
             * On error the name change will be rollled back.
             */
            DAOEventQueue.update((IDataObject) obs, new ObsUpdateErrorHandler(obs, originalName));
        }
    }

    /**
     *  Description of the Method
     *
     * @param  table Description of the Parameter
     * @param  newAssociation Description of the Parameter
     * @param  oldAssociation Description of the Parameter
     */
    public static void updateAssociationOnSelectedObservations(final ObservationTable table,
            final IAssociation newAssociation, final IAssociation oldAssociation) {
        if (table == null) {
            throw new NullPointerException("ObservationTable argument must be non-null");
        }

        // TODO achase 20040429 In this situation, I am updating all the public
        // setters of the oldAssociation with the values from the
        // newAssociation.
        // This is being done to avoid the database calls to add and remove
        // Associations.
        // We should probably encapsulate this functionality within the
        // Association
        // class, because as it stands now, if a new public Setter is created in
        // Association, this set of method calls will also need to be updated.
        /*
         * 2008-12-31 brian: I added the call to update things in the database
         */
        for (IAssociation a : newAssociation.getAssociations()) {
            oldAssociation.addAssociation(a);
        }
        oldAssociation.setLinkName(newAssociation.getLinkName());
        oldAssociation.setLinkValue(newAssociation.getLinkValue());
        oldAssociation.setParent(newAssociation.getParent());
        oldAssociation.setToConcept(newAssociation.getToConcept());

        DAOEventQueue.updateVideoArchiveSet((IDataObject) oldAssociation);
    }

    private static final class ObsUpdateErrorHandler extends DAOExceptionHandler {

        /**
         *
         *
         * @param obs
         * @param originalName
         */
        ObsUpdateErrorHandler(final IObservation obs, final String originalName) {
            setObject(new Object[] { obs, originalName });
        }

        /**
         * <p><!-- Method description --></p>
         *
         *
         * @param e
         */
        protected void doAction(final Exception e) {
            final Object[] params = (Object[]) getObject();
            final IObservation obs = (IObservation) params[0];
            final String originalName = (String) params[1];
            obs.setConceptName(originalName);
        }
    }
}
