/*
 * Copyright 2005 MBARI
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1 
 * (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/copyleft/lesser.html
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

//~--- classes ----------------------------------------------------------------

/**
 * <p><!-- Insert Description --></p>
 * @author  Brian Schlining
 * @version  $Id: ValuePanel.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public abstract class ValuePanel extends JPanel {

    private static final Pattern PATTERN = Pattern.compile("\\B[A-Z]+");

    //~--- fields -------------------------------------------------------------

    /**
	 * @uml.property  name="constrainCheckBox"
	 * @uml.associationEnd  
	 */
    private JCheckBox constrainCheckBox = null;
    /**
	 * @uml.property  name="returnCheckBox"
	 * @uml.associationEnd  
	 */
    private JCheckBox returnCheckBox = null;
    /**
	 * @uml.property  name="valueName"
	 */
    private final String valueName;

    //~--- constructors -------------------------------------------------------

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

    //~--- get methods --------------------------------------------------------

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
	 * @uml.property  name="returnCheckBox"
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
	 * @uml.property  name="sQL"
	 */
    public abstract String getSQL();

    /**
	 * <p><!-- Method description --></p>
	 * @return
	 * @uml.property  name="valueName"
	 */
    public String getValueName() {
        return valueName;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * <p><!-- Method description --></p>
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
        this.setBorder(
                javax.swing.BorderFactory.createTitledBorder(null, title,
                javax.swing.border.TitledBorder.LEFT,
                    javax.swing.border.TitledBorder.TOP, null, null));
        this.add(getReturnCheckBox(), null);
        this.add(getConstrainCheckBox(), null);
    }
}
