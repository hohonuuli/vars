/*
 * @(#)IConcept.java   2008.12.30 at 01:50:53 PST
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

import vars.ILazy;

import java.util.List;
import java.util.Set;

/**
 *
 * @author brian
 */
public interface Concept extends KnowledgebaseObject, ILazy {

    String ORIGINATOR_UNKNOWN = "unknown";

    String PROP_NODC_CODE = "nodcCode";
    String PROP_ORIGINATOR = "originator";
    String PROP_RANK_LEVEL = "rankLevel";
    String PROP_RANK_NAME = "rankName";
    String PROP_REFERENCE  = "reference";
    String PROP_STRUCTURE_TYPE = "structureType";
    String PROP_PARENT_CONCEPT = "parentConcept";

    /**
     * Adds a child <code>Concept</code> object to this <code>Concept</code>.
     *
     * @param child
     * A <code>Concept</code> object identified as a child of this
     * <code>Concept</code> in the <code>KnowledgeBase</code>
     * hierarchy.
     */
    void addChildConcept(Concept child);

    /**
     * Associates a <code>ConceptName</code> to this <code>Concept</code>.
     * If a ConceptName is primary and it's added to a concept that already has
     * a primary conceptName, then the existing primary name is changed to type
     * NAMETYPE_SYNONYM while the new one becomes the primary name.
     *
     * @param conceptName
     * A <code>ConceptName</code> to associate with this <code>Concept</code>.
     * @return Description of the Return Value
     * @see ConceptName
     */
    void addConceptName(ConceptName conceptName);


    /**
     * Returns an array of the <code>Concept</code> objects identified as
     * immediately below this <code>Concept</code> in the <code>KnowledgeBase</code>
     * hierarchy.
     *
     * @return An array of the children <code>Concept</code> objects of this
     * <code>Concept</code>.
     *
     */
    List<Concept> getChildConcepts();

    /**
     * <p> <strong>This method should not be called directly by an application developer. </strong> </p> <p> In order to speed up database transactions most of the anxillary classes are now associated with a delegate rather than directly to a <code>Concept</code>. This delegate is lazy loaded using <code>IDAO</code> objects because castor does not support lazy loading. This <em>should</em> speed up transactions greatly. </p>
     * @return  The conceptDelegate which matintains associations to anxillary  information classes.
     */
    ConceptMetadata getConceptMetadata();

    /**
     * Gets the <code>ConceptName</code> object of the specified String name.
     * Returns <code>null</code> if this <code>Concept</code> has no such
     * associated <code>ConceptName</code>.
     *
     * @param name
     * Description of the Parameter
     * @return The <code>ConceptName</code> object of the specified String
     * name; <code>null</code> if this <code>Concept</code> has no
     * such <code>ConceptName</code>.
     * @see ConceptName
     */
    ConceptName getConceptName(String name);

    /**
     * This method returns a defensive copy. Adds or removes will have no affect on the database. However, modifications to the objects can be persisted.
     * @return  The collection of ConceptNames associated with this Concept. This  a syncrhonized collection so you will need to synchronize on it before iterating.
     * @uml.property  name="conceptNameColl"
     */
    Set<ConceptName> getConceptNames();

    /**
     * Gets the nodcCode attribute of the Concept object
     * @return  The nodcCode value
     */
    String getNodcCode();

    /**
     * Gets the String name of the originator of the placement of this <code>Concept</code> in the Knowledge Base heirarchy. (i.e The name of the person who entered this concept into the Knowledge Base)
     * @return  The String name of the originator of the placement of this  <code>Concept</code> in the Knowledge Base heirarchy.
     */
    String getOriginator();

    /**
     * Gets the parentConcept <code>Concept</code> of this <code>Concept</code>. <code>Concept</code> objects at the root of the <code>KnowledgeBase</code> hierarchy do not have a parentConcept <code>Concept</code>.
     * @return  The parentConcept <code>Concept</code> of this <code>Concept</code>.  null if no parent is found.
     */
    Concept getParentConcept();


