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

/**
 *
 * @author brian
 */
public class LinkUtilities {

    public static String formatAsLongString(ILink link) {
        StringBuilder sb = new StringBuilder();
        sb.append(link.getFromConcept()).append(ILink.DELIMITER);
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
}
