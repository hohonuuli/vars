package vars;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 19, 2009
 * Time: 2:56:30 PM
 * To change this template use File | Settings | File Templates.
 */
public enum UserAccountRoles {

    ADMINISTRATOR("Admin"), MAINTENANCE("Maint"), READONLY("ReadOnly");

    private final String roleName;


    UserAccountRoles(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    @Override
    public String toString() {
        return roleName;
    }


}
