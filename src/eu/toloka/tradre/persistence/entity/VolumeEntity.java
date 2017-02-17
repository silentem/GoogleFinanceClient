package eu.toloka.tradre.persistence.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
@Entity
@Table(name = "VOLUME")
@SequenceGenerator(name = "volumeSeq", sequenceName = "volumeSeq")
@SuppressWarnings({"UnusedDeclaration"})
public class VolumeEntity extends BasicEntity{
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "volumeSeq")
    public Integer id;

    @Column
    public String symbol;

    @Column
    public Long v0945 = 0l;

    @Column
    public Long v1000 = 0l;

    @Column
    public Long v1015 = 0l;

    @Column
    public Long v1030 = 0l;

    @Column
    public Long v1045 = 0l;

    @Column
    public Long v1100 = 0l;

    @Column
    public Long v1115 = 0l;

    @Column
    public Long v1130 = 0l;

    @Column
    public Long v1145 = 0l;

    @Column
    public Long v1200 = 0l;

    @Column
    public Long v1215 = 0l;

    @Column
    public Long v1230 = 0l;

    @Column
    public Long v1245 = 0l;

    @Column
    public Long v1300 = 0l;

    @Column
    public Long v1315 = 0l;

    @Column
    public Long v1330 = 0l;

    @Column
    public Long v1345 = 0l;

    @Column
    public Long v1400 = 0l;

    @Column
    public Long v1415 = 0l;

    @Column
    public Long v1430 = 0l;

    @Column
    public Long v1445 = 0l;

    @Column
    public Long v1500 = 0l;

    @Column
    public Long v1515 = 0l;

    @Column
    public Long v1530 = 0l;

    @Column
    public Long v1545 = 0l;

    @Column
    public Long v1600 = 0l;
                      

}