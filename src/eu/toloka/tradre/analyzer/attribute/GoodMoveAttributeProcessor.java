package eu.toloka.tradre.analyzer.attribute;

import eu.toloka.tradre.analyzer.Bar;

import java.util.Stack;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class GoodMoveAttributeProcessor implements IAttributeProcessor{
    private final float movement;
    private final float movementInPercents;
    private final int stackSize;

    private Stack<Bar> historyStack = new Stack<>();

    public GoodMoveAttributeProcessor(float movement, float movementInPercents,  int stackSize) {
        this.movement = movement;
        this.movementInPercents = movementInPercents;
        this.stackSize = stackSize;
    }

    @Override
    public void process(Bar bar) {
        historyStack.push(bar);

        if(historyStack.size() > stackSize){
            historyStack.removeElementAt(0);
        }

        Bar firstBar = historyStack.get(0);

        float price = firstBar.open();

        float low = firstBar.low();
        float high = firstBar.high();

        for(Bar b: historyStack){
            low = Math.min(b.low(), low);
            high = Math.max(b.high(), high);
        }

        if((high - low)*100/price > movementInPercents || high - low > movement){
            if(bar.close() >= price && bar.green()){
                bar.getAttributeList().add(EAttribute.GOOD_MOVE_UP);
                historyStack.get(stackSize - 2).getAttributeList().remove(EAttribute.GOOD_MOVE_UP);
            }

            if(bar.close() < price && bar.red()){
                bar.getAttributeList().add(EAttribute.GOOD_MOVE_DOWN);
                historyStack.get(stackSize - 2).getAttributeList().remove(EAttribute.GOOD_MOVE_DOWN);
            }
        }
    }



}
