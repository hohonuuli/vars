/*
 * @(#)PersistenceSubscriber.java   2009.10.29 at 12:51:18 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.knowledgebase.ui.persistence;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.DAO;
import vars.knowledgebase.ui.Lookup;
import vars.shared.ui.kbtree.ConceptTree;

/**
 * An EventBus topic subscriber that deals with the persistence transactions
 * for the knowledgebase UI.
 *
 * @param <T>
 */
public abstract class PersistenceSubscriber<T> implements EventTopicSubscriber<T> {

    final DAO dao;
    final String myTopic;
    final Logger log = LoggerFactory.getLogger(getClass());

    /**
     *
     * @param myTopic The Topic that this subscriber should respond to.
     * @param dao The {@link DAO} object that this subscriber will use for
     *      persistent transactions
     */
    public PersistenceSubscriber(String myTopic, DAO dao) {
        super();
        this.myTopic = myTopic;
        this.dao = dao;
    }

    /**
     * You can override this method to do any work that needs to be done after
     * the database transaction is completed.
     *
     * @param obj
     * @return
     */
    T after(T obj) {
        return obj;
    }

    /**
     * Any homework that needs to be done with an object before it is persisted
     * should be done in this method. You can do database interactions in this
     * method, although it's recommended that you only do lookups not inserts
     * or updates.
     *
     * @param obj
     * @return
     */
    T before(T obj) {
        return obj;
    }

    /**
     * Implement this to do your persistent transaction (CRUD)
     *
     * @param obj
     * @return
     */
    abstract T doPersistenceThing(T obj);

    /**
     * This method should return the string that will be used to refresh the
     * knowledgebase. The {@link ConceptTree} will be opened to the node with
     * the matching name.
     *
     * @param obj
     * @return
     */
    abstract String getLookupName(T obj);

    public void onEvent(String topic, T data) {
        if (myTopic.equals(topic)) {
            String lookupName = getLookupName(data);
            try {
                data = before(data);
                data = prepareForTransaction(data);
                doPersistenceThing(data);
                data = after(data);
            }
            catch (Exception e) {
                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
                EventBus.publish(Lookup.TOPIC_REFRESH_KNOWLEGEBASE, lookupName);
            }
            EventBus.publish(Lookup.TOPIC_REFRESH_KNOWLEGEBASE, lookupName);
        }
    }

    /**
     * Similar to before. This method is called immediatly after 'before' but
     * before the 'prepareForTransaction' method. It's currently used by the delete
     * subscribers to remove the relations that need to be disconnected before
     * the delete occurs
     *
     * @param obj
     * @return
     */
    abstract T prepareForTransaction(T obj);
}