    /**
     * Gets the unique <code>ConceptName</code> object which represents the primary name of this <code>Concept</code>.
     * @return  The unique <code>ConceptName</code> object which represents  the primary name of this <code>Concept</code>.
     * @see  ConceptName
     */
    ConceptName getPrimaryConceptName();


    /**
     * Gets the rankLevel attribute of the Concept object
     * @return  The rankLevel value
     */
    String getRankLevel();

    /**
     * Gets the rankName attribute of the Concept object
     * @return  The rankName value
     */
    String getRankName();

    /**
     * Gets the reference attribute of the Concept object
     * @return  The reference value
     */
    String getReference();

    /**
     * Returns the root concept of this branch of concepts
     * @return The root concept
     */
    Concept getRootConcept();


    /**
     * Gets the structure type of this <code>Concept</code>.
     * @return  The structure type of this <code>Concept</code>.
     */
    String getStructureType();

    /**
     * Indicates whether this <B>Concept </B> has any child concepts in the
     * hierarchy.
     *
     * @return A boolean whether this <B>Concept </B> has any children.
     */
    boolean hasChildConcepts();

    /**
     * Searches down through the concept heirarchy to see if this concept or
     * one of its descendents contains a ConceptName that matches the argument
     *
     * @param child
     * The String name to search for. This is compared against all
     * ConceptNames that this concept and its descendents contain.
     * @return true if this concept or one of its descendents has a ConceptName
     * that matches the argument.
     */
    boolean hasDescendent(String child);

    /**
     * Returns boolean whether this <B>Concept </B> has details associated with
     * it (it is more than just a ConceptName).
     *
     * @return A boolean whether this <B>Concept </B> has associated details.
     */
    boolean hasDetails();

    /**
     * Indicates whether this <code>Concept</code> has a parentConcept <code>Concept</code>
     * in the hierarchy of the <code>KnowledgeBase</code>.
     *
     * @return <code>true</code> if this <code>Concept</code> has a
     * parentConcept <code>Concept</code> in the hierarchy of the
     * <code>KnowledgeBase</code>;<code>false</code> otherwise.
     */
    boolean hasParent();


    /**
     * Removes a child <B>Concept </B> from this <B>Concept </B>.
     *
     * @param childConcept
     * A <B>Concept </B> to remove from this <B>Concept </B>.
     * @return <code>true</code> if the child <code>Concept</code> is
     * removed.
     * @see Concept
     */
    void removeChildConcept(Concept childConcept);

    /**
     * Removes the specified <B>ConceptName</B> from this <B>Concept </B>.
     *
     * @param conceptName
     * The <B>ConceptName </B> to remove from this <B>Concept </B>.
     * @return
     * <code>true</code> if successfully removed.
     * @see ConceptName
     */
    void removeConceptName(ConceptName conceptName);


    /**
     * Sets the nodcCode attribute of the Concept object
     * @param nodcCode  The new nodcCode value
     */
    void setNodcCode(String nodcCode);

    /**
     * Sets the originator attribute of the Concept object
     * @param originator  The new originator value
     */
    void setOriginator(String originator);

    /**
     * Sets the rankLevel attribute of the Concept object
     * @param rankLevel  The new rankLevel value
     */
    void setRankLevel(String rankLevel);

    /**
     * Sets the rankName attribute of the Concept object
     * @param rankName  The new rankName value
     */
    void setRankName(String rankName);

    /**
     * Sets the reference attribute of the Concept object
     * @param reference  The new reference value
     */
    void setReference(String reference);

    /**
     * Sets the structureType attribute of the Concept object. Valid values are Concept.TAXONOMY, Concept>LITHOLOGY
     * @param structureType  The new structureType value
     */
    void setStructureType(String structureType);

    void setTaxonomyType(String taxonomyType);

    String getTaxonomyType();

  
}
