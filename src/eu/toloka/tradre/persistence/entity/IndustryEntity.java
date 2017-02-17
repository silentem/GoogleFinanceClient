package eu.toloka.tradre.persistence.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */

@Entity
@Table(name = "INDUSTRY")
@SequenceGenerator(name = "industrySeq", sequenceName = "industrySeq")
@SuppressWarnings({"UnusedDeclaration"})
public class IndustryEntity {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "industrySeq")
    public Integer id;

    @Column
    public String name;

    @ManyToOne(cascade = CascadeType.ALL)
    public SectorEntity sector;
}
