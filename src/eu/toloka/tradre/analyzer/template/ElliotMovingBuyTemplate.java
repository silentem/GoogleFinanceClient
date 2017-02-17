package eu.toloka.tradre.analyzer.template;

import eu.toloka.tradre.analyzer.Alert;
import eu.toloka.tradre.analyzer.Bar;
import eu.toloka.tradre.analyzer.attribute.EAttribute;
import eu.toloka.tradre.persistence.entity.StockEntity;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class ElliotMovingBuyTemplate implements ITemplate {

    private final StockEntity stock;
    private final EAttribute maximum;
    private final EAttribute minimum;

    private Bar start;
    private Bar top;

    private int status = 0;

    public ElliotMovingBuyTemplate(StockEntity stock, EAttribute maximum, EAttribute minimum) {
        this.stock = stock;
        this.maximum = maximum;
        this.minimum = minimum;
    }

    public void process(Bar bar, Integer i, Boolean beep) {

//        if (status == 2 && bar.low() < bar.getEntity().moving55) {
//            Alert.alert(stock, "(EM55) buy", top.time(), bar.time(), false, false);
//        }
//
//        if (status == 2 && bar.low() < bar.getEntity().moving89) {
//            Alert.alert(stock, "(EM89) buy", top.time(), bar.time(), false, false);
//        }

//        if (status == 2 && bar.getEntity().moving144 != null && bar.low() < bar.getEntity().moving144) {
//            Alert.alert(stock, "(EM144) buy", top.time(), bar.time(), false, false);
//        }

        if (status == 2 && bar.getEntity().moving200 != null && bar.low() < bar.getEntity().moving200) {
            Alert.alert(stock, "(EM200) buy", top.time(), bar.time(), true, null);
        }

        if (status == 2 && bar.has(maximum) && top.high() < bar.high()) {
            top = bar;
        }

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
