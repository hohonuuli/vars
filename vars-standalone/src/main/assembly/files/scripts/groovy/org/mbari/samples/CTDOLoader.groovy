package org.mbari.samples

import org.mbari.expd.jdbc.BaseDAOImpl
import org.mbari.sql.QueryableImpl
import org.mbari.sql.QueryFunction
import org.slf4j.LoggerFactory
import vars.ToolBox

class SamplingEvent {

    def SamplingEvent(id, date, rov) {
        this.id = id;
        this.epochSeconds = date;
        setRov(rov);
    }
/**
     * The sample collection event unique identifier
     */
    final int id

    /**
     * Time of the collection event
     */
    final long epochSeconds

    /**
     * The name of the rov that did the collecting. This should be the full name
     * like 'Ventana', 'Tiburon'
     */
    final String rov

    def setRov(String name) {

        if (name.equalsIgnoreCase('Ventana')) {
            name = 'vtna'
        }
        else if (name.equalsIgnoreCase('Tiburon')) {
            name = 'tibr'
        }
        else if (name.equalsIgnoreCase('DocRicketts')) {
            name = 'docr'
        }
        else {
            name = 'none'
        }
        this.rov = name
    }

    String toString() {
        return "${getClass()} [id=$id, epochSeconds=${epochSeconds}, rov=${rov}]"
    }

}

/**
 *
 * @author Brian Schlining
 * @since Aug 30, 2010
 */
class CTDOLoader {

    final toolBox = new ToolBox()
    final log = LoggerFactory.getLogger(getClass())
    final samplesDatabase = new DatabaseUtility().samplesDatabase

    final ctdDatabase = toolBox.getDaoFactory().newCtdDatumDAO()
    final diveDatabase = toolBox.getDaoFactory().newDiveDAO()

    def findSamplingEvents() {

        def sql = """
            SELECT DISTINCT
                CollectionEventID,
                DateDiff(ss,'01/01/70',CollectionEventDTG) AS EpochSeconds,
                ROVName
             FROM
                CollectionEvent AS CE RIGHT OUTER JOIN
                CollectionGroup AS CG ON CE.CollectionGroupID = CG.CollectionGroupID
            WHERE
                (CollectionEventDTG IS NOT NULL) AND
                (ROVName IS NOT NULL) AND
                (CTDLookupStatus > 0) AND
                (CollectionEventDTG > '01/01/70')
            ORDER BY
                EpochSeconds
        """.stripIndent()

        // Function that converts a result set to a list of SamplingEvents
        def handler = {rs ->
            def values = []
            while(rs.next()) {
                values << new SamplingEvent(rs.getInt(1), rs.getInt(2), rs.getString(3))
            }
            return values
        } as QueryFunction

        return samplesDatabase.executeQueryFunction(sql, handler)

    }

    def load() {
        // List of sample collection events needing CTD data
        def events = findSamplingEvents()

        log.info("Updating ${events.size()} sampling events")

        events.each { SamplingEvent e ->

            def sampleDate = new Date(e.epochSeconds * 1000)

            def dive = diveDatabase.findByPlatformAndDate(e.rov, sampleDate)

            if (dive) {
                def ctdData = ctdDatabase.fetchCtdData(dive, [sampleDate], 7.5);
                if (ctdData) {
                    def datum = ctdData[0]
                    def uSql = """
                        UPDATE
                            CollectionEvent
                        SET
                            Salinity = ${datum.salinity ?: 'NULL'},
                            Temperature = ${datum.temperature ?: 'NULL'},
                            CTDPressure = ${datum.pressure ?: 'NULL'},
                            DissolvedOxygen = ${datum.oxygen ?: 'NULL'},
                            CTDLookupStatus = 0
                        WHERE
                        CollectionEventID = ${e.id}
                    """.stripIndent()
                    samplesDatabase.executeUpdate(uSql)
                }
                else {
                    log.info("No CTD data was found for ${e}")
                }
            }
            else {
               log.info("No dive was found for ${e}");
            }


        }


    }

}

