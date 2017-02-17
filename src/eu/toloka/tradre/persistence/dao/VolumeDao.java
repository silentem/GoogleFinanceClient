package eu.toloka.tradre.persistence.dao;

import eu.toloka.tradre.persistence.entity.VolumeEntity;

import java.util.List;

import static eu.toloka.tradre.persistence.PersistenceManager.getEntityManager;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class VolumeDao {

    public static VolumeEntity getVolume(String symbol) {
        String query = "SELECT OBJECT(entity) FROM VolumeEntity entity WHERE entity.symbol = '" + symbol + "' ";

        @SuppressWarnings({"unchecked", "UnnecessaryLocalVariable"})
        List<VolumeEntity> list = (List<VolumeEntity>) getEntityManager().createQuery(query).getResultList();

        return list.size() == 0 ? null : list.get(0);
    }

}
