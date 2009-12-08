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


package vars.annotation.ui.buttons;

import javax.swing.ImageIcon;


import vars.annotation.ui.actions.AddPropertyAction;
import vars.annotation.ui.ToolBelt;

/**
 * <p>Adds a questionable id association to the currently selected observation.</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 */
public class PQuestionableIdButton extends PropButton {



    /**
     *
     */
    public PQuestionableIdButton(ToolBelt toolBelt) {
        super();
        setAction(new AddQuestionableIdPropAction(toolBelt));
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/question.png")));
        setToolTipText("identity in question");
        setEnabled(false);
    }
    
    private class AddQuestionableIdPropAction extends AddPropertyAction {



        /**
         *
         */
        public AddQuestionableIdPropAction(ToolBelt toolBelt) {
            super(toolBelt, "identity-certainty", "self", "maybe");
        }
    }
}
