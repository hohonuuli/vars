package vars.knowledgebase;

import vars.knowledgebase.IConcept;
import vars.knowledgebase.IConceptName;
import vars.knowledgebase.IHistory;
import vars.knowledgebase.ILinkRealization;
import vars.knowledgebase.ILinkTemplate;
import vars.knowledgebase.IMedia;
import vars.knowledgebase.IUsage;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 6, 2009
 * Time: 2:50:58 PM
 * To change this template use File | Settings | File Templates.
 */
public interface KnowledgebaseFactory {

    /* --- Knowledgebase --- */

    IConcept newConcept();

    IConceptName newConceptName();

    IHistory newHistory();

    ILinkRealization newLinkRealization();

    ILinkTemplate newLinkTemplate();

    IMedia newMedia();


    IUsage newUsage();
}
