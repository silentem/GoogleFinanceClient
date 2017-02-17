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
public class ElliotBuyTemplate implements ITemplate {

    private final StockEntity stock;
    private final EAttribute maximum;
    private final EAttribute minimum;
    private final EAttribute max;
    private final EAttribute min;
    private final List<Bar> localMinList = new ArrayList<Bar>();

    private Bar start;
    private Bar top;

    private Bar localMax = null;

    private Boolean watchOut = false;
    private Boolean lookForEnter = false;

    private int status = 0;

    public ElliotBuyTemplate(StockEntity stock, EAttribute maximum, EAttribute minimum, EAttribute max, EAttribute min) {
        this.stock = stock;
        this.maximum = maximum;
        this.minimum = minimum;
        this.max = max;
        this.min = min;
    }

    public void process(Bar bar, Integer i, Boolean beep) {
        if (status == 3 && bar.low() < localMinList.get(localMinList.size() - 2).low()) {
            Alert.alert(stock, "(E1) sell", localMax.time(), bar.time(), false, null);
        }

        if (status == 3 && bar.close() - 0.05 > localMinList.get(localMinList.size() - 1).low()) {
            Alert.alert(stock, "(E) buy", localMax.time(), bar.time(), false, null);
        }
        
        if (watchOut && bar.has(min)) {
            localMinList.add(bar);

            if(lookForEnter){
                status++;
            }
        }

        if(localMinList.size() >= 2){
            Bar locMin1 = localMinList.get(localMinList.size() - 2);
            Bar locMin2 = localMinList.get(localMinList.size() - 1);

            if (watchOut && localMax != null && bar.high() - 0.03 > localMax.high() &&
                    locMin1.time() < localMax.time() &&
                    locMin2.time() > localMax.time() &&
                    locMin1.low() - 0.02 < locMin2.low()) {
                lookForEnter = true;
                Alert.alert(stock, "(EB) buy", localMax.time(), bar.time(), false, null);
            }
        }

        if (watchOut && bar.has(max)) {
            localMax = bar;
        }

        if (status == 2 && (top.high() - start.low()) * 0.62 < top.high() - bar.low()) {
            watchOut = true;
        }

        if (status == 2 && bar.has(maximum) && top.high() < bar.high()) {
            top = bar;
        }

        //bar.high() > pivot && 
        if (status == 1 && bar.has(maximum) && bar.high() - start.low() >= 0.50) {
            if (bar.time() < Alert.dayBegin.getTime()) {
                return;
            }

            status++;
            top = bar;
        }

        if (status == 0 && bar.has(minimum)) {
            start = bar;
            status++;
        }
    }
}
