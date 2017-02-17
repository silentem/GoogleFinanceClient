package eu.toloka.tradre.test;

import eu.toloka.tradre.persistence.PersistenceManager;
import eu.toloka.tradre.persistence.dao.StockDao;
import eu.toloka.tradre.persistence.entity.StockEntity;
import eu.toloka.tradre.persistence.entity.ProxyEntity;
import eu.toloka.tradre.proxy.ProxyUtils;
import eu.toloka.tradre.spider.Spider;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class UpdateRelated extends Spider {

    @SuppressWarnings({"UnusedDeclaration"})
    public UpdateRelated(StockEntity stock) {
        super(stock, 120);
    }

    public static void main(final String[] args) {
        schedule(StockDao.StockType.BETA_IS_0, 20, UpdateRelated.class);
    }

    @Override
    protected String getUrlString(StockEntity stock, int interval, ProxyEntity proxy) {
        return proxy.url + "/browse.php?u=http://www.google.com/finance/related?q=" + stock.exchange + ":" + stock.symbol;
    }

    private int counter = 0;

    @Override
    protected void processReader(StockEntity stock, int interval, BufferedReader reader, ProxyEntity proxy) throws IOException {
        boolean gotIn = false;

        String line;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("google.finance.data = {common:")) {
                gotIn = true;

                line = line.replaceAll("google.finance.data = ", "").replaceAll("stickyUrlArgs:\"\"};", "stickyUrlArgs:\"\"}");

                JSONObject json = (JSONObject) JSONSerializer.toJSON(line);
                JSONObject company = json.getJSONObject("company");
                JSONObject related = company.getJSONObject("related");
                JSONArray rows = related.getJSONArray("rows");

                stock.related = "";

                for (Object o : rows) {
                    JSONObject row = (JSONObject) o;
                    JSONArray values = row.getJSONArray("values");

                    StockEntity st = StockDao.getStock(String.valueOf(values.get(8)), String.valueOf(values.get(0)));

                    if (st != null && values.get(8).equals("NYSE") || values.get(8).equals("NASDAQ")) {
                        stock.related += values.get(8) + ":" + values.get(0) + " ";
                    }
                }

                stock.related = stock.related.trim();
                stock.beta = 1f;
                PersistenceManager.merge(stock);

                System.out.println(proxy.url + " " + stock.exchange + ":" + stock.symbol);
            }

            if(line.contains("500 Internal Server Error")){
                gotIn = true;
                stock.beta = 2f;
                PersistenceManager.merge(stock);

                System.err.println("500 Internal Server Error: " + stock.exchange + ":" + stock.symbol);
            }
        }

        if (!gotIn){
            System.err.println(proxy.url + " is not valid " + stock.exchange + ":" + stock.symbol);
            ProxyUtils.error(proxy);

            if (counter++ < 5) {
                processStock(stock, 120);
            }
        }
    }

}
