/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation;

import java.util.Collection;
import java.util.List;

import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.LinkTemplate;

/**
 * DAO used by the Annotation application for special operations
 * 
 * @author brian
 */
public interface AnnotationPersistenceService {

    boolean doesConceptNameExist(String conceptname);
    
    
    /**
     * Provides a quick lookup of a concept by name. This is an optimized
     * routine that only returns concepts you intend to read. Modifiying
     * concepts in the annotation ui could cause Persistence Exceptions since
     * no effort is being made to hang on to the reference of modified concepts
     * 
     * @param name
     * @return
     */
    Concept findConceptByName(String name);
    
    Concept findRootConcept();
    
    /**
     * Retrieve the underlying {@link ConceptDAO} used.
     * @return
     */
    ConceptDAO getReadOnlyConceptDAO();
    
    Collection<LinkTemplate> findLinkTemplatesFor(Concept concept);
    
    Collection<Integer> findAllReferenceNumbers(VideoArchiveSet videoArchiveSet, Concept concept);

    /**
     * Find Descendant Names as a sorted List of Strings
     * @param concept
     * @return
     */
    List<String> findDescendantNamesFor(Concept concept);

    List<String> findAllVideoArchiveNames();
    

}
