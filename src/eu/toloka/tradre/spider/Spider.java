package eu.toloka.tradre.spider;

import eu.toloka.tradre.persistence.dao.StockDao;
import eu.toloka.tradre.persistence.entity.BarEntity;
import eu.toloka.tradre.persistence.entity.BarPk;
import eu.toloka.tradre.persistence.entity.ProxyEntity;
import eu.toloka.tradre.persistence.entity.StockEntity;
import eu.toloka.tradre.proxy.ProxyUtils;
import eu.toloka.tradre.time.TimingUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public abstract class Spider implements Runnable {

    protected abstract String getUrlString(StockEntity stock, int interval, ProxyEntity proxy);
    protected abstract void processReader(StockEntity stock, int interval, BufferedReader reader, ProxyEntity proxy) throws IOException;

    private final StockEntity stock;
    private final int interval;

    protected Spider(StockEntity stock, int interval) {
        this.stock = stock;
        this.interval = interval;
    }

    @Override
    public void run() {
        processStock(stock, interval);
    }

    protected void processStock(StockEntity stock, int interval){
        ProxyEntity proxy = ProxyUtils.getNextProxyEntity();
        BufferedReader reader = null;

        try {
            String urlString = getUrlString(stock, interval, proxy);

            URLConnection conn = new URL(urlString).openConnection();

            if (proxy.cookies != null) {
                conn.setRequestProperty("Cookie", proxy.cookies);
            }

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            processReader(stock, interval, reader, proxy);

        } catch (Exception ex) {
            ProxyUtils.error(proxy);
            System.err.println(proxy.url + " " + stock.symbol + " " + ex.getMessage());
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


    protected void populate(BarEntity bar, List<BarEntity> list) {
//        bar.rsi13 = Statistics.calculateRelativeStrength(list, 13);
//        bar.rsi21 = Statistics.calculateRelativeStrength(list, 21);
//        bar.rsi34 = Statistics.calculateRelativeStrength(list, 34);

        bar.moving3 = Statistics.calculateSimpleMovingAverage(list, 3);
        bar.moving5 = Statistics.calculateSimpleMovingAverage(list, 5);
        bar.moving8 = Statistics.calculateSimpleMovingAverage(list, 8);
        bar.moving13 = Statistics.calculateSimpleMovingAverage(list, 13);
        bar.moving21 = Statistics.calculateSimpleMovingAverage(list, 21);
        bar.moving34 = Statistics.calculateSimpleMovingAverage(list, 34);
        bar.moving55 = Statistics.calculateSimpleMovingAverage(list, 55);
        bar.moving89 = Statistics.calculateSimpleMovingAverage(list, 89);
        bar.moving144 = Statistics.calculateSimpleMovingAverage(list, 144);
        bar.moving200 = Statistics.calculateSimpleMovingAverage(list, 200);

        bar.deviation13 = Statistics.deviation(list, bar.moving13, 13);
//        bar.deviation21 = Statistics.deviation(list, bar.moving21, 21);
    }

    private long startTime;

    protected BarEntity parseString(String line, String symbol, Integer interval) {
        if (!line.matches("(a[0-9]{10}|[0-9]+),[0-9\\.]+,[0-9\\.]+,[0-9\\.]+,[0-9\\.]+,[0-9]+")){
            return null;
        }

        String data[] = line.split(",");
        long time;

        if (data[0].matches("a[0-9]{10}")) {
            startTime = Long.parseLong(data[0].substring(1)) * 1000 - TimingUtils.offsetInMillis;
            time = startTime;
        } else {
            time = startTime + Long.parseLong(data[0]) * interval * 1000;
        }

//COLUMNS=DATE,CLOSE,HIGH,LOW,OPEN,VOLUME

        BarPk barPk = new BarPk();
        barPk.symbol = symbol.trim();
        barPk.time = time;

        BarEntity bar = new BarEntity();
        bar.barPk = barPk;

        Calendar calendar = Calendar.getInstance();
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

    protected static void schedule(StockDao.StockType stockType, int threadsInPool, Class clazz){
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(threadsInPool);
        List<StockEntity> stockEntities = StockDao.getStockList(stockType);

        try {
            Constructor constructor = clazz.getConstructor(StockEntity.class);

            int i = 0;
            for (StockEntity stock : stockEntities) {
                executorService.schedule( (Runnable)constructor.newInstance(stock) , 1000 + 100 * i++, TimeUnit.MILLISECONDS);
            }

            executorService.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
