/*
 * @(#)MergeStatusDAO.java   2010.01.14 at 10:27:08 PST
 *
 * Copyright 2009 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.integration;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Dec 26, 2009
 * Time: 5:57:56 PM
 * To change this template use File | Settings | File Templates.
 */
public interface MergeStatusDAO {

    List<MergeStatus> findAll();

    /**
     *
     * @param id
     */
    MergeStatus find(Long id);

    MergeStatus findByPlatformAndSequenceNumber(String platform, Number sequenceNumber);

    /**
     * Find any merge status messages containing the given string.
     * Use % for wild cards; for example findByStatusMessage('%CONSERVATIVE%')
     *
     * @return A list of Longs (primary keys for VideoArchiveSets)
     */
    List<MergeStatus> findByStatusMessage(String msg);

    /**
     * Find any VideoArchiveSets whose merge failed
     *
     * @return A list of Longs (primary keys for VideoArchiveSets)
     */
    List<Long> findFailedSets();

    List<Long> findSetsWithEditedNav();

    /**
     * Find any VideoArchiveSets that have not been merged
     *
     * @return A list of Longs (primary keys for VideoArchiveSets)
     */
    List<Long> findUnmergedSets();

    List<Long> findUpdatedSets();

    /**
     *
     * @param mergeStatus
     */
    void update(MergeStatus mergeStatus);

    void close();
}
