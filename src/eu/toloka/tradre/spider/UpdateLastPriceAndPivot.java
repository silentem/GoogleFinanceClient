package eu.toloka.tradre.spider;

import eu.toloka.tradre.persistence.PersistenceManager;
import eu.toloka.tradre.persistence.dao.StockDao;
import eu.toloka.tradre.persistence.entity.BarEntity;
import eu.toloka.tradre.persistence.entity.ProxyEntity;
import eu.toloka.tradre.persistence.entity.StockEntity;
import eu.toloka.tradre.proxy.ProxyUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class UpdateLastPriceAndPivot extends Spider {

    @SuppressWarnings({"UnusedDeclaration"})
    public UpdateLastPriceAndPivot(StockEntity stock) {
        super(stock, 86400);
    }

    public static void main(final String[] args) {
        schedule(StockDao.StockType.BETA_IS_0, 20, UpdateLastPriceAndPivot.class);
    }

    protected String getUrlString(StockEntity stock, int interval, ProxyEntity proxy) {
        return proxy.url + "http://www.google.com/finance/getprices?" +
                ("q=" + stock.symbol + "&x=" + stock.exchange + "&i=" + interval +
                        "&p=2d&f=d,c,v,o,h,l&df=cpct&auto=1&ts=" + new Date().getTime()).replaceAll("&", "%26");
    }

    protected void processReader(StockEntity stock, int interval, BufferedReader reader, ProxyEntity proxy) throws IOException {
        String line = reader.readLine();

        if (line != null && line.contains("EXCHANGE")) {
            BarEntity bar = null;

            while ((line = reader.readLine()) != null) {
                if (line.contains("sorry.google.com") || line.contains("503 Service Unavailable")) {
                    ProxyUtils.error(proxy);
                    break;
                }

                bar = parseString(line, stock.symbol, interval);
            }

            if (bar == null) {
                System.err.println(proxy.url + " " + stock.symbol + ": No data received");
            } else {
                stock.pivot = (2 * bar.open + bar.high + bar.low) / 4f;
                stock.lastPrice = bar.close;
                stock.beta = 1f;
                PersistenceManager.merge(stock);
//                System.out.println(proxy.url + " " + stock.symbol + " pivot updated");
            }

        } else {
            ProxyUtils.error(proxy);
            System.err.println(proxy.url + " " + stock.symbol + " Error wrong first line: " + line);
        }
    }
}
