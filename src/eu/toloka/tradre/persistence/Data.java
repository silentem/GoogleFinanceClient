package eu.toloka.tradre.persistence;

import eu.toloka.tradre.persistence.entity.CountryEntity;

import static eu.toloka.tradre.persistence.PersistenceManager.*;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class Data {
    public static void main(String[] args) {
         getEntityManager().getTransaction().begin();

         try{
             CountryEntity stockEntity = new CountryEntity();

             getEntityManager().persist(stockEntity);
             getEntityManager().remove(stockEntity);
             getEntityManager().getTransaction().commit();
         }catch (Exception ex){
             getEntityManager().getTransaction().rollback();
             ex.printStackTrace();
         }
     }

}
