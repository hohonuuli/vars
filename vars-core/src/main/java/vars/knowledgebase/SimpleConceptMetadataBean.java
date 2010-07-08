/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.knowledgebase;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author brian
 */
public class SimpleConceptMetadataBean implements ConceptMetadata {

    private final Set<History> histories = new HashSet<History>();
    private final Set<LinkRealization> linkRealizations = new HashSet<LinkRealization>();
    private final Set<LinkTemplate> linkTemplates = new HashSet<LinkTemplate>();
    private final Set<Media> medias = new HashSet<Media>();
    private Concept concept;

    public SimpleConceptMetadataBean() {
    }



    public SimpleConceptMetadataBean(Concept concept) {
        this.concept = concept;
    }

    public boolean hasPrimaryImage() {
        return false;
    }

    public Media getPrimaryMedia(MediaTypes mediaType) {
        return null;
    }

    public Media getPrimaryImage() {
        return null;
    }

    public void addArtifact(Artifact artifact) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeArtifact(Artifact artifact) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addHistory(History history) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addLinkRealization(LinkRealization linkRealization) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addLinkTemplate(LinkTemplate linkTemplate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addMedia(Media media) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Concept getConcept() {
        return concept;
    }

    public Collection<Artifact> getArtifacts() {
        return new HashSet<Artifact>();
    }

    public Set<History> getHistories() {
        return new HashSet<History>(histories); // return defensive copy
    }

    public Set<LinkRealization> getLinkRealizations() {
        return new HashSet<LinkRealization>(linkRealizations); // return defensive copy
    }

    public Set<LinkTemplate> getLinkTemplates() {
        return new HashSet<LinkTemplate>(linkTemplates); // return defensive copy
    }

    public Set<Media> getMedias() {
        return new HashSet<Media>(medias); // return defensive copy
    }

    public Usage getUsage() {
        return null;
    }

    public void removeHistory(History history) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeLinkRealization(LinkRealization linkRealization) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeLinkTemplate(LinkTemplate linkTemplate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeMedia(Media media) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setUsage(Usage usage) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isPendingApproval() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public Object getPrimaryKey() {
    	return null;
    }

}
