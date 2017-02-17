package eu.toloka.tradre.persistence.entity;

import javax.persistence.*;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */

@Entity
@Table(name = "MOVING")
@SequenceGenerator(name = "movingSeq", sequenceName = "movingSeq")
@SuppressWarnings({"UnusedDeclaration"})
public class MovingEntity extends BasicEntity{
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "movingSeq")
    public Integer id;

    @Column
    public Integer chartInter;

    @Column
    public Integer inter;

    @Column
    public Boolean m8;

    @Column
    public Boolean m13;

    @Column
    public Boolean m21;

    @Column
    public Boolean m34;

    @Column
    public Boolean m55;

    @Column
    public Boolean m89;

    @Column
    public Boolean m144;

    @Column
    public Boolean m200;
}