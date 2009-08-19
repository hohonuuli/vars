/*
 * @(#)IUserAccount.java   2008.12.30 at 01:50:52 PST
 *
 * Copyright 2007 MBARI
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars;

/**
 *
 * @author brian
 */
public interface IUserAccount extends IVARSObject {

    /**
     * Description of the Field
     */
    String PASSWORD_DEFAULT = "guest";

    /**
     * Description of the Field
     */
    String USERNAME_DEFAULT = "default";

    String PROP_FIRST_NAME = "firstName";
    String PROP_LAST_NAME = "lastName";
    String PROP_PASSWORD = "password";
    String PROP_USER_NAME = "userName";
    String PROP_ROLE = "role";
    String PROP_AFFILIATION = "affiliation";

    /**
     * Gets the password for this <code>UserAccount</code>.
     * @return     The password for this <code>UserAccount</code>.
     */
    char[] getPassword();

    /**
     * Gets the KB Maint <code>Role</code> for this <code>UserAccount</code>.
     * @return     The KB Maint <code>Role</code> for this <code>UserAccount</code>.
     */
    IRole getRole();

    /**
     * Gets the user name for this <code>UserAccount</code>.
     * @return     The user name for this <code>UserAccount</code>.
     * @uml.property  name="userName"
     */
    String getUserName();

    String getFirstName();

    String getLastName();

    String getAffiliation();

    boolean isAdministrator();


    boolean isMaintainer();


    boolean isReadOnly();

    /**
     * Sets the password for this <code>UserAccount</code>.
     * @param password    The password for this <code>UserAccount</code>.
     */
    void setPassword(char[] password);

    /**
     * Sets the permissible <code>Role</code> for this <code>UserAccount</code>.
     * @param role  The role to set.
     * @uml.property  name="role"
     */
    void setRole(final IRole role);

    /**
     * Sets the username for this <code>UserAccount</code>.
     * @param userName  The new userName value
     * @uml.property  name="userName"
     */
    void setUserName(String userName);

    void setFirstName(String firstName);
    void setLastName(String lastName);
    void setAffiliation(String affiliation);
}
