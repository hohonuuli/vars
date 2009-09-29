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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptMetadata;
import vars.knowledgebase.jpa.ConceptImpl;
import vars.knowledgebase.jpa.ConceptMetadataImpl;
import vars.knowledgebase.jpa.GConceptName;
import vars.knowledgebase.jpa.GHistory;
import vars.knowledgebase.jpa.GLinkRealization;
import vars.knowledgebase.jpa.GLinkTemplate;
import vars.knowledgebase.jpa.GMedia;
import vars.knowledgebase.jpa.GUsage;
import vars.annotation.VideoArchiveSet;
import vars.annotation.VideoArchive;
import vars.annotation.VideoFrame;
import vars.annotation.Observation;
import vars.annotation.jpa.*;

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
    public static Map<Class, Collection> primaryKeyMap(Concept concept) {

        // Map for Concepts
        Map<Class, Collection> map = new HashMap<Class, Collection>() {

            {
                put(ConceptImpl.class, new ArrayList());
                put(GConceptName.class, new ArrayList());
                put(ConceptMetadataImpl.class, new ArrayList());
                put(GHistory.class, new ArrayList());
                put(GLinkRealization.class, new ArrayList());
                put(GLinkTemplate.class, new ArrayList());
                put(GMedia.class, new ArrayList());
                put(GUsage.class, new ArrayList());
            }
        };

        primaryKeyMap((Concept) concept, map);

        return map;
    }

    public static Map<Class, Collection> primaryKeyMap(VideoArchiveSet videoArchiveSet) {
        Map<Class, Collection> map = new HashMap<Class, Collection>() {

            {
                put(GVideoArchiveSet.class, new ArrayList());
                put(GCameraDeployment.class, new ArrayList());
                put(GVideoArchive.class, new ArrayList());
                put(GVideoFrame.class, new ArrayList());
                put(GPhysicalData.class, new ArrayList());
                put(GCameraData.class, new ArrayList());
                put(GObservation.class, new ArrayList());
                put(GAssociation.class, new ArrayList());
            }
        };

        map.get(GVideoArchiveSet.class).add(((JPAEntity) videoArchiveSet).getId());
        map.get(GCameraDeployment.class).addAll(primaryKeys(videoArchiveSet.getCameraDeployments()));

        Collection<VideoArchive> videoArchives = videoArchiveSet.getVideoArchives();

        map.get(GVideoArchive.class).addAll(primaryKeys(videoArchives));

        for (VideoArchive va : videoArchives) {
            for (VideoFrame videoFrame : va.getVideoFrames()) {
                primaryKeyMap((VideoFrame) videoFrame, map);
            }
        }

        return map;
    }

    @SuppressWarnings("unchecked")
    private static void primaryKeyMap(Concept concept, Map<Class, Collection> map) {
        JPAEntity c = (JPAEntity) concept;
        map.get(ConceptImpl.class).add(new Long(c.getId()));
        map.get(GConceptName.class).addAll(primaryKeys(concept.getConceptNames()));

        ConceptMetadata metadata = (ConceptMetadataImpl) concept.getConceptMetadata();
        JPAEntity cm = (JPAEntity) metadata;
        map.get(ConceptMetadataImpl.class).add(cm.getId());
        map.get(GHistory.class).addAll(primaryKeys(metadata.getHistories()));
        map.get(GLinkRealization.class).addAll(primaryKeys(metadata.getLinkRealizations()));
        map.get(GLinkTemplate.class).addAll(primaryKeys(metadata.getLinkTemplates()));
        map.get(GMedia.class).addAll(primaryKeys(metadata.getMedias()));
        map.get(GUsage.class).add(((JPAEntity) metadata.getUsage()).getId());

        // Process the child conceptNames
        for (Object child : concept.getChildConcepts()) {
            primaryKeyMap((Concept) child, map);
        }

    }

    private static void primaryKeyMap(VideoFrame videoFrame, Map<Class, Collection> map) {
        map.get(GVideoFrame.class).add(((JPAEntity) videoFrame).getId());
        map.get(GPhysicalData.class).add(((JPAEntity) videoFrame.getPhysicalData()).getId());
        map.get(GPhysicalData.class).add(((JPAEntity) videoFrame.getPhysicalData()).getId());

        Collection obs = videoFrame.getObservations();

        map.get(GObservation.class).add(primaryKeys(obs));

        Collection ass = map.get(GAssociation.class);

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
