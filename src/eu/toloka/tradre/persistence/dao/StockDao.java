package eu.toloka.tradre.persistence.dao;

import eu.toloka.tradre.persistence.entity.StockEntity;

import java.util.ArrayList;
import java.util.List;

import static eu.toloka.tradre.persistence.PersistenceManager.getEntityManager;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class StockDao {
    public enum StockType {
        ALL, WATCHED, EXISTING, BETA_IS_0, BETA_IS_0_ORDER_BY_DEVIATION, SYMBOL
    }

    public static List<StockEntity> getStockList(StockType type, String... symbol) {
        String query;

        switch (type) {
            default:
            case ALL:
                query = "SELECT OBJECT(entity) FROM StockEntity entity ORDER BY entity.symbol ";
                break;
            case EXISTING:
                query = "SELECT OBJECT(entity) FROM StockEntity entity WHERE entity.active = true ORDER BY entity.symbol";
                break;
            case WATCHED:
                query = "SELECT OBJECT(entity) FROM StockEntity entity WHERE entity.watch = 1 ORDER BY entity.symbol";
                break;
            case BETA_IS_0:
                query = "SELECT OBJECT(entity) FROM StockEntity entity WHERE entity.beta = 0 AND (entity.active = true OR entity.active = false) ORDER BY entity.symbol";
                break;
            case BETA_IS_0_ORDER_BY_DEVIATION:
                query = "SELECT OBJECT(entity) FROM StockEntity entity WHERE entity.watch = 2 AND entity.beta = 0 ORDER BY entity.deviation DESC";
                break;
            case SYMBOL:
                query = "SELECT OBJECT(entity) FROM StockEntity entity WHERE entity.symbol = '" + symbol[0] + "'";
        }

        @SuppressWarnings({"unchecked", "UnnecessaryLocalVariable"})
        List<StockEntity> list = (List<StockEntity>) getEntityManager().createQuery(query).getResultList();
        return list;
    }

    public static StockEntity getStock(String symbol) {
        String query = "SELECT OBJECT(entity) FROM StockEntity entity WHERE entity.symbol = '" + symbol + "' ";

        @SuppressWarnings({"unchecked", "UnnecessaryLocalVariable"})
        List<StockEntity> list = (List<StockEntity>) getEntityManager().createQuery(query).getResultList();

        return list.size() == 0 ? null : list.get(0);
    }

    public static StockEntity getStock(String exchange, String symbol) {
        String query = "SELECT OBJECT(entity) FROM StockEntity entity WHERE entity.symbol = '" + symbol + "' AND entity.exchange = '" + exchange + "' ";

        @SuppressWarnings({"unchecked", "UnnecessaryLocalVariable"})
        List<StockEntity> list = (List<StockEntity>) getEntityManager().createQuery(query).getResultList();

        return list.size() == 0 ? null : list.get(0);
    }
}
