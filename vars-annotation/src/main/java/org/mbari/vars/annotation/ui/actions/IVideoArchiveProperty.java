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
Created on Nov 3, 2004
 *
TODO To change the template for this generated file go to
Window - Preferences - Java - Code Style - Code Templates
 */
package org.mbari.vars.annotation.ui.actions;

import vars.annotation.VideoArchive;

/**
 * @author  brian
 */
public interface IVideoArchiveProperty {

    VideoArchive getVideoArchive();

    void setVideoArchive(VideoArchive videoArchive);
}
