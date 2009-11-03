package vars;

/**
 * Inteface for VARS link style classes (Association, LinkTemplate, LinkRealization)
 */
public interface ILink {

    String PROP_LINKNAME = "linkName";
    String PROP_LINKVALUE = "linkValue";
    String PROP_TOCONCEPT = "toConcept";
    String VALUE_NIL = "nil";
    String VALUE_SELF = "self";
    /**
     * Delimiter for String representations
     */
    String DELIMITER = " | ";
    String DELIMITER_REGEXP = " \\| ";

    String getFromConcept();

    String getLinkName();

    void setLinkName(String linkName);

    String getToConcept();

    void setToConcept(String toConcept);

    String getLinkValue();

    void setLinkValue(String linkValue);

    /**
     * @return A delimited representation of the data contents in the form:
     * [fromConcept]DELIMITER[linkName]DELIMITER[toConcept]DELIMITER[linkValue]
     */
    String stringValue();

}
