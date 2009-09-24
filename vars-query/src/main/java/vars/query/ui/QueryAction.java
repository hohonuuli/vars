/*
 * @(#)QueryAction.java   2009.09.23 at 10:47:25 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.query.ui;

import vars.query.IQueryable;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.SwingUtilities;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.sql.QueryResults;
import org.mbari.util.ExceptionHandler;
import org.mbari.util.ExceptionHandlerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.knowledgebase.ConceptDAO;
import vars.query.QueryResultsDecorator;

/**
 * <p>This action takes a SQL query (as a string) and an IQueryable object, executes
 * the query in a seperate thread and returns the results as a QueryResults object.
 * Here's an example of usage:</p>
 *
 * <pre>
 * // Create our IQueryable object to execute our query
 * IQueryable q = new IQueryable() {
 *     public QueryResults executeQuery(String query) {
 *         // Get a JDBC connection
 *         Connection c = ConnectionFactory.getConnection("vars");
 *         Statement stmt = conn.createStatement();
 *         ResultSet rs = stmt.executeQuery(query);
 *         QueryResults queryResults = new QueryResults(rs);
 *         s.close();
 *         c.close();
 *         return queryResults;
 *     }
 * };
 *
 * <strong>// Initialize our QueryAction
 * QueryAction a = new QueryAction("SELECT * from MyTable", q);</strong>
 *
 * // We'll add some exception handling just to notify us of an error. This is optional
 * a.addExceptionHandler(new ExceptionHandler() {
 *     public void doAction(Exception e) {
 *         e.printStackTrace();
 *     }
 * });
 *
 * <strong>// Since it's multithreaded we can't just call getQueryResults because the
 * // query may not be finished (Calling getQueryResults would return null). So we
 * // attach a PropertyChangeListener to handle post query work
 * a.addPropertyChangeListener("queryResults", new PropertyChangeListener() {
 *     public void propertyChange(PropertyChangeEvent evt) {
 *         System.out.println("Query is done");
 *         // Normally you would put save or display code here.
 *     }
 * });</strong>
 *
 * </pre>
 *
 *
 * @author  Brian Schlining
 * @version  $Id: QueryAction.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class QueryAction extends ActionAdapter {

    private static final long serialVersionUID = -7024848875942066527L;
    private static final Logger log = LoggerFactory.getLogger(QueryAction.class);

    /**
         * @uml.property  name="changeSupport"
         */
    private PropertyChangeSupport changeSupport2 = new PropertyChangeSupport(this);

    /**
         * @uml.property  name="exceptionSupport"
         * @uml.associationEnd  multiplicity="(1 1)"
         */
    private ExceptionHandlerSupport exceptionSupport = new ExceptionHandlerSupport();

    /**
         * @uml.property  name="finished"
         */
    private volatile boolean finished;

    /**
         * @uml.property  name="query"
         */
    private final String query;

    /**
         * @uml.property  name="queryResults"
         * @uml.associationEnd
         */
    private volatile QueryResults queryResults;
    private final QueryResultsDecorator queryResultsDecorator;

    /**
         * @uml.property  name="queryable"
         * @uml.associationEnd  multiplicity="(1 1)"
         */
    private final IQueryable queryable;
    private boolean showBasicPhylogeny;
    private boolean showFullPhylogeny;
    private boolean showHierarchy;

    /**
         * @uml.property  name="thread"
         */
    private Thread thread;

    /**
     * Constructor for the QueryAction object
     *
     * @param  query Description of the Parameter
     * @param  queryable Description of the Parameter
     * @param conceptDAO
     */
    public QueryAction(final String query, IQueryable queryable, ConceptDAO conceptDAO) {
        this(query, queryable, conceptDAO, false, false, false);
    }

    /**
     * Constructs ...
     *
     * @param query
     * @param queryable
     * @param conceptDAO
     * @param showHiearchy
     * @param showBasicPhylogeny
     * @param showFullPhylogeny
     */
    public QueryAction(final String query, IQueryable queryable, ConceptDAO conceptDAO, boolean showHiearchy,
                       boolean showBasicPhylogeny, boolean showFullPhylogeny) {
        this.query = query;
        this.queryable = queryable;
        this.queryResultsDecorator = new QueryResultsDecorator(conceptDAO);
        this.showHierarchy = showHiearchy;
        this.showBasicPhylogeny = showBasicPhylogeny;
        this.showFullPhylogeny = showFullPhylogeny;
    }

    /**
     * @param  eh
     */
    public synchronized void addExceptionHandler(ExceptionHandler eh) {
        exceptionSupport.addExceptionHandler(eh);
    }

    /*
     *  (non-Javadoc)
     *  @see javax.swing.AbstractAction#addPropertyChangeListener(java.beans.PropertyChangeListener)
     */

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
     * <p><!-- Method description --></p>
     *
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
                    queryResults = queryable.executeQuery(query);
                    queryResults.coalesce(query);

                    if (showHierarchy) {
                        queryResultsDecorator.addHierarchy(queryResults);
                    }

                    // Either show Full or Basic phylogeny. Basic is a subset of full
                    // so we'll check to see if full is selected first.
                    if (showFullPhylogeny) {
                        queryResultsDecorator.addFullPhylogeny(queryResults);
                        QueryResultsDecorator.dropEmptyColumns(queryResults);
                    }
                    else if (showBasicPhylogeny) {
                        queryResultsDecorator.addBasicPhylogeny(queryResults);
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
        thread = new Thread(worker, QueryAction.this.getClass().getName() + "-" + System.currentTimeMillis());
        thread.start();
    }

    /**
         * @return  Returns the query.
         * @uml.property  name="query"
         */
    public String getQuery() {
        return query;
    }

    /**
         * @return   Returns the queryResults.
         * @uml.property  name="queryResults"
         */
    public synchronized QueryResults getQueryResults() {
        return queryResults;
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

    /*
     *  (non-Javadoc)
     *  @see javax.swing.AbstractAction#removePropertyChangeListener(java.beans.PropertyChangeListener)
     */

    /**
     *  Description of the Method
     *
     * @param  listener Description of the Parameter
     */
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
         * @uml.property  name="finished"
         */
    synchronized void setFinished(final boolean complete) {
        boolean oldValue = this.finished;
        boolean newValue = complete;
        this.finished = complete;
        changeSupport2.firePropertyChange("finished", oldValue, newValue);
    }

    /**
         * @param queryResults  The queryResults to set.
         * @uml.property  name="queryResults"
         */
    synchronized void setQueryResults(QueryResults queryResults) {
        QueryResults oldValue = this.queryResults;
        QueryResults newValue = queryResults;
        this.queryResults = queryResults;
        changeSupport2.firePropertyChange("queryResults", oldValue, newValue);
    }
}
