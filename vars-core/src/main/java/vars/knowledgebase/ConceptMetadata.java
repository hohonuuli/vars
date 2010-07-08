/*
 * @(#)IConceptMetadata.java   2008.12.30 at 01:50:53 PST
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

import java.util.Collection;
import java.util.Set;

/**
 *
 * @author brian
 */
public interface ConceptMetadata extends KnowledgebaseObject {

    String PROP_CONCEPT = "concept";
    String PROP_USAGE = "usage";

    /**
     * Returns boolean whether this <B>Concept </B> has a primary image
     * associated with it
     *
     * @return A boolean whether this <B>Concept </B> has a primary image.
     */
    boolean hasPrimaryImage();

    /**
     *
     * @return true if it contains a history that has not been approved or reject
     */
    boolean isPendingApproval();

    Media getPrimaryMedia(MediaTypes mediaType);


    Media getPrimaryImage();

    /**
     * Adds a <code>History</code> to this <code>Concept</code>.
     *
     *
     * @param  history              A <code>History</code> to be assigned to this <code>Concept</code>.
     * @return          Description of the Return Value
     * @see             History
     */
    void addHistory(History history);

    /**
     * Adds a <code>LinkRealization</code> object to the collection
     * maintained by this <code>Concept</code>.
     *
     *
     * @param  linkRealization              A <code>LinkRealization</code> with information referring
     * to this <code>Concept</code>.
     * @return                  Description of the Return Value
     * @see                     LinkRealization
     */
    void addLinkRealization(LinkRealization linkRealization);

    /**
     * Adds a <code>LinkTemplate</code> to the collection maintained by this
     * <code>Concept</code>.
     *
     *
     * @param  linkTemplate              A <code>LinkTemplate</code> to be assigned to this <code>Concept</code>
     * node.
     * @return               Description of the Return Value
     * @see                  LinkTemplate
     */
    void addLinkTemplate(LinkTemplate linkTemplate);

    /**
     * Adds a <code>Media</code> to the collection maintained by this <code>Concept</code>.
     *
     *
     * @param  media              The feature to be added to the Media attribute
     * @return        Description of the Return Value
     * @see           Media
     */
    void addMedia(Media media);

    void addArtifact(Artifact artifact);

    void removeArtifact(Artifact artifact);

    Collection<Artifact> getArtifacts();


    /**
     * Gets the concept attribute of the ConceptDelegate object
     * @return     The concept value
     */
    Concept getConcept();


    /**
     * @return
     */
    Collection<History> getHistories();


    /**
     * @return
     */
    Collection<LinkRealization> getLinkRealizations();

    /**
     * @return
     */
    Collection<LinkTemplate> getLinkTemplates();

    /**
     * @return
     */
    Collection<Media> getMedias();


    /**
     * @return
     */
    Usage getUsage();

    /**
     * Removes a <B>History </B> from this <B>Concept </B>.
     *
     *
     * @param  history              A <B>History </B> to remove from this <B>Concept </B>.
     * @return          <code>true</code> if the <code>History</code> object is
     * removed.
     * @see             History
     */
    void removeHistory(History history);

    /**
     * Removes a <B>LinkRealization </B> from this <B>Concept </B>.
     *
     *
     * @param  linkRealization              A <B>LinkRealization </B> to remove from this <B>Concept
     * </B>.
     * @return                  <code>true</code> if the <code>LinkRealization</code> is
     * removed.
     * @see                     LinkRealization
     */
    void removeLinkRealization(LinkRealization linkRealization);

    /**
     * Removes a <B>LinkTemplate </B> from this <B>Concept </B>.
     *
     *
     * @param  linkTemplate              A <B>LinkTemplate </B> to remove from this <B>Concept </B>.
     * @return               <code>true</code> if the <code>LinkTemplate</code> is
     * removed.
     * @see                  LinkTemplate
     */
    void removeLinkTemplate(LinkTemplate linkTemplate);

    /**
     * Removes a <B>Media </B> from this <B>Concept </B>.
     *
     *
     * @param  media              A <B>Media </B> to remove from this <B>Concept </B>.
     * @return        <code>true</code> if the <code>Media</code> object is
     * removed.
     * @see           Media
     */
    void removeMedia(Media media);


    /**
     * Sets the <code>Usage</code> for this <code>Concept</code>.
     * @param usage               A <code>Usage</code> to be assigned to this <code>Concept</code>  node.
     * @see  Usage
     */
    void setUsage(Usage usage);
}
