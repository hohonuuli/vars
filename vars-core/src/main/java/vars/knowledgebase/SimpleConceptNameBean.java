/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.knowledgebase;


/**
 *
 * @author brian
 */
public class SimpleConceptNameBean implements ConceptName {

    private String name;
    private String nameType;
    private Concept concept;
    private String author;

    public SimpleConceptNameBean(String name, String nameType) {
        this.name = name;
        this.nameType = nameType;
    }

    public String getAuthor() {
        return author;
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
        this.author = author;
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
        return name;
    }
    
    public Object getPrimaryKey() {
    	return null;
    }

}
