/*
 * @(#)JXObservationTableColumnModel.java   2009.11.12 at 10:53:50 PST
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



package vars.annotation.ui.table;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.swing.table.TableColumn;
import org.jdesktop.swingx.table.DefaultTableColumnModelExt;
import vars.annotation.Association;
import vars.annotation.CameraData;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.VideoFrame;
import vars.annotation.ui.Lookup;

/**
 *
 *
 */
public class JXObservationTableColumnModel extends DefaultTableColumnModelExt {

    private boolean imageView = false;

    /**
     * Constructs ...
     */
    public JXObservationTableColumnModel() {
        super();
        addColumn(new TimeCodeColumn());
        addColumn(new ObservationColumn());
        addColumn(new AssociationColumn());
        addColumn(new FGSColumn());
        addColumn(new ObserverColumn());
        addColumn(new CameraDirectionColumn());
        addColumn(new RecordedDateColumn());
    }

    public JXObservationTableColumnModel(boolean showVideoArchiveName) {
        this();
        if (showVideoArchiveName) {
            addColumn(new VideoArchiveNameColumn());
        }
    }


    /**
     * Set the state of the view. Full view includes all available columns.
     * 'Mini-view' hides teh notes columne and teh VideoArchviveNameColumn. The
     * miniview is used in the annotation editor, the full view is used in
     * the videoarchiveSetEditorPanel.
     *
     * @param imageView
     */
    public void setImageView(boolean imageView) {
        this.imageView = imageView;
        List<TableColumn> columns = getColumns(true);
        for (TableColumn tableColumn : columns) {

            if (tableColumn instanceof ValueColumn) {
                ValueColumn valueColumn = (ValueColumn) tableColumn;
                String id = (String) valueColumn.getIdentifier();
                if (id != null) {
                    if (id.equalsIgnoreCase(RecordedDateColumn.ID)) {
                        valueColumn.setVisible(imageView);
                    }
                }
            }
        }
    }

    public boolean isImageView() {
        return imageView;
    }


    public class AssociationColumn extends ValueColumn {


        public final static int COLUMN_INDEX = 2;

        /**
         * Table identifier. Useful for locating a column in a table
         */
        public final static String ID = "Descriptions";

        /**
         * Constructor for the AssociationColumn object
         */
        public AssociationColumn() {

            // super();
            super(ID, COLUMN_INDEX, 120);
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
            return List.class;
        }

