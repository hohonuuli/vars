package vars.migrations

import org.mbari.sql.QueryFunction
import vars.annotation.jpa.VideoArchiveImpl
import vars.annotation.jpa.VideoFrameImpl
import vars.annotation.jpa.ObservationImpl
import vars.annotation.jpa.AssociationImpl
import vars.annotation.jpa.CameraDataImpl
import vars.annotation.jpa.PhysicalDataImpl
import vars.annotation.jpa.CameraDeploymentImpl
import org.slf4j.LoggerFactory

/**
 * Code for removing table rows that contain duplicate foreign keys in a 1-1 relationship.
 * The earliest row (determined by the lowest id (primary key)) is kept, the others are
 * deleted.
 */
class DestroyDuplicateFKFunction {

    final log = LoggerFactory.getLogger(getClass())

    private final handler = {rs ->
        def values = []
        while(rs.next()) {
            values << rs.getLong(1)
        }
        return values
    } as QueryFunction


    private final toolBox = new vars.ToolBox()

    private final dataMap = [
            (CameraDataImpl.class): new TableColumn("CameraData", "VideoFrameID_FK"),
            (PhysicalDataImpl.class): new TableColumn("PhysicalData", "VideoFrameID_FK")
    ]

    void apply() {
        def dao = toolBox.toolBelt.annotationDAOFactory.newDAO()
        dao.startTransaction()
        dataMap.each { clazz, tableColumn ->
            def duplicateFKs = findDuplicateForeignKeys(tableColumn)
            log.info("Found ${duplicateFKs.size()} duplicated foreign keys for ${tableColumn.table}.${tableColumn.column}")
            def n = 0
            duplicateFKs.each { fk ->

                def ids = findPrimaryKeysForForeignKey(tableColumn, fk)

                if (ids.size() > 1) {
                    ids[1..-1].each { id ->
                        def obj = dao.findByPrimaryKey(clazz, id as Long)
                        dao.remove(obj)
                        n++
                    }
                }
            }

            log.info("Deleted ${n} ${clazz} objects")
        }
        dao.endTransaction()

    }

    /**
     * Find a list of each VideoArchive.name that exists more than once in the annotation
     *  datbase
     *
     * @return A List of duplicate names
     */
    def findDuplicateForeignKeys(tableColumn) {

        return toolBox.toolBelt.annotationPersistenceService.executeQueryFunction("""
SELECT
    ${tableColumn.column}, count(*) as counter
FROM
    ${tableColumn.table}
GROUP BY
    ${tableColumn.column}
HAVING COUNT(*) > 1
        """, handler)
    }


    /**
     * Find all the id's for the given foreign key 
     */
    def findPrimaryKeysForForeignKey(tableColumn, fk) {
        return toolBox.toolBelt.annotationPersistenceService.executeQueryFunction("""
SELECT
    id
FROM
    ${tableColumn.table}
WHERE
    ${tableColumn.column} = ${fk}
ORDER BY
    id
     """, handler)
    }

}
