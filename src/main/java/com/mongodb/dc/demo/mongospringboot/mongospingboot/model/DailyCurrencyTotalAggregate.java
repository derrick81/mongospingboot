package com.mongodb.dc.demo.mongospringboot.mongospingboot.model;

import java.time.LocalDateTime;

import org.bson.types.Decimal128;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DailyCurrencyTotalAggregate {
    private String currency;
    @Field("dailyTotal")
    private Decimal128 dailyTotal;
    private LocalDateTime date;
}
