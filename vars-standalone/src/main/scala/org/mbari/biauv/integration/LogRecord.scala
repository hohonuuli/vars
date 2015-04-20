package org.mbari.biauv.integration

import scala.beans.BeanProperty

/**
 *  Describes the record being stored. Also includes a reference to the data
 * 
 * @author Brian Schlining
 * @since Sep 7, 2010
 */
class LogRecord(@BeanProperty val format: String, @BeanProperty val shortName: String,
                @BeanProperty val longName: String, @BeanProperty val units: String) {
    @BeanProperty var data: List[AnyVal] = Nil

    override def toString = {
        "LogRecord [format=" + format + ", shortName=" + shortName + ", longName=" + longName +
                ", units=" + units + "]"
    }
}
