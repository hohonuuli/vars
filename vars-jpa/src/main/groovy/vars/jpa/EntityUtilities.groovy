package vars.jpa

import org.mbari.jpax.NonManagedEAO
import vars.annotation.IVideoArchiveSet

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 11, 2009
 * Time: 3:26:39 PM
 * To change this template use File | Settings | File Templates.
 */

class EntityUtilities {

    final NonManagedEAO eao;

    def EntityUtilities(NonManagedEAO eao) {
        this.eao = eao;
    }

    /**
     * Builds a text tree of the videoArchiveSet
     */
    def buildTextTree(IVideoArchiveSet videoArchiveSet) {
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
}