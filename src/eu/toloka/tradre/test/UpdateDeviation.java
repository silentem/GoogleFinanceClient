package eu.toloka.tradre.test;

import eu.toloka.tradre.persistence.PersistenceManager;
import eu.toloka.tradre.persistence.dao.StockDao;
import eu.toloka.tradre.persistence.entity.BarEntity;
import eu.toloka.tradre.persistence.entity.ProxyEntity;
import eu.toloka.tradre.persistence.entity.StockEntity;
import eu.toloka.tradre.proxy.ProxyUtils;
import eu.toloka.tradre.spider.Spider;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class UpdateDeviation extends Spider {

    @SuppressWarnings({"UnusedDeclaration"})
    public UpdateDeviation(StockEntity stock) {
        super(stock, 120);
    }

    public static void main(final String[] args) {
        schedule(StockDao.StockType.BETA_IS_0, 20, UpdateDeviation.class);
    }

    protected String getUrlString(StockEntity stock, int interval, ProxyEntity proxy) {
        return proxy.url + "/browse.php?u=http://www.google.com/finance/getprices?" +
                ("q=" + stock.symbol + "&x=" + stock.exchange + "&i=" + interval +
                        "&p=30d&f=d,c,v,o,h,l&df=cpct&auto=1&ts=" + new Date().getTime()).replaceAll("&", "%26");
    }

    protected void processReader(StockEntity stock, int interval, BufferedReader reader, ProxyEntity proxy) throws IOException {
        String line = reader.readLine();

        if (line != null && line.contains("EXCHANGE")) {
            List<BarEntity> barList = new ArrayList<BarEntity>();
            BarEntity bar = null;
            Float deviation = 0f;
            Float f = 0f;

            while ((line = reader.readLine()) != null) {
                if (line.contains("sorry.google.com") || line.contains("503 Service Unavailable")) {
                    ProxyUtils.error(proxy);
                    break;
                }

                if ((bar = parseString(line, stock.symbol, interval)) == null)
                    continue;

                barList.add(bar);
                populate(bar, barList);

                if (bar.deviation13 == null)
                    continue;

                deviation += bar.deviation13;
                f++;
            }

            if (bar == null) {
                System.err.println(proxy.url + " " + stock.symbol + ": No data received");
            } else {
                stock.deviation = deviation / f;
                stock.beta = 1f;
                PersistenceManager.merge(stock);

                System.out.println(proxy.url + " " + stock.symbol);
            }

        } else {
            ProxyUtils.error(proxy);
            System.err.println(proxy.url + " " + stock.symbol + " Error wrong first line: " + line);
        }
    }
}
