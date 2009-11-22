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


/**
 *
 * @author Brian Schlining
 */
public class BooleanValuePanel extends ValuePanel {


    /**
     * @param name
     */
    public BooleanValuePanel(String name) {
        super(name);
        initialize();
    }


    /**
     *
     *
     * @return
     */
    public String getSQL() {
        // TODO Auto-generated method stub
        return "";
    }


    private void initialize() {
        getConstrainCheckBox().setEnabled(false);
        add(new JPanel());
    }
}
