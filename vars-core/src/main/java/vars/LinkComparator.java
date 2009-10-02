/*
 * @(#)LinkComparator.java   2009.10.02 at 02:09:11 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vars;

import java.util.Comparator;

/**
 * For comparing links using linkName, toConcept and linkValue fields.
 * @author brian
 */
public class LinkComparator implements Comparator<ILink> {

    public int compare(ILink o1, ILink o2) {
        final String s1 = o1.getLinkName() + ILink.DELIMITER + o1.getToConcept() + ILink.DELIMITER + o1.getLinkValue();
        final String s2 = o2.getLinkName() + ILink.DELIMITER + o2.getToConcept() + ILink.DELIMITER + o2.getLinkValue();

        return s1.compareTo(s2);
    }
}
