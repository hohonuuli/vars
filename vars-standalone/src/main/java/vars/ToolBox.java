package vars;


import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Scopes;
import org.mbari.expd.DAOFactory;
import org.mbari.expd.jdbc.ExpdModule;
import org.mbari.vars.integration.MergeHistoryDAOImpl;
import org.mbari.vars.integration.MergeStatusDAOImpl;
import vars.annotation.ui.*;
import vars.annotation.ui.ToolBelt;
import vars.integration.MergeHistoryDAO;
import vars.integration.MergeStatusDAO;
import vars.shared.InjectorModule;

import java.util.prefs.Preferences;

/**
 * @author Brian Schlining
 * @since 2015-07-16T10:03:00
 */
public class ToolBox {

    private final vars.annotation.ui.ToolBelt toolBelt;
    private final Injector injector;

    public ToolBox() {
        injector = Guice.createInjector(new ScriptingModule());
        toolBelt = injector.getInstance(ToolBelt.class);
    }

    public Injector getInjector() {
        return injector;
    }

    public vars.annotation.ui.ToolBelt getToolBelt() {
        return toolBelt;
    }

    /**
     *
     * @return an instance of an EXPD DAOFactory
     */
    public DAOFactory getDaoFactory() {
        return injector.getInstance(DAOFactory.class);
    }


    public MergeHistoryDAO getMergeHistoryDAO() {
        return injector.getInstance(MergeHistoryDAO.class);
    }

    public MergeStatusDAO getMergeStatusDAO() {
        return injector.getInstance(MergeStatusDAO.class);
    }

    public Preferences getPreferences(String username) {
        VarsUserPreferencesFactory factory = injector.getInstance(VarsUserPreferencesFactory.class);
        return factory.userRoot(username);
    }
}

class ScriptingModule implements Module {

    public void configure(Binder binder) {
        binder.install(new InjectorModule("annotation-app"));
        binder.install(new ExpdModule());
        binder.bind(MergeStatusDAO.class).to(MergeStatusDAOImpl.class).in(Scopes.SINGLETON);
        binder.bind(MergeHistoryDAO.class).to(MergeHistoryDAOImpl.class).in(Scopes.SINGLETON);
    }
}
