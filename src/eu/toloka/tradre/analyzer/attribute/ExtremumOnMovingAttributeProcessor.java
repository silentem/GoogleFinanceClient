package eu.toloka.tradre.analyzer.attribute;

import eu.toloka.tradre.analyzer.Bar;

import java.util.ArrayList;
import java.util.List;

import static eu.toloka.tradre.analyzer.attribute.EAttribute.EAttributeType.*;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class ExtremumOnMovingAttributeProcessor implements IAttributeProcessor {
    private final EAttribute movingAttribute;
    private final EAttribute extremumAttribute;

    private List<Bar> list = null;

    public ExtremumOnMovingAttributeProcessor(Integer m1, Integer m2) {
        this.movingAttribute = EAttribute.getMovingAttribute(m1, m2);
        this.extremumAttribute = EAttribute.getExtremumAttribute(m1, m2);
    }

    @Override
    public void process(Bar bar) {

        if (bar.getAttributeList().contains(this.movingAttribute)) {
            if (list == null) {
                list = new ArrayList<Bar>();
            }

            list.add(bar);

            Bar extremum = null;

            for (Bar b : list) {
                b.getAttributeList().remove(extremumAttribute);

                if (MAXIMUM.equals(extremumAttribute.getType())) {
                    extremum = extremum == null ? b : (extremum.getEntity().high < b.getEntity().high ? b : extremum);
                }

                if (MINIMUM.equals(extremumAttribute.getType())) {
                    extremum = extremum == null ? b : (extremum.getEntity().low > b.getEntity().low ? b : extremum);
                }
            }

            if (extremum != null) {
                extremum.getAttributeList().add(extremumAttribute);
            }
            
        } else {
            list = null;
        }
    }
}
