/*
 * @(#)MeasurementCompletedListener.java   2012.11.26 at 08:48:29 PST
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



package vars.annotation.ui.imagepanel;

/**
 * @author Brian Schlining
 * @since 2011-08-30
 */
public interface MeasurementCompletedListener {

    void onComplete(MeasurementCompletedEvent event);
}
