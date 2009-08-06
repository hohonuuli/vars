package vars.jpa;

import javax.persistence.Column;
import javax.persistence.Version;
import java.sql.Timestamp
import vars.IUserAccount;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Jun 19, 2009
 * Time: 10:02:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class UserAccount implements Serializable {

    /** Optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "LAST_UPDATED_TIME")
    private Timestamp updatedTime
}
