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
Created on Oct 22, 2003
 */
package vars.annotation.ui.table;

import java.util.Enumeration;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import vars.annotation.AssociationList;
import vars.annotation.CameraData;
import vars.annotation.VideoArchive;
import vars.annotation.VideoFrame;
import vars.annotation.Observation;

/**
 * <p><!--Insert summary here--></p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 * @version $Id: ObservationColumnModel.java 314 2006-07-10 02:38:46Z hohonuuli $
 * @stereotype  thing
 */
public class ObservationColumnModel extends DefaultTableColumnModel {

    /**
     *
     */
    private static final long serialVersionUID = -1153806983377991121L;

    /**
     *  Description of the Field
     */
    public final static Integer SAMPLE = Integer.valueOf(3);

    /**
     *  Description of the Field
     */
    public final static Integer NONE = Integer.valueOf(0);

    /**
     *  Description of the Field
     */
    public final static Integer FRAMEGRAB_AND_SAMPLE = Integer.valueOf(2);

    /**
     *  Description of the Field
     */
    public final static Integer FRAMEGRAB = Integer.valueOf(1);
    private static TableColumnModel model;

    /**
     * Constructs ...
     *
     */
    private ObservationColumnModel() {
        super();
        addColumn(new TimeCodeColumn());
        addColumn(new ObservationColumn());
        addColumn(new AssociationColumn());
        addColumn(new FGSColumn());
        addColumn(new ObserverColumn());
        addColumn(new CameraDirectionColumn());
        addColumn(new VideoArchiveNameColumn());
        addColumn(new NotesColumn());
    }

    /**
     * Find a column based on the identifier.
     *
     * @param  id
     * @return
     */
    public int findColumn(final Object id) {
        int idx = 0;
        int out = -1;
        for (final Enumeration e = getColumns(); e.hasMoreElements(); ) {
            final ValueColumn element = (ValueColumn) e.nextElement();
            if (element.getIdentifier().equals(id)) {
                out = idx;

                break;
            }

            idx++;
        }

        return out;
    }

    /**
     *  Description of the Method
     *
     * @param  columnIndex Description of the Parameter
     * @param  newIndex Description of the Parameter
     */
    @Override
    public void moveColumn(final int columnIndex, final int newIndex) {
        if (columnIndex != newIndex) {

            // TODO 20040518 brian: This needs to be fixed
            // This is B-A-D bad. The model indices are supposed to stay
            // the same while the tablecolumns are free to roam about. For
            // whatever reason, that is not happening, so we go with...
            // the old switcharoo!
            final TableColumn tc1 = this.getColumn(columnIndex);
            final TableColumn tc2 = this.getColumn(newIndex);
            tc1.setModelIndex(newIndex);
            tc2.setModelIndex(columnIndex);
        }

        super.moveColumn(columnIndex, newIndex);
    }

    /**
     *  Gets the instance attribute of the ObservationColumnModel class
     *
     * @return  The instance value
     */
    public static TableColumnModel getInstance() {
        if (model == null) {
            model = new ObservationColumnModel();
        }

        return model;
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
        public final static String ID = "Associations";

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
        public Object getValue(final IObservation observation) {
            return observation.getAssociationList();
        }

        /**
         *  Sets the value attribute of the AssociationColumn object
         *
         * @param  observation The new value value
         * @param  value The new value value
         */
        @Override
        public void setValue(final IObservation observation, final Object value) {

            // Do nothing. The AssociationEditorPanel sets the values;
        }
    }

    /**
     * <p><!-- Class description --></p>
     *
     * @version    $Id: ObservationColumnModel.java 314 2006-07-10 02:38:46Z hohonuuli $
     * @author     <a href="http://www.mbari.org">Monterey Bay Aquarium Research Institute</a>
     */
    public class CameraDirectionColumn extends ValueColumn {

        /**
         *  Description of the Field
         */
        public final static int COLUMN_INDEX = 5;

        /**
         * Table identifier. Useful for locating a column in a table
         */
        public final static String ID = "Camera Direction";

        /**
         *
         */
        private static final long serialVersionUID = -7190223602981334448L;

        /**
         * Constructor for the CameraDirectionColumn object
         */
        public CameraDirectionColumn() {

            // super();
            super(COLUMN_INDEX);
            setIdentifier(ID);
            setHeaderValue(ID);
            setMinWidth(20);
            setPreferredWidth(50);
        }

