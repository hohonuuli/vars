/*
 * @(#)RefineSearchPanel.java   2009.11.21 at 08:13:30 PST
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



package vars.query.ui;

import com.google.inject.Inject;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import mbarix4j.awt.event.ActionAdapter;

import mbarix4j.text.IgnoreCaseToStringComparator;
import org.mbari.util.Dispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.query.QueryPersistenceService;

/**
 * @author Brian Schlining
 */
public class RefineSearchPanel extends JPanel {

    private static final Comparator<Object> COMPARATOR = new IgnoreCaseToStringComparator();

    /**
     * This is a list of Colunn names who's return boxes should be checked by default
     * if found in the database.
     *
     * TODO 20050502 brian: Hack alert!! This should be pulled out into a properties
     * file.
     */
    private static final Set<String> DEFAULT_RETURNS = new TreeSet<String>(COMPARATOR);

    /**
     * In order to make usage easier for users we're grouping related columns
     * together in the user interface. To do this we have to know what
     * columns will appear; i.e. this is hacky and not very dynamic. We could
     * pull this information out into an XML file later on if needed. For now
     * if a column doesn't appear in the column groups it gets put into the
     * "Other" group.
     */
    private static final Map<String, Set<String>> COLUMN_GROUPS = new TreeMap<String, Set<String>>(COMPARATOR);

    static {
        DEFAULT_RETURNS.add("Associations");
        DEFAULT_RETURNS.add("ConceptName");
        DEFAULT_RETURNS.add("Depth");
        DEFAULT_RETURNS.add("Latitude");
        DEFAULT_RETURNS.add("Longitude");
        DEFAULT_RETURNS.add("RecordedDate");
        DEFAULT_RETURNS.add("Image");
        DEFAULT_RETURNS.add("Observer");
        DEFAULT_RETURNS.add("TapeTimeCode");
        DEFAULT_RETURNS.add("VideoArchiveName");
        DEFAULT_RETURNS.add("RovName");
        DEFAULT_RETURNS.add("DiveNumber");
    }

    static {

        /*
         * The dive group
         */
        Set<String> set = new TreeSet<String>();
        COLUMN_GROUPS.put("Dive", set);
        set.add("ChiefScientist");
        set.add("DiveNumber");
        set.add("DiveStartDate");
        set.add("DiveEndDate");
        set.add("RovName");
        set.add("ShipName");

        /*
         * The observation group
         */
        set = new TreeSet<String>();
        COLUMN_GROUPS.put("Observation", set);
        set.add("ConceptName");
        set.add("HDTimeCode");
        set.add("InSequence");
        set.add("ObservationDate");
        set.add("Observer");
        set.add("RecordedDate");
        set.add("TapeTimeCode");
        set.add("Notes");

        /*
         * The Associations group
         */
        set = new TreeSet<String>();
        COLUMN_GROUPS.put("Association", set);
        set.add("Associations");
        set.add("LinkName");
        set.add("LinkValue");
        set.add("ToConcept");

        /*
         * The Camera group
         */
        set = new TreeSet<String>();
        COLUMN_GROUPS.put("Camera Data", set);
        set.add("CameraDirection");
        set.add("CameraName");
        set.add("FieldWidth");
        set.add("Focus");
        set.add("Iris");
        set.add("Zoom");

        /*
         * The Physical Data group
         */
        set = new TreeSet<String>();
        COLUMN_GROUPS.put("Physical Data", set);
        set.add("Depth");
        set.add("Longitude");
        set.add("Latitude");
        set.add("Light");
        set.add("Oxygen");
        set.add("Salinity");
        set.add("Temperature");
    }

    private final Logger log = LoggerFactory.getLogger(getClass());
    private String[] stringConstraints = new String[] { "Observer", "ShipName", "PlatformName", "ChiefScientist",
            "Direction" };
    private Collection constraintNames;

    /**
     * Metadata representing information about the Annotations view in the database.
     */
    private Map metadata;
    private JPanel panel;
    private final QueryPersistenceService queryDAO;
    private JScrollPane scrollPane;
    private Collection valuePanels;

