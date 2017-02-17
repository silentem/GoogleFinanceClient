package eu.toloka.tradre.analyzer.attribute;

import eu.toloka.tradre.analyzer.Bar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static eu.toloka.tradre.analyzer.attribute.EAttribute.EAttributeType.*;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class RemoveSmallMovingRangesAttributeProcessor implements IAttributeProcessor{
    private final Map<EAttribute, List<Bar>> map = new HashMap<EAttribute, List<Bar>>();

    @Override
    public void process(Bar bar) {

        for(EAttribute attribute: EAttribute.values()){
            if(MOVING.equals(attribute.getType())){
                if(map.containsKey(attribute) && !bar.getAttributeList().contains(attribute)){
                    List<Bar> barList = map.get(attribute);

                    if(barList.size() <= 3){
                        EAttribute counterAttribute = attribute.getMovingCounterAttribute();

                        for(Bar b: barList){
                            b.getAttributeList().remove(attribute);
                            b.getAttributeList().add(counterAttribute);
                        }

                        List<Bar> counterBarList = map.get(counterAttribute);

                        if(counterBarList != null){
                            barList.addAll(counterBarList);
                        }

                        map.put(counterAttribute, barList);
                    }

                    map.remove(attribute);
                }

                if(map.containsKey(attribute) && bar.getAttributeList().contains(attribute)){
                    map.get(attribute).add(bar);
                }

                if(!map.containsKey(attribute) && bar.getAttributeList().contains(attribute)){
                    List<Bar> barList = new ArrayList<Bar>();
                    barList.add(bar);

                    map.put(attribute, barList);
                }
            }
        }
    }
}
