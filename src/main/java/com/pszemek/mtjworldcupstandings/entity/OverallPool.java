package com.pszemek.mtjworldcupstandings.entity;

import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity(name = "overall_pool")
@Getter
public class OverallPool {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "amount", columnDefinition = "numeric(7, 2) default 0")
    private BigDecimal amount;

    public OverallPool setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }
}
