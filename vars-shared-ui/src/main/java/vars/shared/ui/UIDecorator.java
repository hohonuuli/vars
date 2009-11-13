/*
 * @(#)UIDecorator.java   2009.11.13 at 03:10:36 PST
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



package vars.shared.ui;

/**
 * Marker interface used to tag a Swing Decorator. Useful for just hanging
 * onto the decorator references so they don't get garbage collected by stuffing
 * them into a collection somewhere.
 * @author brian
 */
public interface UIDecorator {}
