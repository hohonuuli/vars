package vars.jpa;

/**
 * This Guice Module sets up the Factories needed
 */
public class VarsJpaTestModule extends VarsJpaModule {
    
    private static final String puName = "vars-hibernate-development";

    public VarsJpaTestModule() {
        super(puName, puName, puName);
    }

}
