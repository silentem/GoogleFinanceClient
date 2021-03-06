package eu.toloka.tradre.proxy;

import eu.toloka.tradre.persistence.PersistenceManager;
import eu.toloka.tradre.persistence.dao.ProxyDao;
import eu.toloka.tradre.persistence.entity.ProxyEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class FindProxiesInAllProxySitesCom implements Runnable {

    private static Object[][] proxyMetaArray = {
            {44, 25, "CGIProxy"},
            {45, 25, "PHProxy"},
            {46, 25, "Zelune"},
            {47, 25, "Happy Proxy"},
            {48, 25, "ASProxy"},
            {49, 25, "Other Proxies"},
            {50, 25, "Surrogafier"},
            {51, 25, "Glype"},
    };

    private static Object[] proxyMeta = proxyMetaArray[0];

    private static final String URL = "http://www.allproxysites.com/category.php?id=" + proxyMeta[0] + "&start=";
    private static final Set<String> processed = new HashSet<String>();

    private final String testUrl;
    private final String proxyUrl;
    private final String ip;

    private FindProxiesInAllProxySitesCom(String testUrl, String proxyUrl, String ip) {
        this.testUrl = testUrl;
        this.proxyUrl = proxyUrl;
        this.ip = ip;
    }

    @SuppressWarnings({"ConstantConditions"})
    public static void main(final String[] args) throws Exception {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        int j = 0;

        for (int i = 0; i < (int) proxyMeta[1]; i++) {
            try {
                URL url = new URL(URL + (i * 15));

                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.contains("rel=\"nofollow\"")) {
                        String proxyUrl = line.replaceAll(".*rel=\"nofollow\">([^<]*)</a>.*", "$1").trim().replaceAll("/$", "");

                        if (proxyUrl.endsWith(".co.cc") || proxyUrl.endsWith(".cz.cc")) {
                            continue;
                        }

                        String ip = getUrlIP(proxyUrl);

                        if (ip == null || processed.contains(ip)) {
                            System.err.println(proxyUrl);
                            continue;
                        }

                        processed.add(ip);

                        ProxyEntity proxyEntity = ProxyDao.getProxy(ip.substring(0, ip.lastIndexOf(".")));

                        if (proxyEntity != null) {
                            System.err.println(proxyUrl);
                            continue;
                        }

                        String testUrl = proxyUrl + "/browse.php?u=http://www.google.com/finance/getprices?q=GOOG";
                        executorService.schedule(new FindProxiesInAllProxySitesCom(testUrl, proxyUrl, ip), 1000 + 100 * j++, TimeUnit.MILLISECONDS);
                    }
                }

                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();
    }

    @Override
    public void run() {
        try {
            processProxy(testUrl, proxyUrl, ip);
        } catch (IOException e) {
            System.err.println("! " + e.getMessage());
        }
    }

    public static void processProxy(String testUrl, String proxyUrl, String ip) throws IOException {

        BufferedReader proxyReader = null;

        try {
            String cookies = ProxyUtils.getCookies(testUrl);
            Date date = new Date();

            URLConnection conn = new URL(testUrl).openConnection();
            conn.setRequestProperty("Cookie", cookies);
            proxyReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            processProxyReader(proxyReader, proxyUrl, date, ip);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        } finally {
            if (proxyReader != null) {
                proxyReader.close();
            }
        }
    }

    private static void processProxyReader(BufferedReader reader, String proxyUrl, Date date, String ip) throws IOException {
        String s = reader.readLine();

        if (("EXCHANGE%3D" + "NASDAQ").equals(s)) {
            ProxyEntity proxyEntity = new ProxyEntity();
            proxyEntity.url = proxyUrl;
            proxyEntity.software = (String) proxyMeta[2];
            proxyEntity.ip = ip;
            proxyEntity.active = true;
            proxyEntity.responseTime = (int) (new Date().getTime() - date.getTime());

            PersistenceManager.merge(proxyEntity);

            System.out.println(proxyUrl);
        }
    }

    public static String getUrlIP(String url) {
        String ip = null;

        try {
            String line;
            Process p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "ping.exe " + url.substring(7).replaceAll("/.*", ""));
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

            while ((line = input.readLine()) != null) {

                if (line.matches(".*\\[[0-9\\.]*\\].*")) {
                    ip = line.replaceAll(".*\\[([0-9\\.]*)\\].*", "$1");
                    break;
                }
            }

            input.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return ip;
    }

}
