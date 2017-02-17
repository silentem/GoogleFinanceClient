package eu.toloka.tradre.analyzer.template;

import eu.toloka.tradre.analyzer.Alert;
import eu.toloka.tradre.analyzer.BarList;
import eu.toloka.tradre.persistence.entity.StockEntity;

import java.util.Collections;
import java.util.Set;


/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class FalseBreakBuyTemplate implements IBulkTemplate {

    private final StockEntity stock;
    private final Set<String> symbolSet;

    public FalseBreakBuyTemplate(StockEntity stock, Set<String> symbolSet) {
        this.stock = stock;
        this.symbolSet = symbolSet;
    }

    public void process(BarList barList) {
        Collections.reverse(barList);

        if (barList.get(0).green() &&
                barList.get(0).close() > barList.get(0).m21() &&
                barList.get(0).low() < barList.get(0).m21() &&
                barList.get(3).red() && barList.get(4).red() && barList.get(4).open() - barList.get(3).close() > 0.1
//                &&
//                (
//                        (barList.get(3).red() && barList.get(4).red() && barList.get(4).open() - barList.get(3).close() > 0.1) ||
//                (barList.get(2).red() && barList.get(2).red() && barList.get(3).open() - barList.get(2).close() > 0.1)) &&
//                 barList.get(0).m55() > barList.get(4).m55()
                ) {

            Alert.alert(stock, "(A) buy", barList.get(0).time(), barList.get(0).time(), false, null);
            symbolSet.add(stock.symbol);
        }


    }
}
