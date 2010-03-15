/*
 * @(#)AssociationComboBoxTest.java   2010.03.15 at 02:39:53 PDT
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



package vars.annotation.ui.videoset;

import java.awt.BorderLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import org.mbari.swing.SearchableComboBoxModel;
import org.mbari.text.ObjectToStringConverter;
import vars.ILink;
import vars.LinkBean;
import vars.LinkComparator;
import vars.LinkUtilities;
import vars.shared.ui.LinkListCellRenderer;

/**
 *
 * @author brian
 */
public class AssociationComboBoxTest {

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        JComboBox associationComboBox = new JComboBox();
        associationComboBox.setRenderer(new LinkListCellRenderer());
        SearchableComboBoxModel<ILink> model = new SearchableComboBoxModel<ILink>(new LinkComparator(),
                new ObjectToStringConverter<ILink>() {
            public String convert(ILink object) {
                return LinkUtilities.formatAsString(object);
            }
        });
        associationComboBox.setModel(model);
        frame.add(associationComboBox, BorderLayout.CENTER);
        ILink link1 = new LinkBean("linkName | toConcept | linkValue");
        ILink link2 = new LinkBean(ILink.VALUE_NIL, ILink.VALUE_NIL, ILink.VALUE_NIL);
        model.addElement(link1);
        model.addElement(link2);
        associationComboBox.setSelectedItem(link2);
        frame.pack();
        frame.setVisible(true);

    }
}
