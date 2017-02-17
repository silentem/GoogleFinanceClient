package eu.toloka.tradre.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.SequenceGenerator;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */

@Entity
@Table(name = "SECTOR")
@SequenceGenerator(name = "sectorSeq", sequenceName = "sectorSeq")
@SuppressWarnings({"UnusedDeclaration"})
public class SectorEntity extends BasicEntity{
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sectorSeq")
    public Integer id;

    @Column
    public String name;
}
