package eu.toloka.tradre.analyzer.attribute;

import static eu.toloka.tradre.analyzer.attribute.EAttribute.EAttributeType.*;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */

@SuppressWarnings({"UnusedDeclaration"})
public enum EAttribute {
    M3overM5(3, 5), M5overM3(5, 3),
    M5overM8(5, 8), M8overM5(8, 5),
    M8overM13(8, 13), M13overM8(13, 8),
    M13overM21(13, 21), M21overM13(21, 13),
    M21overM34(21, 34), M34overM21(34, 21),

    MAXonM3overM5(3, 5, MAXIMUM), MINonM5overM3(5, 3, MINIMUM),
    MAXonM5overM8(5, 8, MAXIMUM), MINonM8overM5(8, 5, MINIMUM),
    MAXonM8overM13(8, 13, MAXIMUM), MINonM13overM8(13, 8, MINIMUM),
    MAXonM13overM21(13, 21, MAXIMUM), MINonM21overM13(21, 13, MINIMUM),
    MAXonM21overM34(21, 34, MAXIMUM), MINonM34overM21(34, 21, MINIMUM),

    DAY_END,
    GOOD_MOVE_UP,
    GOOD_MOVE_DOWN,
    ROLLBACK_UP,
    ROLLBACK_DOWN,
    ;

    private EAttributeType type = null;
    private Integer m1 = null;
    private Integer m2 = null;

    EAttribute(Integer m1, Integer m2) {
        this.type = MOVING;
        this.m1 = m1;
        this.m2 = m2;
    }

    EAttribute(Integer m1, Integer m2, EAttributeType type) {
        this.type = type;
        this.m1 = m1;
        this.m2 = m2;
    }

    EAttribute() {
    }

    public EAttributeType getType() {
        return type;
    }

    public Integer getUpperMoving() {
        return m1;
    }

    public Integer getLowerMoving() {
        return m2;
    }

    public static EAttribute getMovingAttribute(Integer m1, Integer m2) {
        for(EAttribute a: EAttribute.values()){
            if(MOVING.equals(a.getType()) &&
                a.getUpperMoving().equals(m1) && a.getLowerMoving().equals(m2)){

                return a;
            }
        }

        return null;
    }

    public static EAttribute getExtremumAttribute(Integer m1, Integer m2) {
        for(EAttribute a: EAttribute.values()){
            if((MAXIMUM.equals(a.getType()) || MINIMUM.equals(a.getType())) &&
                a.getUpperMoving().equals(m1) && a.getLowerMoving().equals(m2)){

                return a;
            }
        }

        return null;
    }

    public EAttribute getMovingCounterAttribute() {
        return getMovingAttribute(getLowerMoving(), getUpperMoving());
    }

    public enum EAttributeType {
        MOVING, MAXIMUM, MINIMUM
    }

}
