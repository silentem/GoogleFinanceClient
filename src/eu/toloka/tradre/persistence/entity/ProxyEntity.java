package eu.toloka.tradre.persistence.entity;

import javax.persistence.*;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
@Entity
@Table(name = "PROXY")
@SequenceGenerator(name = "proxySeq", sequenceName = "proxySeq")
@SuppressWarnings({"UnusedDeclaration"})
public class ProxyEntity extends BasicEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proxySeq")
    public Integer id;

    @Column
    public String url;

    @Column
    public String ip;

    @Column
    public String software;

    @Column
    public Integer responseTime;

    @Column
    public Integer counter;

    @Column
    public String domain;

    @Column
    public Boolean active;

    @Transient
    public String cookies;

    @Column
    public Integer error;

    @Column
    public Integer noData;
}
