/*
 * @(#)MergeHistoryDAO.java   2013.03.07 at 02:03:45 PST
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
 * @author Brian Schlining
 * @since 2013-03-06
 */
public interface MergeHistoryDAO {

    void close();

    List<MergeHistory> find(Long videoArchiveId, boolean isHD);

    List<MergeHistory> findAllMostRecent(boolean isHD);

    List<MergeHistory> findByPlatformAndSequenceNumber(String platform, Number sequenceNumber, boolean isHD);

    MergeHistory findMostRecent(final Long videoArchiveSetId, boolean isHD);

    List<Long> findSetsWithEditedNav();

    List<Long> findUnmergedSets();

    List<Long> findUpdatedSets();

    void update(MergeHistory mergeHistory);
}
