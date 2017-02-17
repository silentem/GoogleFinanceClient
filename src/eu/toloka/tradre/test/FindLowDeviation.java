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
public class FindLowDeviation extends Spider {

    @SuppressWarnings({"UnusedDeclaration"})
    public FindLowDeviation(StockEntity stock) {
        super(stock, 120);
    }

    public static void main(final String[] args) {
        PersistenceManager.update("update StockEntity s set s.lowDeviation = false");

        schedule(StockDao.StockType.BETA_IS_0_ORDER_BY_DEVIATION, 20, FindLowDeviation.class);
    }

    @Override
    protected String getUrlString(StockEntity stock, int interval, ProxyEntity proxy) {
        return proxy.url + "/browse.php?u=http://www.google.com/finance/getprices?" +
                ("q=" + stock.symbol + "&x=" + stock.exchange + "&i=" + interval +
                        "&p=1d&f=d,c,v,o,h,l&df=cpct&auto=1&ts=" + new Date().getTime()).replaceAll("&", "%26");
    }

    @Override
    protected void processReader(StockEntity stock, int interval, BufferedReader reader, ProxyEntity proxy) throws IOException {
        String line = reader.readLine();

        if (line != null && line.contains("EXCHANGE")) {
            List<BarEntity> barList = new ArrayList<BarEntity>();
            BarEntity bar = null;

            while ((line = reader.readLine()) != null) {
                if (line.contains("sorry.google.com") || line.contains("503 Service Unavailable")) {
                    ProxyUtils.error(proxy);
                    break;
                }

                if ((bar = parseString(line, stock.symbol, interval)) == null)
                    continue;

                barList.add(bar);
                populate(bar, barList);
            }

            if (bar == null) {
                ProxyUtils.error(proxy);
                System.err.println(proxy.url + " " + stock.symbol + ": No data received");
            } else {
                float c = 0;
                float sumDeviation = 0f;

                for (int i = barList.size() - 1; i > 0; i--) {
                    if (barList.get(i).deviation13 != null &&
                            (c == 0 || sumDeviation / c < stock.deviation * 0.38)) {
                        sumDeviation += barList.get(i).deviation13;
                        c++;
                    } else {
                        break;
                    }
                }

                if (c > 60) {
                    System.out.println(stock.symbol + "(D) " + stock.deviation);
//                    stock.lowDeviation = true;
                }

                stock.beta += 1f;
                PersistenceManager.merge(stock);
            }

        } else {
            ProxyUtils.error(proxy);
            System.err.println(proxy.url + " " + stock.symbol + " Error wrong first line: " + line);
        }
    }
}
