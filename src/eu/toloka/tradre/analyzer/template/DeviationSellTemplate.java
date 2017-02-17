package eu.toloka.tradre.analyzer.template;

import eu.toloka.tradre.analyzer.Alert;
import eu.toloka.tradre.analyzer.Bar;
import eu.toloka.tradre.analyzer.attribute.EAttribute;
import eu.toloka.tradre.persistence.entity.StockEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class DeviationSellTemplate implements ITemplate {

    private final StockEntity stock;
    private final EAttribute max;
    private final EAttribute min;
    private final List<Bar> localMaxList = new ArrayList<Bar>();

    private Bar localMin = null;

    private Boolean lookForEnter = false;

    public DeviationSellTemplate(StockEntity stock, EAttribute max, EAttribute min) {
        this.stock = stock;
        this.max = max;
        this.min = min;
    }

    public void process(Bar bar, Integer i, Boolean beep) {

        if(localMaxList.size() >= 2){
            if (bar.time() < Alert.dayBegin.getTime()) {
                return;
            }

            Bar locMax1 = localMaxList.get(localMaxList.size() - 2);
            Bar locMax2 = localMaxList.get(localMaxList.size() - 1);

            if (localMin != null && bar.low() + 0.03 < localMin.low() &&
                    locMax1.time() < localMin.time() &&
                    locMax2.time() > localMin.time() &&
                    locMax1.low() + 0.02 > locMax2.low()) {
                 lookForEnter = true;
            }
        }

        if (bar.has(max)) {
            localMaxList.add(bar);

            if(lookForEnter){
                Alert.alert(stock, "(D) sell", localMin.time(), bar.time(), false, null);
            }
        }

        if (bar.has(min)) {
            localMin = bar;
        }

    }
}
