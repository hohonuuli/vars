package vars.query.ui;

import org.bushe.swing.event.EventBus;
import org.jdesktop.swingx.JXDatePicker;
import org.mbari.sql.QueryResults;
import org.mbari.swing.JSimpleButton;
import org.mbari.swing.SpinningDial;
import org.mbari.swingworker.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.query.QueryPersistenceService;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Brian Schlining
 * @since 2013-07-18
 */
public class JXDateValuePanel extends ValuePanel {

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static {
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private final Logger log = LoggerFactory.getLogger(getClass());
    private JLabel maxLabel = null;
    private JLabel minLabel = null;
    private JXDatePicker maxEntryField;
    private JXDatePicker minEntryField;
    private final QueryPersistenceService queryPersistenceService;
    private JButton scanButton;
    private ActionListener constrainActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (JXDatePicker.COMMIT_KEY.equals(e.getActionCommand())) {
                getConstrainCheckBox().setSelected(true);
            }
        }
    };
    private PropertyChangeListener constrainPropertyListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("date".equals(evt.getPropertyName())) {
                getConstrainCheckBox().setSelected(true);
            }
        }
    };

    public JXDateValuePanel(String name, QueryPersistenceService queryDAO) {
        super(name);
        this.queryPersistenceService = queryDAO;
        initialize();
    }

    JXDatePicker getMaxDateField() {
        if (maxEntryField == null) {
            maxEntryField = new JXDatePicker(new Date());
            maxEntryField.setFormats(dateFormat);
            maxEntryField.addActionListener(constrainActionListener);
            maxEntryField.addPropertyChangeListener(constrainPropertyListener);
        }
        return maxEntryField;
    }

    JXDatePicker getMinDateField() {
        if (minEntryField == null) {
            minEntryField = new JXDatePicker(App.MIN_RECORDED_DATE);
            minEntryField.setFormats(dateFormat);
            minEntryField.addActionListener(constrainActionListener);
            minEntryField.addPropertyChangeListener(constrainPropertyListener);
        }
        return minEntryField;
    }


    @Override
    public String getSQL() {
        StringBuffer sb = new StringBuffer();

        try {
            if (getConstrainCheckBox().isSelected()) {
                String minDate = dateFormat.format(getMinDateField().getDate());
                String maxDate = dateFormat.format(getMaxDateField().getDate());

                sb.append(" ").append(getValueName()).append(" BETWEEN '");
                sb.append(minDate).append("' AND '").append(maxDate).append("'");
            }
        }
        catch (Exception e) {

            // NEED TO LOG
        }

        return sb.toString();
    }

    /**
     * The scan button scans for min and max values and sets them in the text fields.
     * @return The scanButton
     */
    private JButton getScanButton() {
        if (scanButton == null) {
            final ImageIcon icon = new ImageIcon(getClass().getResource("/images/vars/query/16px/refresh.png"));

            scanButton = new JSimpleButton(icon);
            scanButton.setToolTipText("Retrieve minimum and maximum " + getValueName() + " values from database");

                /*
                 * When the button is pressed it queries the database for the max and min values. When they are
                 * returned it sets them in the UI.
                 */
            scanButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {

                    final String sql = "SELECT MIN(" + getValueName() + ") AS minValue, MAX(" + getValueName() +
                            ") AS maxValue FROM Annotations WHERE " + getValueName() + " IS NOT NULL";

                        /*
                         * Execute the queries asynchronously so that the UI doesn't block. WHile executing display a
                         * wait indicator over the valuepanel so that no further actions can be taken on it.
                         */
                    scanButton.setIcon(new SpinningDial(16, 16));
                    getMinDateField().setEnabled(false);
                    getMaxDateField().setEnabled(false);
                    scanButton.setEnabled(false);

                    SwingWorker swingWorker = new SwingWorker() {

                        QueryResults queryResults = null;

                        @Override
                        protected Object doInBackground() throws Exception {
                            try {
                                queryResults = queryPersistenceService.executeQuery(sql);
                            }
                            catch (Exception e1) {
                                EventBus.publish(StateLookup.TOPIC_NONFATAL_ERROR,
                                        "An error occurred while executing the SQL statement: '" + sql + "'");
                                log.error("An error occurred while executing the SQL statement: '" + sql + "'", e1);
                            }

                            return queryResults;
                        }

                        @Override
                        protected void done() {

                            Date min = null;
                            Date max = null;

                            if ((queryResults != null) && (queryResults.rowCount() == 1)) {
                                List values = queryResults.getResults("minValue");

                                min = (Date) values.get(0);
                                values = queryResults.getResults("maxValue");
                                max = (Date) values.get(0);
                            }

                            getMinDateField().setDate(min);
                            getMaxDateField().setDate(max);
                            scanButton.setIcon(icon);
                            getMinDateField().setEnabled(true);
                            getMaxDateField().setEnabled(true);
                            scanButton.setEnabled(true);
                        }
                    };

                    swingWorker.execute();


                }

            });
        }

        return scanButton;
    }


    private void initialize() {
        minLabel = new JLabel();
        maxLabel = new JLabel();
        minLabel.setText(" Min ");
        maxLabel.setText(" Max ");
        this.add(minLabel, null);
        this.add(getMinDateField());
        this.add(maxLabel, null);
        this.add(getMaxDateField());
        this.add(Box.createHorizontalGlue());
        this.add(getScanButton());
    }
}
