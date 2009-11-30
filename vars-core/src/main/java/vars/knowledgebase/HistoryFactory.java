/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.knowledgebase;

import com.google.inject.Inject;
import java.util.Date;
import vars.UserAccount;

/**
 *
 * @author brian
 */
public class HistoryFactory {

    private final KnowledgebaseFactory knowledgebaseFactory;

    @Inject
    public HistoryFactory(KnowledgebaseFactory knowledgebaseFactory) {
        this.knowledgebaseFactory = knowledgebaseFactory;
    }

    private History newHistory(UserAccount userAccount, String action, String fieldName, String oldValue, String newValue) {
        final History history = knowledgebaseFactory.newHistory();
        history.setCreatorName(userAccount.getUserName());
        history.setCreationDate(new Date());
        history.setAction(action);
        history.setField(fieldName);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        return history;
    }

    /**
     * Create a History object
     *
     */
    public History add(UserAccount userAccount, ConceptName conceptName) {
        return newHistory(userAccount, History.ACTION_ADD, History.FIELD_CONCEPTNAME, null, conceptName.getName());
    }

    /**
     *
     * @param userAccount The UserAccount for the user adding a concept.
     * @param concept The concept that is being added
     * @return A History object representing a new Concept. This History object
     *  should be added to the parent of the Concept supplied as an argument
     */
    public History add(UserAccount userAccount, Concept concept) {
        return newHistory(userAccount, History.ACTION_ADD, History.FIELD_CONCEPT_CHILD, null,  concept.getPrimaryConceptName().getName());
    }

    public History add(UserAccount userAccount, LinkRealization linkRealization) {
        return newHistory(userAccount, History.ACTION_ADD, History.FIELD_LINKREALIZATION, null, linkRealization.stringValue());
    }

    public History add(UserAccount userAccount, LinkTemplate linkTemplate) {
        return newHistory(userAccount, History.ACTION_ADD, History.FIELD_LINKTEMPLATE, null, linkTemplate.stringValue());
    }

    public History add(UserAccount userAccount, Media media) {
        return newHistory(userAccount, History.ACTION_ADD, History.FIELD_MEDIA, null, media.getUrl());
    }



    public History delete(UserAccount userAccount, ConceptName conceptName) {
        return newHistory(userAccount, History.ACTION_DELETE, History.FIELD_CONCEPTNAME, conceptName.getName(), null);
    }

    /**
     * When deleting a Concept the History object should be added to the parent
     * of the Concept you are deleting.
     */
    public History delete(UserAccount userAccount, Concept concept) {
        return newHistory(userAccount, History.ACTION_DELETE, History.FIELD_CONCEPT_CHILD, concept.getPrimaryConceptName().getName(), null);
    }

    public History delete(UserAccount userAccount, LinkRealization linkRealization) {
        return newHistory(userAccount, History.ACTION_DELETE, History.FIELD_LINKREALIZATION, linkRealization.stringValue(), null);
    }

    public History delete(UserAccount userAccount, LinkTemplate linkTemplate) {
        return newHistory(userAccount, History.ACTION_DELETE, History.FIELD_LINKTEMPLATE, linkTemplate.stringValue(), null);
    }

    public History delete(UserAccount userAccount, Media media) {
        return newHistory(userAccount, History.ACTION_DELETE, History.FIELD_MEDIA, media.getUrl(), null);
    }



    public History replaceParentConcept(UserAccount userAccount, Concept oldParent, Concept newParent) {
        return newHistory(userAccount, History.ACTION_REPLACE, History.FIELD_CONCEPT_PARENT,
                oldParent.getPrimaryConceptName().getName(), newParent.getPrimaryConceptName().getName());
    }

    public History replaceOriginator(UserAccount userAccount, String oldOrig, String newOrig ) {
        return newHistory(userAccount, History.ACTION_REPLACE, History.FIELD_CONCEPT_ORIGINATOR, oldOrig, newOrig);
    }

    public History replaceRankName(UserAccount userAccount, String oldRankName, String newRankName) {
    	return newHistory(userAccount, History.ACTION_REPLACE, History.FIELD_CONCEPT_RANKNAME, oldRankName, newRankName);
    }

    public History replaceRankLevel(UserAccount userAccount, String oldRankLevel, String newRankLevel) {
    	return newHistory(userAccount, History.ACTION_REPLACE, History.FIELD_CONCEPT_RANKLEVEL, oldRankLevel, newRankLevel);
    }

    public History replaceStructureType(UserAccount userAccount, String oldSt, String newSt) {
        return newHistory(userAccount, History.ACTION_REPLACE, History.FIELD_CONCEPT_STRUCTURETYPE, oldSt, newSt);
    }

    public History replaceReference(UserAccount userAccount, String oldRef, String newRef) {
        return newHistory(userAccount, History.ACTION_REPLACE, History.FIELD_CONCEPT_REFERENCE, oldRef, newRef);
    }

    public History replaceConceptName(UserAccount userAccount, ConceptName oldName, ConceptName newName) {
        return newHistory(userAccount, History.ACTION_REPLACE, History.FIELD_CONCEPTNAME, oldName.stringValue(), newName.stringValue());
    }

    public History replacePrimaryConceptName(UserAccount userAccount, ConceptName oldName, ConceptName newName) {
        return newHistory(userAccount, History.ACTION_REPLACE, History.FIELD_CONCEPTNAME_PRIMARY, oldName.getName(), newName.getName());
    }

    public History replaceNodcCode(UserAccount userAccount, String oldCode, String newCode) {
        return newHistory(userAccount, History.ACTION_REPLACE, History.FIELD_CONCEPT_NODCCODE, oldCode, newCode);
    }

    public History replaceLinkTemplate(UserAccount userAccount, LinkTemplate oldValue, LinkTemplate newValue) {
        return newHistory(userAccount, History.ACTION_REPLACE, History.FIELD_LINKTEMPLATE, oldValue.stringValue(), newValue.stringValue());
    }

    public History replaceLinkRealization(UserAccount userAccount, LinkRealization oldValue, LinkRealization newValue) {
        return newHistory(userAccount, History.ACTION_REPLACE, History.FIELD_LINKREALIZATION, oldValue.stringValue(), newValue.stringValue());
    }

}

