package eu.toloka.tradre.analyzer.attribute;

import eu.toloka.tradre.analyzer.Bar;
import eu.toloka.tradre.persistence.entity.BarEntity;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class MovingIntersectAttributeProcessor implements IAttributeProcessor{
    private final Integer moving1;
    private final Integer moving2;

    public MovingIntersectAttributeProcessor(Integer moving1, Integer moving2) {
        this.moving1 = moving1;
        this.moving2 = moving2;
    }

    @Override
    public void process(Bar bar) {
        Float moving1Value = getMovingValue(bar, moving1);
        Float moving2Value = getMovingValue(bar, moving2);

        if(moving1Value == null || moving2Value == null){
            return;
        }

        if(moving1Value > moving2Value){
            bar.getAttributeList().remove(EAttribute.getMovingAttribute(moving2, moving1));
            bar.getAttributeList().add(EAttribute.getMovingAttribute(moving1, moving2));
        }

        if(moving1Value < moving2Value){
            bar.getAttributeList().remove(EAttribute.getMovingAttribute(moving1, moving2));
            bar.getAttributeList().add(EAttribute.getMovingAttribute(moving2, moving1));
        }
    }

    private Float getMovingValue(Bar bar, Integer moving){
        switch (moving){
            case 3: return bar.getEntity().getValue(BarEntity.ValueType.MOV3);
            case 5: return bar.getEntity().getValue(BarEntity.ValueType.MOV5);
            case 8: return bar.getEntity().getValue(BarEntity.ValueType.MOV8);
            case 13: return bar.getEntity().getValue(BarEntity.ValueType.MOV13);
            case 21: return bar.getEntity().getValue(BarEntity.ValueType.MOV21);
            case 34: return bar.getEntity().getValue(BarEntity.ValueType.MOV34);
            case 144: return bar.getEntity().getValue(BarEntity.ValueType.MOV144);
            case 200: return bar.getEntity().getValue(BarEntity.ValueType.MOV200);
        }

        return null;
    }

}
