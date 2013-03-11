import vars.annotation.VideoArchiveSetDAO
import vars.integration.MergeHistory
import vars.integration.MergeType

/**
 * Shoe horn records from EXPDMergeStatus into EXPDMergeHistory. We only care about the PESSIMISTIC
 * merges. We'll ignore all others so that they're remerged using a PRAGMATIC merge on the first
 * run.
 *
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
    if (status.statusMessage.contains(MergeType.PESSIMISTIC.name())) {
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
                if (videoFrameCount > 0) {

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
}

