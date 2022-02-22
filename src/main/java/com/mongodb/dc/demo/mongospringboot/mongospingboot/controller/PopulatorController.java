package com.mongodb.dc.demo.mongospringboot.mongospingboot.controller;

import com.mongodb.dc.demo.mongospringboot.mongospingboot.Populator;
import com.mongodb.dc.demo.mongospringboot.mongospingboot.model.Account;
import com.mongodb.dc.demo.mongospringboot.mongospingboot.repository.AccountRepository;
import com.mongodb.dc.demo.mongospringboot.mongospingboot.repository.TransactionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableMongoRepositories //https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mongo-repo-usage
public class PopulatorController {
    @Autowired
    private AccountRepository accountRepo;

	@Autowired
    private TransactionRepository transactionRepo;

    @GetMapping("/populate")
    Account[] populateAccountsAndTransactions(@RequestParam int numAccts, @RequestParam int numTxns){
        Populator populator = Populator.instance();
        Account[] accounts = populator.populateAccounts(accountRepo, numAccts);
        populator.populateTransactions(transactionRepo, accounts, numTxns);   
        return accounts;
    }
}
