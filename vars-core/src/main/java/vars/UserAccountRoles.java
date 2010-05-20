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
    
    /**
     * Return the role that corresponds to 'roleName'. Useful for matching the
     * value that's stored in the database to the correct role.
     * @param roleName The string name of the role (Admin, Maint or ReadOnly). The serach
     * 		is case insensitive and will match using the roleName or long name of the role
     * @return The matching UserAccountRole or null of the string provided doens't match any
     * 		of the roles
     */
    public static UserAccountRoles getRole(String roleName) {
    	UserAccountRoles[] roles = values();
    	UserAccountRoles matchingRole = null;
    	for(UserAccountRoles role: roles) {
    		if (role.getRoleName().toLowerCase().startsWith(roleName.toLowerCase())) {
    			matchingRole = role;
    			break;
    		}
    	}
    	return matchingRole;
    }


}
