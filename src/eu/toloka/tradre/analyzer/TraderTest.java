package eu.toloka.tradre.analyzer;


import eu.toloka.tradre.persistence.dao.StockDao;
import eu.toloka.tradre.persistence.entity.StockEntity;
import eu.toloka.tradre.spider.GoogleFinance;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class TraderTest implements Runnable {
    private static final Map<String, BarCluster> clusterMap = new HashMap<String, BarCluster>();
    private static int i = 0;
    private static float price = 70f;

    private final List<StockEntity> stockList;
    private final Integer interval;
    public static Set<String> symbolSet = new HashSet<String>();

    @SuppressWarnings({"UnusedDeclaration"})
    public TraderTest(List<StockEntity> stockList, Integer interval) {
        this.stockList = stockList;
        this.interval = interval;
    }

    public static void main(final String[] args) {

        schedule(300, 120, 25);

        readInput();
    }


    private static void readInput() {
        Scanner in = new Scanner(System.in);

        do {
            try {
                String line = in.nextLine().trim();

                if (line.equalsIgnoreCase("on")) {
                    Alert.on();
                } else if (line.equalsIgnoreCase("off")) {
                    Alert.off();
                } else if (line.equalsIgnoreCase("s")) {
                    List<String> list = new ArrayList<String>(Alert.insertedList);
                    Collections.sort(list);
                    System.out.println(list);
                } else if (line.matches("[0-9]+")) {
                    price = Integer.parseInt(line);
                } else if (line.startsWith("+")) {
                    price += Integer.parseInt(line.substring(1));
                    System.out.println(price);
                } else if (line.startsWith("-")) {
                    price -= Integer.parseInt(line.substring(1));
                    System.out.println(price);
                } else {
                    if (Alert.lastStock != null) {
                        Alert.openChrome(Alert.lastStock.symbol);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } while (true);

    }

    private static void schedule(Integer interval, Integer period, Integer threads) {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(threads);
        List<StockEntity> allStocksList = StockDao.getStockList(StockDao.StockType.WATCHED);
        int stackSize = (int) Math.ceil((float) allStocksList.size() / period);

        List<StockEntity> localList = new ArrayList<StockEntity>();

        try {
            int i = 1;
            for (StockEntity stock : allStocksList) {
                localList.add(stock);

                if (i++ % stackSize == 0) {
                    executorService.scheduleWithFixedDelay(new TraderTest(localList, interval), 5 + i / stackSize, (int) period, TimeUnit.SECONDS);
                    localList = new ArrayList<StockEntity>();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        for (StockEntity stock : stockList) {

            switch (interval) {
                case 300:
                    if (stock.symbol.equals(".DJI")) {
                        System.out.println("\n5MIN " + new Date());
                    }

//                    System.out.println(stock.symbol);
                    GoogleFinance.updateBarListMap(stock.symbol, stock.exchange, interval, "3d", clusterMap);

                    BarCluster barCluster = clusterMap.get(stock.symbol);

                    if (barCluster == null) {
                        continue;
                    }

                    BarList barList = barCluster.getBarList(interval);

                    if (barList.size() == 0) {
                        continue;
                    }

                    analyse(stock, barList);
            }
        }
    }

    public static BarCluster getBarCluster(String symbol) {
        return clusterMap.get(symbol);
    }

    private void analyse(StockEntity stock, BarList barList) {

        barList = barList.lastBars(7);

//        VolumeEntity volumeEntity = VolumeDao.getVolume(stock.symbol);
//        Long volume = 0l;

        for (int i = 0; i < barList.size(); i++) {
            Bar bar = barList.get(i);

            if(Math.abs(bar.m21() - bar.close()) > price / 100 ){
//                Alert.alert(stock, "(S)", bar.time(), bar.time(), false, false, symbolSet);
            }
        }


        barList = barList.lastBars(3);

        if(barList.size() == 3){

            if(barList.get(1).green() && barList.get(2).green() &&
                    barList.get(0).closeOpen() > 0.2 &&
                    barList.get(1).closeOpen() * 3 < barList.get(0).closeOpen() &&
                    barList.get(2).closeOpen() * 3 < barList.get(0).closeOpen() &&
                    barList.get(1).low() < barList.get(2).low()){
                Alert.alert(stock, "(S) buy", barList.get(0).time(), barList.get(0).time(), false, symbolSet);
            }

            if(barList.get(1).red() && barList.get(2).red() &&
                    barList.get(0).openClose() > 0.2 &&
                    barList.get(1).openClose() * 3 < barList.get(0).openClose() &&
                    barList.get(2).openClose() * 3 < barList.get(0).openClose() &&
                    barList.get(1).high() > barList.get(2).high()){
                Alert.alert(stock, "(S) sell", barList.get(0).time(), barList.get(0).time(), false, symbolSet);
            }
        }




//        new FalseBreakBuyTemplate(stock, symbolSet).process(barList);

//        List<ITemplate> templates = new ArrayList<ITemplate>();
//        templates.add(new AssBuyTemplate(stock));
//        templates.add(new AssSellTemplate(stock));

//        for (int i = 0; i < barList.size(); i++) {
//            Bar bar = barList.get(i);

//            if (bar.low() > bar.m21() && bar.red()) {
//                removeOldTemplates(templates, AssBuyTemplate.class);

//                templates.add(new AssBuyTemplate(stock));
//            }
//
//            if (bar.high() < bar.m21() && bar.green()) {
//                removeOldTemplates(templates, AssSellTemplate.class);
//                templates.add(new AssSellTemplate(stock));
//            }

//            for (ITemplate template : templates) {
//                template.process(bar, i, false);
//            }
//        }

        System.out.print("");

        if (i++ % 10 == 0) {
            System.out.print(".");
        }


//        barList.addAttribute(new AddDayEndAttributeProcessor());
//
//        barList.addAttribute(new MovingIntersectAttributeProcessor(5, 8));
//        barList.addAttribute(new MovingIntersectAttributeProcessor(8, 13));
//        barList.addAttribute(new MovingIntersectAttributeProcessor(13, 21));
//        barList.addAttribute(new MovingIntersectAttributeProcessor(21, 34));

//        barList.addAttribute(new RemoveSmallMovingRangesAttributeProcessor());
//
//        barList.addAttribute(new MovingIntersectAttributeProcessor(3, 5));
//
//        -----------------------------

//        barList.addAttribute(new ExtremumOnMovingAttributeProcessor(3, 5));
//        barList.addAttribute(new ExtremumOnMovingAttributeProcessor(5, 3));
//
//        barList.addAttribute(new ExtremumOnMovingAttributeProcessor(5, 8));
//        barList.addAttribute(new ExtremumOnMovingAttributeProcessor(8, 5));
//
//        barList.addAttribute(new ExtremumOnMovingAttributeProcessor(8, 13));
//        barList.addAttribute(new ExtremumOnMovingAttributeProcessor(13, 8));
//
//        barList.addAttribute(new ExtremumOnMovingAttributeProcessor(13, 21));
//        barList.addAttribute(new ExtremumOnMovingAttributeProcessor(21, 13));

//        barList.addAttribute(new ExtremumOnMovingAttributeProcessor(21, 34));
//        barList.addAttribute(new ExtremumOnMovingAttributeProcessor(34, 21));


//        List<ITemplate> templates = new ArrayList<ITemplate>();

//        int barCount = 0;
//
//        for (int i = 0; i < barList.size(); i++) {
//            Bar bar = barList.get(i);
//

//            if (stock.pivot != null && bar.has(minimum138)) {
//                removeOldTemplates(templates, ElliotMovingBuyTemplate.class);
//                templates.add(new ElliotMovingBuyTemplate(stock, maximum, minimum));
//            }
//
//            if (stock.pivot != null && bar.has(maximum813)) {
//                removeOldTemplates(templates, ElliotMovingSellTemplate.class);
//                templates.add(new ElliotMovingSellTemplate(stock, maximum, minimum));
//            }

// !!!!!!!!!!!!!1
//            if (bar.time() >= Alert.dayBegin.getTime() || Alert.TEST_MODE) {
//                if (bar.getEntity().moving13 != null && bar.high() - price / 100 > bar.getEntity().moving13) {
//                    Alert.alert(stock, "(P) sell", bar.time(), false, true);
//                }
//
//                if (bar.getEntity().moving13 != null && bar.low() + price / 100 < bar.getEntity().moving13) {
//                    Alert.alert(stock, "(P) buy", bar.time(), false, true);
//                }
//            }


//            if (bar.getEntity().moving21 != null && bar.getEntity().moving55 != null &&
//                    bar.low() < bar.getEntity().moving21 &&
//                    bar.getEntity().moving21 - bar.getEntity().moving55 > 0.4) {
//                if (bar.time() >= Alert.dayBegin.getTime() || Alert.TEST_MODE) {
//                    Alert.alert(stock, "(21M) sell", bar.time(), true, true);
//                }
//            }
//
//            if (bar.getEntity().moving21 != null && bar.getEntity().moving55 != null &&
//                    bar.getEntity().moving21 < bar.high() &&
//                    -bar.getEntity().moving21 + bar.getEntity().moving55 > 0.4) {
//                if (bar.time() >= Alert.dayBegin.getTime() || Alert.TEST_MODE) {
//                    Alert.alert(stock, "(21M) buy", bar.time(), true, true);
//                }
//            }


//            for (ITemplate template : templates) {
//                template.process(bar, i, barList.size() - i < 15);
//            }
//        }

//        System.out.print("");
//
//        if (i++ % 10 == 0) {
//            System.out.print(".");
//        }
    }

//    private static void removeOldTemplates(List<ITemplate> templates, Class templateClass) {
//        List<ITemplate> templatesToDelete = new ArrayList<ITemplate>();
//
//        for (ITemplate template : templates) {
//            if (template.getClass().equals(templateClass)) {
//                templatesToDelete.add(template);
//            }
//        }
//
//        if (templatesToDelete.size() > 3) {
//            templatesToDelete.remove(templatesToDelete.size() - 1);
//            templatesToDelete.remove(templatesToDelete.size() - 1);
//            templatesToDelete.remove(templatesToDelete.size() - 1);
//
//            templates.removeAll(templatesToDelete);
//        }
//    }

//    private static final EAttribute maximum = EAttribute.getExtremumAttribute(8, 13);
//    private static final EAttribute minimum = EAttribute.getExtremumAttribute(13, 8);

//    private static final EAttribute maximum35 = EAttribute.getExtremumAttribute(3, 5);
//    private static final EAttribute minimum53 = EAttribute.getExtremumAttribute(5, 3);

//    private static final EAttribute maximum813 = EAttribute.getExtremumAttribute(8, 13);
//    private static final EAttribute minimum138 = EAttribute.getExtremumAttribute(13, 8);

}
