package eu.toloka.tradre.proxy;

import eu.toloka.tradre.persistence.dao.ProxyDao;
import eu.toloka.tradre.persistence.entity.ProxyEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class FindProxiesInGoogle implements Runnable {
    private static final String URL = "http://www.google.com/m?gl=uk&q=%22browse.php?u%22&hl=en&sa=N&start=";
    private static final Set<String> processed = new HashSet<String>();

    private final String testUrl;
    private final String proxyUrl;
    private final String ip;

    private FindProxiesInGoogle(String testUrl, String proxyUrl, String ip) {
        this.testUrl = testUrl;
        this.proxyUrl = proxyUrl;
        this.ip = ip;
    }

    public static void main(final String[] args) throws Exception {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(20);

        for (int i = 0; i < 50; i++) {

            URL url = new URL(URL + (i * 10));

            System.out.println(URL + (i * 10));

            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;
            Set<String> set = new HashSet<String>();

            while ((line = reader.readLine()) != null) {
                if (line.startsWith(" <wml> <head>")) {
                    String[] ss = line.split("href=\"");

                    for (String proxyUrl : ss) {
                        if (proxyUrl.matches(".*http://[^\"=]*/browse.php.*")) {
                            proxyUrl = proxyUrl.replaceAll(".*(http://[^\"=]*)/browse.php.*", "$1");

                            if (proxyUrl.endsWith(".co.cc") || proxyUrl.endsWith(".cz.cc")) {
                                System.err.println(proxyUrl);
                                continue;
                            }

                            set.add(proxyUrl);
                        }
                    }
                }
            }

            reader.close();

            for (String proxyUrl : set) {

                String ip = FindTechFaqProxies.getUrlIP(proxyUrl);

                if(ip == null || processed.contains(ip)){
                    System.err.println(proxyUrl);
                    continue;
                }

                processed.add(ip);
                
                ProxyEntity proxyEntity = ProxyDao.getProxy(ip);

                if (proxyEntity != null) {
                    continue;
                }

                String testUrl = proxyUrl + "/browse.php?u=http://www.google.com/finance/getprices?q=GOOG";
                executorService.schedule(new FindProxiesInGoogle(testUrl, proxyUrl, ip), 100, TimeUnit.MILLISECONDS);
            }

            Thread.sleep(10000);
        }

        executorService.shutdown();
    }

    @Override
    public void run() {
        try {
            FindTechFaqProxies.processProxy(testUrl, proxyUrl, ip);
        } catch (IOException e) {
            System.err.println("! " + e.getMessage());
        }
    }
}
