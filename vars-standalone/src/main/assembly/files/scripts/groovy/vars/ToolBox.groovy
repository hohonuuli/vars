import com.google.inject.Injector
import vars.annotation.ui.Lookup
import vars.annotation.ui.ToolBelt

class ToolBox

    ToolBelt toolBelt
    Injector injector
    
    def ToolBox() {
        injector = Lookup.guiceInjectorDispatcher.valueObject
        toolBelt = injector.getInstance(ToolBelt.class)                           
    }

}