        /**
         *  Gets the value attribute of the AssociationColumn object
         *
         * @param  observation Description of the Parameter
         * @return  The value value
         */
        public Object getValue(final Observation observation) {
            return new Vector<Association>(observation.getAssociations());
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


    public class CameraDirectionColumn extends ValueColumn {

        public final static int COLUMN_INDEX = 5;

        /**
         * Table identifier. Useful for locating a column in a table
         */
        public final static String ID = "Camera Direction";

        /**
         * Constructor for the CameraDirectionColumn object
         */
        public CameraDirectionColumn() {

            // super();
            super(ID, COLUMN_INDEX);
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
        public Object getValue(final Observation observation) {
            final VideoFrame vf = observation.getVideoFrame();
            String value = "";

            if (vf != null) {
                final CameraData cd = vf.getCameraData();

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
        public void setValue(final Observation observation, final Object value) {

            // TODO implement this if needed
        }
    }



    public class FGSColumn extends ValueColumn {

        /**  */
        public final static int COLUMN_INDEX = 3;

        /**
         * Table identifier. Useful for locating a column in a table
         */
        public final static String ID = "FG/S";



        /**
         * Constructor for the FGSColumn object
         */
        public FGSColumn() {

            // super();
            super(ID, COLUMN_INDEX, 45);
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
        public Object getValue(final Observation observation) {
            final boolean hasSample = observation.hasSample();
            boolean hasFramegrab = false;
            final VideoFrame vf = observation.getVideoFrame();

            if (vf != null) {
                hasFramegrab = vf.hasImageReference();
            }

            Integer out = ObservationTableModel.NONE;

            if (hasFramegrab && hasSample) {
                out = ObservationTableModel.FRAMEGRAB_AND_SAMPLE;
            }
            else if (!hasFramegrab && hasSample) {
                out = ObservationTableModel.SAMPLE;
            }
            else if (hasFramegrab && !hasSample) {
                out = ObservationTableModel.FRAMEGRAB;
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


    class NotesColumn extends ValueColumn {

        public final static int COLUMN_INDEX = 8;

        /**
         * Table identifier. Useful for locating a column in a table
         */
        public final static String ID = "Notes";

        /**
         * Constructor for the NotesColumn object
         */
        public NotesColumn() {

            // super();
            super(ID, COLUMN_INDEX);
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
        public Object getValue(final Observation observation) {
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
        public void setValue(final Observation observation, final Object value) {
            observation.setNotes((String) value);
        }
    }



    public class ObservationColumn extends ValueColumn {


        public final static int COLUMN_INDEX = 1;

        /**
         * Table identifier. Useful for locating a column in a table
         */
        public final static String ID = "Observation";


        /**
         * Constructor for the ObservationColumn object
         */
        public ObservationColumn() {

            // super();
            // ModelIndex = 2, FieldWidth, Use default editor and renderers
            super(ID, COLUMN_INDEX, 140);
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



    public class ObserverColumn extends ValueColumn {


        public final static int COLUMN_INDEX = 4;

        public final static String ID = "Observer";


        /**
         * Constructor for the ObserverColumn object
         */
        public ObserverColumn() {

            // super();
            super(ID, COLUMN_INDEX);
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

    public class RecordedDateColumn extends ValueColumn {

        public final static int COLUMN_INDEX = 6;

        /**
         * Table identifier. Useful for locating a column in a table
         */
        public final static String ID = "Recorded Date";

        public final DateFormat dateFormat = Lookup.DATE_FORMAT_UTC;

        public RecordedDateColumn() {
            super(ID, COLUMN_INDEX, 180);
            setIdentifier(ID);
            setResizable(false);
            setHeaderValue(ID);
        }

        @Override
        public Class getColumnClass() {
            return String.class;
        }

        @Override
        public Object getValue(Observation observation) {
            Date date = observation.getVideoFrame().getRecordedDate();
            return date == null ? "    -  -     :  :   " : dateFormat.format(date);
        }

        @Override
        public boolean isSortable() {
            return true;
        }

        @Override
        public void setValue(Observation observation, Object value) {
            // DO Nothing
        }
    }


    /**
     *  # ObservationTableModel lnkObservationTableModel;
     */
    public class TimeCodeColumn extends ValueColumn {

        public final static int COLUMN_INDEX = 0;

        /**
         * Table identifier. Useful for locating a column in a table
         */
        public final static String ID = "Timecode";


        /**
         * Constructor for the TimeCodeColumn object
         */
        public TimeCodeColumn() {

            // super();
            // ModelIndex = 1, FieldWidth, Use default editor and renderers
            super(ID, COLUMN_INDEX, 90);
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


    public class VideoArchiveNameColumn extends ValueColumn {

        /**
         *  Description of the Field
         */
        public final static int COLUMN_INDEX = 7;

        /**
         * Table identifier. Useful for locating a column in a table
         */
        public final static String ID = "Tape ID";

        /**
         * Constructor for the VideoArchiveNameColumn object
         */
        public VideoArchiveNameColumn() {

            // super();
            super(ID, COLUMN_INDEX);
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
        public Object getValue(final Observation observation) {
            final VideoFrame vf = observation.getVideoFrame();
            String value = "";

            if (vf != null) {
                final VideoArchive va = vf.getVideoArchive();

                if (va != null) {
                    value = va.getName();

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
        public void setValue(final Observation observation, final Object value) {

            // TODO implement this if needed
        }
    }
}
