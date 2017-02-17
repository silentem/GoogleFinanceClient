package eu.toloka.tradre.analyzer;

import eu.toloka.tradre.analyzer.attribute.ConsolidationAttributeProcessor;
import eu.toloka.tradre.analyzer.attribute.EAttribute;
import eu.toloka.tradre.analyzer.attribute.GoodMoveAttributeProcessor;
import eu.toloka.tradre.analyzer.attribute.RollbackAttributeProcessor;
import eu.toloka.tradre.persistence.dao.StockDao;
import eu.toloka.tradre.persistence.entity.StockEntity;
import eu.toloka.tradre.spider.GoogleFinance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
@SuppressWarnings("deprecation")
public class Trader2 implements Runnable {
    public static Float profit = 0f;
    public static Float loss = 0f;
//    public static StockWindow window = new StockWindow();
    private static int i = 0;
    private final List<StockEntity> stockList;

    @SuppressWarnings({"UnusedDeclaration"})
    public Trader2(List<StockEntity> stockList) {
        this.stockList = stockList;
    }

    public static void main(final String[] args) {


        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(15);
        List<StockEntity> allStocksList = StockDao.getStockList(StockDao.StockType.WATCHED);
        allStocksList.forEach(System.out::println);
        int stackSize = (int) Math.ceil((float) allStocksList.size() / 120);

        List<StockEntity> localList = new ArrayList<>();

        try {
            int i = 1;
            for (StockEntity stock : allStocksList) {
                localList.add(stock);

                if (i++ % stackSize == 0) {
                    executorService.scheduleWithFixedDelay(
                            new Trader2(localList), 5 + i / stackSize, (int) 300, TimeUnit.SECONDS);
                    localList = new ArrayList<>();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        for (StockEntity stock : stockList) {
            System.out.println(stock);
            analyse2(stock, 86400, "30d", 60, "25m", 0.6f);
            analyse2(stock, 3600, "5d", 60, "25m", 0.4f);
            analyse2(stock, 1800, "5d", 60, "25m", 0.35f);
           analyse2(stock, 900, "5d", 60, "25m", 0.35f);
          analyse2(stock, 600, "5d", 60, "25m", 0.30f);
            analyse(stock, 120, "1d");
            analyse(stock, 600, "1d");
            analyse(stock, 300, "1d");


            analyse2(stock, 3600, "2d", 60, "25m", 0.2f);
//            analyseTest(stock, 3600, "30d", 0.2f);
//            analyseTest(stock, 86400, "30d", 0.2f);
//            analyseTest2(stock, 1800, "30d");
//            analyseTest2(stock, 86400, "30d");

//            analyseTest3(stock, 300, "1d");
//            analyseTest3(stock, 120, "1d");

        }
    }

    private void analyse(StockEntity stock, Integer interval, String period) {

//        System.out.println("analyse");

        BarList barList = GoogleFinance.getBarList(stock.symbol, stock.exchange, interval, period);

        barList.forEach(bar ->
                System.out.println("BarList: " + bar + " Name: " + stock.symbol));

        barList = barList.lastBars(10);

        barList.addAttribute(new GoodMoveAttributeProcessor(0.4f, 3f, 5));
        barList.addAttribute(new ConsolidationAttributeProcessor(5));

        for (Bar bar : barList.lastBars(2)) {

            if (bar.getAttributeList().contains(EAttribute.ROLLBACK_UP)) {
//                window.addSymbol(stock.symbol);
                System.out.println(stock.symbol + " UP " + bar.high() + " " + bar.toString());
            }

            if (bar.getAttributeList().contains(EAttribute.ROLLBACK_DOWN)) {
//                window.addSymbol(stock.symbol);
                System.out.println(stock.symbol + " DOWN " + bar.low() + " " + bar.toString());
            }
        }

        if (i++ % 10 == 0) {
            System.out.print(".");
        }
    }

    private void analyse2(StockEntity stock, Integer interval, String period, Integer interval2, String period2, Float secondBar) {

//        System.out.println("analyse2");

        BarList barList = GoogleFinance.getBarList(stock.symbol, stock.exchange, interval, period);
        BarList barList2 = GoogleFinance.getBarList(stock.symbol, stock.exchange, interval2, period2);



        if (barList.size() < 3 || barList2.size() < 3) {
//            System.err.println(stock.symbol + " <3");
            return;
        }

        Bar grandPrev = barList.beforeLast();
        Bar prev = barList.last();
        Bar bar = barList2.last();

        System.out.println("BarList: " + grandPrev + " Name: " + stock.symbol);
        System.out.println("BarList: " + prev + " Name: " + stock.symbol);
        System.out.println("BarList: " + bar + " Name: " + stock.symbol);

        if (prev.open() < prev.close() && grandPrev.open() < grandPrev.close()) {

            Float prevMove = prev.close() - prev.open();
            Float grandPrevMove = grandPrev.close() - grandPrev.open();

            if (//bar.open() - prev.low() < 0.50 &&
                    prevMove * 4 < grandPrevMove &&
                            prevMove > prev.open() - prev.low() &&
                            prevMove < (prev.high() - prev.close()) / 2 &&
                            grandPrevMove > secondBar &&
                            bar.open() > prev.open()
//                            new Date(bar.time()).getHours() != 10 &&
//                    !(new Date(bar.time()).getHours() == 10 && new Date(bar.time()).getMinutes() == 0)
                    ) {
                System.out.println(stock.symbol + " " + interval / 60);
            }
        }

        if (prev.open() > prev.close() && grandPrev.open() > grandPrev.close()) {

            Float prevMove = prev.open() - prev.close();
            Float grandPrevMove = grandPrev.open() - grandPrev.close();

            if (//prev.high() - bar.open() < 0.50 &&
                    prevMove * 4 < grandPrevMove &&
                            prevMove > prev.high() - prev.open() &&
                            prevMove < (prev.close() - prev.low()) / 2 &&
                            grandPrevMove > secondBar &&
                            bar.open() < prev.open()
//                            new Date(bar.time()).getHours() != 10 &&
//                    !(new Date(bar.time()).getHours() == 10 && new Date(bar.time()).getMinutes() == 0)
                    ) {

                System.out.println(stock.symbol + " " + interval / 60);
            }
        }

        System.out.print("");

        if (i++ % 10 == 0) {
            System.out.print(".");
        }
    }


    private void analyseTest3(StockEntity stock, Integer interval, String period) {

        BarList barList = GoogleFinance.getBarList(stock.symbol, stock.exchange, interval, period);

        barList.addAttribute(new GoodMoveAttributeProcessor(0.6f, 5f, 3));
        barList.addAttribute(new RollbackAttributeProcessor());

        for (Bar bar : barList) {

            if (bar.getAttributeList().contains(EAttribute.ROLLBACK_UP)) {
//                window.addSymbol(stock.symbol);
                System.out.println(stock.symbol + " UP " + bar.open() + " " + bar.toString());
            }

            if (bar.getAttributeList().contains(EAttribute.ROLLBACK_DOWN)) {
//                window.addSymbol(stock.symbol);
                System.out.println(stock.symbol + " DOWN " + bar.open() + " " + bar.toString());
            }
        }

        if (i++ % 10 == 0) {
            System.out.print(".");
        }
    }

}


//    private void analyseTest(StockEntity stock, Integer interval, String period, Float secondBar) {
//
//        BarList barList = GoogleFinance.getBarList(stock.symbol, stock.exchange, interval, period);
//
//        if (barList.size() < 3) {
////            System.err.println(stock.symbol + " <3");
//            return;
//        }
//
//        barList = barList.getSubList(0, barList.size() - 2);
//
////        assert barList.size() == 3;
//
////        int red = 0, green = 0;
//        Bar grandPrev = null;
//        Bar prev = null;
//
//        for (Bar bar : barList) {
//
//            if (grandPrev != null && prev != null) {
//
//                if (prev.open() < prev.close() && grandPrev.open() < grandPrev.close()) {
//
//                    Float prevMove = prev.close() - prev.open();
//                    Float grandPrevMove = grandPrev.close() - grandPrev.open();
//
//                    if (grandPrevMove * 2 < prevMove &&
//                            prevMove < grandPrevMove * 4 &&
//                            prev.open() > grandPrev.open() &&
//                            bar.open() - prev.low() < 0.50 &&
//                            prev.open() < bar.open() &&
////                            new Date(bar.time()).getHours() == 10 &&
////                    new Date(bar.time()).getHours() != 16 &&
//                            Math.abs(prevMove) > secondBar) {
//
//                        assert bar.open() - prev.low() > 0;
//
//                        Float move = bar.close() - bar.open();
//                        Float stopLoss = Math.min(-move, bar.open() - prev.low());
//
//                        if (move > 0) {
//                            profit += move;
//                            System.out.println(stock.symbol + " +" + move + " " + new Date(bar.time()) + " " + profit);
//                        }
//
//                        if (move <= 0) {
//                            loss += stopLoss;
//                            System.err.println(stock.symbol + " -" + stopLoss + " " + new Date(bar.time()) + " " + loss);
//                        }
//
//                    }
//
////                    Alert.alert(stock, "S (buy) " + (interval/60), bar.time(), false);
//                }
//
//                if (prev.open() > prev.close() && grandPrev.open() > grandPrev.close()) {
//
//                    Float prevMove = prev.open() - prev.close();
//                    Float grandPrevMove = grandPrev.open() - grandPrev.close();
//
//
//                    if (grandPrevMove * 2 < prevMove &&
//                            prevMove < grandPrevMove * 4 &&
//                            prev.open() < grandPrev.open() &&
//                            prev.high() - bar.open() < 0.50 &&
//                            prev.open() > bar.open() &&
////                            new Date(bar.time()).getHours() == 10 &&
////                    new Date(bar.time()).getHours() != 16 &&
//                            Math.abs(prevMove) > secondBar) {
//
//
//                        assert prev.high() - bar.open() > 0;
//
//                        Float move = bar.open() - bar.close();
//                        Float stopLoss = Math.min(-move, prev.high() - bar.open());
//
//                        if (move > 0) {
//                            System.out.println(stock.symbol + " +" + move + " " + new Date(bar.time()) + " " + profit);
//                            profit += move;
//                        }
//
//                        if (move <= 0) {
//                            loss += stopLoss;
//                            System.err.println(stock.symbol + " -" + stopLoss + " " + new Date(bar.time()) + " " + loss);
//                        }
//
//                    }
//
////                    Alert.alert(stock, "S (buy) " + (interval/60), bar.time(), false);
//                }
//            }
//
//            grandPrev = prev;
//            prev = bar;
//        }
//
//        System.out.print("");
//
//        if (i++ % 10 == 0) {
//            System.out.print(".");
//        }
//
//    }
//
//
//    private void analyseTest2(StockEntity stock, Integer interval, String period) {
//
//        BarList barList = GoogleFinance.getBarList(stock.symbol, stock.exchange, interval, period);
//
//        if (barList.size() < 3) {
////            System.err.println(stock.symbol + " <3");
//            return;
//        }
//
////        barList = barList.getSubList(0, barList.size() - 2);
//
////        assert barList.size() == 3;
//
////        int red = 0, green = 0;
//        Bar grandGrandPrev = null;
//        Bar grandPrev = null;
//        Bar prev = null;
//
//        for (Bar bar : barList) {
//
//            if (grandGrandPrev != null && grandPrev != null && prev != null) {
//
//                if (prev.green() && grandPrev.green()) {
//
//                    Float prevMove = prev.close() - prev.open();
//                    Float grandPrevMove = grandPrev.close() - grandPrev.open();
//                    Float grandGrandPrevMove = Math.abs(grandGrandPrev.close() - grandGrandPrev.open());
//
//                    if (bar.open() - prev.low() < 0.50 &&
////                            prevMove * 4 < grandPrevMove &&
//                            prevMove * 4 < grandPrevMove &&
//                            prevMove > prev.open() - prev.low() &&
//                            prevMove < (prev.high() - prev.close()) / 2 &&
//                            grandPrevMove > 0.25 &&
//                            bar.open() > prev.open()
////                            new Date(bar.time()).getHours() != 10 &&
////                            !(new Date(bar.time()).getHours() == 10 && new Date(bar.time()).getMinutes() == 0)
//                            ) {
//
//                        assert bar.open() - prev.low() > 0;
//
//                        Float move = bar.close() - bar.open();
//                        Float stopLoss = Math.min(-move, bar.open() - prev.low());
//
//                        if (move > 0) {
//                            profit += move;
//                            System.out.println(stock.symbol + " +" + move + " " + new Date(bar.time()) + " " + profit);
//                        }
//
//                        if (move <= 0) {
//                            loss += stopLoss;
//                            System.err.println(stock.symbol + " -" + stopLoss + " " + new Date(bar.time()) + " " + loss);
//                        }
//
//                    }
//
////                    Alert.alert(stock, "S (buy) " + (interval/60), bar.time(), false);
//                }
//
//                if (prev.red() && grandPrev.red()) {
//
//                    Float prevMove = prev.open() - prev.close();
//                    Float grandPrevMove = grandPrev.open() - grandPrev.close();
//                    Float grandGrandPrevMove = Math.abs(grandGrandPrev.open() - grandGrandPrev.close());
//
//
//                    if (prev.high() - bar.open() < 0.50 &&
//                            prevMove * 4 < grandPrevMove &&
//                            prevMove > prev.high() - prev.open() &&
//                            prevMove < (prev.close() - prev.low()) / 2 &&
//                            grandPrevMove > 0.25 &&
//                            bar.open() < prev.open()
////                            new Date(bar.time()).getHours() != 10 &&
////                            !(new Date(bar.time()).getHours() == 10 && new Date(bar.time()).getMinutes() == 0)
//                            ) {
//
//
//                        assert prev.high() - bar.open() > 0;
//
//                        Float move = bar.open() - bar.close();
//                        Float stopLoss = Math.min(-move, prev.high() - bar.open());
//
//                        if (move > 0) {
//                            System.out.println(stock.symbol + " +" + move + " " + new Date(bar.time()) + " " + profit);
//                            profit += move;
//                        }
//
//                        if (move <= 0) {
//                            loss += stopLoss;
//                            System.err.println(stock.symbol + " -" + stopLoss + " " + new Date(bar.time()) + " " + loss);
//                        }
//
//                    }
//
////                    Alert.alert(stock, "S (buy) " + (interval/60), bar.time(), false);
//                }
//            }
//
//            grandGrandPrev = grandPrev;
//            grandPrev = prev;
//            prev = bar;
//        }
//
//        System.out.print("");
//
////        if (i++ % 10 == 0) {
////            System.out.print(".");
////        }
//
//    }
//
