package eu.toloka.tradre.spider;

import eu.toloka.tradre.persistence.dao.StockDao;
import eu.toloka.tradre.persistence.entity.StockEntity;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import eu.toloka.tradre.persistence.PersistenceManager;

import javax.persistence.EntityManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */

public class StockScreener {
//    private static final String URL = "http://www.google.com/finance?q=((exchange:NYSE)+OR+(exchange:NASDAQ)+OR+(exchange:AMEX))&restype=company&output=json&noIL=1&num=50&start=";
  private static final String URL_NYSE = "http://www.google.com/finance?q=%5B(exchange%20%3D%3D%20%22NYSE%22)%20%26%20(last_price%20%3E%3D%205)%20%26%20(last_price%20%3C%3D%20100)%5D&restype=company&output=json&noIL=1&num=100&start=";
  private static final String URL_NASDAQ = "http://www.google.com/finance?q=%5B(exchange%20%3D%3D%20%22NASDAQ%22)%20%26%20(last_price%20%3E%3D%205)%20%26%20(last_price%20%3C%3D%20100)%5D&restype=company&output=json&noIL=1&num=100&start=";
  private static final String URL_NYSEAMEX = "http://www.google.com/finance?q=%5B(exchange%20%3D%3D%20%22NYSEAMEX%22)%20%26%20(last_price%20%3E%3D%205)%20%26%20(last_price%20%3C%3D%20100)%5D&restype=company&output=json&noIL=1&num=100&start=";
  private static final String URL_NYSEARCA = "http://www.google.com/finance?q=%5B(exchange%20%3D%3D%20%22NYSEARCA%22)%20%26%20(last_price%20%3E%3D%205)%20%26%20(last_price%20%3C%3D%20100)%5D&restype=company&output=json&noIL=1&num=100&start=";
  private static final EntityManager entityManager = PersistenceManager.getEntityManager();

  public static void main(String[] args) {
    loadExchange(URL_NYSE);
    loadExchange(URL_NASDAQ);
    loadExchange(URL_NYSEAMEX);
    loadExchange(URL_NYSEARCA);
  }

  private static void loadExchange(String requestString) {
    int stocksNumber = 0;
    int i = 0;

    while (i * 100 <= ((stocksNumber / 100) + 1) * 100) {
      String s = "";

      try {
        URL url = new URL(requestString + i * 100);
        System.out.println(url.toExternalForm());

        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

        String line;

        while ((line = reader.readLine()) != null) {
          if (line.startsWith("\"num_company_results\"")) {
            stocksNumber = new Integer(line.replaceAll("\"num_company_results\" : \"([0-9]*)\",.*", "$1"));
          }

          s += line;
        }

        reader.close();
      } catch (Exception e) {
        e.printStackTrace();
      }

      if (s.equals("")) {
        continue;
      }

      s = s.replaceAll(",\"mf_search" + "results\" : \\[\\]", "");

      JSONObject json = (JSONObject) JSONSerializer.toJSON(s);
      JSONArray results = json.getJSONArray("search" + "results");

      entityManager.getTransaction().begin();

      for (Object o : results) {
        JSONObject jsonStock = (JSONObject) o;

        String symbol = jsonStock.getString("ticker");
        if ("".equals(symbol)) {
          continue;
        }

        StockEntity stock = StockDao.getStock(symbol);

        if (stock == null) {
          stock = new StockEntity();
          stock.id = jsonStock.getInt("id");
          stock.active = true;
          stock.symbol = jsonStock.getString("ticker");
          stock.exchange = jsonStock.getString("exchange");
          stock.companyName = jsonStock.getString("title");
        }

        JSONArray columns = jsonStock.getJSONArray("columns");

        for (Object c : columns) {
          JSONObject jsonColumn = (JSONObject) c;
          String field = jsonColumn.getString("field");

          if (field.equals("QuoteLast")) {
            try {
              stock.lastPrice = (float) jsonColumn.getDouble("value");
            } catch (Exception ex) {
              System.err.println(ex.getMessage() + " : " + jsonColumn.get("value") + " (" + stock.symbol + ")");
            }
          }
        }

        entityManager.merge(stock);
      }

      i++;

      entityManager.getTransaction().commit();
    }
  }
}
