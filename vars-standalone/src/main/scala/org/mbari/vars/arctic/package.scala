package org.mbari.vars

import java.util.Date

/**
 *
 *
 * @author Brian Schlining
 * @since 2015-02-25T17:24:00
 */
package object arctic {

  trait LogRecord {
    def salinity: Option[Float]
    def temperature: Option[Float]
    def depth: Option[Float]
    def latitude: Option[Double]
    def longitude: Option[Double]
    def gpsTime: String
  }

  case class RawLogRecord(salinity: Option[Float],
                          temperature: Option[Float],
                          depth: Option[Float],
                          latitude: Option[Double],
                          longitude: Option[Double],
                          gpsTime: String) extends LogRecord {

    def asFullLogRecord(gpsDate: Date): FullLogRecord = FullLogRecord(salinity,
        temperature, depth, latitude, longitude, gpsTime, gpsDate)

  }



  case class FullLogRecord(salinity: Option[Float],
                           temperature: Option[Float],
                           depth: Option[Float],
                           latitude: Option[Double],
                           longitude: Option[Double],
                           gpsTime: String,
                           gpsDate: Date) extends LogRecord

}
