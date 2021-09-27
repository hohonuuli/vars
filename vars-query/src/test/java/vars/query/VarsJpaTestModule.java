package vars.query;

import vars.jpa.VarsJpaModule;

public class VarsJpaTestModule extends VarsJpaModule {
    
  private static final String puName = "vars-jpa-test";

  public VarsJpaTestModule() {
      super(puName, puName, puName);
  }

}
