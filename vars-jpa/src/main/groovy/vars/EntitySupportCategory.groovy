package vars

import groovy.inspect.Inspector
import vars.jpa.JPAEntity
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat

/**
 * Category for mixing in a default to string for objects. This implementation is
 * particulary fast due to lots of introspection.
 */
public class EntitySupportCategory {

    static final log = LoggerFactory.getLogger(EntitySupportCategory.class)
    static final dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    static {
        dateFormat.setTimeZone(TimeZone.getTimeZone('UTC'))
    }


    static String basicToString(JPAEntity obj, def propNames) {

        def n = propNames.size() - 1

        def s = new StringBuilder("${obj.getClass().simpleName} ([id=${obj.id}] ")

        propNames.eachWithIndex { prop , idx ->
            s << "${prop}="
            try {

                def p = obj[prop]
                def value = (p != null && p instanceof Date) ? dateFormat.format(p) : p?.toString()
                s << value
                if (idx < n) {
                    s << ", "
                }
            }
            catch (Exception e) {
                log.warn("${obj.getClass()} does not contain property '${prop}'") 
            }
        }

        s << ")"

        return s.toString()

    }

    static boolean equals(JPAEntity thisObj, JPAEntity thatObj, def propNames) {

        def isEqual = true

        if (thisObj.is(thatObj)) {
            // Do nothing isEqual is already true
            //isEqual = true
        }
        else if (!thatObj || thisObj.getClass() != thatObj.getClass()) {
            isEqual = false
        }
        else {
            for (prop in propNames) {
                def thisValue = thisObj[prop]
                def thatValue = thatObj[prop]
                if (thisValue ? !thisValue.equals(thatValue) : thatValue != null) {
                    isEqual = false
                    break
                }
            }
        }

        return isEqual
    }

    static int hashCode(JPAEntity obj, def propNames) {
        int result = 0
        for (prop in propNames) {
            def value = obj[prop]
            result = 31 * result + (value?.hashCode() ?: 0)
        }
        return result
    }

}

