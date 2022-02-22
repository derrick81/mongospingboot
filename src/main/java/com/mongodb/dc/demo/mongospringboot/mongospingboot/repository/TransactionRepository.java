package com.mongodb.dc.demo.mongospringboot.mongospingboot.repository;

import com.mongodb.dc.demo.mongospringboot.mongospingboot.model.Transaction;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
}
