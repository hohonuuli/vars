/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
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

    Collection<String> findAllReferenceNumbers(VideoArchiveSet videoArchiveSet, Concept concept);

    /**
     * Find Descendant Names as a sorted List of Strings
     * @param concept
     * @return
     */
    List<String> findDescendantNamesFor(Concept concept);

    List<String> findAllVideoArchiveNames();

    void updateConceptNameUsedByAnnotations(Concept concept);

    /**
     * Find all platforms that were used in the annotations database.
     *
     * @return A List of each distinct platform found in the VideoArchiveSet table.
     */
    List<String> findAllPlatformNames();

    Date findEarliestAnnotationDate();

    /**
     * Find the recorded date (from VideoFrame) of the latest annotation made in the VARS database
     * @return The date of the latest annotation
     */
    Date findLatestAnnotationDate();

    /**
     * Find all Observations that represent a single siting in a particular VideoArchive
     * @param videoArchive THe videoArchive of interest
     * @param conceptName The concept of interest
     * @param referenceNumber The reference-number used to tag a particular siting
     * @return
     */
    Collection<Observation> findAllObservationsByNameAndReferenceNumber(VideoArchive videoArchive,
                                                                        String conceptName,
                                                                        int referenceNumber);

    /**
     * Lookup the id of a VideoFrame by timecode and videoarchive name
     * @param timecode The timecode of interest
     * @param videoArchiveName The VideoArchive of intereste
     * @return The primary key of the VideoFrame or null if no match was found
     */
    Long findTimeCodeByVideoArchiveName(String timecode, String videoArchiveName);

    /**
     * Look up all camera platforms found in the database.
     * @return
     */
    List<String> findAllCameraPlatforms();

}
