package eu.toloka.tradre.spider;

import eu.toloka.tradre.analyzer.Bar;
import eu.toloka.tradre.analyzer.BarCluster;
import eu.toloka.tradre.analyzer.BarList;
import eu.toloka.tradre.persistence.PersistenceManager;
import eu.toloka.tradre.persistence.entity.BarEntity;
import eu.toloka.tradre.persistence.entity.BarPk;
import eu.toloka.tradre.persistence.entity.ProxyEntity;
import eu.toloka.tradre.proxy.ProxyUtils;
import eu.toloka.tradre.time.TimingUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class GoogleFinance {

  private static final String request = "http://www.google.com/finance/getprices?q=%s&x=%s&i=%d&p=%s&f=d,c,v,o,h,l&df=cpct&auto=1&ts=%d";

  public static void updateBarListMap(String symbol, String exchange, int interval, String period, Map<String, BarCluster> clusterMap) {
    BarCluster barCluster = clusterMap.get(symbol);

    if (barCluster == null) {
      barCluster = new BarCluster();
      clusterMap.put(symbol, barCluster);
    }

    BarList barList = barCluster.getBarList(interval);

    // remove the last, may be not really correct bar
    if (barList.size() > 1) {
      barList.remove(barList.size() - 1);
    }

    updateBarList(symbol, exchange, interval, period, barList);

    switch (interval) {
      case 60:
        barCluster.m2dirty = true;
        break;
      case 300:
        barCluster.m10dirty = true;
        break;
      case 1800:
        barCluster.h1dirty = true;
    }
  }

  public static BarList mergeBarList(int destinationInterval, BarList barList) {

    BarList destinationBarList = new BarList(barList.size() / 2);

    if (barList.size() == 0) {
      return destinationBarList;
    }

    Bar lastBar = null;

    if (destinationInterval == 120) {
      for (Bar sourceBar : barList) {
        if (sourceBar.minutes() % 2 == 0) {
          sourceBar = mergeBars(lastBar, sourceBar, destinationInterval);
          destinationBarList.add(sourceBar);
          populate(sourceBar.getEntity(), destinationBarList.getBarEntityList());
          lastBar = null;
        } else {
          lastBar = sourceBar;
        }
      }
    }

    if (destinationInterval == 600) {
      for (Bar sourceBar : barList) {
        if (String.valueOf(sourceBar.minutes()).endsWith("0")) {
          sourceBar = mergeBars(lastBar, sourceBar, destinationInterval);
          destinationBarList.add(sourceBar);
          populate(sourceBar.getEntity(), destinationBarList.getBarEntityList());
          lastBar = null;
        } else {
          lastBar = sourceBar;
        }
      }
    }

    if (destinationInterval == 3600) {
      for (Bar sourceBar : barList) {
        if (sourceBar.minutes() == 0) {
          if (sourceBar.hours() != 10) {
            sourceBar = mergeBars(lastBar, sourceBar, destinationInterval);
          }

          destinationBarList.add(sourceBar);
          populate(sourceBar.getEntity(), destinationBarList.getBarEntityList());
          lastBar = null;
        } else {
          lastBar = sourceBar;
        }
      }
    }

    return destinationBarList;
  }

  public static Bar mergeBars(Bar lastBar, Bar currentBar, int interval) {
    Bar bar = new Bar(new BarEntity());

    BarPk barPk = new BarPk();
    barPk.time = currentBar.time();
    barPk.symbol = currentBar.getEntity().barPk.symbol;

    bar.getEntity().interval = interval;
    bar.getEntity().barPk = barPk;

    if (lastBar == null) {
      bar.getEntity().open = currentBar.open();
      bar.getEntity().close = currentBar.close();
      bar.getEntity().high = currentBar.high();
      bar.getEntity().low = currentBar.low();
      bar.getEntity().volume = currentBar.getEntity().volume;

      return bar;
    }

    bar.getEntity().open = lastBar.open();
    bar.getEntity().close = currentBar.close();
    bar.getEntity().high = Math.max(lastBar.high(), currentBar.high());
    bar.getEntity().low = Math.min(lastBar.low(), currentBar.low());
    bar.getEntity().volume = lastBar.getEntity().volume + currentBar.getEntity().volume;

    return bar;
  }

  // interval 60, 120, 300, 600, 900, 1800, 3600, 14400, 86400

  public static BarList getBarList(String symbol, String exchange, int interval, String period) {
    BarList barList = new BarList(10);
    updateBarList(symbol, exchange, interval, period, barList);
    return barList;
  }

  private static void updateBarList(String symbol, String exchange, int interval, String period, BarList barList) {
    ProxyEntity proxy = ProxyUtils.getNextProxyEntity();


    String requestString = String.format(request, symbol, exchange, interval, barList.size() == 0 ? period : "25m", System.currentTimeMillis());
    String urlString = proxy.url + requestString.replaceAll("&", "%26");

    BufferedReader reader = null;
    Calendar calendar = Calendar.getInstance();

    try {
      URLConnection conn = new URL(urlString).openConnection();

      if (proxy.cookies != null) {
        conn.setRequestProperty("Cookie", proxy.cookies);
      }

      Long t = System.currentTimeMillis();
      reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      Long responseTime = System.currentTimeMillis() - t;
      proxy.responseTime = (int) ((proxy.responseTime * proxy.counter + responseTime) / (proxy.counter + 1));
      proxy.counter++;

//      PersistenceManager.merge(proxy);

      String line;
      long startTime = 0;
      BarEntity bar;

      Set<Long> timeSet = barList.getTimeSet();

      while ((line = reader.readLine()) != null) {
        if (line.contains("sorry.google.com") || line.contains("503 Service Unavailable")) {
          ProxyUtils.error(proxy);
          break;
        }

        if (!line.matches("(a[0-9]{10}|[0-9]+),[0-9\\.]+,[0-9\\.]+,[0-9\\.]+,[0-9\\.]+,[0-9]+")) {
          continue;
        }

        String data[] = line.split(",");

        long time;

        if (data[0].matches("a[0-9]{10}")) {
          startTime = Long.parseLong(data[0].substring(1)) * 1000 - TimingUtils.offsetInMillis;
          time = startTime;
        } else {
          time = startTime + Long.parseLong(data[0]) * interval * 1000;
        }

        bar = getBarEntity(data, symbol, time, interval, calendar);

        if (!timeSet.contains(bar.barPk.time)) {
          barList.add(new Bar(bar));
          populate(bar, barList.getBarEntityList());
        }
      }

      if (barList.size() == 0) {
        System.err.println(urlString + " ");
        ProxyUtils.noData(proxy);
      }
    } catch (Exception ex) {
      System.err.println(proxy.url + " " + ex.getMessage());
//      ProxyUtils.error(proxy);

      if (!ex.getMessage().startsWith("Connection reset") &&
              !ex.getMessage().startsWith("Unexpected end of") &&
              !ex.getMessage().equals("Connection refused: connect") &&
              !ex.getMessage().equals("Premature EOF") &&
              !ex.getMessage().startsWith("Server returned HTTP response code") &&
              !ex.getMessage().startsWith("Connection timed out") &&
              !(ex instanceof FileNotFoundException)) {
        ex.printStackTrace();
      }
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private static BarEntity getBarEntity(String data[], String symbol, long time, int interval, Calendar calendar) {
//              COLUMNS=DATE,CLOSE,HIGH,LOW,OPEN,VOLUME

    BarPk barPk = new BarPk();
    barPk.symbol = symbol.trim();
    barPk.time = time;

    BarEntity bar = new BarEntity();
    bar.barPk = barPk;

    calendar.setTimeInMillis(time);
    calendar.set(Calendar.HOUR_OF_DAY, 9);
    calendar.set(Calendar.MINUTE, 30);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    bar.date = calendar.getTimeInMillis();
    bar.interval = interval;
    bar.close = Float.parseFloat(data[1]);
    bar.high = Float.parseFloat(data[2]);
    bar.low = Float.parseFloat(data[3]);
    bar.open = Float.parseFloat(data[4]);
    bar.volume = Long.parseLong(data[5]);

    return bar;
  }

  private static void populate(BarEntity bar, List<BarEntity> list) {
//        bar.rsi13 = Statistics.calculateRelativeStrength(list, 13);
//        bar.rsi21 = Statistics.calculateRelativeStrength(list, 21);
//        bar.rsi34 = Statistics.calculateRelativeStrength(list, 34);

//        bar.moving3 = Statistics.calculateSimpleMovingAverage(list, 3);
    bar.moving5 = Statistics.calculateSimpleMovingAverage(list, 5);
    bar.moving8 = Statistics.calculateSimpleMovingAverage(list, 8);
//        bar.moving13 = Statistics.calculateSimpleMovingAverage(list, 13);
    bar.moving21 = Statistics.calculateSimpleMovingAverage(list, 21);
//        bar.moving34 = Statistics.calculateSimpleMovingAverage(list, 34);
    bar.moving55 = Statistics.calculateSimpleMovingAverage(list, 55);
//        bar.moving89 = Statistics.calculateSimpleMovingAverage(list, 89);
//        bar.moving144 = Statistics.calculateSimpleMovingAverage(list, 144);
    bar.moving200 = Statistics.calculateSimpleMovingAverage(list, 200);

//        bar.deviation13 = Statistics.deviation(list, bar.moving13, 13);
//        bar.deviation21 = Statistics.deviation(list, bar.moving21, 21);
  }
}
