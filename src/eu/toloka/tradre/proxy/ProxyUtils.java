package eu.toloka.tradre.proxy;

import eu.toloka.tradre.persistence.PersistenceManager;
import eu.toloka.tradre.persistence.dao.ProxyDao;
import eu.toloka.tradre.persistence.entity.ProxyEntity;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */

public class ProxyUtils {
    private static final List<ProxyEntity> list;
    private static final List<ProxyEntity> cookiesSetList = new ArrayList<ProxyEntity>();
    private static int proxyCounter = 0;

    static {
        PersistenceManager.update("UPDATE ProxyEntity proxy SET proxy.counter = 0, proxy.responseTime = 0");
        list = ProxyDao.getProxyList();
        Collections.shuffle(list);
    }

    public static ProxyEntity getNextProxyEntity() {
        if(proxyCounter > list.size() - 1){
            proxyCounter = 0;
        }

        ProxyEntity proxyEntity = list.get(proxyCounter++);


        if(!proxyEntity.active ||
                (!cookiesSetList.contains(proxyEntity) && setCookies(proxyEntity) == null)){
            return getNextProxyEntity();
        }

        return proxyEntity;
    }

    private static ProxyEntity setCookies(ProxyEntity proxyEntity) {
        try {
            proxyEntity.cookies = getCookies(proxyEntity.url);
            cookiesSetList.add(proxyEntity);

            return proxyEntity;

        } catch (Exception ex) {
            System.err.println(proxyEntity.url + " " + ex.getMessage());
            error(proxyEntity);
            return null;
        }
    }

    public static String getCookies(String proxyString) throws Exception{
        URL url = new URL(proxyString);
        URLConnection conn = url.openConnection();
        conn.connect();

        Map<String, List<String>> headers = conn.getHeaderFields();
        List<String> values = headers.get("Set-Cookie");

        if(values == null){
            return null;
        }

        String cookieValue = null;

        for (String v : values) {
            cookieValue = cookieValue == null ? v : cookieValue + ";" + v;
        }

        return cookieValue;
    }

    public static void error(ProxyEntity proxy) {
        proxy.error = proxy.error == null ? 1 : proxy.error + 1;
        proxy.cookies = null;
        
        PersistenceManager.merge(proxy);
    }

  public static void noData(ProxyEntity proxy) {
      proxy.noData = proxy.noData == null ? 1 : proxy.noData + 1;
      proxy.cookies = null;

      PersistenceManager.merge(proxy);
  }
}
