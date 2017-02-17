package eu.toloka.tradre.persistence;

import eu.toloka.tradre.persistence.entity.BarEntity;
import eu.toloka.tradre.persistence.entity.BasicEntity;
import org.eclipse.persistence.jpa.PersistenceProvider;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.List;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class PersistenceManager {
    private static final EntityManager entityManager;
    private static final EntityManagerFactory factory;

    static {
        factory = new PersistenceProvider().createEntityManagerFactory("tradre", null);
        entityManager = factory.createEntityManager();
    }

    public static EntityManager getEntityManager(){
        return entityManager;
    }

    public static void merge(BasicEntity entity){
        EntityManager entityManager = factory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.merge(entity);
        entityManager.getTransaction().commit();
    }

    public static void merge(List<? extends BasicEntity> entityList){
        EntityManager entityManager = factory.createEntityManager();
        entityManager.getTransaction().begin();

        for(BasicEntity entity: entityList){
            entityManager.merge(entity);
        }

        entityManager.getTransaction().commit();
    }

    public static void update(String update){
        EntityManager entityManager = factory.createEntityManager();
        entityManager.getTransaction().begin();
        Query query = entityManager.createQuery(update);
        query.executeUpdate();
        entityManager.getTransaction().commit();
    }
}