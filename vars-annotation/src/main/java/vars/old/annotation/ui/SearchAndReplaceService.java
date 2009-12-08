/*
 * @(#)SearchAndReplaceService.java   2009.11.18 at 04:22:40 PST
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



/**
 * @created  August 18, 2004
 */
package vars.old.annotation.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.JTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.ILink;
import vars.LinkComparator;
import vars.UserAccount;
import vars.annotation.Association;
import vars.annotation.Observation;
import vars.annotation.ui.table.IObservationTable;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.PersistenceController;

/**
 * <p>Search and replace functions for ObservationTables.</p>
 *
 * <p>Also to note, all the methods are static, which should make them really easy
 * to move to the ObservationTable class, or to call from that class.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class SearchAndReplaceService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Comparator<ILink> comparator = new LinkComparator();
    private final PersistenceController persistenceController;

    /**
     * Constructs ...
     *
     * @param persistenceController
     */
    public SearchAndReplaceService(PersistenceController persistenceController) {
        super();
        this.persistenceController = persistenceController;
    }

    /**
     *  Gets the associations from the observations at the specified rows.
     *
     * @param  table The observation table of interest
     * @param  rows The rows in the table
     * @return  An array of associations for observations at the selected
     *          rows. This include all child, grand-child, etc. associations.
     */
    public Association[] getAssociationsAtRows(final IObservationTable table, final int[] rows) {
        if ((table == null) || (rows == null)) {
            throw new IllegalArgumentException("both arguments must be non-null");
        }

        final List<Association> associations = new ArrayList<Association>();
        for (int i = 0; i < rows.length; i++) {
            final Observation observation = table.getObservationAt(rows[i]);
            associations.addAll(observation.getAssociations());
        }

        final Association[] associationsArray = (Association[]) associations.toArray(new Association[0]);

        return associationsArray;
    }

    /**
     *  Returns all associations in the table
     *
     * @param  table THe ObservationTable of interest
     * @return  An array of all associations in the table.
     */
    public Association[] getAssociationsInTable(final IObservationTable table) {
        final SortedSet<Association> associations = new TreeSet<Association>();
        Observation observation;
        for (int i = 0; i < table.getJTable().getRowCount(); i++) {
            observation = table.getObservationAt(i);
            associations.addAll(observation.getAssociations());
        }

        final Association[] associationArray = (Association[]) associations.toArray(new Association[0]);

        return associationArray;
    }

    /**
     *  Returns all concept names (as Strings) in an ObservationTable
     *
     * @param  table The table of interest
     * @return  A string array of all conceptNames in the table.
     */
    public String[] getConceptNamesInTable(final IObservationTable table) {

        final Set<String> conceptNamesInTable = new HashSet<String>();
        for (int i = 0; i < table.getJTable().getRowCount(); i++) {
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
    public int[] getMatchingRows(final IObservationTable table, final String conceptNameToSearchFor,
                                 final Association associationToSearchFor) {
        if (table == null) {
            throw new NullPointerException("ObservationTable argument must be non-null");
        }

        if ((conceptNameToSearchFor == null) && (associationToSearchFor == null)) {
            throw new IllegalArgumentException("The concept name or association or both must be non-null");
        }

        final List<Integer> selectedRows = new ArrayList<Integer>();
        final int rowCount = table.getJTable().getRowCount();
        Observation currentObservation;
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

                /* need to check if selectThisRow is already false, if so, then
                 * it will stay false because that means the conceptName didn't
                 * match
                 */
                if (selectThisRow && currentObservation.getAssociations().contains(associationToSearchFor)) {
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
        for (final Iterator<Integer> rowIterator = selectedRows.iterator(); rowIterator.hasNext();
                selectedRowsArrayIndex++) {
            selectedRowsArray[selectedRowsArrayIndex] = rowIterator.next().intValue();
        }

        return selectedRowsArray;
    }

    /**
     *  Gets the selectedObservations attribute of the SearchAndReplace class
     *
     * @param  table Description of the Parameter
     * @return  The selectedObservations value
     */
    public Observation[] getSelectedObservations(final IObservationTable table) {

        final int[] selectedRows = table.getJTable().getSelectedRows();
        final List<Observation> observationList = new ArrayList<Observation>();
        for (int i = 0; i < selectedRows.length; i++) {
            final Observation observation = table.getObservationAt(selectedRows[i]);
            observationList.add(observation);
        }

        return (Observation[]) observationList.toArray(new Observation[0]);
    }

    /**
     *  Description of the Method
     *
     * @param  table Description of the Parameter
     */
    public void removeAllAssociationsOnSelectedObservations(final IObservationTable table) {
        final int[] selectedRows = table.getJTable().getSelectedRows();
        Collection<Observation> observations = new ArrayList<Observation>(selectedRows.length);
        for (int i = 0; i < selectedRows.length; i++) {
            observations.add(table.getObservationAt(selectedRows[i]));
        }

        persistenceController.deleteAllAssociationsFrom(observations);
    }

    /**
     *  Description of the Method
     *
     * @param  table Description of the Parameter
     * @param  association Description of the Parameter
     */
    public void removeAssociationOnSelectedObservations(final IObservationTable table, final Association association) {

        final int[] selectedRows = table.getJTable().getSelectedRows();
        Collection<Association> associationsToDelete = new ArrayList<Association>();
        for (int i = 0; i < selectedRows.length; i++) {
            final Observation obs = table.getObservationAt(selectedRows[i]);

            final Collection<Association> associations = new ArrayList<Association>(obs.getAssociations());
            for (Association a : associations) {
                if (comparator.compare(a, association) == 0) {
                    associationsToDelete.add(a);
                }
            }

            persistenceController.deleteAssociations(associationsToDelete);
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
    public void selectMatchingObservations(final IObservationTable table, final String conceptNameToSearchFor,
            final Association associationToSearchFor) {

        if ((conceptNameToSearchFor == null) && (associationToSearchFor == null)) {
            throw new IllegalArgumentException("The concept name or association or both must be non-null");
        }

        final int[] selectedRows = getMatchingRows(table, conceptNameToSearchFor, associationToSearchFor);
        final JTable t = table.getJTable();
        t.clearSelection();

        for (int i = 0; i < selectedRows.length; i++) {
            t.getSelectionModel().addSelectionInterval(selectedRows[i], selectedRows[i]);
        }
    }

    /**
     *  Sets the conceptNameForSelectedObservations attribute of the SearchAndReplace class
     *
     * @param  table The new conceptNameForSelectedObservations value
     * @param  conceptName The new conceptNameForSelectedObservations value
     */
    public void setConceptNameForSelectedObservations(final IObservationTable table, final String conceptName) {
        if (conceptName == null) {
            throw new IllegalArgumentException("conceptName argument must be non-null");
        }

        final int[] selectedRows = table.getJTable().getSelectedRows();
        final UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
        final Date obsDate = new Date();
        Collection<Observation> observations = new ArrayList<Observation>();
        for (int i = 0; i < selectedRows.length; i++) {
            Observation observation = table.getObservationAt(selectedRows[i]);
            observation.setConceptName(conceptName);
            observation.setObserver(userAccount.getUserName());
            observation.setObservationDate(obsDate);
            observations.add(observation);
        }

        persistenceController.updateObservations(observations);

    }

    /**
     *  Description of the Method
     *
     * @param  table Description of the Parameter
     * @param  newAssociation Description of the Parameter
     * @param  oldAssociation Description of the Parameter
     */
    public void updateAssociationOnSelectedObservations(final IObservationTable table,
            final Association newAssociation, final Association oldAssociation) {

        final int[] selectedRows = table.getJTable().getSelectedRows();
        Collection<Association> updatedAssociations = new ArrayList<Association>();
        for (int i = 0; i < selectedRows.length; i++) {
            Observation observation = table.getObservationAt(selectedRows[i]);
            for (Association association : new ArrayList<Association>(observation.getAssociations())) {
                if (comparator.compare(association, oldAssociation) == 0) {
                    association.setLinkName(newAssociation.getLinkName());
                    association.setToConcept(newAssociation.getToConcept());
                    association.setLinkValue(newAssociation.getLinkValue());
                    updatedAssociations.add(association);
                }
            }
        }

        persistenceController.updateAssociations(updatedAssociations);

    }
}
