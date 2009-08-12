package vars

import groovy.inspect.Inspector
import vars.jpa.JPAEntity
import org.slf4j.LoggerFactory

/**
 * Category for mixing in a default to string for objects. This implementation is
 * particulary fast due to lots of introspection.
 */
public class EntityToStringCategory {

    static log = LoggerFactory.getLogger(EntityToStringCategory.class)

    static String basicToString(JPAEntity obj, def propNames) {

        //def f = obj.getClass().declaredFields
        //f.each { log.debug(it.name) }
        //def propNames = obj.getClass().declaredFields.findAll { it.name.startsWith("PROP_")}.collect { it.get(obj) }
        def n = propNames.size() - 1

        String s = "${obj.getClass().simpleName} ([id=${obj.id}] "

        propNames.eachWithIndex { prop , idx ->
            s += "${prop}="
            try {
                def value = obj."$prop"?.toString() // TODO add iso8601 formatting for dates
                s += value
                if (idx < n) {
                    s += ", "
                }
            }
            catch (Exception e) {
                log.warn("${obj.getClass()} does not contain property '${prop}'") 
            }
        }

        s += ")"

        return s

    }
}

