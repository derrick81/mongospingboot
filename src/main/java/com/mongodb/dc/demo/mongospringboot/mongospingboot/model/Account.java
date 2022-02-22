package com.mongodb.dc.demo.mongospringboot.mongospingboot.model;

import java.time.LocalDateTime;
import java.util.List;

import org.bson.types.Decimal128;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Document("accounts")
@Data
@NoArgsConstructor
public class Account {
    
    /*
        1. A property or field annotated with @Id (org.springframework.data.annotation.Id) maps to the _id field.
        2. A property or field without an annotation but named id maps to the _id field.

        1. If possible, an id property or field declared as a String in the Java class is converted to and stored as an ObjectId by using a Spring Converter<String, ObjectId>. Valid conversion rules are delegated to the MongoDB Java driver. If it cannot be converted to an ObjectId, then the value is stored as a string in the database.
        2. An id property or field declared as BigInteger in the Java class is converted to and stored as an ObjectId by using a Spring Converter<BigInteger, ObjectId>.

        https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mongo-template.id-handling
     */
    @Id
    private String accountNo;
    private AccountType type;
    private int branchCode;
    private CustomerBrief customer;
    private Decimal128 balance;
    private LocalDateTime lastUpdate = LocalDateTime.now();
    private List<TransactionBrief> lastTenTransactions;

    @NoArgsConstructor //Required for POST creating a new account
    @Getter
    public class CustomerBrief{
        private String id;
        private String firstName;
        private String lastName;

        @PersistenceConstructor
        public CustomerBrief(String id, String firstName, String lastName){
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class TransactionBrief{
        private String toAccountCustomerName;
        private Decimal128 amount;
        private String currency;
        private LocalDateTime transactionDateTime;

        @PersistenceConstructor
        public TransactionBrief(String toAccountCustomerName, Decimal128 amount, 
                                String currency, LocalDateTime transactionDateTime){
            this.toAccountCustomerName = toAccountCustomerName;
            this.amount = amount;
            this.currency = currency;
            this.transactionDateTime = transactionDateTime;
        }
    }

    public enum AccountType{
        SAVINGS, CURRENT, CREDIT, DEBIT, INVESTMENT, LOAN
    }

    public Account(AccountType type, int branchCode, Decimal128 initialBalance, 
                    String customerId, String customerFirstName, String customerLastName){
        this.type = type;
        this.branchCode = branchCode;
        this.balance = initialBalance;
        this.customer = new CustomerBrief(customerId, customerFirstName, customerLastName);
        this.lastUpdate = LocalDateTime.now();
    }
}
