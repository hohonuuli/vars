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


package vars.shared.ui; 

import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptName;

//~--- interfaces -------------------------------------------------------------

/**
 * An interface type for notification that a <code>Concept</code> or
 * <code>ConceptName</code> object has been added or removed from the
 * <code>KnowledgeBase</code>.
 */
public interface ConceptChangeListener {

    /**
     * Call back for notification a <code>Concept</code> has been added.
     *
     * @param concept
     */
    void addedConcept(Concept concept);

    /**
     * Call back for notification a <code>ConceptName</code> has been added.
     *
     * @param conceptName
     */
    void addedConceptName(ConceptName conceptName);

    /**
     * Call back for notification a <code>Concept</code> has been removed.
     *
     * @param concept
     */
    void removedConcept(Concept concept);

    /**
     * Call back for notification a <code>ConceptName</code> has been removed.
     *
     * @param conceptName
     */
    void removedConceptName(ConceptName conceptName);
}
