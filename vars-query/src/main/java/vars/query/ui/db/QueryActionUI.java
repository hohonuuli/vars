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


package vars.query.ui.db;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.sql.QueryResults;
import org.mbari.util.Dispatcher;
import org.mbari.util.ExceptionHandler;
import vars.query.ui.App;
import vars.query.ui.QueryResultsFrame;
import vars.query.ui.StateLookup;

//~--- classes ----------------------------------------------------------------

/**
 * <p>Class that packages up the UI components needed to deal with a query
 * action. It does the following:
 * <ol>
 * <li>Shows a dialog that shows elapsed time and allows user to cancel the action</li>
 * <li>Displays the results when the query is completed</li>
 * <li>Shows an error dialog if the query fails</li>
 * </ol></p>
 *
 * @author Brian Schlining
 */
public class QueryActionUI {


    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private final QueryAction queryAction;

    private final PropertyChangeListener queryResultsListener = new QueryResultsListener();

    private final ExceptionHandler queryExceptionHandler = new QueryExceptionHandler();

    private final QueryActionDialog queryActionDialog = new QueryActionDialog();

    private final String databaseUrl;


    /**
     * Constructs ...
     *
     *
     * @param queryAction
     */
    public QueryActionUI(QueryAction queryAction, String databaseUrl) {
        this.queryAction = queryAction;
        this.databaseUrl = databaseUrl;
        initialize();
    }


    /**
     *
     */
    private void initialize() {
        /*
         * When the query is complete we want to show the results.
         * This propertychangeListener will display a frame of
         * results when the query is completed.
         */
        queryAction.addPropertyChangeListener(
                "queryResults", queryResultsListener);
        queryAction.addExceptionHandler(queryExceptionHandler);
        queryActionDialog.setVisible(true);
    }

    //~--- inner classes ------------------------------------------------------

    /**
	 * @author  brian
	 */
    private class QueryActionDialog extends JDialog {

        private JPanel buttonPanel;
        private JButton cancelButton;
        private JPanel mainPanel;
        private final Date startDate = new Date();
        private final DateFormat dateFormat = new SimpleDateFormat(
            "hh:mm:ss a z");
        private JLabel startLabel;

        /**
         * This timer updates the elapsed time.
         */
        private final Timer timer = new Timer(950, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JLabel label = getTimerLabel();
                long currentTime = System.currentTimeMillis();
                long startTime = startDate.getTime();
                long elapsedTime = currentTime - startTime;
                double minutes = elapsedTime / 1000D / 60D;
                int min = (int) Math.floor(minutes);
                int seconds = (int) Math.round((minutes - min) * 60D);
                label.setText(
                        "    Elapsed time: " + min + " minutes " + seconds +
                        " seconds");
            }

        });
        private JLabel timerLabel;
        private JLabel topLabel;

        /**
         * Constructs ...
         *
         */
        QueryActionDialog() {
            super(((App) Dispatcher.getDispatcher(App.class).getValueObject()).getQueryFrame());
            initialize();
            timer.start();
        }

        /**
		 * <p><!-- Method description --></p>
		 * @return
		 */
        private JPanel getButtonPanel() {
            if (buttonPanel == null) {
                buttonPanel = new JPanel();
                buttonPanel.setLayout(
                        new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
                buttonPanel.add(Box.createHorizontalGlue());
                buttonPanel.add(getCancelButton());
            }

            return buttonPanel;
        }

        private JButton getCancelButton() {
            if (cancelButton == null) {
                cancelButton = new JButton();
                cancelButton.setText("Cancel");
                cancelButton.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        queryAction.removePropertyChangeListener(
                                "queryResults", queryResultsListener);
                        queryAction.removeExceptionHandler(
                                queryExceptionHandler);
                        queryAction.cancel();
                        QueryActionDialog.this.dispose();
                    }

                });
            }

            return cancelButton;
        }


        private JPanel getMainPanel() {
            if (mainPanel == null) {
                mainPanel = new JPanel();
                mainPanel.setLayout(
                        new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
                mainPanel.add(getTopLabel());
                mainPanel.add(getStartLabel());
                mainPanel.add(getTimerLabel());
                mainPanel.add(getButtonPanel());
            }

            return mainPanel;
        }

        private JLabel getStartLabel() {
            if (startLabel == null) {
                startLabel = new JLabel("    Started at " +
                        dateFormat.format(startDate));
            }

            return startLabel;
        }

        private JLabel getTimerLabel() {
            if (timerLabel == null) {
                timerLabel = new JLabel();
                timerLabel.setText("    Elapsed time: 0 minutes  0 seconds");
            }

            return timerLabel;
        }

        private JLabel getTopLabel() {
            if (topLabel == null) {
                topLabel = new JLabel("Executing query");
            }

            return topLabel;
        }

        private void initialize() {
            this.getContentPane().add(getMainPanel());
            this.pack();
            // this.setSize(300, 120);
            this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            this.setLocationRelativeTo(null);
        }
    }


    /*
     * Cancel the query if an exception is thrown
     */
    private class QueryExceptionHandler extends ExceptionHandler {

        /**
         * Constructs ...
         *
         */
        public QueryExceptionHandler() {
            super(Exception.class);
        }

        /**
         *
         * @param e
         */
        protected void doAction(final Exception e) {
            log.info("Unable to complete query.", e);
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    queryActionDialog.dispose();
                    // Use EventBus to deal with error messages
                    EventBus.publish(StateLookup.TOPIC_NONFATAL_ERROR, e);
                }
            });

            if (!SwingUtilities.isEventDispatchThread()) {
                Thread.currentThread().interrupt();
            }
        }
    }


    /**
     * When the query is complete we want to show the results.
     * This propertychangeListener will display a frame of
     * results when the query is completed.
     */
    private class QueryResultsListener implements PropertyChangeListener {

        /**
         * <p><!-- Method description --></p>
         *
         *
         * @param evt
         */
        public void propertyChange(PropertyChangeEvent evt) {
            final QueryResults queryResults = (QueryResults) evt.getNewValue();
            if (queryResults != null) {
                log.info("Query has been completed");
                SwingUtilities.invokeLater(() -> {
                    queryActionDialog.dispose();
                    QueryResultsFrame f = new QueryResultsFrame(
                        queryResults, queryAction.getSQL(), databaseUrl);
                    f.pack();
                    f.setVisible(true);

                });
            }
        }
    }
}