        /**
         *  Gets the columnClass attribute of the CameraDirectionColumn object
         *
         * @return  The columnClass value
         */
        public Class getColumnClass() {
            return String.class;
        }

        /**
         *  Gets the value attribute of the CameraDirectionColumn object
         *
         * @param  observation Description of the Parameter
         * @return  The value value
         */
        public Object getValue(final IObservation observation) {
            final IVideoFrame vf = observation.getVideoFrame();
            String value = "";
            if (vf != null) {
                final ICameraData cd = vf.getCameraData();
                if (cd != null) {
                    value = cd.getDirection();

                    if (value == null) {
                        value = "";
                    }
                }
            }

            return value;
        }

        /**
         *  Gets the sortable attribute of the CameraDirectionColumn object
         *
         * @return  The sortable value
         */
        @Override
        public boolean isSortable() {
            return true;
        }

        /**
         *  Sets the value attribute of the CameraDirectionColumn object
         *
         * @param  observation The new value value
         * @param  value The new value value
         */
        @Override
        public void setValue(final IObservation observation, final Object value) {

            // TODO implement this if needed
        }
    }

    /**
     * <p><!-- Class description --></p>
     *
     * @version    $Id: ObservationColumnModel.java 314 2006-07-10 02:38:46Z hohonuuli $
     * @author     <a href="http://www.mbari.org">Monterey Bay Aquarium Research Institute</a>
     */
    public class FGSColumn extends ValueColumn {

        /**
         *  Description of the Field
         */
        public final static int COLUMN_INDEX = 3;

        /**
         * Table identifier. Useful for locating a column in a table
         */
        public final static String ID = "FG/S";

        /**
         *
         */
        private static final long serialVersionUID = -7760950390342678527L;

        /**
         * Constructor for the FGSColumn object
         */
        public FGSColumn() {

            // super();
            super(COLUMN_INDEX, 45);
            setMaxWidth(45);
            setMinWidth(45);
            setPreferredWidth(45);
            setIdentifier(ID);
            setResizable(false);
            setCellRenderer(new FGSCellRenderer());
            setHeaderValue(ID);
        }

        /**
         *  Gets the columnClass attribute of the FGSColumn object
         *
         * @return  The columnClass value
         */
        public Class getColumnClass() {
            return Integer.class;
        }

        /**
         *  Gets the value attribute of the FGSColumn object
         *
         * @param  observation Description of the Parameter
         * @return  The value value
         */
        public Object getValue(final IObservation observation) {
            final boolean hasSample = observation.hasSample();
            boolean hasFramegrab = false;
            final IVideoFrame vf = observation.getVideoFrame();
            if (vf != null) {
                hasFramegrab = vf.hasFrameGrab();
            }

            Integer out = NONE;
            if (hasFramegrab && hasSample) {
                out = FRAMEGRAB_AND_SAMPLE;
            }
            else if (!hasFramegrab && hasSample) {
                out = SAMPLE;
            }
            else if (hasFramegrab &&!hasSample) {
                out = FRAMEGRAB;
            }

            return out;
        }

        /**
         *  Gets the sortable attribute of the FGSColumn object
         *
         * @return  The sortable value
         */
        @Override
        public boolean isSortable() {
            return true;
        }
    }

    /**
     * <p><!-- Class description --></p>
     *
     * @version    $Id: ObservationColumnModel.java 314 2006-07-10 02:38:46Z hohonuuli $
     * @author     <a href="http://www.mbari.org">Monterey Bay Aquarium Research Institute</a>
     */
    public class NotesColumn extends ValueColumn {

        /**
         *  Description of the Field
         */
        public final static int COLUMN_INDEX = 7;

        /**
         * Table identifier. Useful for locating a column in a table
         */
        public final static String ID = "Notes";

        /**
         *
         */
        private static final long serialVersionUID = 3417882875517833612L;

        /**
         * Constructor for the NotesColumn object
         */
        public NotesColumn() {

            // super();
            super(COLUMN_INDEX);
            setIdentifier(ID);
            setHeaderValue(ID);
            setMinWidth(60);
            setPreferredWidth(130);
        }

        /**
         *  Gets the columnClass attribute of the NotesColumn object
         *
         * @return  The columnClass value
         */
        public Class getColumnClass() {
            return String.class;
        }

