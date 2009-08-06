package vars.knowledgebase;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 5, 2009
 * Time: 2:23:33 PM
 * To change this template use File | Settings | File Templates.
 */
public enum ConceptNameTypes {

//        final static String NAMETYPE_ALTERNATE = "Alternate";
//
//    /**
//     *  Common name
//     */
//    final static String NAMETYPE_COMMON = "Common";
//
//    /**
//     * Indicates
//     */
//    final static String NAMETYPE_FORMER = "Former";
//
//    /**
//     *  The primary name of a concept. For organisms this is generally a
//     * genus-species composite
//     */
//    final static String NAMETYPE_PRIMARY = "Primary";
//
//    /**
//     *  Synonym for the concept
//     */
//    final static String NAMETYPE_SYNONYM = "Synonym";

    PRIMARY("primary"), ALTERNATE("alternate"), COMMON("common"), FORMER("former"), SYNONYM("synonym");


    private final String name;

    ConceptNameTypes(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }
}

