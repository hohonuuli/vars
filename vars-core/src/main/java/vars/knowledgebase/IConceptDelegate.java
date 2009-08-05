/*
 * @(#)IConceptDelegate.java   2008.12.30 at 01:50:53 PST
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

import java.util.Set;

/**
 *
 * @author brian
 */
public interface IConceptDelegate {

    String PROP_CONCEPT = "concept";
    String PROP_USAGE = "usage";

    /**
     * Returns boolean whether this <B>Concept </B> has a primary image
     * associated with it
     *
     * @return A boolean whether this <B>Concept </B> has a primary image.
     */
    boolean hasPrimaryImage();


    IMedia getPrimaryImage();

    /**
     * Adds a <code>History</code> to this <code>Concept</code>.
     *
     *
     * @param  history              A <code>History</code> to be assigned to this <code>Concept</code>.
     * @return          Description of the Return Value
     * @see             IHistory
     */
    boolean addHistory(IHistory history);

    /**
     * Adds a <code>LinkRealization</code> object to the collection
     * maintained by this <code>Concept</code>.
     *
     *
     * @param  linkRealization              A <code>LinkRealization</code> with information referring
     * to this <code>Concept</code>.
     * @return                  Description of the Return Value
     * @see                     ILinkRealization
     */
    boolean addLinkRealization(ILinkRealization linkRealization);

    /**
     * Adds a <code>LinkTemplate</code> to the collection maintained by this
     * <code>Concept</code>.
     *
     *
     * @param  linkTemplate              A <code>LinkTemplate</code> to be assigned to this <code>Concept</code>
     * node.
     * @return               Description of the Return Value
     * @see                  ILinkTemplate
     */
    boolean addLinkTemplate(ILinkTemplate linkTemplate);

    /**
     * Adds a <code>Media</code> to the collection maintained by this <code>Concept</code>.
     *
     *
     * @param  media              The feature to be added to the Media attribute
     * @return        Description of the Return Value
     * @see           IMedia
     */
    boolean addMedia(IMedia media);

    /**
     * Adds a <code>SectionInfo</code> to the collection maintained by this
     * <code>Concept</code>.
     *
     *
     * @param  sectionInfo              A <code>SectionInfo</code> to be assigned to this <code>Concept</code>
     * node.
     * @return              Description of the Return Value
     * @see                 ISectionInfo
     */
    boolean addSectionInfo(ISectionInfo sectionInfo);

    /**
     * Gets the concept attribute of the ConceptDelegate object
     * @return     The concept value
     */
    IConcept getConcept();


    /**
     * @return
     */
    Set<? extends IHistory> getHistorySet();


    /**
     * @return
     */
    Set<? extends ILinkRealization> getLinkRealizationSet();

    /**
     * @return
     */
    Set<? extends ILinkTemplate> getLinkTemplateSet();

    /**
     * @return
     */
    Set<? extends IMedia> getMediaSet();

    /**
     * @return
     */
    Set<? extends ISectionInfo> getSectionInfoSet();

    /**
     * @return
     * @uml.property  name="usage"
     */
    IUsage getUsage();

    /**
     * Removes a <B>History </B> from this <B>Concept </B>.
     *
     *
     * @param  history              A <B>History </B> to remove from this <B>Concept </B>.
     * @return          <code>true</code> if the <code>History</code> object is
     * removed.
     * @see             IHistory
     */
    boolean removeHistory(IHistory history);

    /**
     * Removes a <B>LinkRealization </B> from this <B>Concept </B>.
     *
     *
     * @param  linkRealization              A <B>LinkRealization </B> to remove from this <B>Concept
     * </B>.
     * @return                  <code>true</code> if the <code>LinkRealization</code> is
     * removed.
     * @see                     ILinkRealization
     */
    boolean removeLinkRealization(ILinkRealization linkRealization);

    /**
     * Removes a <B>LinkTemplate </B> from this <B>Concept </B>.
     *
     *
     * @param  linkTemplate              A <B>LinkTemplate </B> to remove from this <B>Concept </B>.
     * @return               <code>true</code> if the <code>LinkTemplate</code> is
     * removed.
     * @see                  ILinkTemplate
     */
    boolean removeLinkTemplate(ILinkTemplate linkTemplate);

    /**
     * Removes a <B>Media </B> from this <B>Concept </B>.
     *
     *
     * @param  media              A <B>Media </B> to remove from this <B>Concept </B>.
     * @return        <code>true</code> if the <code>Media</code> object is
     * removed.
     * @see           IMedia
     */
    boolean removeMedia(IMedia media);

    /**
     * Removes a <B>SectionInfo </B> from this <B>Concept </B>.
     *
     *
     * @param  sectionInfo              A <B>SectionInfo </B> to remove from this <B>Concept </B>.
     * @return              <code>true</code> if the <code>SectionInfo</code> is
     * removed.
     * @see                 ISectionInfo
     */
    boolean removeSectionInfo(ISectionInfo sectionInfo);



    /**
     * Sets the <code>Usage</code> for this <code>Concept</code>.
     * @param usage               A <code>Usage</code> to be assigned to this <code>Concept</code>  node.
     * @see  Usage
     * @uml.property  name="usage"
     */
    void setUsage(IUsage usage);
}