        /**
         *  Gets the value attribute of the NotesColumn object
         *
         * @param  observation Description of the Parameter
         * @return  The value value
         */
        public Object getValue(final IObservation observation) {
            return observation.getNotes();
        }

        /**
         *  Gets the sortable attribute of the NotesColumn object
         *
         * @return  The sortable value
         */
        @Override
        public boolean isSortable() {
            return false;
        }

        /**
         *  Sets the value attribute of the NotesColumn object
         *
         * @param  observation The new value value
         * @param  value The new value value
         */
        @Override
        public void setValue(final IObservation observation, final Object value) {
            observation.setNotes((String) value);
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
        public final static String ID = "Concept";

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
            return IObservation.class;
        }

        /**
         *  Gets the value attribute of the ObservationColumn object
         *
         * @param  observation Description of the Parameter
         * @return  The value value
         */
        public Object getValue(final IObservation observation) {
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
        public void setValue(final IObservation observation, final Object value) {
            observation.setConceptName((String) value);
        }
    }

    /**
     * <p><!-- Class description --></p>
     *
     * @version    $Id: ObservationColumnModel.java 314 2006-07-10 02:38:46Z hohonuuli $
     * @author     <a href="http://www.mbari.org">Monterey Bay Aquarium Research Institute</a>
     */
    public class ObserverColumn extends ValueColumn {

        /**
         *  Description of the Field
         */
        public final static int COLUMN_INDEX = 4;

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
        public Object getValue(final IObservation observation) {
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
        public void setValue(final IObservation observation, final Object value) {

            // TODO implement this if needed
        }
    }

    /**
     * @author  brian
     * @version
     * @directed
     */

    /*
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
        public final static String ID = "Time-code";

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
        public Class getColumnClass() {
            return String.class;
        }

        /**
         *  Gets the value attribute of the TimeCodeColumn object
         *
         * @param  observation Description of the Parameter
         * @return  The value value
         */
        public Object getValue(final IObservation observation) {
            return observation.getVideoFrame().getTimeCode();
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
        public void setValue(final IObservation observation, final Object value) {
            observation.getVideoFrame().setTimeCode((String) value);
        }
    }

    /**
     * <p><!-- Class description --></p>
     *
     * @version    $Id: ObservationColumnModel.java 314 2006-07-10 02:38:46Z hohonuuli $
     * @author     <a href="http://www.mbari.org">Monterey Bay Aquarium Research Institute</a>
     */
    public class VideoArchiveNameColumn extends ValueColumn {

        /**
         *  Description of the Field
         */
        public final static int COLUMN_INDEX = 6;

        /**
         * Table identifier. Useful for locating a column in a table
         */
        public final static String ID = "Tape ID";

        /**
         *
         */
        private static final long serialVersionUID = 2124750916306550322L;

        /**
         * Constructor for the VideoArchiveNameColumn object
         */
        public VideoArchiveNameColumn() {

            // super();
            super(COLUMN_INDEX);
            setIdentifier(ID);
            setHeaderValue(ID);
            setMinWidth(20);
            setPreferredWidth(50);
        }

        /**
         *  Gets the columnClass attribute of the VideoArchiveNameColumn object
         *
         * @return  The columnClass value
         */
        public Class getColumnClass() {
            return String.class;
        }

        /**
         *  Gets the value attribute of the VideoArchiveNameColumn object
         *
         * @param  observation Description of the Parameter
         * @return  The value value
         */
        public Object getValue(final IObservation observation) {
            final IVideoFrame vf = observation.getVideoFrame();
            String value = "";
            if (vf != null) {
                final IVideoArchive va = vf.getVideoArchive();
                if (va != null) {
                    value = va.getVideoArchiveName();

                    if (value == null) {
                        value = "";
                    }
                }
            }

            return value;
        }

        /**
         *  Gets the sortable attribute of the VideoArchiveNameColumn object
         *
         * @return  The sortable value
         */
        @Override
        public boolean isSortable() {
            return true;
        }

        /**
         *  Sets the value attribute of the VideoArchiveNameColumn object
         *
         * @param  observation The new value value
         * @param  value The new value value
         */
        @Override
        public void setValue(final IObservation observation, final Object value) {

            // TODO implement this if needed
        }
    }
}
