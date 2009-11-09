package vars.jpa;

import javax.persistence.Column;
import javax.persistence.Version;
import java.sql.Timestamp
import vars.UserAccount
import vars.Role
import javax.persistence.Transient
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.EntityListeners
import javax.persistence.NamedQueries
import javax.persistence.NamedQuery
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.TableGenerator
import org.jasypt.util.password.BasicPasswordEncryptor
import vars.UserAccountRoles
import javax.persistence.GenerationType
import vars.jpa.TransactionLogger
import vars.EntitySupportCategory

@Entity(name = "UserAccount")
@Table(name = "UserAccount")
@EntityListeners( value = [TransactionLogger.class, KeyNullifier.class] )
@NamedQueries( value = [
    @NamedQuery(name = "UserAccount.findById",
                query = "SELECT v FROM UserAccount v WHERE v.id = :id"),
    @NamedQuery(name = "UserAccount.findByUserName",
                query = "SELECT v FROM UserAccount v WHERE v.userName = :userName"),
    @NamedQuery(name = "UserAccount.findByFirstName",
                query = "SELECT c FROM UserAccount c WHERE c.firstName = :firstName"),
    @NamedQuery(name = "UserAccount.findByLastName",
                query = "SELECT c FROM UserAccount c WHERE c.lastName = :lastName"),
    @NamedQuery(name = "UserAccount.findByAffiliation",
                query = "SELECT c FROM UserAccount c WHERE c.affiliation LIKE :affiliation"),
    @NamedQuery(name = "UserAccount.findByRole",
                query = "SELECT c FROM UserAccount c WHERE c.role LIKE :role"),
    @NamedQuery(name = "UserAccount.findAll",
                query = "SELECT c FROM UserAccount c")
])
public class GUserAccount implements Serializable, UserAccount, JPAEntity {

    @Transient
    private static final PROPS = Collections.unmodifiableList([UserAccount.PROP_USER_NAME])

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "UserAccount_Gen")
    @TableGenerator(name = "UserAccount_Gen", table = "UniqueID",
            pkColumnName = "TableName", valueColumnName = "NextID",
            pkColumnValue = "UserName", allocationSize = 1)
    Long id

    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime

    @Column(name = "Password", nullable = false, length = 50)
    String password

    @Column(name = "Role", nullable = false, length = 10)
    String role

    @Column(name = "UserName", nullable = false, unique = true, length = 50)
    String userName

    @Column(name = "FirstName", length = 50)
    String firstName

    @Column(name = "LastName", length = 50)
    String lastName

    @Column(name = "Affiliation", length = 512)
    String affiliation

    @Column(name = "Email", length = 50)
    String email

    public boolean isAdministrator() {
        return UserAccountRoles.ADMINISTRATOR.getRoleName().equals(role);
    }

    public boolean isMaintainer() {
        return UserAccountRoles.MAINTENANCE.getRoleName().equals(role);
    }

    public boolean isReadOnly() {
        return UserAccountRoles.READONLY.getRoleName().equals(role);
    }

    public String toString ( ) {
        return "UserAccount([id = " + id + ", userName='" + userName + '\'' + ')' ;
    }

    @Override
    boolean equals(that) {
        return EntitySupportCategory.equals(this, that, PROPS)
    }

    @Override
    int hashCode() {
        return EntitySupportCategory.hashCode(this, PROPS)
    }

    String getPassword() {
        return password
    }

    void setPassword(String unencryptedPassword) {
        this.password = (new BasicPasswordEncryptor()).encryptPassword(unencryptedPassword);
    }

    boolean authenticate(String unencryptedPassword) {
        (new BasicPasswordEncryptor()).checkPassword(unencryptedPassword, password)
    }

}
