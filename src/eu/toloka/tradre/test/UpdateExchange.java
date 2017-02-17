package eu.toloka.tradre.test;

import eu.toloka.tradre.persistence.PersistenceManager;
import eu.toloka.tradre.persistence.dao.StockDao;
import eu.toloka.tradre.persistence.entity.StockEntity;
import eu.toloka.tradre.persistence.entity.ProxyEntity;
import eu.toloka.tradre.spider.Spider;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class UpdateExchange extends Spider {

    @SuppressWarnings({"UnusedDeclaration"})
    public UpdateExchange(StockEntity stock) {
        super(stock, 120);
    }

    public static void main(final String[] args) {
        schedule(StockDao.StockType.BETA_IS_0, 20, UpdateExchange.class);
    }

    @Override
    protected String getUrlString(StockEntity stock, int interval, ProxyEntity proxy) {
        return proxy.url + "/browse.php?u=http://www.google.com/finance/getprices?q=" + stock.symbol;
    }

    @Override
    protected void processReader(StockEntity stock, int interval, BufferedReader reader, ProxyEntity proxy) throws IOException {
        String line = reader.readLine();

        if (line != null && line.contains("EXCHANGE")) {
            stock.exchange = line.replaceAll(".*%3D", "");
            stock.beta = 1f;
            PersistenceManager.merge(stock);

            System.out.println(proxy.url + " " + stock.symbol);
        }
    }
}
