package eu.toloka.tradre.analyzer;

import eu.toloka.tradre.spider.GoogleFinance;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class BarCluster {
    private final BarList m1List = new BarList(800);
    private BarList m2List = new BarList(400);
    private final BarList m5List = new BarList(800);
    private BarList m10List = new BarList(400);
    private final BarList m15List = new BarList(300);
    private final BarList m30List = new BarList(400);
    private BarList h1List = new BarList(200);
    private final BarList d1List = new BarList(200);

    public boolean m2dirty = true;
    public boolean m10dirty = true;
    public boolean h1dirty = true;

    public BarList getBarList(Integer interval) {
        switch (interval) {
            case 60:
                return m1List;
            case 120:
                if (m2dirty) {
                    m2List = GoogleFinance.mergeBarList(120, m1List);
                    m2dirty = false;
                }

                return m2List;
            case 300:
                return m5List;
            case 600:
                if (m10dirty) {
                    m10List = GoogleFinance.mergeBarList(600, m5List);
                    m10dirty = false;
                }

                return m10List;
            case 900:
                return m15List;
            case 1800:
                return m30List;
            case 3600:
                if (h1dirty) {
                    h1List = GoogleFinance.mergeBarList(3600, m30List);
                    h1dirty = false;
                }

                return h1List;
            case 3600 * 24:
                return d1List;
        }

        return null;
    }
}
