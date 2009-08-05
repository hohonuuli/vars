/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.services;

import java.util.Collection;
import vars.knowledgebase.IConcept;
import vars.knowledgebase.IConceptName;

/**
 *
 * @author brian
 */
public interface KnowledgebaseLookupService {

    IConcept findConceptByName(String name);

    IConcept findConceptRoot();

    Collection<IConceptName> findConceptNamesBySubstring(String substring);

    Collection<IConceptName> findConceptNamesStartingWith(String s);

}
