package vars
import com.google.inject.Injector
import com.google.inject.Scopes
import org.mbari.vars.integration.MergeHistoryDAOImpl
import vars.annotation.ui.ToolBelt
import com.google.inject.Module
import com.google.inject.Binder
import vars.integration.MergeHistoryDAO
import vars.shared.InjectorModule
import org.mbari.expd.DAOFactory
import vars.integration.MergeStatusDAO
import org.mbari.vars.integration.MergeStatusDAOImpl

import com.google.inject.Guice

import org.mbari.expd.jdbc.ExpdModule

class ToolBox {

    def toolBelt
    final Injector injector
    
    def ToolBox() {
        injector = Guice.createInjector(new ScriptingModule())
        toolBelt = injector.getInstance(ToolBelt.class)
    }

    /**
     *
     * @return an instance of an EXPD DAOFactory
     */
    def getDaoFactory() {
        injector.getInstance(DAOFactory.class)
    }


    def getMergeHistoryDAO() {
        injector.getInstance(MergeHistoryDAO.class)
    }

    def getMergeStatusDAO() {
        injector.getInstance(MergeStatusDAO.class)
    }

    def getPreferences(String username) {
        def factory = injector.getInstance(VarsUserPreferencesFactory.class);
        return factory.userRoot(username);
    }

}


class ScriptingModule implements Module {

    void configure(Binder binder) {
        binder.install(new InjectorModule('annotation-app'))
        binder.install(new ExpdModule())
        binder.bind(MergeStatusDAO.class).to(MergeStatusDAOImpl.class).in(Scopes.SINGLETON)
        binder.bind(MergeHistoryDAO.class).to(MergeHistoryDAOImpl.class).in(Scopes.SINGLETON)
    }

}