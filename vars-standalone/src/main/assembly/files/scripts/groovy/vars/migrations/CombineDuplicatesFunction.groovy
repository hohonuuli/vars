package vars.migrations

import org.mbari.sql.QueryFunction
import org.slf4j.LoggerFactory
import vars.ToolBox
import vars.annotation.jpa.VideoArchiveImpl
import vars.annotation.jpa.VideoFrameDAOImpl
import vars.annotation.VideoArchive
import vars.annotation.VideoFrameDAO
import vars.annotation.VideoFrame

/**
 * Combines duplicate VideoArchiveNames. This is really a one-off script for migrating from
 * the old VARS to the new, since vars-redux doesn't allow duplicate names.
 *
 */
class CombineDuplicatesFunction {

    private toolBox = new ToolBox();
    final log = LoggerFactory.getLogger(getClass())

    void apply() {
        def duplicates = findDuplicateNames()
        duplicates.each { name ->
            mergeDuplicates(name)
        }
    }

    /**
     * Find a list of each VideoArchive.name that exists more than once in the annotation
     *  datbase
     *
     * @return A List of duplicate names
     */
    def findDuplicateNames() {

        def handler = {rs ->
            def dups = []
            while(rs.next()) {
                dups << rs.getString(1)
            }
            return dups
        } as QueryFunction

        return toolBox.toolBelt.annotationPersistenceService.executeQueryFunction("""
SELECT
    videoArchiveName, count(*) as counter
FROM
    VideoArchive
GROUP BY
    videoArchiveName
HAVING COUNT(*) > 1
        """, handler)
    }

    /**
     * Combine the data from the same dives into a single videoArchiveSet
     */
    def mergeDuplicates(name) {
        def handler = {rs ->
            def ids = []
            while (rs.next()) {
                ids << rs.getLong(1)
            }
            return ids
        } as QueryFunction

        // Get the ID (Primary Key) of all videoarchives with the given name
        def queryable = toolBox.toolBelt.annotationPersistenceService
        def ids = queryable.executeQueryFunction("SELECT DISTINCT id FROM VideoArchive WHERE videoArchiveName = '${name}' ORDER BY id" as String, handler)
        if (ids.size() == 1) {
            log.debug("Only 1 VideoArchive named '${name}' was found. No merging needed!")
            return
        }
        else {
            log.debug("Primary keys of duplicates ${ids}")
        }

        // DAOTX: Start transaction
        // We'll use the first videoarchiveset we find a the 'master'. Data from the others
        // will be moved into the master.
        def videoArchiveDAO = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveDAO()
        def videoFrameDAO = new VideoFrameDAOImpl(videoArchiveDAO.entityManager)
        videoArchiveDAO.startTransaction()
        def targetVa = videoArchiveDAO.findByPrimaryKey(VideoArchiveImpl.class, ids[0])

        ids[1..-1].each { id ->
            VideoArchive sourceVa = videoArchiveDAO.findByPrimaryKey(VideoArchiveImpl.class, id)
            log.debug("Merging duplicate ${sourceVa} into ${targetVa}")
            mergeVideoArchive(sourceVa, targetVa, videoFrameDAO)
            def sourceVas = sourceVa.videoArchiveSet
            sourceVas.removeVideoArchive(sourceVa)
            videoArchiveDAO.remove(sourceVa)
            if (sourceVas.videoArchives.size() == 0) {
                videoArchiveDAO.remove(sourceVas)
            }
        }

        videoArchiveDAO.endTransaction()

    }

    /**
     * This needs to be called within a DAO transaction
     */
    private mergeVideoArchive(VideoArchive source, VideoArchive target, VideoFrameDAO videoFrameDAO) {
        log.debug("Processing ${source.videoFrames.size()} VideoFrames")
        def sourceVideoFrames = videoFrameDAO.findAllByVideoArchivePrimaryKey(source.id)
        def targetVideoFrames = videoFrameDAO.findAllByVideoArchivePrimaryKey(target.id)
        sourceVideoFrames.each { VideoFrame sourceVf ->
            VideoFrame targetVf = targetVideoFrames.find { it.timecode.equals(sourceVf.timecode) }
            // If the timecode exists in the target, move the observations from the source to the target
            if (targetVf) {
                def observations = new ArrayList(sourceVf.observations)
                log.debug("Moving ${observations.size()} observations from ${sourceVf} to ${targetVf}")
                observations.each { obs ->
                    sourceVf.removeObservation(obs)
                    targetVf.addObservation(obs)
                }

                if (!targetVf.hasImageReference() && sourceVf.hasImageReference()) {
                    targetVf.cameraData.imageReference = sourceVf.cameraData.imageReference
                }

                if (sourceVf.observations.size() == 0) {
                    videoFrameDAO.remove(sourceVf)
                }
            }
            else {
                log.debug("Moving ${sourceVf}} from ${source} to ${target}")
                source.removeVideoFrame(sourceVf)
                target.addVideoFrame(sourceVf)
            }
        }
    }



}