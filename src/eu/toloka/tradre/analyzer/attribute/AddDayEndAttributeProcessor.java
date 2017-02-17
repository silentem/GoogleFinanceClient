package eu.toloka.tradre.analyzer.attribute;

import eu.toloka.tradre.analyzer.Bar;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class AddDayEndAttributeProcessor implements IAttributeProcessor{
    private Bar previousBar = null;

    @Override
    public void process(Bar bar) {
        if(previousBar == null){
            previousBar = bar;
            return;
        }

        if(bar.getEntity().date > previousBar.getEntity().date){
            previousBar.getAttributeList().add(EAttribute.DAY_END);
        }

         previousBar = bar;
    }
}
