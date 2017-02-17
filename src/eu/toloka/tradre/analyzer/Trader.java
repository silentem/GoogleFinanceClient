package eu.toloka.tradre.analyzer;

import eu.toloka.tradre.analyzer.template.ITemplate;
import eu.toloka.tradre.persistence.dao.StockDao;
import eu.toloka.tradre.persistence.entity.StockEntity;
import eu.toloka.tradre.spider.GoogleFinance;
import eu.toloka.tradre.spider.Statistics;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class Trader implements Runnable {
  private static final Map<String, BarCluster> clusterMap = new HashMap<String, BarCluster>();
  private static int i = 0;

  private final List<StockEntity> stockList;
  private final Set<String> symbolSet;
  private final Integer interval;
  private final String period;

  private static final Map<String, Float> deviationMap = new HashMap<String, Float>();

  private static Set<String> pool = new HashSet<String>();

  @SuppressWarnings({"UnusedDeclaration"})
  public Trader(List<StockEntity> stockList, Set<String> symbolSet, Integer interval, String period) {
    this.stockList = stockList;
    this.symbolSet = symbolSet;
    this.interval = interval;
    this.period = period;
  }

  public static void main(final String[] args) {
    Alert.clearPool();
    Set<String> symbolSet5min = new HashSet<String>();
    schedule(symbolSet5min, 300, "2d", 120, 25);

//    pool = Alert.readPool();
//    Set<String> symbolSetOther = new HashSet<String>();
//    new StockWindow(symbolSetOther, 900, 25, 50);
//    schedule(symbolSetOther, 60, "2d", 120, 25);

//        readInput();
  }


//    private static void readInput() {
//        Scanner in = new Scanner(System.in);
//
//        do {
//            try {
//                String line = in.nextLine().trim();
//
//                if (line.equalsIgnoreCase("on")) {
//                    Alert.on();
//                } else if (line.equalsIgnoreCase("off")) {
//                    Alert.off();
//                } else if (line.equalsIgnoreCase("s")) {
//                    List<String> list = new ArrayList<String>(Alert.insertedList);
//                    Collections.sort(list);
//                    System.out.println(list);
//                } else if (line.matches("[0-9]+")) {
//                    price = Integer.parseInt(line);
//                } else if (line.startsWith("+")) {
//                    price += Integer.parseInt(line.substring(1));
//                    System.out.println(price);
//                } else if (line.startsWith("-")) {
//                    price -= Integer.parseInt(line.substring(1));
//                    System.out.println(price);
//                } else {
//                    if (Alert.lastStock != null) {
//                        Alert.openChrome(Alert.lastStock.symbol);
//                    }
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        } while (true);
//
//    }

  private static void schedule(Set<String> symbolSet, Integer interval, String period, Integer polingPeriod, Integer threads) {
    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(threads);
    List<StockEntity> allStocksList = StockDao.getStockList(StockDao.StockType.WATCHED, ".DJI");
    int stackSize = (int) Math.ceil((float) allStocksList.size() / polingPeriod);

    List<StockEntity> localList = new ArrayList<StockEntity>();

    try {
      int i = 1;
      for (StockEntity stock : allStocksList) {
        localList.add(stock);

        if (i++ % stackSize == 0) {
          executorService.scheduleWithFixedDelay(new Trader(localList, symbolSet, interval, period), 5 + i / stackSize, (int) polingPeriod, TimeUnit.SECONDS);
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
      if (stock.symbol.equals(".DJI")) {
        System.out.println("\n" + new Date());
      }

      GoogleFinance.updateBarListMap(stock.symbol, stock.exchange, interval, period, clusterMap);

      BarCluster barCluster = clusterMap.get(stock.symbol);

      if (barCluster == null || stock.symbol.equals(".DJI")) {
        continue;
      }

      BarCluster djiBarCluster = clusterMap.get(".DJI");

      analyse(stock, barCluster.getBarList(interval), djiBarCluster.getBarList(interval), interval);
    }
  }

  public static BarCluster getBarCluster(String symbol) {
    return clusterMap.get(symbol);
  }

  private void analyse(StockEntity stock, BarList barList, BarList djiBarList, Integer interval) {

    if (barList.size() == 0) {
      return;
    }

    if (interval == 60) {
      float priceLow = 70f;
      float priceHigh = 150f;

      barList = barList.lastBars(3);

      for (Bar bar : barList) {
        Float diff = Math.abs(bar.m21() - bar.close());

        if (diff > priceLow / 100 && diff < priceHigh / 100) {
          if (!pool.contains(stock.symbol)) {
            pool.add(stock.symbol);
            Alert.appendStockToPool(stock.symbol);
          }

          Alert.alert(stock, "(S)", bar.time(), bar.time(), false, symbolSet);
        }
      }
    }

    if (interval == 300) {

//              pool.contains(stock.symbol) &&

      try{
        Float deviation0 = Statistics.deviation(barList.getBarEntityList(), barList.last().moving(8), 8);

        Float oldDeviation = deviationMap.get(stock.symbol);

//barList.lastBars(8).getOCHLRelation() < 0.5 &&
        if(oldDeviation != null && oldDeviation * 1.1 < deviation0
                && oldDeviation < 0.04
                && (
                (barList.last().green() && barList.beforeLast().red() && barList.last().closeOpen() > 0.05 && barList.beforeLast().openClose() > 0.05 )
                ||
                (barList.last().red() && barList.beforeLast().green() && barList.last().openClose() > 0.05 && barList.beforeLast().closeOpen() > 0.05 )
                )){
            Alert.alert(stock, "(D)", barList.last().time(),
                    barList.last().time(), false, symbolSet);
        }

//        if(oldDeviation != null && oldDeviation * 0.7 > deviation0 && oldDeviation > 0.05){
//            Alert.alert(stock, "(D2)", barList.lastBars(1).get(0).time(),
//                    barList.last().time(), false, symbolSet);
//        }

        deviationMap.put(stock.symbol, deviation0);
      }catch (Exception ex){
        ex.printStackTrace();
      }

//        Float deviation1 = Statistics.deviation(barList.getBarEntityList(), barList.lastBars(1).get(0).moving(13), 13);
//        Float deviation2 = Statistics.deviation(barList.getBarEntityList(), barList.lastBars(1).get(0).moving(21), 21);

//        Float correlation = getCorrelation(djiBarList, barList, 21);
//
//        if (correlation < 0.3 && correlation > -0.3) {
//          if (
//                  (deviation1 > deviation0 * 2 && deviation0 < 0.025)
//                          ||
//                  (deviation2 > deviation1 * 2 && deviation1 < 0.025)
//                  ) {
//            Alert.alert(stock, "(D)", barList.lastBars(1).get(0).time(),
//                    barList.lastBars(1).get(0).time(), false, symbolSet);
//          }
//        }

//            barList = barList.lastBars(12);
//
//            if (barList.getFlatBarsRelation() < 0.1 && barList.getHighLowAbs() > 0.15) {
//                {
//                    Collections.reverse(barList);
//                    Integer[][] redGreenCounts = getRedGreenCounts(barList);
//
//                    upScenario(redGreenCounts, stock, barList.get(0).time());
//                    downScenario(redGreenCounts, stock, barList.get(0).time());
//
////                    if (!up && !down) {
////                        symbolSet.remove(stock.symbol);
////                    }
//                }
//
//                barList.remove(0);
//
//                {
//                    Collections.reverse(barList);
//                    Integer[][] redGreenCounts = getRedGreenCounts(barList);
//
//                    upScenario(redGreenCounts, stock, barList.get(0).time());
//                    downScenario(redGreenCounts, stock, barList.get(0).time());
//
////                    if (!up && !down) {
////                        symbolSet.remove(stock.symbol);
////                    }
//                }
//            }
    }

    if (i++ % 10 == 0) {
      System.out.print(".");
    }
  }

//    private boolean upScenario(Integer[][] redGreenCounts, StockEntity stock, Long time) {
//        Boolean red = false;
//        Boolean green = false;
//
//        for (Integer[] redGreen : redGreenCounts) {
//            if (redGreen[0] >= 3) {
//                red = true;
//            }
//
//            if (red && redGreen[1] < 7 && redGreen[1] > 0) {
//                break;
//            }
//
//            if (red && redGreen[1] >= 7) {
//                green = true;
//            }
//        }
//
//        if (red && green) {
//            Alert.alert(stock, "(up)", time, time, true, symbolSet2);
//            return true;
//        }
//
//        return false;
//    }
//
//    private boolean downScenario(Integer[][] redGreenCounts, StockEntity stock, Long time) {
//        Boolean red = false;
//        Boolean green = false;
//
//        for (Integer[] redGreen : redGreenCounts) {
//            if (redGreen[1] >= 3) {
//                green = true;
//            }
//
//            if (green && redGreen[0] < 7 && redGreen[0] > 0) {
//                break;
//            }
//
//            if (green && redGreen[0] >= 7) {
//                red = true;
//            }
//        }
//
//        if (red && green) {
//            Alert.alert(stock, "(down)", time, time, true, symbolSet2);
//            return true;
//        }
//
//        return false;
//    }
//
//    private Integer[][] getRedGreenCounts(BarList barList) {
//        Integer[][] redGreenCounts = new Integer[barList.size()][2];
//
//        int i = 0;
//
//        for (Bar bar : barList) {
//            redGreenCounts[i][0] = 0;
//            redGreenCounts[i][1] = 0;
//
//            if (bar.red()) {
//                if (i == 0) {
//                    redGreenCounts[i][0] = 1;
//                } else {
//                    redGreenCounts[i][0] = redGreenCounts[i - 1][0] + 1;
//                    redGreenCounts[i - 1][0] = 0;
//                }
//            }
//
//            if (bar.green()) {
//                if (i == 0) {
//                    redGreenCounts[i][1] = 1;
//                } else {
//                    redGreenCounts[i][1] = redGreenCounts[i - 1][1] + 1;
//                    redGreenCounts[i - 1][1] = 0;
//                }
//            }
//
//            i++;
//        }
//
//        return redGreenCounts;
//    }

  private static float getCorrelation(BarList list1, BarList list2, int n) {
    Map<Long, Bar> map1 = new HashMap<Long, Bar>();
    Map<Long, Bar> map2 = new HashMap<Long, Bar>();

    for (Bar bar : list1) {
      map1.put(bar.time(), bar);
    }

    for (Bar bar : list2) {
      map2.put(bar.time(), bar);
    }

    Set<Long> commonSet = new HashSet<Long>(map1.keySet());
    commonSet.retainAll(map2.keySet());

    List<Long> list = new ArrayList<Long>(commonSet);

    Collections.sort(list);

    List<Bar> l1 = new ArrayList<Bar>(list.size());
    List<Bar> l2 = new ArrayList<Bar>(list.size());

    for (Long l : list) {
      l1.add(map1.get(l));
      l2.add(map2.get(l));
    }

    return Statistics.calculateCorrelation(l1, l2, Math.min(n, list.size()));
  }
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


//        barList = barList.lastBars(3);
//
//        if (barList.size() == 3) {
//
//            if (barList.get(1).green() && barList.get(2).green() &&
//                    barList.get(0).closeOpen() > 0.2 &&
//                    barList.get(1).closeOpen() * 3 < barList.get(0).closeOpen() &&
//                    barList.get(2).closeOpen() * 3 < barList.get(0).closeOpen() &&
//                    barList.get(1).low() < barList.get(2).low()) {
//                Alert.alert(stock, "(S) buy", barList.get(0).time(), barList.get(0).time(), false, false, symbolSet2);
//            }
//
//            if (barList.get(1).red() && barList.get(2).red() &&
//                    barList.get(0).openClose() > 0.2 &&
//                    barList.get(1).openClose() * 3 < barList.get(0).openClose() &&
//                    barList.get(2).openClose() * 3 < barList.get(0).openClose() &&
//                    barList.get(1).high() > barList.get(2).high()) {
//                Alert.alert(stock, "(S) sell", barList.get(0).time(), barList.get(0).time(), false, false, symbolSet2);
//            }
//        }


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
