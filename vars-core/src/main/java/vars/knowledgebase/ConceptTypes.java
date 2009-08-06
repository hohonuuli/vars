package vars.knowledgebase;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 5, 2009
 * Time: 2:02:16 PM
 * To change this template use File | Settings | File Templates.
 */
public enum ConceptTypes {

//    /**
//     * Designation for Concept that represents has a lithology structure.
//     */
//    String LITHOLOGY = "lithology";
//
//    /**
//     * Description of the Field
//     */
//    String ORIGINATOR_UNKNOWN = "unknown";
//
//    /**
//     * Designation for Concept that represents has a taxonomy structure.
//     */
//    String TAXONOMY = "taxonomy";

    LITHOLOGY("lithology"), TAXONOMY("taxonomy"), UNSPECIFIED("unspecified");

    private final String name;

    ConceptTypes(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}
