package vars.annotation.ui.roweditor;



import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.ListModel;
import org.mbari.swing.ListListModel;

import vars.annotation.Association;
import vars.annotation.Observation;

/**
 * <p>Basically an ArrayList meant for storing Associations so that they can be
 *  viewed in a GUI component.</p>
 *
 * <p>An AssociationList is an aggregation of <code>Association</code> objects.
 * Typically they are all associated with a specific <code>Observation</code>.
 * However, each <code>Association</code> contains a reference to its parent and children, so
 * this list class doesn't need to track the parent object. The current implementation
 * maintains references to Association objects internally as an ArrayList.
 * However, future implementations may use a database connection as underlying storage.
 *</p>
 *
 * <h2><u>UML</u></h2>
 * <pre>
 *                   1 1
 *  [AssociationList]-->[Observation]
 *                 \ 1       |1
 *                  \        |
 *                   \ *     |*
 *                   [Association]
 *
 *      NOTE: The associationlist has references to all the Associations related
 *            to an Observation.
 * </pre>
 *
 * @author <a href="mailto:brian@mbari.org">Brian Schlining</a>
 */
public class AssociationList extends Vector<Association> {
    
    public static final String PROP_OBSERVATION = "observation";
    public static final String PROP_ASSOCIATIONS = "associations";


    /**
     * Listen for changes in an associations parent or association properties. 
     * If these occur then we'll need to refresh the list.
     */
    private PropertyChangeListener listener = new PropertyChangeListener() {

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equalsIgnoreCase(Association.PROP_OBSERVATION)) {
                refresh();
            }
        }
    };


    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private ListListModel listModel;

    /**
     * AssociationLists are usually derived from a single parent observation. 
     * For VARS purposes this list will be created using AssociationList(Observation). 
     * For convenience we'll store a reference to the parent observation here.
     */
    private Observation observation;

    /**
     * Generate an AssociationList populated with the children of an observation (and there children , and so on)
     * @param observation
     */
    public AssociationList(final Observation observation) {
        setObservation(observation);
    }

    /**
     * Add the descendant associations so that they can be viewed.
     * @param assoc
     */
    private void addAssociation(Association assoc) {
        add(assoc);
        pcs.firePropertyChange(PROP_ASSOCIATIONS, null, this);
    }

    /**
     * Add a listener. A ProeprtyChangeEvent is fired when the contents of the
     * list are refreshed, either triggered by a change in a field in one
     * of the associations or by setting a new Observation.
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    /**
     * @return A ListModel that can be used to render an association
     *                 list in a table.
     */
    public ListModel getListModel() {
        if (listModel == null) {
            listModel = new ListListModel(this);
        }

        return listModel;
    }

    /**
     * Return the parent observation of this list
     * @return  The parent observation used to create the AssociationList
     */
    public Observation getObservation() {
        return observation;
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    public void refresh() {
        setObservation(getObservation());
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    /**
     * Set the parent observation. Invoking his method will clear the contents 
     * of the <code>AssociationList</code> and re-populate it with the tree of 
     * child associations of the parent <Observation</code>
     * @param  observation
     */
    public void setObservation(final Observation observation) {

        // Remove references to existing ProeprtyChangeListeners
        if (observation != null) {
            observation.removePropertyChangeListener(listener);

            for (Iterator<Association> i = iterator(); i.hasNext(); ) {
                final Association a = i.next();
                a.removePropertyChangeListener(Association.PROP_OBSERVATION, listener);
            }
        }

        clear();

        // Add the new associations
        this.observation = observation;

        if (observation != null) {

            if (observation != null) {
                Collection<Association> assocColl = new ArrayList<Association>(observation.getAssociations());
                for (Association association : assocColl) {
                    addAssociation(association);
                }
            }
        }

        pcs.firePropertyChange(PROP_OBSERVATION, null, this);
    }
}
