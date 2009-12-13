/*
 * @(#)UserAccountImpl.java   2009.11.10 at 11:55:32 PST
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.jpa;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;
import org.jasypt.util.password.BasicPasswordEncryptor;
import vars.UserAccount;
import vars.UserAccountRoles;

/**
 * Class description
 *
 *
 * @version        $date$, 2009.11.10 at 11:55:32 PST
 * @author         Brian Schlining [brian@mbari.org]
 */
@Entity(name = "UserAccount")
@Table(name = "UserAccount")
@EntityListeners({ TransactionLogger.class, KeyNullifier.class })
@NamedQueries( {

    @NamedQuery(name = "UserAccount.findById", query = "SELECT v FROM UserAccount v WHERE v.id = :id") ,
    @NamedQuery(name = "UserAccount.findByUserName",
                query = "SELECT v FROM UserAccount v WHERE v.userName = :userName") ,
    @NamedQuery(name = "UserAccount.findByFirstName",
                query = "SELECT c FROM UserAccount c WHERE c.firstName = :firstName") ,
    @NamedQuery(name = "UserAccount.findByLastName",
                query = "SELECT c FROM UserAccount c WHERE c.lastName = :lastName") ,
    @NamedQuery(name = "UserAccount.findByAffiliation",
                query = "SELECT c FROM UserAccount c WHERE c.affiliation LIKE :affiliation") ,
    @NamedQuery(name = "UserAccount.findByRole", query = "SELECT c FROM UserAccount c WHERE c.role LIKE :role") ,
    @NamedQuery(name = "UserAccount.findAll", query = "SELECT c FROM UserAccount c")

})
public class UserAccountImpl implements Serializable, UserAccount, JPAEntity {

    @Column(name = "Affiliation", length = 512)
    String affiliation;

    @Column(name = "Email", length = 50)
    String email;

    @Column(name = "FirstName", length = 50)
    String firstName;

    @Id
    @Column(
        name = "id",
        nullable = false,
        updatable = false
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "UserAccount_Gen")
    @TableGenerator(
        name = "UserAccount_Gen",
        table = "UniqueID",
        pkColumnName = "TableName",
        valueColumnName = "NextID",
        pkColumnValue = "UserName",
        allocationSize = 1
    )
    Long id;

    @Column(name = "LastName", length = 50)
    String lastName;

    @Column(
        name = "Password",
        nullable = false,
        length = 50
    )
    String password;
    
    @Column(
        name = "Role",
        nullable = false,
        length = 10
    )
    String role;

    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime;
    @Column(
        name = "UserName",
        nullable = false,
        unique = true,
        length = 50
    )
    String userName;

    public boolean authenticate(String unencryptedPassword) {
        return (new BasicPasswordEncryptor()).checkPassword(unencryptedPassword, password);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final UserAccountImpl other = (UserAccountImpl) obj;
        if ((this.userName == null) ? (other.userName != null) : !this.userName.equals(other.userName)) {
            return false;
        }

        return true;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public Long getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + ((this.userName != null) ? this.userName.hashCode() : 0);

        return hash;
    }

    public boolean isAdministrator() {
        return UserAccountRoles.ADMINISTRATOR.getRoleName().equals(role);
    }

    public boolean isMaintainer() {
        return UserAccountRoles.MAINTENANCE.getRoleName().equals(role);
    }

    public boolean isReadOnly() {
        return UserAccountRoles.READONLY.getRoleName().equals(role);
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public void setId(Long id) {
    	this.id = id;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(String unencryptedPassword) {
        this.password = (new BasicPasswordEncryptor()).encryptPassword(unencryptedPassword);
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append(" ([id=").append(getId()).append("] ");
        sb.append("userName=").append(userName).append(")");
        return sb.toString();
    }
    
    public Object getPrimaryKey() {
    	return getId();
    }
}
