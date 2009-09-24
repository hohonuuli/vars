/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.query;

import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptName;

/**
 *
 * @author brian
 */
public class SimpleConceptName implements ConceptName {

    private String name;
    private String nameType;
    private Concept concept;

    public SimpleConceptName(String name, String nameType) {
        this.name = name;
        this.nameType = nameType;
    }

    public String getAuthor() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Concept getConcept() {
        return concept;
    }

    public String getName() {
        return name;
    }

    public String getNameType() {
        return nameType;
    }

    public void setAuthor(String author) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setConcept(Concept concept) {
        this.concept = concept;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNameType(String nameType) {
        this.nameType = nameType;
    }

    public String stringValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
