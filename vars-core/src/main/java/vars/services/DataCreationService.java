package vars.services;

import vars.annotation.IAssociation;
import vars.annotation.IObservation;
import vars.annotation.IVideoFrame;
import vars.annotation.IVideoArchive;
import vars.annotation.IVideoArchiveSet;
import vars.IUserAccount;
import vars.knowledgebase.IConcept;
import vars.knowledgebase.IConceptDelegate;
import vars.knowledgebase.IConceptName;
import vars.knowledgebase.IHistory;
import vars.knowledgebase.ILinkRealization;
import vars.knowledgebase.ILinkTemplate;
import vars.knowledgebase.IMedia;
import vars.knowledgebase.ISectionInfo;
import vars.knowledgebase.IUsage;

/**
 * Interface for creating new classes
 */
public interface DataCreationService {

    /* --- Annotation --- */

    IAssociation newAssociation();

    IObservation newObservation();

    IVideoFrame newVideoFrame();

    IVideoArchive newVideoArchive();

    IVideoArchiveSet newVideoArchiveSet();

    /* --- Knowledgebase --- */

    IConcept newConcept();

    IConceptDelegate newConceptDelegate();

    IConceptName newConceptName();

    IHistory newHistory();

    ILinkRealization newLinkRealization();

    ILinkTemplate newLinkTemplate();

    IMedia newMedia();

    ISectionInfo newSectionInfo();

    IUsage newUsage();

    /* --- Misc --- */

    IUserAccount newUserAccount();


}
