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

import javax.swing.JPanel;

//~--- classes ----------------------------------------------------------------

/**
 * <p><!-- Insert Description --></p>
 *
 * @author Brian Schlining
 * @version $Id: BooleanValuePanel.java 314 2006-07-10 02:38:46Z hohonuuli $
 */
public class BooleanValuePanel extends ValuePanel {

    /**
     * 
     */
    private static final long serialVersionUID = 6982730305624742487L;

    /**
     * @param name
     */
    public BooleanValuePanel(String name) {
        super(name);
        initialize();
    }

    //~--- get methods --------------------------------------------------------

    /*
     *  (non-Javadoc)
     * @see query.ui.ValuePanel#getSQL()
     */

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @return
     */
    public String getSQL() {
        // TODO Auto-generated method stub
        return "";
    }

    //~--- methods ------------------------------------------------------------

    /**
     * <p><!-- Method description --></p>
     *
     */
    private void initialize() {
        getConstrainCheckBox().setEnabled(false);
        add(new JPanel());
    }
}
