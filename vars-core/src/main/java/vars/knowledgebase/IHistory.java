/*
 * @(#)IHistory.java   2008.12.30 at 01:50:53 PST
 *
 * Copyright 2007 MBARI
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



package vars.knowledgebase;

import java.util.Date;
import vars.IUserAccount;

/**
 *
 * @author brian
 */
public interface IHistory {

    String PROP_ACTION = "action";
    String PROP_APPROVAL_DATE = "approvalDate";
    String PROP_APPROVER_NAME = "approverName";
    String PROP_COMMENT = "comment";
    String PROP_CREATION_DATE = "creationDate";
    String PROP_CREATOR_NAME = "creatorName";
    String PROP_FIELD = "field";
    String PROP_NEW_VALUE = "newValue";
    String PROP_OLD_VALUE = "oldValue";

    /**
     * String representation of the add action.
     */
    String ACTION_ADD = "ADD";

    /**
     * String representation of the delete action.
     */
    String ACTION_DELETE = "DELETE";

    /**
     * Prefix for action that has been rejected.
     */
    String ACTION_REJECT = "REJECT:";

    /**
     * String representation of the replace action.
     */
    String ACTION_REPLACE = "REPLACE";

    /**
     * Field description
     */
    String FIELD_CONCEPT = "Concept";
    String FIELD_CONCEPTNAME = "ConceptName";
    String FIELD_CONCEPTNAME_AUTHOR = "ConceptName.author";
    String FIELD_CONCEPTNAME_PRIMARY = "Concept.primaryConceptName";
    String FIELD_CONCEPT_CHILD = "Concept.child";
    String FIELD_CONCEPT_NODCCODE = "NodcCode";
    String FIELD_CONCEPT_ORIGINATOR = "Originator";
    String FIELD_CONCEPT_PARENT = "Concept.parent";
    String FIELD_CONCEPT_RANKLEVEL = "RankLevel";
    String FIELD_CONCEPT_RANKNAME = "RankName";
    String FIELD_CONCEPT_REFERENCE = "Reference";
    String FIELD_CONCEPT_STRUCTURETYPE = "StructureType";
    String FIELD_LINKREALIZATION = "LinkRealization";
    String FIELD_LINKTEMPLATE = "LinkTemplate";
    String FIELD_MEDIA = "Media";
    String FIELD_SECTIONINFO = "SectionInfo";

    /**
     * Special Date used to indicate the approval Date has not been set.
     */
    Date NOT_APPROVED = null;


    /**
     * Gets the action of this <code>History</code> as a String.
     * @return  The action of this <code>History</code>.
     */
    String getAction();

    /**
     * Gets the approval date of this <code>History</code>.
     * @return  The approval date of this <code>History</code>.
     */
    Date getApprovalDate();

    /**
     * @return  the approverName
     */
    String getApproverName();

    /**
     * Method description
     * @return
     */
    String getComment();

    /**
     * Not for Developer use. This is required for Castor/DAO
     * @return  The conceptDelegate value
     */
    IConceptDelegate getConceptDelegate();

    /**
     * Gets the creation date of this <code>History</code>.
     *
     * @return The creation date of this <code>History</code>.
     */
    Date getCreationDate();

    /**
     * Gets the previous value of the field name from the description of this <code>History</code>. This value represents the value of the field name before the action occurred.
     * @return  The previous value of the field name from the description of this  <code>History</code>.
     */
    String getCreatorName();

    /**
     * @return
     */
    String getField();

    /**
     * Method description
     * @return
     */
    String getNewValue();

    /**
     * Method description
     * @return
     */
    String getOldValue();

    /**
     * @return
     */
    boolean getRejected();

    /**
     * Determines whether the action of this <code>History</code> is an add.
     *
     * @return <code>true</code> if the action of this <code>History</code>
     * is an add.
     */
    boolean isAdd();

    /**
     * Gets whether this <code>History</code> has been approved.
     *
     * NOTE: Changes that are not approved are stored in the database with an
     * approvalDtg == 0 (ie 1/1/1970)
     *
     * @return <code>true</code> if this <code>History</code> has been
     * approved.
     */
    boolean isApproved();

    /**
     * Determines whether the action of this <code>History</code> is a delete.
     *
     * @return <code>true</code> if the action of this <code>History</code>
     * is a delete.
     */
    boolean isDelete();

    /**
     * Determines whether this <code>History</code> was rejected.
     *
     * @return <code>true</code> if this <code>History</code> was rejected.
     */
    boolean isRejected();

    /**
     * Determines whether the action of this <code>History</code> is a
     * replace.
     *
     * @return <code>true</code> if the action of this <code>History</code>
     * is an replace.
     */
    boolean isReplace();


    /**
     * Method description
     * @param  action
     * @uml.property  name="action"
     */
    void setAction(String action);

    /**
     * Sets the approval Date this <code>History</code>. DO NOT CALL THIS DIRECTLY! USE <code>approve()</code> INSTEAD.
     * @param approvalDate  The approval Data of this <code>History</code>.
     * @thows  IllegalArgumentException If the approval Date is <code>null</code>.
     * @uml.property  name="approvalDate"
     */
    void setApprovalDate(Date approvalDate);

    /**
     * @param approverName  the approverName to set
     * @uml.property  name="approverName"
     */
    void setApproverName(String approverName);

    /**
     * Method description
     * @param  comment
     * @uml.property  name="comment"
     */
    void setComment(String comment);


    /**
     * Sets the Date the user created this <code>History</code>. Necessary for Castor. Developers should use appropriate constructor.
     * @param creationDate  The Date the user created this <code>History</code>.
     * @thows  IllegalArgumentException If the creation Date is <code>null</code>.
     */
    void setCreationDate(Date creationDate);

    /**
     * Sets the name of the user that created this <code>History<code>.
     * @param creatorName                      The name of the user that created this <code>History<code>.
     */
    void setCreatorName(String creatorName);

    /**
     * Method description
     * @param  field
     */
    void setField(String field);

    /**
     * Method description
     * @param  newValue
     * @uml.property  name="newValue"
     */
    void setNewValue(String newValue);

    /**
     * Method description
     * @param  oldValue
     * @uml.property  name="oldValue"
     */
    void setOldValue(String oldValue);

    /**
     * Method description
     * @param  rejected
     * @uml.property  name="rejected"
     */
    void setRejected(boolean rejected);

    String stringValue();
}
