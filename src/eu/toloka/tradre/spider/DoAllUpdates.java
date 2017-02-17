package eu.toloka.tradre.spider;

import eu.toloka.tradre.persistence.dao.StockDao;
import eu.toloka.tradre.persistence.entity.StockEntity;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class DoAllUpdates {

    public static void main(final String[] args) {
//        PersistenceManager.update("update StockEntity s set s.beta = 0 where s.beta < 1 and s.watch = 2");
//        PersistenceManager.update("update StockEntity s set s.lowDeviation = false");

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(20);
        List<StockEntity> stockEntities = StockDao.getStockList(StockDao.StockType.BETA_IS_0);

        try {
            int i = 0;
            for (StockEntity stock : stockEntities) {
//                executorService.schedule(new FindLowDeviation(stock), 1000 + 100 * i, TimeUnit.MILLISECONDS);
                executorService.schedule(new UpdateLastPriceAndPivot(stock), 1000 * 2 + 100 * i, TimeUnit.MILLISECONDS);
                i++;
            }

            executorService.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
