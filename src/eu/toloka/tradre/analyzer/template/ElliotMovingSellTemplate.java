package eu.toloka.tradre.analyzer.template;

import eu.toloka.tradre.analyzer.Alert;
import eu.toloka.tradre.analyzer.Bar;
import eu.toloka.tradre.analyzer.attribute.EAttribute;
import eu.toloka.tradre.persistence.entity.StockEntity;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class ElliotMovingSellTemplate implements ITemplate {

    private final StockEntity stock;
    private final EAttribute maximum;
    private final EAttribute minimum;

    private Bar start;
    private Bar bottom;

    private int status = 0;

    public ElliotMovingSellTemplate(StockEntity stock, EAttribute maximum, EAttribute minimum) {
        this.stock = stock;
        this.maximum = maximum;
        this.minimum = minimum;
    }

    public void process(Bar bar, Integer i, Boolean beep) {

//        if (status == 2 && bar.low() > bar.getEntity().moving55) {
//            Alert.alert(stock, "(EM55) sell", bottom.time(), bar.time(), false, false);
//        }
//
//        if (status == 2 && bar.low() > bar.getEntity().moving89) {
//            Alert.alert(stock, "(EM89) sell", bottom.time(), bar.time(), false, false);
//        }

//        if (status == 2 && bar.getEntity().moving144 != null && bar.low() > bar.getEntity().moving144) {
//            Alert.alert(stock, "(EM144) sell", bottom.time(), bar.time(), true, false);
//        }

        if (status == 2 && bar.getEntity().moving200 != null && bar.low() > bar.getEntity().moving200) {
            Alert.alert(stock, "(EM200) sell", bottom.time(), bar.time(), true, null);
        }

        if (status == 2 && bar.has(minimum) && bottom.low() > bar.low()) {
             bottom = bar;
         }

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
