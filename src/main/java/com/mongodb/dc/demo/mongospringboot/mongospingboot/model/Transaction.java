package com.mongodb.dc.demo.mongospringboot.mongospingboot.model;

import java.time.LocalDateTime;

import org.bson.types.Decimal128;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Document("transactions")
@Data
@NoArgsConstructor
public class Transaction {
    @Id
    private String id;
    private AccountBrief fromAccount;
    private AccountBrief toAccount;
    private Decimal128 amount;
    private String currency; 
    private LocalDateTime timestamp;
    
    @NoArgsConstructor //Required for POST creating a new account
    @Getter
    public class AccountBrief{
        private String accountNo;
        private String customerName;

        @PersistenceConstructor
        public AccountBrief(String accountNo, String customerName){
            this.accountNo = accountNo;
            this.customerName = customerName;
        }

        public void setCustomerName(String firstName, String lastName){
            this.customerName = firstName + " " + lastName;
        }
    }

    public Transaction(String fromAccountNo, String fromCustomerName, 
                       String toAccountNo, String toCustomerName, 
                       Decimal128 amount, String currency, LocalDateTime transactionTimestamp){
        this.fromAccount = new AccountBrief(fromAccountNo, fromCustomerName);
        this.toAccount = new AccountBrief(toAccountNo, toCustomerName);
        this.amount = amount;
        this.currency = currency;
        this.timestamp = transactionTimestamp;
    }
}
