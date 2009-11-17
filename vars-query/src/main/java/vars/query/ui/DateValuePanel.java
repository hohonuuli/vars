/*
 * @(#)DateValuePanel.java   2009.11.16 at 08:48:35 PST
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import mseries.Calendar.MDateChanger;
import mseries.Calendar.MDefaultPullDownConstraints;
import mseries.ui.MChangeEvent;
import mseries.ui.MChangeListener;
import mseries.ui.MDateEntryField;
import org.bushe.swing.event.EventBus;
import org.mbari.sql.QueryResults;
import org.mbari.swing.JSimpleButton;
import org.mbari.swing.SpinningDial;
import org.mbari.swingworker.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.query.SpecialQueryDAO;

/**
 * @author Brian Schlining
 */
public class DateValuePanel extends ValuePanel {

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static {
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private final Logger log = LoggerFactory.getLogger(getClass());
    private JLabel maxLabel = null;
    private JLabel minLabel = null;
    private MDateEntryField maxEntryField;
    private MDateEntryField minEntryField;
    private final SpecialQueryDAO queryDAO;
    private JButton scanButton;

    /**
     * This is the default constructor
     *
     * @param name
     * @param queryDAO
     */
    public DateValuePanel(String name, SpecialQueryDAO queryDAO) {
        super(name);
        this.queryDAO = queryDAO;
        initialize();
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @return
     */
    MDateEntryField getMaxDateField() {
        if (maxEntryField == null) {
            maxEntryField = new MDateEntryField(dateFormat);

            MDefaultPullDownConstraints c = new MDefaultPullDownConstraints();

            c.firstDay = Calendar.MONDAY;
            c.changerStyle = MDateChanger.SPINNER;
            c.selectionClickCount = 1;
            maxEntryField.setConstraints(c);
            maxEntryField.setShowTodayButton(true, true);
            maxEntryField.setValue(new Date());

            /*
             * Turn on the contrain box if a value is selected
             */
            maxEntryField.addMChangeListener(new MChangeListener() {

                public void valueChanged(MChangeEvent event) {
                    getConstrainCheckBox().setSelected(true);
                }
            });
        }

        return maxEntryField;
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @return
     */
    MDateEntryField getMinDateField() {
        if (minEntryField == null) {
            minEntryField = new MDateEntryField(dateFormat);

            MDefaultPullDownConstraints c = new MDefaultPullDownConstraints();

            c.firstDay = Calendar.MONDAY;
            c.changerStyle = MDateChanger.SPINNER;
            c.selectionClickCount = 1;
            minEntryField.setConstraints(c);
            minEntryField.setShowTodayButton(true, true);
            minEntryField.setValue(QueryApp.MIN_RECORDED_DATE);

            /*
             * Turn on the contrain box if a value is selected
             */
            minEntryField.addMChangeListener(new MChangeListener() {

                public void valueChanged(MChangeEvent event) {
                    getConstrainCheckBox().setSelected(true);
                }
            });
        }

        return minEntryField;
    }



    /**
     *
     * @return
     */
    public String getSQL() {
        StringBuffer sb = new StringBuffer();

        try {
            if (getConstrainCheckBox().isSelected()) {
                String minDate = dateFormat.format(getMinDateField().getValue());
                String maxDate = dateFormat.format(getMaxDateField().getValue());

                sb.append(" ").append(getValueName()).append(" BETWEEN '");
                sb.append(minDate).append("' AND '").append(maxDate).append("'");
            }
        }
        catch (ParseException e) {

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
                                queryResults = queryDAO.executeQuery(sql);
                            }
                            catch (Exception e1) {
                                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR,
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

                            getMinDateField().setValue(min);
                            getMaxDateField().setValue(max);
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

    /**
     * This method initializes this
     *
     */
    private void initialize() {
        minLabel = new JLabel();
        maxLabel = new JLabel();
        minLabel.setText(" Min ");
        maxLabel.setText(" Max ");
        this.add(minLabel, null);
        this.add(getMinDateField());
        this.add(maxLabel, null);
        this.add(getMaxDateField());
        this.add(getScanButton());
    }
}
