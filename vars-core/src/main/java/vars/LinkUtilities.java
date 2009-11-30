/*
 * @(#)LinkUtilites.java   2009.11.09 at 04:56:26 PST
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

import java.util.Collection;
import java.util.Comparator;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 *
 * @author brian
 */
public class LinkUtilities {
    
    private static final Comparator<ILink> COMPARATOR = new LinkComparator();

    public static String formatAsLongString(ILink link) {
        StringBuilder sb = new StringBuilder();
        String fromConcept = link.getFromConcept();
        fromConcept = (fromConcept == null) ? ILink.VALUE_NIL : fromConcept;
        sb.append(fromConcept).append(ILink.DELIMITER);
        sb.append(link.getLinkName()).append(ILink.DELIMITER);
        sb.append(link.getToConcept()).append(ILink.DELIMITER);
        sb.append(link.getLinkValue());

        return sb.toString();
    }

    public static String formatAsString(ILink link) {
        StringBuilder sb = new StringBuilder();
        sb.append(link.getLinkName()).append(ILink.DELIMITER);
        sb.append(link.getToConcept()).append(ILink.DELIMITER);
        sb.append(link.getLinkValue());

        return sb.toString();
    }
    
    /**
     * Return all links in a collection that match a given {@link ILink}. This compares the linkName,
     * toConcept, and linkValue fields
     * 
     * @param links
     * @param templateLink
     * @return
     */
    public static Collection<ILink> findMatchingLinksIn(Collection<ILink> links, final ILink templateLink) {
        return Collections2.filter(links, new Predicate<ILink>() {
            public boolean apply(ILink input) {
                return COMPARATOR.compare(input, templateLink) == 0;
            }
        });
    }
}
