package eu.toloka.tradre.analyzer.attribute;

import eu.toloka.tradre.analyzer.Bar;
import eu.toloka.tradre.analyzer.BarList;

import java.util.Stack;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class ConsolidationAttributeProcessor implements IAttributeProcessor{
    private boolean hasUpAttribute = false;
    private boolean hasDownAttribute = false;
    private BarList movement = null;
    private int bars;

    private BarList historyStack = new BarList(0);


    public ConsolidationAttributeProcessor(int bars) {
        this.bars = bars;
    }

    @Override
    public void process(Bar bar) {

        if(bar.getAttributeList().contains(EAttribute.GOOD_MOVE_UP)){
            movement = historyStack.lastBars(bars);
            historyStack = new BarList(0);
            hasUpAttribute = true;
            hasDownAttribute = false;
        }

        if(bar.getAttributeList().contains(EAttribute.GOOD_MOVE_DOWN)){
            movement = historyStack.lastBars(bars);
            historyStack = new BarList(0);
            hasUpAttribute = false;
            hasDownAttribute = true;
        }

        if(historyStack.size() > 0){


            if(hasUpAttribute && historyStack.getHighLowAbs() * 4 < movement.getHighLowAbs() &&
                    Math.abs(historyStack.getHigh() - movement.getHigh()) < 0.1){
                bar.getAttributeList().add(EAttribute.ROLLBACK_UP);
                hasUpAttribute = false;
            }

            if(hasDownAttribute && historyStack.getHighLowAbs() * 4 < movement.getHighLowAbs() &&
                    Math.abs(historyStack.getLow() - movement.getLow()) < 0.1){
                bar.getAttributeList().add(EAttribute.ROLLBACK_DOWN);
                hasDownAttribute = false;
            }
        }


        if(historyStack.size() > 20){
            hasUpAttribute = false;
            hasDownAttribute = false;
        }

        historyStack.add(bar);
    }

}
