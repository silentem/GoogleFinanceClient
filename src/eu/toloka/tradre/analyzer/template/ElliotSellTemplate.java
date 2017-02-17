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
public class ElliotSellTemplate implements ITemplate {

    private final StockEntity stock;
    private final EAttribute maximum;
    private final EAttribute minimum;
    private final EAttribute max;
    private final EAttribute min;
    private final List<Bar> localMaxList = new ArrayList<Bar>();

    private Bar start;
    private Bar bottom;

    private Bar localMin = null;

    private Boolean watchOut = false;
    private Boolean lookForEnter = false;

    private int status = 0;

    public ElliotSellTemplate(StockEntity stock, EAttribute maximum, EAttribute minimum, EAttribute max, EAttribute min) {
        this.stock = stock;
        this.maximum = maximum;
        this.minimum = minimum;
        this.max = max;
        this.min = min;
    }

    public void process(Bar bar, Integer i, Boolean beep) {
        if (status == 3 && bar.high() > localMaxList.get(localMaxList.size() - 2).high()) {
            Alert.alert(stock, "(E1) buy", localMin.time(), bar.time(), false, null);
        }

        if (status == 3 && bar.close() + 0.05 < localMaxList.get(localMaxList.size() - 1).high()) {
            Alert.alert(stock, "(E) sell", localMin.time(), bar.time(), false, null);
        }

        if (watchOut && bar.has(max)) {
            localMaxList.add(bar);

            if(lookForEnter){
                status++;
            }
        }

        if(localMaxList.size() >= 2){
            Bar locMax1 = localMaxList.get(localMaxList.size() - 2);
            Bar locMax2 = localMaxList.get(localMaxList.size() - 1);

            if (watchOut && localMin != null && bar.low() + 0.03 < localMin.low() &&
                    locMax1.time() < localMin.time() &&
                    locMax2.time() > localMin.time() &&
                    locMax1.low() + 0.02 > locMax2.low()) {
                lookForEnter = true;
                Alert.alert(stock, "(EB) sell", localMin.time(), bar.time(), false, null);
            }
        }

        if (watchOut && bar.has(min)) {
            localMin = bar;
        }

        if (status == 2 && (start.high() - bottom.low()) * 0.62 < bar.high() - bottom.low()) {
            watchOut = true;
        }

        if (status == 2 && bar.has(minimum) && bottom.low() > bar.low()) {
            bottom = bar;
        }

        //bar.low() < pivot && 
        if (status == 1 && bar.has(minimum) && start.high() - bar.low() >= 0.50) {
            if (bar.time() < Alert.dayBegin.getTime()) {
                return;
            }

            status++;
            bottom = bar;
        }

        if (status == 0 && bar.has(maximum)) {
            start = bar;
            status++;
        }
    }
}
