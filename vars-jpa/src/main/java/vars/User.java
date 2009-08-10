package vars;


import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.Transient;
import javax.persistence.TemporalType;
import javax.persistence.Temporal;
import java.util.Date;

@Entity
public class User {


    @Id
    private long id;

    private String name;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date date;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
