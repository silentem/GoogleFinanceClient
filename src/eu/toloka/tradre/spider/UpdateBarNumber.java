package eu.toloka.tradre.spider;

import eu.toloka.tradre.persistence.PersistenceManager;
import eu.toloka.tradre.persistence.dao.StockDao;
import eu.toloka.tradre.persistence.entity.BarEntity;
import eu.toloka.tradre.persistence.entity.StockEntity;
import eu.toloka.tradre.persistence.entity.ProxyEntity;
import eu.toloka.tradre.proxy.ProxyUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class UpdateBarNumber extends Spider {

    @SuppressWarnings({"UnusedDeclaration"})
    public UpdateBarNumber(StockEntity stock) {
        super(stock, 300);
    }

    public static void main(final String[] args) {
        schedule(StockDao.StockType.BETA_IS_0, 20, UpdateBarNumber.class);
    }

    @Override
    protected String getUrlString(StockEntity stock, int interval, ProxyEntity proxy) {
        return proxy.url + "http://www.google.com/finance/getprices?" +
                ("q=" + stock.symbol + "&x=" + stock.exchange + "&i=" + interval +
                    "&p=5d&f=d,c,v,o,h,l&df=cpct&auto=1&ts=" + new Date().getTime()).replaceAll("&", "%26");
    }

    @Override
    protected void processReader(StockEntity stock, int interval, BufferedReader reader, ProxyEntity proxy) throws IOException {
        String line = reader.readLine();

        if (line != null && line.contains("EXCHANGE")) {
            List<BarEntity> barList = new ArrayList<BarEntity>();
            BarEntity bar = null;

            while ((line = reader.readLine()) != null) {
                if(line.contains("sorry.google.com") || line.contains("503 Service Unavailable")){
                    ProxyUtils.error(proxy);
                    break;
                }

                if ((bar = parseString(line, stock.symbol, interval)) == null)
                    continue;

                barList.add(bar);
            }

            if(bar == null){
                System.err.println(proxy.url + " " + stock.symbol + ": No data received");
            }else{
                stock.barsNumber = barList.size();
                stock.beta = 1f;
                System.out.println(proxy.url + " " + stock.symbol + " " + new Date(bar.barPk.time));
            }

            PersistenceManager.merge(stock);
        } else {
            ProxyUtils.error(proxy);
            System.err.println(proxy.url + " " + stock.symbol + " Error wrong first line: " + line);
        }
    }
}
