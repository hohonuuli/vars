package vars
import com.google.inject.Injector
import org.mbari.expd.jdbc.DAOFactoryImpl
import vars.annotation.ui.Lookup
import vars.annotation.ui.ToolBelt

class ToolBox {

    final toolBelt
    final injector
    final daoFactory
    
    def ToolBox() {
        injector = Lookup.guiceInjectorDispatcher.valueObject
        toolBelt = injector.getInstance(ToolBelt.class)
        daoFactory = new DAOFactoryImpl()
    }

}