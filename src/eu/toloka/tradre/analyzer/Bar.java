package eu.toloka.tradre.analyzer;

import eu.toloka.tradre.analyzer.attribute.EAttribute;
import eu.toloka.tradre.persistence.entity.BarEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class Bar{

//    private static SimpleDateFormat f = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat ff = new SimpleDateFormat("dd HH:mm");
    private final BarEntity barEntity;
    private final List<EAttribute> attributeList = new ArrayList<EAttribute>();

    public Boolean red(){
        return barEntity.open >= barEntity.close;
    }

    public Boolean green(){
        return barEntity.open <= barEntity.close;
    }

    public Float open(){
        return barEntity.open;
    }

    public Float high(){
        return barEntity.high;
    }

    public Float low(){
        return barEntity.low;
    }

    public Float close(){
        return barEntity.close;
    }

    public Float openClose(){
        return barEntity.open - barEntity.close;
    }

    public Float closeOpen(){
        return barEntity.close - barEntity.open;
    }

    public Float m21(){
        return barEntity.moving21;
    }

    public Float m55(){
        return barEntity.moving55;
    }

    public Float m89(){
        return barEntity.moving89;
    }

    public Float m144(){
        return barEntity.moving144;
    }


    public Float m200(){
        return barEntity.moving200;
    }

    public Long time(){
        return barEntity.barPk.time;
    }

    public Integer minutes(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(barEntity.barPk.time);
        return calendar.get(Calendar.MINUTE);
    }

    @SuppressWarnings({"deprecation"})
    public Integer hours(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(barEntity.barPk.time);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public Bar(BarEntity barEntity) {
        this.barEntity = barEntity;
    }

    public Boolean has(EAttribute attr){
        return attributeList.contains(attr);
    }

    public Float moving(Integer moving){
        return barEntity.getMoving(moving);
    }

    public List<EAttribute> getAttributeList(){
        return attributeList;
    }

    public BarEntity getEntity() {
        return barEntity;
    }

    @Override
    public String toString() {
        return String.valueOf(new Date(barEntity.barPk.time) +
                " L: " + low() + " H: " + high() + " O: " + open() + " C: " + close());
    }
}