    /**
     * This is the default constructor
     *
     * @param queryDAO
     */
    @Inject
    public RefineSearchPanel(QueryPersistenceService queryDAO) {
        super();
        constraintNames = Arrays.asList(stringConstraints);
        this.queryDAO = queryDAO;
        initialize();
    }

    private void addGroupPanels() {

        /*
         * Need to organize panels by name. This is a Map<String, ValuePanel>
         * where key = columnName, value = the matching valuePanel
         */
        Map panelMap = new TreeMap(COMPARATOR);
        Collection panels = getValuePanels();
        for (Iterator i = panels.iterator(); i.hasNext(); ) {
            ValuePanel vp = (ValuePanel) i.next();
            panelMap.put(vp.getValueName(), vp);
        }

        /*
         * We need a border to highlight the groups.
         */
        Border border = BorderFactory.createLineBorder(Color.BLACK, 3);
        Font font = new java.awt.Font("Dialog", java.awt.Font.BOLD, 12);

        /*
         * This is a collection of columns found. We'll use this later on to
         * remove references to panels that have already been added to the UI
         */
        Collection columnsFound = new ArrayList();
        Set groups = COLUMN_GROUPS.keySet();
        for (Iterator i = groups.iterator(); i.hasNext(); ) {
            JPanel groupPanel = null;
            String groupName = (String) i.next();
            Collection columnNames = (Collection) COLUMN_GROUPS.get(groupName);
            for (Iterator j = columnNames.iterator(); j.hasNext(); ) {
                String columnName = (String) j.next();
                ValuePanel valuePanel = (ValuePanel) panelMap.get(columnName);
                if (valuePanel != null) {
                    if (groupPanel == null) {
                        groupPanel = new JPanel();
                        groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.Y_AXIS));
                        groupPanel.setBorder(BorderFactory.createTitledBorder(border, groupName, TitledBorder.LEFT,
                                TitledBorder.TOP, font, Color.BLACK));
                        getPanel().add(groupPanel);
                        groupPanel.add(makeLabelPanel());
                    }

                    groupPanel.add(valuePanel);
                    columnsFound.add(columnName);
                }
            }
        }

        /*
         * Remove the valuePanels that have been added to the UI from our
         * panelMap
         */
        for (Iterator i = columnsFound.iterator(); i.hasNext(); ) {
            String columnName = (String) i.next();
            panelMap.remove(columnName);
        }

        /*
         * Add any remaining valuepanels
         */
        if (panelMap.size() > 0) {
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            p.setBorder(BorderFactory.createTitledBorder(border, "Miscellaneous", TitledBorder.LEFT, TitledBorder.TOP,
                    font, Color.BLACK));
            getPanel().add(p);
            p.add(makeLabelPanel());
            List columnNames = new ArrayList(panelMap.keySet());
            Collections.sort(columnNames);

            for (Iterator i = columnNames.iterator(); i.hasNext(); ) {
                String columName = (String) i.next();
                ValuePanel valuePanel = (ValuePanel) panelMap.get(columName);
                p.add(valuePanel);
            }
        }
    }

    /**
     * Retrieve the metadata from the Annotation view. This is a Map<String,String> where key = columnName, value = data type.
     * @return
     */
    private Map getMetadata() {
        if (metadata == null) {
            try {
                metadata = queryDAO.getMetaData();
            }
            catch (Exception e) {
                log.error("Failed to lookup metadata from database", e);
                metadata = new HashMap();
            }
        }

        return metadata;
    }

    private JPanel getPanel() {
        if (panel == null) {
            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            addGroupPanels();
        }

        return panel;
    }

    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane();
            scrollPane.setViewportView(getPanel());
        }

        return scrollPane;
    }

    private ValuePanel getValuePanel(final String name, final String type) {
        if (log.isDebugEnabled()) {
            log.debug("Creating a ValuePanel for " + name + " [" + type + "]");
        }

        ValuePanel valuePanel = null;

        if (type.equals("java.lang.String")) {
            try {

                // values = AnnotationBeanDAO.getUniqueValuesByColumn(name);
                if (constraintNames.contains(name)) {

                    /*
                     * List values = (List) Worker.post(new Task() {
                     *
                     *   public Object run() throws Exception {
                     *       return AnnotationBeanDAO.getUniqueValuesByColumn(name);
                     *   }
                     *
                     * });
                     * valuePanel = new StringValuePanel(name, values);
                     */

                    //valuePanel = new StringLikeValuePanel(name);
                    valuePanel = new AdvancedStringValuePanel(name, queryDAO);
                }
                else {
                    valuePanel = new AdvancedStringValuePanel(name, queryDAO);
                }
            }
            catch (Exception e) {
                log.error("Failed to create ValuePanel for " + name, e);
            }
        }
        else if (type.equals("java.sql.Timestamp")) {
            valuePanel = new JXDateValuePanel(name, queryDAO);
        }
        else if (type.equals("java.lang.Boolean")) {
            valuePanel = new BooleanValuePanel(name);
        }
        else {
            valuePanel = new NumberValuePanel(name, queryDAO);
        }

        if (DEFAULT_RETURNS.contains(name)) {
            valuePanel.getReturnCheckBox().setSelected(true);
        }

        return valuePanel;
    }

    /**
     *
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public Collection getValuePanels() {
        if (valuePanels == null) {
            valuePanels = new ArrayList();
            Map metaData = getMetadata();

            /*
             * We dynamically determine what panels to add from the columns found in
             * the Annotation view in the database. We're not showing any columns that
             * are foreign keys though. Foreign keys will end with "ID_FK"
             */
            if (metaData != null) {
                Set keySet = metaData.keySet();
                for (Iterator i = keySet.iterator(); i.hasNext(); ) {
                    String columnName = (String) i.next();
                    if (columnName.toUpperCase().indexOf("ID_FK") < 0) {
                        ValuePanel valuePanel = null;
                        try {
                            valuePanel = getValuePanel(columnName, (String) metaData.get(columnName));
                        }
                        catch (RuntimeException e) {
                            log.warn("Failed to create a ValuePanel for " + columnName, e);
                        }

                        if (valuePanel != null) {
                            valuePanels.add(valuePanel);
                        }
                    }
                }
            }
        }

        return Collections.unmodifiableCollection(valuePanels);
    }

    private void initialize() {
        this.setLayout(new BorderLayout(0, 0));
        this.add(getScrollPane(), BorderLayout.CENTER);

        /*
         * Share the action Maps
         */
        ActionMap actionMap = getActionMap();
        Dispatcher dispatcher = Dispatcher.getDispatcher(App.class);
        App queryApp = (App) dispatcher.getValueObject();
        if (queryApp != null) {
            actionMap.setParent(queryApp.getActionMap());
        }

        /*
         * We add this to the ActionMap. It can be called by the resetAction in
         * the QueryFrame.
         */
        actionMap.put("RESET_RefineSearchPanel", new ActionAdapter() {

            private static final long serialVersionUID = -1644037821540418694L;

            public void doAction() {
                reset();
            }
        });
    }

    private JPanel makeLabelPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(Box.createHorizontalStrut(5));
        JLabel m1 = new JLabel();
        m1.setText("return");
        m1.setFont(new Font("Dialog", Font.PLAIN, 9));
        p.add(m1);
        p.add(Box.createHorizontalStrut(5));
        JLabel m2 = new JLabel();
        m2.setText("constrain");
        m2.setFont(new Font("Dialog", Font.PLAIN, 9));
        p.add(m2);
        p.add(Box.createHorizontalGlue());
        return p;
    }

    /**
     * Resets the panel to it's default state
     *
     */
    public void reset() {
        JPanel myPanel = getPanel();

        /*
         * Loop through all subcomponents
         */
        Component[] c = myPanel.getComponents();
        for (int i = 0; i < c.length; i++) {

            /*
             * If it's a ValuePanel we want to turn off the constrain checkbox
             * and toggle the returns checkbox as appropriate.
             */
            if (c[i] instanceof ValuePanel) {
                ValuePanel valuePanel = (ValuePanel) c[i];
                valuePanel.getConstrainCheckBox().setSelected(false);
                String name = valuePanel.getName();
                boolean selected = false;
                if (DEFAULT_RETURNS.contains(name)) {
                    selected = true;
                }

                valuePanel.getReturnCheckBox().setSelected(selected);
            }
        }
    }
}
