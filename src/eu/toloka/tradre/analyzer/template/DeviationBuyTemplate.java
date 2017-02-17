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
public class DeviationBuyTemplate implements ITemplate {

    private final StockEntity stock;
    private final EAttribute max;
    private final EAttribute min;
    private final List<Bar> localMinList = new ArrayList<Bar>();

    private Bar localMax = null;

    private Boolean lookForEnter = false;

    public DeviationBuyTemplate(StockEntity stock, EAttribute max, EAttribute min) {
        this.stock = stock;
        this.max = max;
        this.min = min;
    }

    public void process(Bar bar, Integer i, Boolean beep) {

        if(localMinList.size() >= 2){
            if (bar.time() < Alert.dayBegin.getTime()) {
                return;
            }

            Bar locMin1 = localMinList.get(localMinList.size() - 2);
            Bar locMin2 = localMinList.get(localMinList.size() - 1);

            if (localMax != null && bar.high() - 0.03 > localMax.high() &&
                    locMin1.time() < localMax.time() &&
                    locMin2.time() > localMax.time() &&
                    locMin1.low() - 0.02 < locMin2.low()) {
                 lookForEnter = true;
            }
        }

        if (bar.has(min)) {
            localMinList.add(bar);

            if(lookForEnter){
                Alert.alert(stock, "(D) buy", localMax.time(), bar.time(), false, null);
            }
        }

        if (bar.has(max)) {
            localMax = bar;
        }
    }
}
