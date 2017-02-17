package eu.toloka.tradre.spider;

import eu.toloka.tradre.persistence.PersistenceManager;
import eu.toloka.tradre.persistence.dao.StockDao;
import eu.toloka.tradre.persistence.entity.BarEntity;
import eu.toloka.tradre.persistence.entity.ProxyEntity;
import eu.toloka.tradre.persistence.entity.StockEntity;
import eu.toloka.tradre.persistence.entity.VolumeEntity;
import eu.toloka.tradre.proxy.ProxyUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class UpdateVolume extends Spider {

    @SuppressWarnings({"UnusedDeclaration"})
    public UpdateVolume(StockEntity stock) {
        super(stock, 900);
    }

    public static void main(final String[] args) {
        schedule(StockDao.StockType.BETA_IS_0, 25, UpdateVolume.class);
    }

    @Override
    protected String getUrlString(StockEntity stock, int interval, ProxyEntity proxy) {
        return proxy.url + "/browse.php?u=http://www.google.com/finance/getprices?" +
                ("q=" + stock.symbol + "&x=" + stock.exchange + "&i=" + interval +
                        "&p=21d&f=d,c,v,o,h,l&df=cpct&auto=1&ts=" + new Date().getTime()).replaceAll("&", "%26");
    }

    @Override
    protected void processReader(StockEntity stock, int interval, BufferedReader reader, ProxyEntity proxy) throws IOException {
        String line = reader.readLine();

        if (line != null && line.contains("EXCHANGE")) {
            List<BarEntity> barList = new ArrayList<BarEntity>();
            BarEntity bar = null;

            while ((line = reader.readLine()) != null) {
                if (line.contains("sorry.google.com") || line.contains("503 Service Unavailable")) {
                    ProxyUtils.error(proxy);
                    break;
                }

                if ((bar = parseString(line, stock.symbol, interval)) == null)
                    continue;

                barList.add(bar);
            }

            if (bar == null) {
                System.err.println(proxy.url + " " + stock.symbol + ": No data received");
            } else {

//                PersistenceManager.merge(barList);

                int i = 0;

                VolumeEntity volumeEntity = new VolumeEntity();
                volumeEntity.symbol = stock.symbol;

                for (BarEntity barEntity : barList) {
                    Date time = new Date(barEntity.barPk.time);

                    switch (time.getHours()) {
                        case 9:
                            switch (time.getMinutes()) {
                                case 45:
                                    volumeEntity.v0945 += barEntity.volume;
                                    i++;
                            }

                            break;
                        case 10:
                            switch (time.getMinutes()) {
                                case 0:
                                    volumeEntity.v1000 += barEntity.volume;
                                    break;
                                case 15:
                                    volumeEntity.v1015 += barEntity.volume;
                                    break;
                                case 30:
                                    volumeEntity.v1030 += barEntity.volume;
                                    break;
                                case 45:
                                    volumeEntity.v1045 += barEntity.volume;
                            }

                            break;
                        case 11:
                            switch (time.getMinutes()) {
                                case 0:
                                    volumeEntity.v1100 += barEntity.volume;
                                    break;
                                case 15:
                                    volumeEntity.v1115 += barEntity.volume;
                                    break;
                                case 30:
                                    volumeEntity.v1130 += barEntity.volume;
                                    break;
                                case 45:
                                    volumeEntity.v1145 += barEntity.volume;
                            }
                            break;
                        case 12:
                            switch (time.getMinutes()) {
                                case 0:
                                    volumeEntity.v1200 += barEntity.volume;
                                    break;
                                case 15:
                                    volumeEntity.v1215 += barEntity.volume;
                                    break;
                                case 30:
                                    volumeEntity.v1230 += barEntity.volume;
                                    break;
                                case 45:
                                    volumeEntity.v1245 += barEntity.volume;
                            }
                            break;
                        case 13:
                            switch (time.getMinutes()) {
                                case 0:
                                    volumeEntity.v1300 += barEntity.volume;
                                    break;
                                case 15:
                                    volumeEntity.v1315 += barEntity.volume;
                                    break;
                                case 30:
                                    volumeEntity.v1330 += barEntity.volume;
                                    break;
                                case 45:
                                    volumeEntity.v1345 += barEntity.volume;
                            }
                            break;
                        case 14:
                            switch (time.getMinutes()) {
                                case 0:
                                    volumeEntity.v1400 += barEntity.volume;
                                    break;
                                case 15:
                                    volumeEntity.v1415 += barEntity.volume;
                                    break;
                                case 30:
                                    volumeEntity.v1430 += barEntity.volume;
                                    break;
                                case 45:
                                    volumeEntity.v1445 += barEntity.volume;
                            }
                            break;
                        case 15:
                            switch (time.getMinutes()) {
                                case 0:
                                    volumeEntity.v1500 += barEntity.volume;
                                    break;
                                case 15:
                                    volumeEntity.v1515 += barEntity.volume;
                                    break;
                                case 30:
                                    volumeEntity.v1530 += barEntity.volume;
                                    break;
                                case 45:
                                    volumeEntity.v1545 += barEntity.volume;
                            }
                            break;
                        case 16:
                            switch (time.getMinutes()) {
                                case 0:
                                    volumeEntity.v1600 += barEntity.volume;
                            }
                    }
                }

                volumeEntity.v0945 /= i;
                volumeEntity.v1000 /= i;
                volumeEntity.v1015 /= i;
                volumeEntity.v1030 /= i;
                volumeEntity.v1045 /= i;
                volumeEntity.v1100 /= i;
                volumeEntity.v1115 /= i;
                volumeEntity.v1130 /= i;
                volumeEntity.v1145 /= i;
                volumeEntity.v1200 /= i;
                volumeEntity.v1215 /= i;
                volumeEntity.v1230 /= i;
                volumeEntity.v1245 /= i;
                volumeEntity.v1300 /= i;
                volumeEntity.v1315 /= i;
                volumeEntity.v1330 /= i;
                volumeEntity.v1345 /= i;
                volumeEntity.v1400 /= i;
                volumeEntity.v1415 /= i;
                volumeEntity.v1430 /= i;
                volumeEntity.v1445 /= i;
                volumeEntity.v1500 /= i;
                volumeEntity.v1515 /= i;
                volumeEntity.v1530 /= i;
                volumeEntity.v1545 /= i;
                volumeEntity.v1600 /= i;

                PersistenceManager.merge(volumeEntity);

                stock.barsNumber = barList.size();
                stock.beta = 1f;
                System.out.println("Url: " + proxy.url + " " + stock.symbol + " " + new Date(bar.barPk.time));
            }

            PersistenceManager.merge(stock);
        } else {
            ProxyUtils.error(proxy);
            System.err.println(proxy.url + " " + stock.symbol + " Error wrong first line: " + line);
        }
    }
}
