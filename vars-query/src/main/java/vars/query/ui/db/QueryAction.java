package vars.query.ui.db;

import org.mbari.awt.event.IAction;
import org.mbari.util.ExceptionHandler;
import vars.query.results.QueryResults;

import java.beans.PropertyChangeListener;

/**
 * @author Brian Schlining
 * @since Nov 10, 2010
 */
public interface QueryAction extends IAction {

    void addExceptionHandler(ExceptionHandler eh);

    void addPropertyChangeListener(PropertyChangeListener listener);

    void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    void cancel();

    QueryResults getQueryResults();

    String getSQL();

    boolean isFinished();

    void removeExceptionHandler(ExceptionHandler eh);

    void removePropertyChangeListener(PropertyChangeListener listener);

    void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);
    

    
}
