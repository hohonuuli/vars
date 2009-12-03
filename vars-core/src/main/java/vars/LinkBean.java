package vars;

/**
 * Generic link. Useful for parsing string representations of links.
 */
public class LinkBean implements ILink {

    private String linkName;
    private String toConcept;
    private String linkValue;
    private String fromConcept;

    public LinkBean() {}

    public LinkBean(String linkName, String toConcept, String linkValue) {
        this.linkName = linkName;
        this.toConcept = toConcept;
        this.linkValue = linkValue;
    }

    public LinkBean(String linkName, String toConcept, String linkValue, String fromConcept) {
        this.linkName = linkName;
        this.toConcept = toConcept;
        this.linkValue = linkValue;
        this.fromConcept = fromConcept;
    }

    public LinkBean(String stringRepresentation) {
        String[] tokens = stringRepresentation.split(DELIMITER_REGEXP);

        if (tokens.length == 3) {
            linkName = tokens[0];
            toConcept = tokens[1];
            linkValue = tokens[2];
        }
        else if (tokens.length == 4){
            fromConcept = tokens[0];
            linkName = tokens[1];
            toConcept = tokens[2];
            linkValue = tokens[3];
        }
        else {
            throw new IllegalArgumentException("Argument " +  stringRepresentation +
                    "' must be in format '" +
                    "'[fromConcept " + DELIMITER + "]" +
                    "linkName " + DELIMITER +
                    "toConcept " + DELIMITER +
                    "linkValue'");
        }
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public String getToConcept() {
        return toConcept;
    }

    public void setToConcept(String toConcept) {
        this.toConcept = toConcept;
    }

    public String getLinkValue() {
        return linkValue;
    }

    public void setLinkValue(String linkValue) {
        this.linkValue = linkValue;
    }

    public String getFromConcept() {
        return fromConcept;
    }

    public void setFromConcept(String fromConcept) {
        this.fromConcept = fromConcept;
    }

    @Override
    public String toString() {
        return linkName + ILink.DELIMITER + toConcept + ILink.DELIMITER + linkValue;
    }

    public String stringValue() {
            return ILink.VALUE_NIL + ILink.DELIMITER + toString();
    }

}
