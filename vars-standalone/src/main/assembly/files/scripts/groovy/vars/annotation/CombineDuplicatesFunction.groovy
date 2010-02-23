package vars.annotation

import org.mbari.sql.QueryFunction
import org.slf4j.LoggerFactory
import vars.ToolBox
import vars.annotation.jpa.VideoArchiveImpl

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
        def ids = queryable.executeQueryFunction("SELECT id FROM VideoArchive WHERE videoArchiveName = '${name}' ORDER BY id" as String, handler)
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
        videoArchiveDAO.startTransaction()
        def targetVas = videoArchiveDAO.findByPrimaryKey(VideoArchiveImpl.class, ids[0]).videoArchiveSet
        log.debug("Using ${targetVas} as the master VideoArchiveSet")

        ids[1..-1].each { id ->
            VideoArchiveSet sourceVas = videoArchiveDAO.findByPrimaryKey(VideoArchiveImpl.class, id).videoArchiveSet
            log.debug("---- Examining ${sourceVas}")
            if (sourceVas.id != targetVas.id) {
                targetVas.videoArchives.each { VideoArchive targetVa ->
                    log.debug("Looking for a match to ${targetVa.name}")
                    def duplicateVa = sourceVas.getVideoArchiveByName(targetVa.name)
                    if (duplicateVa) {
                        log.debug("Found duplicate VideoArchive named '${duplicateVa.name}'")
                        mergeVideoArchive(duplicateVa, targetVa)

                        // Delete the now-empty videoArchive
                        sourceVas.removeVideoArchive(duplicateVa)
                        videoArchiveDAO.remove(duplicateVa)
                    }
                }

                // Move leftover videoarchives from sourceVas to targetVas
                sourceVas.videoArchives.each { VideoArchive va ->
                    log.debug("Moving ${va.name} from ${sourceVas} to ${targetVas}")
                    sourceVas.removeVideoArchive(va)
                    targetVas.addVideoArchive(va)
                }


            }
            else {
                def vaList = targetVas.videoArchives
                targetVas.videoArchives.each { VideoArchive targetVa ->
                    // TODO use for loop and break out if match is found or filter!
                    vaList.each { VideoArchive duplicateVa ->
                        if (targetVa.name.equals(duplicateVa.name) && targetVa.id != duplicateVa.id ) {
                            mergeVideoArchive(duplicateVa, targetVa)

                        }
                    }
                }

            }

        }

        videoArchiveDAO.endTransaction()

    }

    /**
     * This needs to be called within a DAO transaction
     */
    private mergeVideoArchive(VideoArchive source, VideoArchive target) {
        source.videoFrames.each { VideoFrame sourceVf ->
            VideoFrame targetVf = target.findVideoFrameByTimeCode(sourceVf.timecode)
            // If the timecode exists in the target, move the observations from the source to the target
            if (targetVf) {
                def observations = sourceVf.observations
                observations.each { obs ->
                    sourceVf.removeObservation(obs)
                    targetVf.addObservation(obs)
                }

                if (!targetVf.hasImageReference() && sourceVf.hasImageReference()) {
                    targetVf.cameraData.imageReference = sourceVf.cameraData.imageReference
                }
            }
            else {
                source.removeVideoFrame(sourceVf)
                target.addVideoFrame(sourceVf)
            }
        }
    }



}