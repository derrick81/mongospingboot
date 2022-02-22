package com.mongodb.dc.demo.mongospringboot.mongospingboot.controller;

import com.mongodb.MongoException;
import com.mongodb.dc.demo.mongospringboot.mongospingboot.model.*;
import com.mongodb.dc.demo.mongospringboot.mongospingboot.repository.AccountRepository;
import com.mongodb.dc.demo.mongospringboot.mongospingboot.repository.TransactionRepository;

import org.springframework.transaction.annotation.Transactional;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MongoTransactions {
    
    /*
        In proxy mode (which is the default), only external method calls coming in through the proxy are intercepted. This means that self-invocation, in effect, a method within the target object calling another method of the target object, will not lead to an actual transaction at runtime even if the invoked method is marked with @Transactional.
        https://docs.spring.io/spring-framework/docs/4.2.x/spring-framework-reference/html/transaction.html
    */
    @Transactional(rollbackFor = {MongoException.class})
    public Transaction performMongoTransaction(AccountRepository accountRepo, TransactionRepository transactionRepo, 
                                                Account fromAccount, Account toAccount, Transaction transaction){
        Account a = accountRepo.save(fromAccount);
        if(a != null){
            throw new MongoException("Rollback transaction");
        }
        accountRepo.save(toAccount);
        return transactionRepo.save(transaction);
    }
}
