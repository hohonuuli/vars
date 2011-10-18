/*
 * @(#)UIEventSubscriber.java   2011.10.17 at 05:08:10 PDT
 *
 * Copyright 2011 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.annotation.ui.eventbus;

/**
 * @author Brian Schlining
 * @since 2011-10-11
 */
public interface UIEventSubscriber {

    void respondTo(ObservationsAddedEvent event);

    void respondTo(ObservationsChangedEvent event);

    void respondTo(ObservationsRemovedEvent event);

    void respondTo(ObservationsSelectedEvent event);

    void respondTo(VideoArchiveChangedEvent event);

    void respondTo(VideoArchiveSelectedEvent event);

    void respondTo(VideoFramesChangedEvent event);


}
