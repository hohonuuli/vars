package vars.annotation;

import vars.knowledgebase.IConcept;
import vars.IDAO;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 2:38:18 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IVideoArchiveDAO extends IDAO {

    /**
     * Return all reference numbers in the current videoArchive. The reference number
     * is found in Association with the 'linkName | toConcept | linkValue' of
     * 'identity-reference | self | [integer]' where integer is a value
     * egual to or greater than 0. This is used  so that the UI can list the
     * existing reference numbers for users to select from.
     *
     * @param  videoArchive The videoArchive of interest. This find will
     *          search all annotation in this video archive
     * @return  A collection of String objects
     */
    Set<String> findAllReferenceNumbers(IVideoArchive videoArchive);

    /**
     * Return all reference numbers in the current videoArchive for a given concept.
     * The reference number is found in Association with the
     * 'linkName | toConcept | linkValue' of
     * 'identity-reference | self | [integer]' where integer is a value
     * egual to or greater than 0. This is used  so that the UI can list the
     * existing reference numbers for users to select from.
     *
     * @param  videoArchive The videoArchive of interest. This find will
     *          search all annotation in this video archive
     * @param  concept Description of the Parameter
     * @return  A collection (SortedSet) of String objects
     */
    Set<String> findAllReferenceNumbers(IVideoArchive videoArchive, IConcept concept);

    /**
     * Looks up the @link{IVideoArchive} by name. If no match is found a new one is created and returned.
     * 
     * @param platform
     * @param sequenceNumber
     * @param videoArchiveName
     * @return
     */
    IVideoArchive findOrCreateByParameters(String platform, int sequenceNumber, String videoArchiveName);

    /**
     * Lookup a @link{IVideoArchive} by name.
     * @param name
     * @return The matching object. <b>null</b> is returned if no match exists
     */
    IVideoArchive findByName(String name);
}
