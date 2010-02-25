package vars.migrations

import org.mbari.sql.QueryFunction
import vars.annotation.jpa.VideoArchiveImpl
import vars.annotation.jpa.VideoFrameImpl
import vars.annotation.jpa.ObservationImpl
import vars.annotation.jpa.AssociationImpl
import vars.annotation.jpa.CameraDataImpl
import vars.annotation.jpa.PhysicalDataImpl
import vars.annotation.jpa.CameraDeploymentImpl

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Feb 24, 2010
 * Time: 3:45:31 PM
 * To change this template use File | Settings | File Templates.
 */
class DestroyDuplicateFKFunction {



    private final handler = {rs ->
        def dups = []
        while(rs.next()) {
            dups << rs.getLong(1)
        }
        return dups
    } as QueryFunction


    private final toolBox = new vars.ToolBox()

    private final dataMap = [(VideoArchiveImpl.class): new KeyMapper("VideoArchive", "VideoArchiveSetID_FK"),
            (VideoFrameImpl.class): new KeyMapper("VideoFrame", "VideoArchiveID_FK"),
            (ObservationImpl.class): new KeyMapper("Observation", "VideoFrameID_FK"),
            (AssociationImpl.class): new KeyMapper("Association", "ObservationID_FK"),
            (CameraDataImpl.class): new KeyMapper("CameraData", "VideoFrameID_FK"),
            (PhysicalDataImpl.class): new KeyMapper("PhysicalData", "VideoFrameID_FK"),
            (CameraDeploymentImpl.class): new KeyMapper("CameraPlatformDeployment", "VideoArchiveSetID_FK")
    ]

    void apply() {
        def dao = toolBox.toolBelt.annotationDAOFactory.newDAO()
        dao.startTransaction()
        dataMap.each { clazz, keyMapper ->
            def duplicateFKs = findDuplicateForeignKeys(keyMapper)
            duplicateFKs.each { fk ->

                def ids = findPrimaryKeysForForeignKey(keyMapper, fk)

                if (ids.size() > 1) {
                    ids[1..-1].each { id ->
                        def obj = dao.findByPrimaryKey(clazz, id as Long)
                        dao.remove(obj)
                    }
                }
            }
        }
        dao.endTransaction()
    }

    /**
     * Find a list of each VideoArchive.name that exists more than once in the annotation
     *  datbase
     *
     * @return A List of duplicate names
     */
    def findDuplicateForeignKeys(keyMapper) {

        return toolBox.toolBelt.annotationPersistenceService.executeQueryFunction("""
SELECT
    ${keyMapper.column}, count(*) as counter
FROM
    ${keyMapper.table}
GROUP BY
    ${keyMapper.column}
HAVING COUNT(*) > 1
        """, handler)
    }


    /**
     * Find all the id's for the given foreign key 
     */
    def findPrimaryKeysForForeignKey(keyMapper, fk) {
        return toolBox.toolBelt.annotationPersistenceService.executeQueryFunction("""
SELECT
    id
FROM
    ${keyMapper.table}
WHERE
    ${keyMapper.column} = ${fk}
ORDER BY
    id
     """, handler)
    }

    /**
     * for holding values need to search for duplicate foreign keys
     */
    private class KeyMapper {
        def table
        def column

        def KeyMapper(table, column) {
            this.table = table
            this.column = column
        }
    }
}
