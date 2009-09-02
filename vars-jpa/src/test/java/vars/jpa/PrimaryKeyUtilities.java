/*
 * @(#)PrimaryKeyUtilities.java   2009.08.17 at 10:38:29 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.jpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import vars.annotation.IVideoArchive;
import vars.annotation.IVideoFrame;
import vars.annotation.jpa.Association;
import vars.annotation.jpa.CameraData;
import vars.annotation.jpa.CameraDeployment;
import vars.annotation.jpa.Observation;
import vars.annotation.jpa.PhysicalData;
import vars.annotation.jpa.VideoArchive;
import vars.annotation.jpa.VideoArchiveSet;
import vars.annotation.jpa.VideoFrame;
import vars.knowledgebase.IConcept;
import vars.knowledgebase.jpa.Concept;
import vars.knowledgebase.jpa.ConceptMetadata;
import vars.knowledgebase.jpa.ConceptName;
import vars.knowledgebase.jpa.History;
import vars.knowledgebase.jpa.LinkRealization;
import vars.knowledgebase.jpa.LinkTemplate;
import vars.knowledgebase.jpa.Media;
import vars.knowledgebase.jpa.Usage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 11, 2009
 * Time: 1:20:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrimaryKeyUtilities {

    private static final Logger log = LoggerFactory.getLogger(PrimaryKeyUtilities.class);

    /**
     * Checks the database for the presence of all the primary keys supplied
     *
     * @param map See primaryKeyMap
     * @return true if all keys were found false otherwise.
     */
    public static boolean checkDbForAllPks(Map<Class, Collection> map, DAO dao) {
        boolean found = true;

        out:
        {
            for (Class clazz : map.keySet()) {
                Collection primaryKeys = map.get(clazz);

                if (primaryKeys != null) {
                    for (Object key : primaryKeys) {
                        JPAEntity entity = (JPAEntity) dao.findByPrimaryKey(clazz, key);

                        if (entity == null) {
                            log.info("Unable to find " + entity + " with primaryKey = " + key + " in the database");
                            found = false;

                            break out;
                        }
                    }
                }
            }
        }

        return found;

    }

    /**
     * Checks to see if any of the primary keys are present in the database
     *
     * @param map
     * @return true if any of the keys were found. False otherwise.
     */
    public static boolean checkDbForAnyPks(Map<Class, Collection> map, DAO dao) {
        boolean found = false;

        for (Class clazz : map.keySet()) {
            Collection primaryKeys = map.get(clazz);

            if (primaryKeys != null) {
                for (Object key : primaryKeys) {
                    JPAEntity entity = (JPAEntity) dao.findByPrimaryKey(clazz, key);

                    if (entity != null) {
                        log.info("Found " + entity + " with primaryKey = " + key + " in the database");
                        found = true;
                    }
                }
            }
        }

        return found;
    }

    @SuppressWarnings("unchecked")
    public static Map<Class, Collection> primaryKeyMap(IConcept concept) {

        // Map for Concepts
        Map<Class, Collection> map = new HashMap<Class, Collection>() {

            {
                put(Concept.class, new ArrayList());
                put(ConceptName.class, new ArrayList());
                put(ConceptMetadata.class, new ArrayList());
                put(History.class, new ArrayList());
                put(LinkRealization.class, new ArrayList());
                put(LinkTemplate.class, new ArrayList());
                put(Media.class, new ArrayList());
                put(Usage.class, new ArrayList());
            }
        };

        primaryKeyMap((Concept) concept, map);

        return map;
    }

    public static Map<Class, Collection> primaryKeyMap(VideoArchiveSet videoArchiveSet) {
        Map<Class, Collection> map = new HashMap<Class, Collection>() {

            {
                put(VideoArchiveSet.class, new ArrayList());
                put(CameraDeployment.class, new ArrayList());
                put(VideoArchive.class, new ArrayList());
                put(VideoFrame.class, new ArrayList());
                put(PhysicalData.class, new ArrayList());
                put(CameraData.class, new ArrayList());
                put(Observation.class, new ArrayList());
                put(Association.class, new ArrayList());
            }
        };

        map.get(VideoArchiveSet.class).add(videoArchiveSet.getId());
        map.get(CameraDeployment.class).addAll(primaryKeys(videoArchiveSet.getCameraDeployments()));

        Collection<IVideoArchive> videoArchives = videoArchiveSet.getVideoArchives();

        map.get(VideoArchive.class).addAll(primaryKeys(videoArchives));

        for (IVideoArchive va : videoArchives) {
            for (IVideoFrame videoFrame : va.getVideoFrames()) {
                primaryKeyMap((VideoFrame) videoFrame, map);
            }
        }

        return map;
    }

    @SuppressWarnings("unchecked")
    private static void primaryKeyMap(Concept concept, Map<Class, Collection> map) {
        map.get(Concept.class).add(new Long(concept.getId()));
        map.get(ConceptName.class).addAll(primaryKeys(concept.getConceptNames()));

        ConceptMetadata metadata = (ConceptMetadata) concept.getConceptMetadata();

        map.get(ConceptMetadata.class).add(metadata.getId());
        map.get(History.class).addAll(primaryKeys(metadata.getHistories()));
        map.get(LinkRealization.class).addAll(primaryKeys(metadata.getLinkRealizations()));
        map.get(LinkTemplate.class).addAll(primaryKeys(metadata.getLinkTemplates()));
        map.get(Media.class).addAll(primaryKeys(metadata.getMedias()));
        map.get(Usage.class).add(((JPAEntity) metadata.getUsage()).getId());

        // Process the child conceptNames
        for (Object child : concept.getChildConcepts()) {
            primaryKeyMap((Concept) child, map);
        }

    }

    private static void primaryKeyMap(VideoFrame videoFrame, Map<Class, Collection> map) {
        map.get(VideoFrame.class).add(videoFrame.getId());
        map.get(PhysicalData.class).add(((JPAEntity) videoFrame.getPhysicalData()).getId());
        map.get(PhysicalData.class).add(((JPAEntity) videoFrame.getPhysicalData()).getId());

        Collection obs = videoFrame.getObservations();

        map.get(Observation.class).add(primaryKeys(obs));

        Collection ass = map.get(Association.class);

        for (Object o : obs) {
            Observation observation = (Observation) o;

            ass.addAll(primaryKeys(observation.getAssociations()));
        }
    }

    /**
     * Returns the primary keys of all JPAEntities in a collection
     *
     * @param c
     * @return
     */
    private static Collection primaryKeys(Collection c) {

        Collection out = new HashSet();

        for (Object e : c) {
            out.add(((JPAEntity) e).getId());
        }

        return out;
    }
}
