package vars.annotation.ui.table;

import org.jdesktop.swingx.table.DefaultTableColumnModelExt;
import org.mbari.vars.annotation.ui.table.TableCellRenderer4AssociationList;
import org.mbari.vars.annotation.ui.table.ValueColumn;

import vars.annotation.Observation;

public class JXObservationTableColumnModel extends DefaultTableColumnModelExt {

    public JXObservationTableColumnModel() {
        super();
        addColumn(new TimeCodeColumn());
        addColumn(new ObservationColumn());
        addColumn(new AssociationColumn());
        addColumn(new ObserverColumn());
    }
    
    /**
     *  # ObservationTableModel lnkObservationTableModel;
     */
    public class TimeCodeColumn extends ValueColumn {

        /**
         *  Description of the Field
         */
        public final static int COLUMN_INDEX = 0;

        /**
         * Table identifier. Useful for locating a column in a table
         */
        public final static String ID = "Timecode";

        /**
         *
         */
        private static final long serialVersionUID = -2305085330327785378L;

        

        /**
         * Constructor for the TimeCodeColumn object
         */
        public TimeCodeColumn() {

            // super();
            // ModelIndex = 1, FieldWidth, Use default editor and renderers
            super(COLUMN_INDEX, 90);
            setMinWidth(90);
            setMaxWidth(90);
            setPreferredWidth(90);
            setIdentifier(ID);
            setResizable(false);
            setHeaderValue(ID);
        }

        /**
         *  Gets the columnClass attribute of the TimeCodeColumn object
         *
         * @return  The columnClass value
         */
        @SuppressWarnings("unchecked")
        public Class getColumnClass() {
            return String.class;
        }

        /**
         *  Gets the value attribute of the TimeCodeColumn object
         *
         * @param  observation Description of the Parameter
         * @return  The value value
         */
        @Override
        public Object getValue(final Observation observation) {
            return observation.getVideoFrame().getTimecode();
        }

        /**
         *  Gets the sortable attribute of the TimeCodeColumn object
         *
         * @return  The sortable value
         */
        @Override
        public boolean isSortable() {
            return true;
        }

        /**
         *  Sets the value attribute of the TimeCodeColumn object
         *
         * @param  observation The new value value
         * @param  value The new value value
         */
        @Override
        public void setValue(final Observation observation, final Object value) {
            observation.getVideoFrame().setTimecode((String) value);
        }
    }
    
    /**
     * <p><!-- Class description --></p>
     *
     * @version    $Id: ObservationColumnModel.java 314 2006-07-10 02:38:46Z hohonuuli $
     * @author     <a href="http://www.mbari.org">Monterey Bay Aquarium Research Institute</a>
     */
    public class ObservationColumn extends ValueColumn {

        /**
         *  Description of the Field
         */
        public final static int COLUMN_INDEX = 1;

        /**
         * Table identifier. Useful for locating a column in a table
         */
        public final static String ID = "Observation";

        /**
         *
         */
        private static final long serialVersionUID = -7355742260058290831L;

        /**
         * Constructor for the ObservationColumn object
         */
        public ObservationColumn() {

            // super();
            // ModelIndex = 2, FieldWidth, Use default editor and renderers
            super(COLUMN_INDEX, 140);
            setMinWidth(120);
            setPreferredWidth(140);
            setMaxWidth(200);
            setIdentifier(ID);
            setHeaderValue(ID);
        }

        /**
         *  Gets the columnClass attribute of the ObservationColumn object
         *
         * @return  The columnClass value
         */
        public Class getColumnClass() {
            return Observation.class;
        }

        /**
         *  Gets the value attribute of the ObservationColumn object
         *
         * @param  observation Description of the Parameter
         * @return  The value value
         */
        @Override
        public Object getValue(final Observation observation) {
            return observation.getConceptName();
        }

        /**
         *  Gets the sortable attribute of the ObservationColumn object
         *
         * @return  The sortable value
         */
        @Override
        public boolean isSortable() {
            return true;
        }

        /**
         *  Sets the value attribute of the ObservationColumn object
         *
         * @param  observation The new value value
         * @param  value The new value value
         */
        @Override
        public void setValue(final Observation observation, final Object value) {
            observation.setConceptName((String) value);
        }
    }
    
    /**
     * <p><!-- Class description --></p>
     *
     * @version    $Id: ObservationColumnModel.java 314 2006-07-10 02:38:46Z hohonuuli $
     * @author     <a href="http://www.mbari.org">Monterey Bay Aquarium Research Institute</a>
     */
    public class AssociationColumn extends ValueColumn {

        /**
         *  Description of the Field
         */
        public final static int COLUMN_INDEX = 2;

        /**
         * Table identifier. Useful for locating a column in a table
         */
        public final static String ID = "Descriptions";

        /**
         *
         */
        private static final long serialVersionUID = -3758434467336284127L;

        /**
         * Constructor for the AssociationColumn object
         */
        public AssociationColumn() {

            // super();
            super(COLUMN_INDEX, 120);
            setMinWidth(100);
            setPreferredWidth(140);
            setMaxWidth(700);
            setIdentifier(ID);
            setResizable(true);
            setCellRenderer(new TableCellRenderer4AssociationList());
            setHeaderValue(ID);
        }

        /**
         *  Gets the columnClass attribute of the AssociationColumn object
         *
         * @return  The columnClass value
         */
        public Class getColumnClass() {
            return AssociationList.class;
        }

        /**
         *  Gets the value attribute of the AssociationColumn object
         *
         * @param  observation Description of the Parameter
         * @return  The value value
         */
        public Object getValue(final Observation observation) {
            return observation.getAssociationList();
        }

        /**
         *  Sets the value attribute of the AssociationColumn object
         *
         * @param  observation The new value value
         * @param  value The new value value
         */
        @Override
        public void setValue(final Observation observation, final Object value) {

            // Do nothing. The AssociationEditorPanel sets the values;
        }
    }
    
    /**
     * <p><!-- Class description --></p>
     *
     * @author     <a href="http://www.mbari.org">Monterey Bay Aquarium Research Institute</a>
     */
    public class ObserverColumn extends ValueColumn {

        /**
         *  Description of the Field
         */
        public final static int COLUMN_INDEX = 3;

        /**
         *  Description of the Field
         */
        public final static String ID = "Observer";

        /**
         *
         */
        private static final long serialVersionUID = -6201568942121691189L;

        /**
         * Constructor for the ObserverColumn object
         */
        public ObserverColumn() {

            // super();
            super(COLUMN_INDEX);
            setIdentifier(ID);
            setHeaderValue(ID);
            setMinWidth(20);
            setPreferredWidth(50);
        }

        /**
         *  Gets the columnClass attribute of the ObserverColumn object
         *
         * @return  The columnClass value
         */
        public Class getColumnClass() {
            return String.class;
        }

        /**
         *  Gets the value attribute of the ObserverColumn object
         *
         * @param  observation Description of the Parameter
         * @return  The value value
         */
        @Override
        public Object getValue(final Observation observation) {
            String value = "";
            final String observer = observation.getObserver();
            if (observer != null) {
                value = observer;
            }

            return value;
        }

        /**
         *  Sets the value attribute of the ObserverColumn object
         *
         * @param  observation The new value value
         * @param  value The new value value
         */
        @Override
        public void setValue(final Observation observation, final Object value) {

            // TODO implement this if needed
        }
    }
    
}
