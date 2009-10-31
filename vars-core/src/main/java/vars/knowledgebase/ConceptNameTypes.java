package vars.knowledgebase;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 5, 2009
 * Time: 2:23:33 PM
 * To change this template use File | Settings | File Templates.
 */
public enum ConceptNameTypes {

    PRIMARY("primary"), ALTERNATE("alternate"), COMMON("common"), FORMER("former"), SYNONYM("synonym");

    private final String name;

    ConceptNameTypes(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }


}

