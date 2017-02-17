package eu.toloka.tradre.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
@Entity
@Table(name = "STATISTICS")
@SequenceGenerator(name = "statisticsSeq", sequenceName = "statisticsSeq")
@SuppressWarnings({"UnusedDeclaration"})
public class StatisticsEntity extends BasicEntity{
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "statisticsSeq")
    public Integer id;

    @Column
    public String symbol;

    @Column
    public String template;

    @Column
    @Temporal(value = TemporalType.TIMESTAMP)
    public Date date;
}
