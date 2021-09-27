package vars.migrations

import vars.annotation.jpa.CameraDeploymentImpl
import vars.annotation.jpa.PhysicalDataImpl
import vars.annotation.jpa.CameraDataImpl
import vars.annotation.jpa.AssociationImpl
import vars.annotation.jpa.ObservationImpl
import vars.annotation.jpa.VideoFrameImpl
import vars.annotation.jpa.VideoArchiveImpl
import org.slf4j.LoggerFactory
import mbarix4j.sql.QueryFunction
import vars.annotation.jpa.VideoArchiveSetImpl

/**
 * This class is designed to find foriegn key values that point to primary keys
 * that don't exist.
 */
class FixFKFunction {

    final log = LoggerFactory.getLogger(getClass())

    private final toolBox = new vars.ToolBox()

    private final handler = {rs ->
        def values = []
        while(rs.next()) {
            values << rs.getLong(1)
        }
        return values
    } as QueryFunction

    private final dataMap = [
            (VideoArchiveSetImpl.class) : new TableColumn("VideoArchiveSet", null),
            (VideoArchiveImpl.class): new TableColumn("VideoArchive", "VideoArchiveSetID_FK"),
            (VideoFrameImpl.class): new TableColumn("VideoFrame", "VideoArchiveID_FK"),
            (ObservationImpl.class): new TableColumn("Observation", "VideoFrameID_FK"),
            (AssociationImpl.class): new TableColumn("Association", "ObservationID_FK"),
            (CameraDataImpl.class): new TableColumn("CameraData", "VideoFrameID_FK"),
            (PhysicalDataImpl.class): new TableColumn("PhysicalData", "VideoFrameID_FK"),
            (CameraDeploymentImpl.class): new TableColumn("CameraPlatformDeployment", "VideoArchiveSetID_FK")
    ]

    private final parentChildMap = [
            (VideoArchiveSetImpl.class): [VideoArchiveImpl.class, CameraDeploymentImpl.class],
            (VideoArchiveImpl.class): [VideoFrameImpl.class],
            (VideoFrameImpl.class): [ObservationImpl.class, PhysicalDataImpl.class, CameraDataImpl.class],
            (ObservationImpl.class): AssociationImpl.class]

    void apply() {
        parentChildMap.each { parentClass, childClasses ->
            childClasses.each { childClass ->
                log.debug("Searching for disfunctional relationships between ${childClass} and it's owning ${parentClass}")
                def ids = findBadRelation(parentClass, childClass)
                dropBadRelation(childClass, ids)
            }
        }
    }

    /**
     * @param parent The parent class object (e.g. VideoArchiveSetImpl.class)
     * @param child the child class object (e.g VideoArchiveImpl.class)
     */
    def findBadRelation(parentClass, childClass) {
        def parent = dataMap[parentClass]
        def child = dataMap[childClass]
        def sql = """
SELECT
    id
FROM
    ${child.table}
WHERE
    ${child.column} NOT IN (
    SELECT
        id
    FROM
        ${parent.table}
)
        """
        return toolBox.toolBelt.annotationPersistenceService.executeQueryFunction(sql, handler)
    }

    void dropBadRelation(childClass, problemPrimaryKeys) {

        log.debug("Dropping ${problemPrimaryKeys.size()} instances of ${childClass} from the database")
        def dao = toolBox.toolBelt.annotationDAOFactory.newDAO()
        dao.startTransaction()
        problemPrimaryKeys.each { id ->
            def obj = dao.findByPrimaryKey(childClass, id as Long)
            dao.remove(obj)
        }
        dao.endTransaction()
    }


}
