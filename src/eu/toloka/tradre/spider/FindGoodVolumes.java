package eu.toloka.tradre.spider;

import eu.toloka.tradre.persistence.dao.StockDao;
import eu.toloka.tradre.persistence.dao.VolumeDao;
import eu.toloka.tradre.persistence.entity.BarEntity;
import eu.toloka.tradre.persistence.entity.ProxyEntity;
import eu.toloka.tradre.persistence.entity.StockEntity;
import eu.toloka.tradre.persistence.entity.VolumeEntity;
import eu.toloka.tradre.proxy.ProxyUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class FindGoodVolumes extends Spider {
    public static Set<String> symbolSet = new HashSet<String>();

    @SuppressWarnings({"UnusedDeclaration"})
    public FindGoodVolumes(StockEntity stock) {
        super(stock, 900);
    }

    public static void main(final String[] args) {
        schedule(StockDao.StockType.WATCHED, 20, FindGoodVolumes.class);
    }

    @Override
    protected String getUrlString(StockEntity stock, int interval, ProxyEntity proxy) {
        return proxy.url + "/browse.php?u=http://www.google.com/finance/getprices?" +
                ("q=" + stock.symbol + "&x=" + stock.exchange + "&i=" + interval +
                        "&p=1d&f=d,c,v,o,h,l&df=cpct&auto=1&ts=" + new Date().getTime()).replaceAll("&", "%26");
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
//                int avgVolume = 0;
//                int volume = 0;
                float k = 4f;

                VolumeEntity volumeEntity = VolumeDao.getVolume(stock.symbol);

                for (BarEntity barEntity : barList) {
                    Date time = new Date(barEntity.barPk.time);

                    switch (time.getHours()) {
                        case 9:
                            switch (time.getMinutes()) {
                                case 45:
                                    if(volumeEntity.v0945 * k < barEntity.volume){
                                        System.out.println(stock.symbol + " " + time + " avg:" + volumeEntity.v0945 + " today:" + barEntity.volume + " ratio:" + ((float)barEntity.volume/(float)volumeEntity.v0945));
                                        symbolSet.add(stock.symbol);
                                    }
                            }

                            break;
//                        case 10:
//                            switch (time.getMinutes()) {
//                                case 0:
//                                    volume += barEntity.volume;
//                                    avgVolume += volumeEntity.v1000;
//
//                                    if(avgVolume * k < volume){
//                                        System.out.println(stock.symbol + " " + time + " avg:" + avgVolume + " today:" + volume + " ratio:" + ((float)volume/(float)avgVolume));
//                                    }
//                                    break;
//                                case 15:
//                                    volume += barEntity.volume;
//                                    avgVolume += volumeEntity.v1015;
//
//                                    if(avgVolume * k < volume){
//                                        System.out.println(stock.symbol + " " + time + " avg:" + avgVolume + " today:" + volume + " ratio:" + ((float)volume/(float)avgVolume));
//                                    }
//                                    break;
//                                case 30:
//                                    volume += barEntity.volume;
//                                    avgVolume += volumeEntity.v1030;
//
//                                    if(avgVolume * k < volume){
//                                        System.out.println(stock.symbol + " " + time + " avg:" + avgVolume + " today:" + volume + " ratio:" + ((float)volume/(float)avgVolume));
//                                    }
//                                    break;
//                                case 45:
//                                    volume += barEntity.volume;
//                                    avgVolume += volumeEntity.v1045;
//
//                                    if(avgVolume * k < volume){
//                                        System.out.println(stock.symbol + " " + time + " avg:" + avgVolume + " today:" + volume + " ratio:" + ((float)volume/(float)avgVolume));
//                                    }
//                            }
//
//                            break;
//                        case 11:
//                            switch (time.getMinutes()) {
//                                case 0:
//                                    volume += barEntity.volume;
//                                    avgVolume += volumeEntity.v1100;
//
//                                    if(avgVolume * k < volume){
//                                        System.out.println(stock.symbol + " " + time + " avg:" + avgVolume + " today:" + volume + " ratio:" + ((float)volume/(float)avgVolume));
//                                    }
//                                    break;
//                                case 15:
//                                    volume += barEntity.volume;
//                                    avgVolume += volumeEntity.v1115;
//
//                                    if(avgVolume * k < volume){
//                                        System.out.println(stock.symbol + " " + time + " avg:" + avgVolume + " today:" + volume + " ratio:" + ((float)volume/(float)avgVolume));
//                                    }
//                                    break;
//                                case 30:
//                                    volume += barEntity.volume;
//                                    avgVolume += volumeEntity.v1130;
//
//                                    if(avgVolume * k < volume){
//                                        System.out.println(stock.symbol + " " + time + " avg:" + avgVolume + " today:" + volume + " ratio:" + ((float)volume/(float)avgVolume));
//                                    }
//                                    break;
//                                case 45:
//                                    volume += barEntity.volume;
//                                    avgVolume += volumeEntity.v1145;
//
//                                    if(avgVolume * k < volume){
//                                        System.out.println(stock.symbol + " " + time + " avg:" + avgVolume + " today:" + volume + " ratio:" + ((float)volume/(float)avgVolume));
//                                    }
//                            }
//                            break;
//                        case 12:
//                            switch (time.getMinutes()) {
//                                case 0:
//                                    volume += barEntity.volume;
//                                    avgVolume += volumeEntity.v1200;
//
//                                    if(avgVolume * k < volume){
//                                        System.out.println(stock.symbol + " " + time + " avg:" + avgVolume + " today:" + volume + " ratio:" + ((float)volume/(float)avgVolume));
//                                    }
//                                    break;
//                                case 15:
//                                    volume += barEntity.volume;
//                                    avgVolume += volumeEntity.v1215;
//
//                                    if(avgVolume * k < volume){
//                                        System.out.println(stock.symbol + " " + time + " avg:" + avgVolume + " today:" + volume + " ratio:" + ((float)volume/(float)avgVolume));
//                                    }
//                                    break;
//                                case 30:
//                                    volume += barEntity.volume;
//                                    avgVolume += volumeEntity.v1230;
//
//                                    if(avgVolume * k < volume){
//                                        System.out.println(stock.symbol + " " + time + " avg:" + avgVolume + " today:" + volume + " ratio:" + ((float)volume/(float)avgVolume));
//                                    }
//                                    break;
//                                case 45:
//                                    volume += barEntity.volume;
//                                    avgVolume += volumeEntity.v1245;
//
//                                    if(avgVolume * k < volume){
//                                        System.out.println(stock.symbol + " " + time + " avg:" + avgVolume + " today:" + volume + " ratio:" + ((float)volume/(float)avgVolume));
//                                    }
//                            }
//                            break;
//                        case 13:
//                            switch (time.getMinutes()) {
//                                case 0:
//                                    volume += barEntity.volume;
//                                    avgVolume += volumeEntity.v1300;
//
//                                    if(avgVolume * k < volume){
//                                        System.out.println(stock.symbol + " " + time + " avg:" + avgVolume + " today:" + volume + " ratio:" + ((float)volume/(float)avgVolume));
//                                    }
//                                    break;
//                                case 15:
//                                    volume += barEntity.volume;
//                                    avgVolume += volumeEntity.v1315;
//
//                                    if(avgVolume * k < volume){
//                                        System.out.println(stock.symbol + " " + time + " avg:" + avgVolume + " today:" + volume + " ratio:" + ((float)volume/(float)avgVolume));
//                                    }
//                                    break;
//                                case 30:
//                                    volume += barEntity.volume;
//                                    avgVolume += volumeEntity.v1330;
//
//                                    if(avgVolume * k < volume){
//                                        System.out.println(stock.symbol + " " + time + " avg:" + avgVolume + " today:" + volume + " ratio:" + ((float)volume/(float)avgVolume));
//                                    }
//                                    break;
//                                case 45:
//                                    volume += barEntity.volume;
//                                    avgVolume += volumeEntity.v1345;
//
//                                    if(avgVolume * k < volume){
//                                        System.out.println(stock.symbol + " " + time + " avg:" + avgVolume + " today:" + volume + " ratio:" + ((float)volume/(float)avgVolume));
//                                    }
//                            }
//                            break;
//                        case 14:
//                            switch (time.getMinutes()) {
//                                case 0:
//                                    volume += barEntity.volume;
//                                    avgVolume += volumeEntity.v1400;
//
//                                    if(avgVolume * k < volume){
//                                        System.out.println(stock.symbol + " " + time + " avg:" + avgVolume + " today:" + volume + " ratio:" + ((float)volume/(float)avgVolume));
//                                    }
//                                    break;
//                                case 15:
//                                    volume += barEntity.volume;
//                                    avgVolume += volumeEntity.v1415;
//
//                                    if(avgVolume * k < volume){
//                                        System.out.println(stock.symbol + " " + time + " avg:" + avgVolume + " today:" + volume + " ratio:" + ((float)volume/(float)avgVolume));
//                                    }
//                                    break;
//                                case 30:
//                                    volume += barEntity.volume;
//                                    avgVolume += volumeEntity.v1430;
//
//                                    if(avgVolume * k < volume){
//                                        System.out.println(stock.symbol + " " + time + " avg:" + avgVolume + " today:" + volume + " ratio:" + ((float)volume/(float)avgVolume));
//                                    }
//                                    break;
//                                case 45:
//                                    volume += barEntity.volume;
//                                    avgVolume += volumeEntity.v1445;
//
//                                    if(avgVolume * k < volume){
//                                        System.out.println(stock.symbol + " " + time + " avg:" + avgVolume + " today:" + volume + " ratio:" + ((float)volume/(float)avgVolume));
//                                    }
//                            }
//                            break;
//                        case 15:
//                            switch (time.getMinutes()) {
//                                case 0:
//                                    volume += barEntity.volume;
//                                    avgVolume += volumeEntity.v1500;
//
//                                    if(avgVolume * k < volume){
//                                        System.out.println(stock.symbol + " " + time + " avg:" + avgVolume + " today:" + volume + " ratio:" + ((float)volume/(float)avgVolume));
//                                    }
//                                    break;
//                                case 15:
//                                    volume += barEntity.volume;
//                                    avgVolume += volumeEntity.v1515;
//
//                                    if(avgVolume * k < volume){
//                                        System.out.println(stock.symbol + " " + time + " avg:" + avgVolume + " today:" + volume + " ratio:" + ((float)volume/(float)avgVolume));
//                                    }
//                                    break;
//                                case 30:
//                                    volume += barEntity.volume;
//                                    avgVolume += volumeEntity.v1530;
//
//                                    if(avgVolume * k < volume){
//                                        System.out.println(stock.symbol + " " + time + " avg:" + avgVolume + " today:" + volume + " ratio:" + ((float)volume/(float)avgVolume));
//                                    }
//                                    break;
                                case 45:
                                    if(volumeEntity.v1545 * k < barEntity.volume){
                                        System.out.println(stock.symbol + " " + time + " avg:" + volumeEntity.v1545 + " today:" + barEntity.volume + " ratio:" + ((float)barEntity.volume/(float)volumeEntity.v1545));
                                    }
//                            }
//                            break;
//                        case 16:
//                            switch (time.getMinutes()) {
//                                case 0:
//                                    if(volumeEntity.v1600 * k < barEntity.volume){
//                                        System.out.println(stock.symbol + " " + time + " avg:" + volumeEntity.v1600 + " today:" + barEntity.volume + " ratio:" + ((float)barEntity.volume/(float)volumeEntity.v1600));
//                                    }
//                            }
                    }

                }

//                System.err.println(stock.symbol + " " + avgVolume + " " + volume);
            }
        } else {
            ProxyUtils.error(proxy);
            System.err.println(proxy.url + " " + stock.symbol + " Error wrong first line: " + line);
        }
    }
}
