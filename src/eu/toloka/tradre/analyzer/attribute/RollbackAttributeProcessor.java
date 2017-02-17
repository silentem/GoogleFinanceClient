package eu.toloka.tradre.analyzer.attribute;

import eu.toloka.tradre.analyzer.Bar;

import java.util.Stack;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class RollbackAttributeProcessor implements IAttributeProcessor{
    private boolean hasUpAttribute = false;
    private boolean hasDownAttribute = false;

    private Stack<Bar> historyStack = new Stack<>();

    public RollbackAttributeProcessor() {
    }

    @Override
    public void process(Bar bar) {

        if(bar.getAttributeList().contains(EAttribute.GOOD_MOVE_UP)){
            historyStack.empty();
            hasUpAttribute = true;
            hasDownAttribute = false;
        }

        if(bar.getAttributeList().contains(EAttribute.GOOD_MOVE_DOWN)){
            historyStack.empty();
            hasUpAttribute = false;
            hasDownAttribute = true;
        }

        if(historyStack.size() > 2){
            Bar prev1 = historyStack.get(historyStack.size() - 1);
            Bar prev2 = historyStack.get(historyStack.size() - 2);

            if(hasUpAttribute && historyStack.get(1).red() &&
                    prev1.green() && prev2.green() //&& prev1.low() - prev2.low() > 0.1
                    ){
                hasUpAttribute = false;
                historyStack.empty();
                bar.getAttributeList().add(EAttribute.ROLLBACK_UP);
            }

            if(hasDownAttribute && historyStack.get(1).green() &&
                    prev1.red() && prev2.red() //&& prev2.high() - prev1.high() > 0.1
                    ){
                hasDownAttribute = false;
                historyStack.empty();
                bar.getAttributeList().add(EAttribute.ROLLBACK_DOWN);
            }
        }


        if(hasUpAttribute || hasDownAttribute){
            historyStack.push(bar);
        }
    }



}
