/*
 * @(#)PreferenceUpdater.java   2010.05.06 at 03:03:11 PDT
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



package vars.shared.preferences;

/**
 *
 * @author brian
 */
public interface PreferenceUpdater {

    /**
     * Save the current preferences
     */
    void persistPreferences();
}
