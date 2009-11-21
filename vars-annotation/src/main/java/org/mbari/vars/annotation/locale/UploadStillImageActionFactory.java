/*
 * @(#)UploadStillImageActionFactory.java   2009.11.20 at 05:03:28 PST
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



package org.mbari.vars.annotation.locale;

import vars.annotation.ui.PersistenceController;

/**
 * <p></p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 */
public class UploadStillImageActionFactory {

    /**
     *
     */
    private UploadStillImageActionFactory(PersistenceController persistenceController) {

        // No instantiation
    }

    /**
     *
     *
     *
     * @param persistenceController
     * @return
     */
    public static UploadStillImageAction getAction(PersistenceController persistenceController) {
        final String platform = LocaleFactory.getCameraPlatform();
        UploadStillImageAction action = null;
        if ((platform == null) || platform.toLowerCase().equals("shore")) {
            action = new org.mbari.vars.annotation.locale.shore.UploadStillImageAction(persistenceController);
        }
        else {
            action = new org.mbari.vars.annotation.locale.UploadStillImageAction();
        }

        return action;
    }
}
