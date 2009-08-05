/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.services;

import java.util.Collection;
import java.util.List;
import vars.IUserAccount;
import vars.annotation.IVideoArchive;
import vars.annotation.IVideoArchiveSet;

/**
 *
 * @author brian
 */
public interface AnnotationLookupService {

    Collection<IVideoArchiveSet> findAllVideoArchiveSets();

    List<IVideoArchive> findAllVideoArchives();

    IVideoArchive findVideoArchiveByName(String videoArchiveName);

    IUserAccount findUserAccountByName(String username);

}
