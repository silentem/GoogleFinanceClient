package eu.toloka.tradre.analyzer.template;

import eu.toloka.tradre.analyzer.Bar;
import eu.toloka.tradre.analyzer.BarList;
import eu.toloka.tradre.time.TimingUtils;

import java.util.Date;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public interface ITemplate {
    public void process(Bar bar, Integer i, Boolean beep);    
}
