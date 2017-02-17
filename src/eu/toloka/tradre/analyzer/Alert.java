package eu.toloka.tradre.analyzer;

import eu.toloka.tradre.persistence.PersistenceManager;
import eu.toloka.tradre.persistence.entity.StockEntity;
import eu.toloka.tradre.time.TimingUtils;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

//import java.awt.*;
//import java.awt.datatransfer.Clipboard;
//import java.awt.datatransfer.ClipboardOwner;
//import java.awt.datatransfer.StringSelection;
//import java.awt.datatransfer.Transferable;
import java.io.*;
import java.util.*;
import java.util.List;

import static eu.toloka.tradre.time.TimingUtils.timeFormat;
import static eu.toloka.tradre.time.TimingUtils.dateTimeFormat;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class Alert {

  public static final List<String> signals = new ArrayList<String>();

  public static final Date dayBegin = TimingUtils.getDayBegin(TimingUtils.getCurrentDate());
//    public static final Date yesterdayBegin = TimingUtils.getDayBegin(TimingUtils.getCurrentDate());
//    public static final Integer MILLIS_HOUR = 3600 * 1000;
//    public static final Integer OFFSET_HOURS = 5 + TimeZone.getDefault().getRawOffset() / MILLIS_HOUR;
//
//    static {
//        Long time = System.currentTimeMillis() - OFFSET_HOURS * MILLIS_HOUR;
//        time = time / (24 * MILLIS_HOUR) + (long)9.5 * MILLIS_HOUR;
//        dayBegin = new Date(time);
//    }

  public static final Set<String> insertedList = new HashSet<String>();
  public static StockEntity lastStock;

  public static synchronized void openChrome(final String symbol) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String path = "\"C:\\Documents and Settings\\vikkoro\\Local Settings\\Application Data\\Google\\Chrome\\Application\\chrome.exe\" ";
          Process p = Runtime.getRuntime().exec(path + " http://www.google.com/finance?q=" + symbol);
          p.waitFor();
        }
        catch (Exception ex) {
          System.err.println(ex.getMessage());
        }
      }
    }).start();
  }

//    public static void sendSymbolToAndy(final String symbol) {
//        new Thread(new Runnable() {
//            public void run() {
//                try {
//                    URL url = new URL("http://109.108.232.224/trader/a.pl?" + symbol);
//                    InputStream is = url.openStream();
//                    is.close();
//                }
//                catch (Exception ex) {
//                    System.err.println(ex.getMessage());
//                }
//            }
//        }).start();
//    }

  public static synchronized void insert(final String symbol) {
    try {

//            class CO implements ClipboardOwner{
//
//                @Override
//                public void lostOwnership(Clipboard clipboard, Transferable transferable) {
//
//                }
//            }
//
//            CO co = new CO();
//
//            StringSelection stringSelection = new StringSelection(symbol);
//            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//            clipboard.setContents(stringSelection, co);

      String path = "C:\\Perl\\bin\\perl.exe ";
      Process p = Runtime.getRuntime().exec(path + " C:\\Projects\\Tradre\\perl\\insert.pl " + symbol);
      p.waitFor();
    } catch (Exception ex) {
      System.err.println(ex.getMessage());
    }
  }

  public static synchronized void clearPool() {
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter("C:\\Projects\\Tradre\\data\\pool.txt"));
      out.write("");
      out.close();
    } catch (Exception ex) {
      System.err.println(ex.getMessage());
    }
  }

  public static synchronized Set<String> readPool() {
    Set<String> pool = new HashSet<String>();

    try {
      BufferedReader in = new BufferedReader(new FileReader("C:\\Projects\\Tradre\\data\\pool.txt"));

      String line;

      while((line = in.readLine()) != null){
        pool.add(line);
      }

    } catch (Exception ex) {
      System.err.println(ex.getMessage());
    }

    return pool;
  }

  public static synchronized void appendStockToPool(String symbol) {
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter("C:\\Projects\\Tradre\\data\\pool.txt", true));
      out.write(symbol + "\n");
      out.close();
    } catch (Exception ex) {
      System.err.println(ex.getMessage());
    }
  }


  public static synchronized void importSymbols(final Set<String> symbols) {
    try {

      BufferedWriter out = new BufferedWriter(new FileWriter("C:\\Projects\\Tradre\\data\\stock.txt"));

      for (String symbol : symbols) {
        out.write(symbol + "\n");
      }

      out.close();

//            String path = "C:\\Perl\\bin\\perl.exe ";
//            Process p = Runtime.getRuntime().exec(path + " C:\\Projects\\Tradre\\perl\\import.pl");
//            p.waitFor();
    } catch (Exception ex) {
      System.err.println(ex.getMessage());
    }
  }

  private static Boolean on = true;

  private static synchronized void bells() {
    if (on) {
      new Thread(new Runnable() {
        public void run() {
          try {
            InputStream in = new FileInputStream("C:\\Projects\\Tradre\\sound\\magic_bells.wav");
            AudioStream as = new AudioStream(in);
            AudioPlayer.player.start(as);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }).start();
    }
  }

  public static void on() {
    on = true;
  }

  public static void off() {
    on = false;
  }

//    public static void alertMovingShort(StockEntity stock, Bar bar, Float movingValue, String label, Float delta) {
//        alertMovingBuy(stock, bar, movingValue, label, delta, false);
//        alertMovingSell(stock, bar, movingValue, label, delta, false);
//    }

//    private static void alertMovingBuy(StockEntity stock, Bar bar, Float movingValue, String label, Float delta, Boolean addValue) {
//        if (movingValue != null && bar.getEntity().low - movingValue < delta) {
//            if (!(bar.time() < dayBegin.getTime()) || TEST_MODE) {
//                if (signals.contains(stock.symbol + " (P) buy")) {
//                    alert(stock, label + " buy" + (addValue ? " " + movingValue : ""), null, false, false);
//                }
//            }
//        }
//    }
//
//    private static void alertMovingSell(StockEntity stock, Bar bar, Float movingValue, String label, Float delta, Boolean addValue) {
//        if (movingValue != null && movingValue - bar.getEntity().high < delta) {
//            if (!(bar.time() < dayBegin.getTime()) || TEST_MODE) {
//                if (signals.contains(stock.symbol + " (P) sell")) {
//                    alert(stock, label + " sell" + (addValue ? " " + movingValue : ""), null, false, false);
//                }
//            }
//        }
//    }

  public static void alert(StockEntity stock, String label, Long time, Boolean sound) {
    alert(stock, label, time, time, sound, null);
  }

//    public static void alert(StockEntity stock, String label, Long labelTime, Long barTime) {
//        alert(stock, label, labelTime, barTime, true, false);
//    }

  public static void alert(StockEntity stock, String label, Long labelTime, Long barTime, Boolean sound, Set<String> symbolSet) {
    label = stock.symbol + " " + label;
    String labelTimeString = labelTime == null ? "" : " " + timeFormat.format(new Date(labelTime));
    String barTimeString = barTime == null ? "" : " " + dateTimeFormat.format(new Date(barTime));

    if (!signals.contains(label + labelTimeString) || !signals.contains(label)) {
      signals.add(label + labelTimeString);
      signals.add(label);
      signals.add(stock.symbol);

      if (symbolSet != null) {
        symbolSet.add(stock.symbol);
      }

      System.out.println(label + barTimeString);
      lastStock = stock;

      stock.counter = stock.counter + 1;
      PersistenceManager.merge(stock);

      if (sound) {
        bells();
      }
    }
  }

}
