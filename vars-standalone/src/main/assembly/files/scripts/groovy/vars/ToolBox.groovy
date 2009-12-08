package vars
import com.google.inject.Injector
import vars.annotation.ui.Lookup
import vars.annotation.ui.ToolBelt

class ToolBox {

    final toolBelt
    final injector
    
    def ToolBox() {
        injector = Lookup.guiceInjectorDispatcher.valueObject
        toolBelt = injector.getInstance(ToolBelt.class)                           
    }

}