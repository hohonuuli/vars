package vars.annotation;

import vars.knowledgebase.Concept;
import vars.DAO;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 2:38:18 PM
 * To change this template use File | Settings | File Templates.
 */
public interface VideoArchiveDAO extends DAO {

    /**
     * Return all reference numbers in the current videoArchive. The reference number
     * is found in Association with the 'linkName | toConcept | linkValue' of
     * 'identity-reference | self | [integer]' where integer is a value
     * egual to or greater than 0. This is used  so that the UI can list the
     * existing reference numbers for users to select from.
     *
     * @param  videoArchive The videoArchive of interest. This find will
     *          search all annotation in this video archive
     * @param  linkName The linkValue in the associations to search for
     * @return  A collection of String objects
     */
    Set<String> findAllLinkValues(VideoArchive videoArchive, String linkName);

    /**
     * Return all linkValues in the current videoArchive for a given concept and linkName.
     * For example to find all reference numbers in a videoArchive:
     *
     * The reference number is found in Association with the
     * 'linkName | toConcept | linkValue' of
     * 'identity-reference | self | [integer]' where integer is a value
     * egual to or greater than 0. This is used  so that the UI can list the
     * existing reference numbers for users to select from.
     *
     * @param  videoArchive The videoArchive of interest. This find will
     *          search all annotation in this video archive
     * @param  linkName The name of links to match
     * @param  concept If not null then only the linkvalues found for associations to
     *          this concept are returned
     * @return  A collection (SortedSet) of String objects
     */
    Set<String> findAllLinkValues(VideoArchive videoArchive, String linkName, Concept concept);

    /**
     * Looks up the @link{IVideoArchive} by name. If no match is found a new one is created and returned.
     * 
     * @param platform
     * @param sequenceNumber
     * @param videoArchiveName
     * @return
     */
    VideoArchive findOrCreateByParameters(String platform, int sequenceNumber, String videoArchiveName);

    /**
     * Lookup a @link{IVideoArchive} by name.
     * @param name
     * @return The matching object. <b>null</b> is returned if no match exists
     */
    VideoArchive findByName(String name);

    VideoArchive findByPrimaryKey(Object primaryKey);

    /**
     * Removes VideoFrames that do not contain observations.
     *
     * @param videoArchive
     * @return The updated VideoArchive
     */
    VideoArchive deleteEmptyVideoFrames(VideoArchive videoArchive);
}
