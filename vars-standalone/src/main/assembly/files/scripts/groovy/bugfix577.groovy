import vars.annotation.VideoArchiveSetDAO
import vars.integration.MergeHistory
import vars.integration.MergeType

/**
 * Shoe horn records from EXPDMergeStatus into EXPDMergeHistory
 * @author Brian Schlining
 * @since 2013-03-11
 */


def toolbox = new vars.ToolBox()
def mergeStatusDAO = toolbox.mergeStatusDAO
def mergeHistoryDAO = toolbox.mergeHistoryDAO
VideoArchiveSetDAO videoArchiveSetDAO = toolbox.toolBelt.annotationDAOFactory.newVideoArchiveSetDAO()
def cache = toolbox.toolBelt.persistenceCacheProvider

def mergeStatuses = mergeStatusDAO.findAll();
for (status in mergeStatuses) {
    def videoArchiveSet = videoArchiveSetDAO.findByPrimaryKey(status.videoArchiveSetID)
    if (videoArchiveSet) {
        videoArchiveSetDAO.startTransaction()
        for (useHD in [false, true]) {

            // --- Get VideoFrameCount
            // Can't use videoFrameCount from EXPDMergeStatus as it includes both SD and HD frames
            // We split the 2 types apart here.
            def videoFrameCount = 0
            for (videoArchive in videoArchiveSet.videoArchives) {
                if (useHD && videoArchive.name.toUpperCase().endsWith("HD")) {
                    videoFrameCount += videoArchive.getVideoFrames().size()
                }
                else if (!useHD && !videoArchive.name.toUpperCase().endsWith("HD")) {
                    videoFrameCount += videoArchive.getVideoFrames().size()
                }
            }

            // We only need to preserve PESSIMISTIC MERGE Histories
            if (videoFrameCount > 0 && status.statusMessage.contains(MergeType.PESSIMISTIC.name())) {

                // --- Construct mergeHistory
                def mergeHistory = new MergeHistory()
                mergeHistory.dateSource = status.dateSource
                mergeHistory.hd = useHD
                mergeHistory.mergeDate = status.mergeDate
                mergeHistory.mergeType = MergeType.PESSIMISTIC.name()
                mergeHistory.navigationEdited = status.navigationEdited
                mergeHistory.statusMessage = status.statusMessage
                mergeHistory.videoArchiveSetID = status.videoArchiveSetID
                mergeHistory.videoFrameCount = videoFrameCount

                mergeHistoryDAO.update(mergeHistory)
            }

        }
        videoArchiveSetDAO.endTransaction();
        cache.clear()
    }


}

