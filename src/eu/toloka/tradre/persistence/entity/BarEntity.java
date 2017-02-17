package eu.toloka.tradre.persistence.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import java.util.Date;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */

@Entity
@Table(name = "BAR")
@SuppressWarnings({"UnusedDeclaration"})
public class BarEntity extends BasicEntity{

    @EmbeddedId
    public BarPk barPk;

    @Column
    public Long date;

    @Column(name = "INTERVAHL")
    public Integer interval;

    @Column
    public Float high;

    @Column
    public Float low;

    @Column
    public Float open;

    @Column
    public Float close;

    @Column
    public Long volume;

    @Column
    public Float moving3;

    @Column
    public Float moving5;

    @Column
    public Float moving8;

    @Column
    public Float moving13;

    @Column
    public Float moving21;

    @Column
    public Float moving34;

    @Column
    public Float moving55;

    @Column
    public Float moving89;

    @Column
    public Float moving144;

    @Column
    public Float moving200;

    @Column
    public Float deviation13;

    @Column
    public Float deviation21;

    @Column
    public Float rsi13;

    @Column
    public Float rsi21;

    @Column
    public Float rsi34;

    public String toString() {
        return new Date(barPk.time).toString();
    }

    public Float getValue(ValueType value) {
        switch (value) {
            case HIGH:
                return high;
            case LOW:
                return low;
            case OPEN:
                return open;
            case CLOSE:
                return close;
            case VOLUME:
                return Float.valueOf(volume);
            case MOV3:
                return moving3;
            case MOV5:
                return moving5;
            case MOV8:
                return moving8;
            case MOV13:
                return moving13;
            case MOV21:
                return moving21;
            case MOV34:
                return moving34;
            case MOV55:
                return moving55;
            case MOV89:
                return moving89;
            case MOV144:
                return moving144;
            case MOV200:
                return moving200;
            case DEV13:
                return deviation13;
            case DEV21:
                return deviation21;
        }

        return null;
    }

    public Float getMoving(Integer moving) {
        switch (moving) {
            case 3:
                return moving3;
            case 5:
                return moving5;
            case 8:
                return moving8;
            case 13:
                return moving13;
            case 21:
                return moving21;
            case 34:
                return moving34;
            case 55:
                return moving55;
            case 89:
                return moving89;
            case 144:
                return moving144;
            case 200:
                return moving200;
        }

        return null;

    }

    public static enum ValueType {
        HIGH, LOW, OPEN, CLOSE, VOLUME,
        MOV3, MOV5, MOV8, MOV13, MOV21, MOV34, MOV55, MOV89, MOV144, MOV200,
        DEV13, DEV21
    }
}
