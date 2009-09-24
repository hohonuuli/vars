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


package vars.query;

import org.mbari.sql.QueryResults;

//~--- interfaces -------------------------------------------------------------

/**
 * <p><!-- Insert Description --></p>
 *
 * @author Brian Schlining
 * @version $Id: IQueryable.java 311 2006-07-07 23:01:17Z hohonuuli $
 */
public interface IQueryable {

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param query
     *
     * @return
     *
     * @throws Exception
     */
    QueryResults executeQuery(String query) throws Exception;
}
