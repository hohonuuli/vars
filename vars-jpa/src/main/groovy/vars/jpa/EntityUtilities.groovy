package vars.jpa


import vars.annotation.VideoArchiveSet
import vars.knowledgebase.Concept
import vars.annotation.VideoArchiveSet

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 11, 2009
 * Time: 3:26:39 PM
 * To change this template use File | Settings | File Templates.
 */

class EntityUtilities {

    /**
     * Builds a text tree of the videoArchiveSet
     */
    def buildTextTree(VideoArchiveSet videoArchiveSet) {
        def s = "${videoArchiveSet}\n"

        videoArchiveSet.cameraDeployments.each { cd ->
            s += "|-- ${cd}\n"
        }

        videoArchiveSet.videoArchives.each { va ->
            s += "|-- ${va}\n"

            va.videoFrames.each { vf ->
                s += "|    |-- ${vf}\n"

                s += "|        |-- ${vf.physicalData}\n"
                s += "|        |-- ${vf.cameraData}\n"

                vf.observations.each { obs ->
                    s += "|        |-- ${obs}\n"

                    obs.associations.each { ass ->
                        s += "|            |-- ${ass}\n"
                    }
                }

            }
        }

        return s
    }

    def buildTextTree(Concept concept) {
        return buildKBTree(concept, 0)
    }

    private buildKBTree(Concept concept, int depth) {
        def a = "    " * depth
        def s = "${a}>-- ${concept}\n"
        concept.conceptNames.each { cn ->
            s += "${a}    |-- ${cn}\n"
        }
        def metadata = concept.getConceptMetadata()
        s += "${a}    |-- ${metadata}\n"

        metadata.medias.each { m ->
            s += "${a}        |--${m}\n"
        }

        metadata.histories.each { m ->
            s += "${a}        |--${m}\n"
        }

        metadata.linkRealizations.each { m ->
            s += "${a}        |--${m}\n"
        }

        metadata.linkTemplates.each { m ->
            s += "${a}        |--${m}\n"
        }

        depth++
        concept.childConcepts.each { s += buildKBTree(it, depth) }
        return s
    }

}