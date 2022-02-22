package com.mongodb.dc.demo.mongospringboot.mongospingboot.controller;

import java.util.List;
import java.util.Optional;

import com.mongodb.dc.demo.mongospringboot.mongospingboot.model.DailyCurrencyTotalAggregate;
import com.mongodb.dc.demo.mongospringboot.mongospingboot.model.Transaction;
import com.mongodb.dc.demo.mongospringboot.mongospingboot.repository.TransactionRepository;
import com.mongodb.dc.demo.mongospringboot.mongospingboot.repository.TransactionTemplateRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {
    @Autowired
    private TransactionRepository transactionRepo;

    @Autowired
    private TransactionTemplateRepository transactionTemplateRepo;

    // Spring Boot native - READ (Another example)
    @GetMapping("/transactions/{transactionId}")
    Transaction getTransactionById(@PathVariable String transactionId){
        Optional<Transaction> t = transactionRepo.findById(transactionId);
        if(t.isPresent()){
            return t.get();
        }
        return null;
    }

    // MongoTemplate - multi-document transactions
    @PostMapping("/template/transactions")
    Transaction addTransaction_TemplateStyle(@RequestBody Transaction newTransaction) {
        return transactionTemplateRepo.addTransaction(newTransaction);
    }

    @GetMapping("/template/transactions/dailycurrencytotal")
    List<DailyCurrencyTotalAggregate> getDailyCurrencyTotal(){
        return transactionTemplateRepo.getDailyCurrencyTotal();
    }
}
