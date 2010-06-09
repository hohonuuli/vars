package vars
import com.google.inject.Injector
import org.mbari.expd.jdbc.DAOFactoryImpl
import vars.annotation.ui.Lookup
import vars.annotation.ui.ToolBelt
import com.google.inject.Module
import com.google.inject.Binder
import vars.shared.InjectorModule
import org.mbari.expd.DAOFactory
import vars.integration.MergeStatusDAO
import org.mbari.vars.integration.MergeStatusDAOImpl
import vars.integration.MergeFunction
import org.mbari.vars.integration.MergeEXPDAnnotations
import com.google.inject.Guice
import org.mbari.expd.DiveDAO
import org.mbari.expd.jdbc.DiveDAOImpl
import org.mbari.expd.jdbc.ExpdModule

class ToolBox {

    def toolBelt
    final Injector injector
    
    def ToolBox() {
        injector = Guice.createInjector(new ScriptingModule())
        toolBelt = injector.getInstance(ToolBelt.getClass())
    }

    /**
     *
     * @return an instance of an EXPD DAOFactory
     */
    def getDaoFactory() {
        injector.getInstance(DAOFactory.getClass())
    }

    def getMergeStatusDAO() {
        injector.getInstance(MergeStatusDAO.getClass())
    }

}


class ScriptingModule implements Module {

    void configure(Binder binder) {
        binder.install(new InjectorModule('annotation-app'))
        binder.install(new ExpdModule())
        binder.bind(MergeStatusDAO.getClass()).to(MergeStatusDAOImpl.getClass())
    }

}