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


/*
The Monterey Bay Aquarium Research Institute (MBARI) provides this
documentation and code 'as is', with no warranty, express or
implied, of its quality or consistency. It is provided without support and
without obligation on the part of MBARI to assist in its use, correction,
modification, or enhancement. This information should not be published or
distributed to third parties without specific written permission from MBARI
 */
package org.mbari.vars.annotation.ui.actions;

/**
 * <p>Adds 'identity-reference | self | [some integer]' property to the Observation set in
 * the ObservationDispatcher</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 * @version $Id: AddOldRefNumPropAction.java 314 2006-07-10 02:38:46Z hohonuuli $
 */
public final class AddOldRefNumPropAction extends AddPropertyAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * It's static so all instance share the same number
     */
    private static String refNumber;

    /**
     *
     */
    public AddOldRefNumPropAction() {
        super("identity-reference", "self", "0");
    }

    /**
     *     Adds the association 'identity-reference|self|[some integer]'
     *     [some integer] increments by one after each call.
     */
    public void doAction() {
        setLinkValue(refNumber);
        super.doAction();
    }

    /**
     * @return
     */
    public static String getRefNumber() {
        return refNumber;
    }

    /**
     * @param i
     */
    public static void setRefNumber(final int i) {
        refNumber = i + "";
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param refNumber_
     */
    public static void setRefNumber(final String refNumber_) {
        refNumber = refNumber_;
    }
}
