/*
 * @(#)ISectionInfo.java   2008.12.30 at 01:50:53 PST
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
public interface ISectionInfo extends IKnowledgebaseObject {

    String PROP_HEADER = "header";
    String PROP_INFORMATION = "information";
    String PROP_LABEL = "label";

    String HEADER_DESCRIPTION = "Description";


    String HEADER_REFERENCES = "References";


    String HEADER_USEFUL = "Useful and Interesting";


    String LABEL_ANECDOTE = "Anecdote";


    String LABEL_COMMERCIAL_USE = "Commercial Use";


    String LABEL_CONSERVATION = "Conservation";


    String LABEL_DEPTH_DISTRIB = "Depth Distribution";


    String LABEL_DESCRIPTION = "Description";


    String LABEL_HABITAT = "Habitat";


    String LABEL_LITERATURE_REF = "Literature Reference";


    String LABEL_LOCOMOTION = "Locomotion";


    String LABEL_RANGE = "Range";


    String LABEL_REPRODUCTION = "Reproduction";


    String LABEL_SIZE = "Size";


    String LABEL_SPATIAL_DISTRIB = "Spatial Distribution";


    String LABEL_USED_TO_COLLECT = "Used To Collect";

    
    String LABEL_WEB_REF = "Web Reference";

    /**
     * Gets the conceptDelegate attribute of the SectionInfo object
     * @return   The conceptDelegate value
     * @uml.property  name="conceptDelegate"
     */
    IConceptDelegate getConceptDelegate();

    /**
     * Gets the <code>SectionInfo</code> header.
     * @return     The String header of the <code>SectionInfo</code>
     * @uml.property  name="header"
     */
    String getHeader();

    /**
     * Gets the <code>SectionInfo</code> information field.
     * @return     The String information of the <code>SectionInfo</code>
     * @uml.property  name="information"
     */
    String getInformation();

    /**
     * Gets the <code>SectionInfo</code> label.
     * @return     The String label of the <code>SectionInfo</code>
     * @uml.property  name="label"
     */
    String getLabel();


    /**
     * Sets the <code>SectionInfo</code> 'header' attribute.
     * @param header    String header of the <code>SectionInfo</code>
     * @uml.property  name="header"
     */
    void setHeader(String header);


    /**
     * Sets the <code>SectionInfo</code> 'information' attribute.
     * @param information    String information of the <code>SectionInfo</code>
     * @uml.property  name="information"
     */
    void setInformation(String information);

    /**
     * Sets the <code>SectionInfo</code> 'label' attribute.
     * @param label    String label of the <code>SectionInfo</code>
     * @uml.property  name="label"
     */
    void setLabel(String label);

    /**
     * Gets a String representation of this <code>SectionInfo</code> suitable
     * for feeding to the method <code>createFromString()</code> to recreate the
     * content of a <code>SectionInfo</code>. This content is the same as that
     * used in the state-based <code>equals()</code> method.
     *
     * @return    A String representation of this <code>SectionInfo</code> in the format:
     * <pre>
     * header | label | information
     * </pre
     */
    String stringValue();
}
