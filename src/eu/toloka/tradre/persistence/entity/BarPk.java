package eu.toloka.tradre.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */

@Embeddable
public class BarPk implements Serializable {
    @Column
    public String symbol;

    @Column
    public Long time;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BarPk)) return false;

        BarPk barPk = (BarPk) o;

        return symbol.equals(barPk.symbol) && time.equals(barPk.time);
    }

    @Override
    public int hashCode() {
        int result = symbol != null ? symbol.hashCode() : 0;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        return result;
    }
}
