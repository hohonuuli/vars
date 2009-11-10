/*
 * @(#)IMedia.java   2008.12.30 at 01:50:53 PST
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

/**
 *
 * @author brian
 */
public interface Media extends KnowledgebaseObject {

    String TYPE_ICON = "Icon";
    String TYPE_IMAGE = "Image";
    String TYPE_VIDEO = "Video";
    String[] TYPES = { TYPE_ICON, TYPE_IMAGE, TYPE_VIDEO };

    String PROP_CAPTION = "caption";
    String PROP_CREDIT = "credit";
    String PROP_TYPE = "type";
    String PROP_URL = "url";

    /**
     * Gets the <code>Media</code> 'caption' attribute.
     * @return     The String caption of the <code>Media</code>
     */
    String getCaption();

    /**
     * Gets the conceptDelegate attribute of the Media object
     * @return   The conceptDelegate value
     */
    ConceptMetadata getConceptMetadata();

    /**
     * Gets the <code>Media</code> 'credit' attribute.
     * @return     The String credit of the <code>Media</code>
     */
    String getCredit();


    /**
     * Gets the <code>Media</code> 'type' attribute.
     * @return     The Type of the <code>Media</code>.
     */
    String getType();

    /**
     * @return  the url
     */
    String getUrl();

    /**
     * Gets the <code>Media</code> 'primary' attribute.
     *
     * @return    The boolean primary of the <code>Media</code>
     */
    Boolean isPrimary();

    /**
     * Sets the <code>Media</code> 'caption' attribute.
     * @param caption    String caption of the <code>Media</code>
     */
    void setCaption(String caption);



    /**
     * Sets the <code>Media</code> 'credit' attribute.
     * @param credit    String credit of the <code>Media</code>
     */
    void setCredit(String credit);

    /**
     * Sets the <code>Media</code> 'primary' attribute.
     * @param primary    boolean primary of the <code>Media</code>
     */
    void setPrimary(Boolean primary);

    /**
     * Sets the <code>Media</code> 'type' attribute.
     * @param type    String representation of a Type for this <code>Media</code>
     */
    void setType(String type);

    /**
     * @param url  the url to set
     */
    void setUrl(String url);

    /**
     * Gets a String representation of this <code>Media</code> suitable
     * for feeding to the method <code>createFromString()</code> to recreate the
     * content of a <code>Media</code>. This content is the same as that
     * used in the state-based <code>equals()</code> method.
     *
     * @return    A String representation of this <code>Media</code> in the format:
     * <pre>
     * fileName | type | primary (or additional) | credit | caption
     * </pre
     */
    String stringValue();
}
