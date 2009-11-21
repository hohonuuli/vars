/*
 * @(#)UploadStillImageAction.java   2009.11.20 at 03:43:05 PST
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

import org.mbari.awt.event.ActionAdapter;
import org.mbari.vars.annotation.ui.actions.IVideoArchiveProperty;
import vars.annotation.VideoArchive;

/**
 * @author brian
 */
public class UploadStillImageAction extends ActionAdapter implements IVideoArchiveProperty {

    protected VideoArchive videoArchive;

    public UploadStillImageAction() {
        super("Copy Still Images to Archive");
    }

    /**
     *
     */
    public void doAction() {

        /*
         *  Do nothing by default. For shore operations this should be overridden.
         * TO copy images to an archival location and update the URLS in the database.
         */
    }

    /**
     * @return
     */
    public VideoArchive getVideoArchive() {
        return videoArchive;
    }

    /**
     * @param  videoArchive
     */
    public void setVideoArchive(VideoArchive videoArchive) {
        this.videoArchive = videoArchive;
    }
}
