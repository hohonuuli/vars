package vars;

//import vars.annotation.AnnotationDAOFactory;
//import vars.annotation.AnnotationPersistenceService;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Dec 26, 2009
 * Time: 5:57:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class MergeStatusDAO {

//    private final AnnotationDAOFactory annotationDAOFactory;
//    private final AnnotationPersistenceService annotationPersistenceService;
//
//    public MergeStatusDAO(AnnotationDAOFactory annotationDAOFactory, AnnotationPersistenceService annotationPersistenceService) {
//        this.annotationDAOFactory = annotationDAOFactory;
//        this.annotationPersistenceService = annotationPersistenceService;
//    }
//
//    public void insertOrUpdate(MergeStatus mergeStatus) {
//
//        annotationPersistenceService.
//
//    }


//    void update(EXPDMergeStatus expdMergeStatus) {
//
//       def id = annotationPersistenceService.executeQueryFunction("SELECT videoArchiveSetID_FK FROM EXPDMergeStatus " +
//                   "WHERE videoArchiveSetID_FK = ${expdMergeStatus.videoArchiveSetID_FK}",
//               new QueryFunction<Long>() {
//                 Object apply(ResultSet resultSet) {
//                   def id_ = null;
//                   if (resultSet.next()) {
//                       id_ = resultSet.getLong(1)
//                   }
//                   return id_
//                 }
//               });
//
//         def db = ObjectDAO.fetchDatabase()
//         db.begin()
//         def connection = db.getJdbcConnection()
//
//         // Query to see if an id exists. If so update, otherwise insert
//         def id = null
//         try {
//             def statement = connection.createStatement()
//             def sql = "SELECT videoArchiveSetID_FK FROM EXPDMergeStatus " +
//                     "WHERE videoArchiveSetID_FK = ${expdMergeStatus.videoArchiveSetID_FK}"
//             def resultSet = statement.executeQuery(sql)
//             if (resultSet.next()) {
//                 id = resultSet.getLong(1)
//             }
//             resultSet.close()
//         }
//         catch (Exception e) {
//             log.error("Failed to lookup primary key for EXPDMergeStatus", e)
//         }
//
//         def sql = null
//         if (id) {
//             sql = "UPDATE EXPDMergeStatus SET " +
//                     "MergeDate = CONVERT(DATETIME, '${dateFormat.format(expdMergeStatus.mergeDate)}', 120), " +
//                     "IsNavigationEdited = ${expdMergeStatus.navigationEdited}, " +
//                     "StatusMessage = '${expdMergeStatus.statusMessage}', " +
//                     "VideoFrameCount = ${expdMergeStatus.videoFrameCount}, " +
//                     "IsMerged = ${expdMergeStatus.merged}, " +
//                     "DateSource = '${expdMergeStatus.dateSource}' " +
//                     "WHERE VideoArchiveSetID_FK = ${expdMergeStatus.videoArchiveSetID_FK}"
//         }
//         else {
//             sql = "INSERT INTO EXPDMergeStatus (" +
//                     "VideoArchiveSetID_FK, MergeDate, IsNavigationEdited, StatusMessage, " +
//                     "VideoFrameCount, IsMerged, DateSource) " +
//                     "VALUES (${expdMergeStatus.videoArchiveSetID_FK}, " +
//                     "CONVERT(DATETIME, '${dateFormat.format(expdMergeStatus.mergeDate)}', 120), " +
//                     "${expdMergeStatus.navigationEdited}, '${expdMergeStatus.statusMessage}', " +
//                     "${expdMergeStatus.videoFrameCount}, ${expdMergeStatus.merged}, " +
//                     "'${expdMergeStatus.dateSource}')"
//
//         }
//         def statement = connection.createStatement()
//         Logger.log(EXPDMergeStatusDAO.class, "Executing the following SQL:\n${sql}")
//         statement.execute(sql)
//         db.commit()
//         statement.close()
//         db.close()
//     }


}
