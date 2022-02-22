package com.mongodb.dc.demo.mongospringboot.mongospingboot.model;

import org.bson.types.Decimal128;
import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BalanceStatsAggregate {
    @Id
    private String accountType;
    private Decimal128 average;
    private Decimal128 minimum;
    private Decimal128 maximum;
    private double stdDeviation;
}
