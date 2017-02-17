package eu.toloka.tradre.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */

@Entity
@Table(name = "COUNTRY")
@SequenceGenerator(name = "countrySeq", sequenceName = "countrySeq")
@SuppressWarnings({"UnusedDeclaration"})
public class CountryEntity extends BasicEntity{
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "countrySeq")
    public Integer id;

    @Column
    public String name;
}