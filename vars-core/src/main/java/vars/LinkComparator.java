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

import org.mbari.text.IgnoreCaseToStringComparator;

/**
 * For comparing links using linkName, toConcept and linkValue fields.
 * @author brian
 */
public class LinkComparator implements Comparator<ILink> {
    
    private final Comparator comparator = new IgnoreCaseToStringComparator();

    public int compare(ILink o1, ILink o2) {
    	
        int c = comparator.compare(o1.getLinkName(), o2.getLinkName());
        
        if (c == 0) {
            c = comparator.compare(o1.getToConcept(), o2.getToConcept());
        }
        
        if (c == 0) {
            c = comparator.compare(o1.getLinkValue(), o2.getLinkValue());
        }
        
        return c;
    }
}
