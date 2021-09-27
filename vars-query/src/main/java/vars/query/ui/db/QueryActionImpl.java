package vars.query.ui.db;

import mbarix4j.awt.event.ActionAdapter;
import mbarix4j.awt.event.ActionAdapter;
import org.mbari.util.ExceptionHandler;
import org.mbari.util.ExceptionHandlerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.query.QueryPersistenceService;
import vars.query.QueryResultsDecorator;
import vars.query.results.AssociationColumnRemappingDecorator;
import vars.query.results.CoalescingDecorator;
import vars.query.results.QueryResults;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Brian Schlining
 * @since Nov 10, 2010
 */
public class QueryActionImpl extends ActionAdapter implements QueryAction {

    private static final Logger log = LoggerFactory.getLogger(QueryAction.class);

    private PropertyChangeSupport changeSupport2 = new PropertyChangeSupport(this);


    private ExceptionHandlerSupport exceptionSupport = new ExceptionHandlerSupport();


    private volatile boolean finished;


    private final QueryExecutor queryExecutor;

    private volatile QueryResults queryResults;
    private final QueryResultsDecorator queryResultsDecorator;

    private boolean showBasicPhylogeny;
    private boolean showFullPhylogeny;
    private boolean showHierarchy;
    private boolean showAssociationPerColumn;

    private final String coalesceKey;

    private Thread thread;

    /**
     * Constructor for the QueryAction object
     *
     * @param  queryExecutor Description of the Parameter
     * @param queryPersistenceService
     */
    public QueryActionImpl(final QueryExecutor queryExecutor, QueryPersistenceService queryPersistenceService) {
        this(queryExecutor, queryPersistenceService, false, false, false, false);
    }

    /**
     * Constructs ...
     *
     * @param queryExecutor
     * @param queryPersistenceService
     * @param showHiearchy
     * @param showBasicPhylogeny
     * @param showFullPhylogeny
     */
    public QueryActionImpl(final QueryExecutor queryExecutor, QueryPersistenceService queryPersistenceService,
            boolean showHiearchy, boolean showBasicPhylogeny, boolean showFullPhylogeny, boolean showAssociationPerColumn) {
        this.queryExecutor = queryExecutor;
        this.queryResultsDecorator = new QueryResultsDecorator(queryPersistenceService);
        this.showHierarchy = showHiearchy;
        this.showBasicPhylogeny = showBasicPhylogeny;
        this.showFullPhylogeny = showFullPhylogeny;
        this.showAssociationPerColumn = showAssociationPerColumn;

        ResourceBundle bundle = ResourceBundle.getBundle("query-app", Locale.US);
        coalesceKey = bundle.getString("queryresults.coalesce.key");
    }

    /**
     * @param  eh
     */
    public synchronized void addExceptionHandler(ExceptionHandler eh) {
        exceptionSupport.addExceptionHandler(eh);
    }

    /**
     *  Adds a feature to the PropertyChangeListener attribute of the QueryAction object
     *
     * @param  listener The feature to be added to the PropertyChangeListener attribute
     */
    @Override
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport2.addPropertyChangeListener(listener);
    }

    /**
     * @param  propertyName
     * @param  listener
     */
    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        changeSupport2.addPropertyChangeListener(propertyName, listener);
    }

    /**
     */
    public void cancel() {
        if ((thread != null) && thread.isAlive()) {
            thread.interrupt();

            if (!SwingUtilities.isEventDispatchThread()) {
                Thread.currentThread().interrupt();
            }

            setFinished(true);
        }
    }

    /**
     *  Description of the Method
     */
    public void doAction() {

        /*
         * We execute the results in a seperate thread. To get the results when
         * it's completed, add a  propertychange listener to 'queryResults'
         */
        Runnable worker = new Runnable() {

            public void run() {
                QueryResults queryResults = null;
                try {
                    queryResults = queryExecutor.query();

                    if (showAssociationPerColumn) {
                        queryResults = AssociationColumnRemappingDecorator.apply(queryResults);
                    }

                    queryResults = CoalescingDecorator.coalesce(queryResults, coalesceKey);

                    if (showHierarchy) {
                        queryResults = queryResultsDecorator.addHierarchy(queryResults);
                    }

                    // Either show Full or Basic phylogeny. Basic is a subset of full
                    // so we'll check to see if full is selected first.
                    if (showFullPhylogeny) {
                        queryResults = queryResultsDecorator.addFullPhylogeny(queryResults);
                        queryResults = QueryResultsDecorator.dropEmptyColumns(queryResults);
                    }
                    else if (showBasicPhylogeny) {
                        queryResults = queryResultsDecorator.addBasicPhylogeny(queryResults);
                    }

                    if (log.isDebugEnabled()) {
                        log.debug(queryResults.toString());
                    }
                }
                catch (Exception e) {
                    exceptionSupport.handle(e);
                }

                setQueryResults(queryResults);
                setFinished(true);
            }
        };
        thread = new Thread(worker, QueryActionImpl.this.getClass().getName() + "-" + System.currentTimeMillis());
        thread.start();
    }

    /**
         * @return   Returns the queryResults.
         */
    public synchronized QueryResults getQueryResults() {
        return queryResults;
    }

    public String getSQL() {
        return queryExecutor.getSQL();
    }

    /**
     * @return  Returns the finished.
     */
    public synchronized boolean isFinished() {
        return finished;
    }

    /**
     * @param  eh
     */
    public synchronized void removeExceptionHandler(ExceptionHandler eh) {
        exceptionSupport.removeExceptionHandler(eh);
    }

    /**
     *  Description of the Method
     *
     * @param  listener Description of the Parameter
     */
    @Override
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport2.removePropertyChangeListener(listener);
    }

    /**
     * @param  propertyName
     * @param  listener
     */
    public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        changeSupport2.removePropertyChangeListener(propertyName, listener);
    }

    /**
         * @param  complete
         */
    synchronized void setFinished(final boolean complete) {
        boolean oldValue = this.finished;
        boolean newValue = complete;
        this.finished = complete;
        changeSupport2.firePropertyChange("finished", oldValue, newValue);
    }

    /**
         * @param queryResults  The queryResults to set.
         */
    synchronized void setQueryResults(QueryResults queryResults) {
        QueryResults oldValue = this.queryResults;
        QueryResults newValue = queryResults;
        this.queryResults = queryResults;
        changeSupport2.firePropertyChange("queryResults", oldValue, newValue);
    }
}
