/*
 * @(#)IConceptName.java   2008.12.30 at 01:50:53 PST
 *
 * Copyright 2007 MBARI
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



package vars.knowledgebase;

/**
 * <h2><u>Description</u></h2> <p><!--Insert summary here--></p> <h2><u>UML</u></h2> <pre> </pre> <h2><u>License</u></h2> <p><font size="-1" color="#336699"><a href="http://www.mbari.org"> The Monterey Bay Aquarium Research Institute (MBARI)</a> provides this documentation and code &quot;as is&quot;, with no warranty, express or implied, of its quality or consistency. It is provided without support and without obligation on the part of MBARI to assist in its use, correction, modification, or enhancement. This information should not be published or distributed to third parties without specific written permission from MBARI.</font></p> <p><font size="-1" color="#336699">Copyright 2003 MBARI. MBARI Proprietary Information. All rights reserved.</font></p>
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: IConceptName.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public interface ConceptName extends KnowledgebaseObject {

    /**
     *  Used when the author of a conceptname is unknown
     */
    String AUTHOR_UNKNOWN = "unknown";

    String NAME_DEFAULT = "object";

    String NAME_UNKNOWN = "unknown";

    String PROP_NAME = "name";
    String PROP_NAME_TYPE = "nameType";
    String PROP_AUTHOR = "author";
    String PROP_CONCEPT = "concept";


    /**
     * @return
     */
    String getAuthor();

    /**
     * @return
     */
    Concept getConcept();

    /**
     * @return
     */
    String getName();

    /**
     * @return
     */
    String getNameType();

    /**
     * @param  author
     */
    void setAuthor(String author);

    void setConcept(Concept concept);


    /**
     * <p><!-- Method description --></p>
     * @param  name
     * @uml.property  name="name"
     */
    void setName(String name);

    /**
     * <p><!-- Method description --></p>
     * @param  nameType
     * @uml.property  name="nameType"
     */
    void setNameType(String nameType);

    String stringValue();
    
}
