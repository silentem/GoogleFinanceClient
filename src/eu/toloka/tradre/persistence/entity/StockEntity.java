package eu.toloka.tradre.persistence.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */

@Entity
@Table(name = "STOCK")
@SuppressWarnings({"UnusedDeclaration"})
public class StockEntity extends BasicEntity{

    @Id
    @Column(nullable = false)
    public Integer id;

    @Column
    public String symbol;

    @Column
    public String exchange;

    @Column
    public String companyName;

    @Column
    public String country;

    @Column
    public String capitalization;

    @Column
    public Float beta;

    @Column
    public Boolean active;

    @Column
    public Integer watch;

    @Column
    public Float lastPrice;

    @Column
    public Float deviation;

    @Column
    public Integer counter;

    @Column
    public Float pivot;

    @Column
    public Integer barsNumber;

    @Column
    public String related;

    @ManyToOne(cascade = CascadeType.ALL)
    public SectorEntity sector;

    @ManyToOne(cascade = CascadeType.ALL)
    public IndustryEntity industry;

    @Override
    public String toString() {
        return "StockEntity{" +
            "symbol='" + symbol + '\'' +
            ", companyName='" + companyName + '\'' +
            '}';
    }
}
