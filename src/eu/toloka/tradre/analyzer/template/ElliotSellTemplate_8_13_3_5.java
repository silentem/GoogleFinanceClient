package eu.toloka.tradre.analyzer.template;

import eu.toloka.tradre.analyzer.attribute.EAttribute;
import eu.toloka.tradre.persistence.entity.StockEntity;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */

public class ElliotSellTemplate_8_13_3_5 extends ElliotSellTemplate{
    private static final EAttribute maximum = EAttribute.getExtremumAttribute(8, 13);
    private static final EAttribute minimum = EAttribute.getExtremumAttribute(13, 8);
    private static final EAttribute max = EAttribute.getExtremumAttribute(3, 5);
    private static final EAttribute min = EAttribute.getExtremumAttribute(5, 3);

    public ElliotSellTemplate_8_13_3_5(StockEntity stock) {
        super(stock, maximum, minimum, max, min);
    }
}
