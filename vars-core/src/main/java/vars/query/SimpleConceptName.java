/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.query;

import vars.knowledgebase.IConcept;
import vars.knowledgebase.IConceptName;

/**
 *
 * @author brian
 */
public class SimpleConceptName implements IConceptName {

    private String name;
    private String nameType;
    private IConcept concept;

    public SimpleConceptName(String name, String nameType) {
        this.name = name;
        this.nameType = nameType;
    }

    public String getAuthor() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public IConcept getConcept() {
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

    public void setConcept(IConcept concept) {
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
