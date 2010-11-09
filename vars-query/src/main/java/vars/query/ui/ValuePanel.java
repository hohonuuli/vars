/*
 * @(#)ValuePanel.java   2009.11.21 at 08:17:35 PST
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



package vars.query.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 * <p><!-- Insert Description --></p>
 * @author  Brian Schlining
 */
public abstract class ValuePanel extends JPanel {

    private static final Pattern PATTERN = Pattern.compile("\\B[A-Z]+");
    private JCheckBox constrainCheckBox = null;
    private JCheckBox returnCheckBox = null;
    private final String valueName;

    /**
     * Constructs ...
     *
     *
     * @param name
     */
    public ValuePanel(String name) {
        super();
        this.valueName = name;
        initialize();
    }

    public boolean isReturned() {
        return getReturnCheckBox().isSelected();
    }

    public boolean isConstrained() {
        return getConstrainCheckBox().isSelected();
    }

    /**
         * This method initializes jCheckBox
         * @return  javax.swing.JCheckBox
         * @uml.property  name="constrainCheckBox"
         */
    protected JCheckBox getConstrainCheckBox() {
        if (constrainCheckBox == null) {
            constrainCheckBox = new JCheckBox();
            constrainCheckBox.setToolTipText("Constrain");
        }

        return constrainCheckBox;
    }

    /**
         * This method initializes jCheckBox1
         * @return  javax.swing.JCheckBox
         */
    protected JCheckBox getReturnCheckBox() {
        if (returnCheckBox == null) {
            returnCheckBox = new JCheckBox();
            returnCheckBox.setToolTipText("Return");
        }

        return returnCheckBox;
    }

    /**
         * Yes I know embedding SQL directly into UI code is bad. Will need to refactor later. FOr now I'm jsut trying to get it to work.
         * @return
         */
    public abstract String getSQL();

    /**
         * <p><!-- Method description --></p>
         * @return
         */
    public String getValueName() {
        return valueName;
    }

    /**
     *
     */
    private void initialize() {

        /*
         * Attempt to neaten up title a bit this takes camel case column names
         * and splits them up into words so that "ConceptName" would become
         * "Concept Name". We don't want to use this against columns that are
         * all uppercase; this pattern will change 'CONCEPTNAME' to 'C ONCEPTNAME'
         */
        String title = valueName;
        if (!valueName.toUpperCase().equals(valueName)) {
            Matcher matcher = PATTERN.matcher(valueName);
            title = matcher.replaceAll(" $0");
        }

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setBorder(javax.swing.BorderFactory.createTitledBorder(null, title, javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP, null, null));
        this.add(getReturnCheckBox(), null);
        this.add(getConstrainCheckBox(), null);
    }
}
