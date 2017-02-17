package eu.toloka.tradre.analyzer.template;

import eu.toloka.tradre.analyzer.Alert;
import eu.toloka.tradre.analyzer.Bar;
import eu.toloka.tradre.persistence.entity.StockEntity;

import java.util.Set;


/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class AssBuyTemplate implements ITemplate {

    private final StockEntity stock;
    private final Set<String> symbolSet;

    private Bar start;
    private Bar leftAss;

    private int status = 0;

    public AssBuyTemplate(StockEntity stock, Set<String> symbolSet) {
        this.stock = stock;
        this.symbolSet = symbolSet;
    }

    public void process(Bar bar, Integer i, Boolean beep) {
        if (status == 3 && bar.close() > bar.m21()) {
            Alert.alert(stock, "(A) buy", leftAss.time(), bar.time(), false, null);
            symbolSet.add(stock.symbol);
            reset();
        }

        if (status == 2 && bar.m21() - bar.low() > 0.05) {
            status = 3;
        }

        if (status == 1 && bar.low() < bar.m21() && bar.green() && start.high() - bar.low() > 0.1) {
            status = 2;
            leftAss = bar;
        }

        if (status == 1 && bar.low() > bar.m21() && bar.green()) {
            reset();
        }

        if (status == 0 && bar.red()) {
            status = 1;
            start = bar;
        }
    }

    private void reset(){
        status = 0;
        start = null;
        leftAss = null;
    }
}
