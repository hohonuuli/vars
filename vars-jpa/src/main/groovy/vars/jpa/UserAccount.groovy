package vars.jpa;

import javax.persistence.Column;
import javax.persistence.Version;
import java.sql.Timestamp
import vars.IUserAccount
import vars.IRole
import javax.persistence.Transient;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Jun 19, 2009
 * Time: 10:02:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class UserAccount implements Serializable, IUserAccount, JPAEntity {

    @Transient
    private static final PROPS = Collections.unmodifiableList([IUserAccount.PROP_LAST_NAME,
            IUserAccount.PROP_FIRST_NAME, IUserAccount.PROP_USER_NAME])

    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime

    Long id

    char[] password

    IRole role

    String userName

    String firstName

    String lastName

    String affiliation

    public boolean isAdministrator() {
        return false;  // TODO implement this method.
    }

    public boolean isMaintainer() {
        return false;  // TODO implement this method.
    }

    public boolean isReadOnly() {
        return false;  // TODO implement this method.
    }

    @Override
    boolean equals(that) {
        return EntitySupportCategory.equals(this, that, PROPS)
    }

    @Override
    int hashCode() {
        return EntitySupportCategory.hashCode(this, PROPS)
    }

}
