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


package vars;

//~--- interfaces -------------------------------------------------------------

/**
 * <p>Implement this class to listen for when the {@link PersistenceCache} cleared</p>
 *
 */
public interface CacheClearedListener {

    /**
     * <p>This method is invoked immediately after the cache is cleared.</p>
     *
     * @param evt
     */
    void afterClear(CacheClearedEvent evt);

    /**
     * <p>This method is invoked immediately before the cache is cleared.</p>
     *
     *
     * @param evt
     */
    void beforeClear(CacheClearedEvent evt);
}
