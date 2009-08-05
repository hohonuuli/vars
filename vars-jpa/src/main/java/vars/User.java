package vars;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.Transient;

@Entity
public class User
{
    @Transient
    @PersistenceContext(unitName = "vars-jpa")
    private static EntityManager entityManager;

    @Id
    private long id;

    private String name;

    public static User find(long id)
    {
        return entityManager.find(User.class, new Long(id));
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public static EntityManager getEntityManager()
    {
        return entityManager;
    }

    public static void setEntityManager(EntityManager entityManager)
    {
        User.entityManager = entityManager;
    }
}
