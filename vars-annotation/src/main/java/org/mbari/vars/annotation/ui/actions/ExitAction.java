/*
 * @(#)ExitAction.java   2009.11.19 at 10:38:09 PST
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



package org.mbari.vars.annotation.ui.actions;

import org.mbari.awt.event.ActionAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Exits the application. The currently edits will be saved to the database
 * before exiting, frame-capture software is shutdown and frame-grabs are
 * moved to the server.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @created  November 5, 2004
 * @version  $Id: $
 */
public class ExitAction extends ActionAdapter {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * C onstructor for the ExitAction object
     */
    public ExitAction() {
        super("Exit");
    }

    /**
     *  Performs any tasks needed when closing a <code>VideoArchive</code>
     */
    public void doAction() {

        log.info("Shutting down");

        System.exit(0);
    }
}
