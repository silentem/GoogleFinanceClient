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
public class FindProxiesInYoudao implements Runnable {
    private static final String URL = "http://www.youdao.com/search?q=%22browse.php%3Fu%3D%22&ue=utf8&lq=%22browse.php%3Fu%3D%22&timesort=0&start=";
    private static final Set<String> processed = new HashSet<String>();

    private final String testUrl;
    private final String proxyUrl;
    private final String ip;

    private FindProxiesInYoudao(String testUrl, String proxyUrl, String ip) {
        this.testUrl = testUrl;
        this.proxyUrl = proxyUrl;
        this.ip = ip;
    }

    @SuppressWarnings({"ConstantConditions"})
    public static void main(final String[] args) throws Exception {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(20);

        for (int i = 0; i < 50; i++) {

            URL url = new URL(URL + i*10 + "&keyfrom=web.page" + (i + 1));

            System.out.println(URL + i*10 + "&keyfrom=web.page" + (i + 1));

            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;
            Set<String> set = new HashSet<String>();

            try{                          
                while ((line = reader.readLine()) != null) {
                    if (line.matches(".*http://[^\"=]*/<span class=\"hl\">browse.*")) {
                        line = line.replaceAll(".*(http://[^\"=]*)/<span class=\"hl\">browse.*", "$1");
                        line = line.replaceAll("<wbr>", "");

                        if (line.endsWith(".co.cc") || line.endsWith(".cz.cc")) {
                            System.err.println(line);
                            continue;
                        }

                        set.add(line);
                    }
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }

            reader.close();

            for (String proxyUrl : set) {

                String ip = FindTechFaqProxies.getUrlIP(proxyUrl);

                if (ip == null || processed.contains(ip)) {
                    System.err.println(proxyUrl);
                    continue;
                }

                processed.add(ip);

                ProxyEntity proxyEntity = ProxyDao.getProxy(ip);

                if (proxyEntity != null) {
                    continue;
                }

                String testUrl = proxyUrl + "/browse.php?u=http://www.google.com/finance/getprices?q=GOOG";
                executorService.schedule(new FindProxiesInYoudao(testUrl, proxyUrl, ip), 100, TimeUnit.MILLISECONDS);
            }

            Thread.sleep(1000);
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
