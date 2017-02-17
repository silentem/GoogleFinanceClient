package eu.toloka.tradre.persistence.dao;

import eu.toloka.tradre.persistence.entity.ProxyEntity;

import java.util.List;

import static eu.toloka.tradre.persistence.PersistenceManager.getEntityManager;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */

public class ProxyDao {

    @SuppressWarnings("unchecked")
    public static ProxyEntity getProxy(String ip) {
        String query = "SELECT OBJECT(entity) FROM ProxyEntity entity WHERE entity.ip LIKE '" + ip + "%' ";

        @SuppressWarnings({"unchecked", "UnnecessaryLocalVariable"})
        List<ProxyEntity> list = (List<ProxyEntity>) getEntityManager().createQuery(query).getResultList();

        return list.size() == 0 ? null : list.get(0);
    }

    @SuppressWarnings("unchecked")
    public static List<ProxyEntity> getProxyList() {
        String query = "SELECT OBJECT(entity) FROM ProxyEntity entity WHERE entity.active = true";  // and entity.url like '%.tk'

        @SuppressWarnings({"unchecked", "UnnecessaryLocalVariable"})
        List<ProxyEntity> list = (List<ProxyEntity>) getEntityManager().createQuery(query).getResultList();

        return list;
    }

}