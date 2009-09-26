package vars.jpa;

public class VarsJpaDevelopmentModule extends VarsJpaModule {
    
    private static final String puName = "vars-hibernate-development";

    public VarsJpaDevelopmentModule() {
        super(puName, puName, puName);
    }
}