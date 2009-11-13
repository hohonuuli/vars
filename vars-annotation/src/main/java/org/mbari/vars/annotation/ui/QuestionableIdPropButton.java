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


package org.mbari.vars.annotation.ui;

import javax.swing.ImageIcon;
import org.mbari.vars.annotation.ui.actions.AddQuestionableIdPropAction;

/**
 * <p>Adds a questionable id association to the currently selected observation.</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 * @version $Id: QuestionableIdPropButton.java 314 2006-07-10 02:38:46Z hohonuuli $
 * @see org.mbari.vars.annotation.ui.actions.AddQuestionableIdPropAction
 */
public class QuestionableIdPropButton extends PropButton {

    /**
     *
     */
    private static final long serialVersionUID = 1949614784906779977L;

    /**
     *
     */
    public QuestionableIdPropButton() {
        super();
        setAction(new AddQuestionableIdPropAction());
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/question.png")));
        setToolTipText("identity in question");
        setEnabled(false);
    }
}
